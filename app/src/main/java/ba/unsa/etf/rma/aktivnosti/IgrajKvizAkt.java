package ba.unsa.etf.rma.aktivnosti;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.fragmenti.InformacijeFrag;
import ba.unsa.etf.rma.fragmenti.PitanjeFrag;
import ba.unsa.etf.rma.fragmenti.RangLista;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;

public class IgrajKvizAkt extends AppCompatActivity implements PitanjeFrag.PitanjeFragListener {

    private PitanjeFrag pitanjeFrag;
    private InformacijeFrag informacijeFrag;
    private RangLista rangListaFrag;
    private String imeIgraca;
    private  Kviz kviz;
    FragmentManager fm;
    FrameLayout ldetalji;
    AlarmManager manager;
    BroadcastReceiver receiver;
    PendingIntent pintent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragmenti_kviz);

        kviz = (Kviz)getIntent().getSerializableExtra("kviz");

        fm = getFragmentManager();

        ldetalji = (FrameLayout)findViewById(R.id.pitanjePlace);

        if(ldetalji != null){
            pitanjeFrag = (PitanjeFrag)fm.findFragmentById(R.id.pitanjePlace);
            if(pitanjeFrag == null) {
                pitanjeFrag = new PitanjeFrag();
                Bundle argumenti=new Bundle();
                argumenti.putSerializable("kviz", kviz);
                pitanjeFrag.setArguments(argumenti);
                fm.beginTransaction().replace(R.id.pitanjePlace, pitanjeFrag).commit();
            }
        }

        informacijeFrag = (InformacijeFrag)fm.findFragmentById(R.id.informacijePlace);

        if(informacijeFrag == null){
            informacijeFrag = new InformacijeFrag();
            Bundle argumenti=new Bundle();
            argumenti.putSerializable("kviz", kviz);
            informacijeFrag.setArguments(argumenti);
            fm.beginTransaction().replace(R.id.informacijePlace, informacijeFrag).commit();
        } else {
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        if(kviz.getPitanja().size() != 0) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.SECOND, (int) ((kviz.getPitanja().size() / 2.) * 60));

            receiver = dajBroadcastReceiver();

            this.registerReceiver(receiver, new IntentFilter("nesto"));

            pintent = PendingIntent.getBroadcast(this, 0, new Intent("nesto"), 0);
            manager = (AlarmManager) (this.getSystemService(Context.ALARM_SERVICE));

            manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pintent);
        }

    }

    private BroadcastReceiver dajBroadcastReceiver() {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override public void onReceive( Context context, Intent _ )
            {
                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Isteklo vrijeme!!!");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                alertDialog.show();
            }
        };
        return receiver;
    }

    @Override
    public void onInputASent(CharSequence brojTacnih, CharSequence brojPreostalih, CharSequence postotakTacnih) {
        informacijeFrag.updateEditText(brojTacnih, brojPreostalih, postotakTacnih);
    }

    @Override
    public void kvizZavrsen(Double postotakTacnih) {
        manager.cancel(pintent);
        if(rangListaFrag == null) {
            rangListaFrag = new RangLista();
            Bundle argumenti=new Bundle();
            argumenti.putSerializable("kviz", kviz);
            argumenti.putDouble("postotak", postotakTacnih);
            rangListaFrag.setArguments(argumenti);
            fm.beginTransaction().replace(R.id.pitanjePlace, rangListaFrag).commit();
        } else {
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

}
