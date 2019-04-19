package ba.unsa.etf.rma.aktivnosti;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.fragmenti.InformacijeFrag;
import ba.unsa.etf.rma.fragmenti.PitanjeFrag;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;

public class IgrajKvizAkt extends AppCompatActivity implements PitanjeFrag.PitanjeFragListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragmenti_kviz);

        Kviz kviz = (Kviz)getIntent().getSerializableExtra("kviz");

        FragmentManager fm = getFragmentManager();

        FrameLayout ldetalji = (FrameLayout)findViewById(R.id.pitanjePlace);

        if(ldetalji!=null){
            PitanjeFrag pitanjeFrag;
            pitanjeFrag = (PitanjeFrag)fm.findFragmentById(R.id.pitanjePlace);
            if(pitanjeFrag == null) {
                pitanjeFrag = new PitanjeFrag();
                Bundle argumenti=new Bundle();
                argumenti.putSerializable("kviz", kviz);
                pitanjeFrag.setArguments(argumenti);
                fm.beginTransaction().replace(R.id.pitanjePlace, pitanjeFrag).commit();
            }
        }

        InformacijeFrag informacijeFrag = (InformacijeFrag)fm.findFragmentById(R.id.informacijePlace);

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
    public void onInputASent(CharSequence input) {

    }
}
