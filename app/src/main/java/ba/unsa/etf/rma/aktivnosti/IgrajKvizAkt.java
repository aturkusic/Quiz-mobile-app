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
import ba.unsa.etf.rma.klase.Pitanje;

public class IgrajKvizAkt extends AppCompatActivity {

    private TextView textPitanja;
    private TextView nazivKviza;
    private TextView brojTacnih;
    private TextView brojPreostalih;
    private TextView postotakTacnih;
    private ListView listaOdgovora;
    private Button zavrsiKvizBtn;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragmenti_kviz);

        textPitanja = (TextView) findViewById(R.id.tekstPitanja);
        nazivKviza = (TextView) findViewById(R.id.infNazivKviza);
        brojTacnih = (TextView) findViewById(R.id.infBrojTacnihPitanja);
        brojPreostalih = (TextView) findViewById(R.id.infBrojPreostalihPitanja);
        postotakTacnih = (TextView) findViewById(R.id.infProcenatTacni);
        listaOdgovora = (ListView) findViewById(R.id.odgovoriPitanja);
        zavrsiKvizBtn = (Button) findViewById(R.id.btnKraj);


        FragmentManager fm = getFragmentManager();
        FrameLayout ldetalji = (FrameLayout)findViewById(R.id.pitanjePlace);

        if(ldetalji!=null){
            PitanjeFrag pitanjeFrag;
            pitanjeFrag = (PitanjeFrag)fm.findFragmentById(R.id.pitanjePlace);

            if(pitanjeFrag == null) {

                pitanjeFrag = new PitanjeFrag();
                fm.beginTransaction().replace(R.id.pitanjePlace, pitanjeFrag).commit();
            }
        }

        InformacijeFrag informacijeFrag = (InformacijeFrag)fm.findFragmentById(R.id.informacijePlace);

        if(informacijeFrag == null){

            informacijeFrag = new InformacijeFrag();
            fm.beginTransaction().replace(R.id.informacijePlace, informacijeFrag).commit();
        }else{
            fm.popBackStack(null,FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

    }
}
