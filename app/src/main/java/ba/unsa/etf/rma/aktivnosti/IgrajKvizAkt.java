package ba.unsa.etf.rma.aktivnosti;


import android.app.FragmentManager;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragmenti_kviz);

        kviz = (Kviz)getIntent().getSerializableExtra("kviz");

        fm = getFragmentManager();

        ldetalji = (FrameLayout)findViewById(R.id.pitanjePlace);

//        Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
//        i.putExtra(AlarmClock.EXTRA_MESSAGE, "ALARM NAME");
//        i.putExtra(AlarmClock.EXTRA_HOUR, 0);
//        i.putExtra(AlarmClock.EXTRA_MINUTES, 1 );
//        i.putExtra(AlarmClock.EXTRA_SKIP_UI, true);//true if you want to set the Alarm without leaving your activity
//        startActivity(i);

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

    }

    @Override
    public void onInputASent(CharSequence brojTacnih, CharSequence brojPreostalih, CharSequence postotakTacnih) {
        informacijeFrag.updateEditText(brojTacnih, brojPreostalih, postotakTacnih);
    }

    @Override
    public void kvizZavrsen(Double postotakTacnih) {
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
