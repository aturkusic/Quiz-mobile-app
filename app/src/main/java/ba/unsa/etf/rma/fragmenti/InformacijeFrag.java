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
    private TextView nazivKviza;
    private TextView brojTacnih;
    private TextView brojPreostalih;
    private TextView postotakTacnih;
    private Button zavrsiKvizBtn;

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
            postotakTacnih = (TextView) getView().findViewById(R.id.infProcenatTacni);
            zavrsiKvizBtn = (Button) getView().findViewById(R.id.btnKraj);

            nazivKviza.setText(kviz.getNaziv());
            brojTacnih.setText("0");
            String preostali = "";
            if(kviz.getPitanja().size() == 1) preostali = "0";
            else if(kviz.getPitanja().get(kviz.getPitanja().size() - 1).getNaziv().equalsIgnoreCase("Dodaj pitanje"))
                preostali = Integer.toString(kviz.getPitanja().size() - 2);
            else preostali = Integer.toString(kviz.getPitanja().size() - 1);
            brojPreostalih.setText(preostali);
            postotakTacnih.setText("0.0");
        }

        zavrsiKvizBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    public void updateEditText(CharSequence brojTacnihChar, CharSequence brojPreostalihChar, CharSequence postotakTacnihChar) {
        brojTacnih.setText(brojTacnihChar);
        brojPreostalih.setText(brojPreostalihChar);
        postotakTacnih.setText(postotakTacnihChar);
    }


}
