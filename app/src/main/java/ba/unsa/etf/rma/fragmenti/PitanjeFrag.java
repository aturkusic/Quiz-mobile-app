package ba.unsa.etf.rma.fragmenti;

import android.app.FragmentManager;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kviz;

public class PitanjeFrag extends Fragment {
    private Kviz kviz = new Kviz();
    private TextView textPitanja;
    private ListView listaOdgovora;
    private ArrayAdapter<String> adapter;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pitanje, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getArguments().containsKey("kviz")){
            kviz = (Kviz)getArguments().getSerializable("kviz");
            textPitanja = (TextView) getView().findViewById(R.id.tekstPitanja);
            listaOdgovora = (ListView) getView().findViewById(R.id.odgovoriPitanja);
            ArrayList<String> odgovori = kviz.getPitanja().get(0).getOdgovori();

            textPitanja.setText(kviz.getNaziv());

            adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, odgovori);
            listaOdgovora.setAdapter(adapter);

            FragmentManager fm = getFragmentManager();
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
    }
}
