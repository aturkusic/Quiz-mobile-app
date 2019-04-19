package ba.unsa.etf.rma.fragmenti;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kviz;

public class InformacijeFrag extends Fragment {
    InformacijeFragListener listener;
    private TextView nazivKviza;
    private TextView brojTacnih;
    private TextView brojPreostalih;
    private TextView postotakTacnih;
    private Button zavrsiKvizBtn;

    public interface InformacijeFragListener {
        void onInputASent(CharSequence input);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_informacije, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getArguments().containsKey("kviz")) {
            Kviz kviz = (Kviz)getArguments().getSerializable("kviz");
            nazivKviza = (TextView) getView().findViewById(R.id.infNazivKviza);
            brojTacnih = (TextView) getView().findViewById(R.id.infBrojTacnihPitanja);
            brojPreostalih = (TextView) getView().findViewById(R.id.infBrojPreostalihPitanja);
            postotakTacnih = (TextView)getView().findViewById(R.id.infProcenatTacni);
            zavrsiKvizBtn = (Button) getView().findViewById(R.id.btnKraj);



        }
    }

    public void updateEditText(CharSequence newText) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof InformacijeFragListener) {
//            listener = (InformacijeFragListener) context;
//        } else {
//            throw new RuntimeException(context.toString() + " must implement PitanjeFragListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

}
