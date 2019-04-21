package ba.unsa.etf.rma.fragmenti;

import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.KategorijeAdapter;
import ba.unsa.etf.rma.adapteri.ListaAdapter;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;

public class ListaFrag extends Fragment {
    private ArrayList<Kategorija> kategorije = new ArrayList<>();
    private KategorijeAdapter adapter;
    private ListView lista;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.lista_frag, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getArguments().containsKey("kategorije")) {
            kategorije = (ArrayList<Kategorija>) getArguments().getSerializable("kategorije");
            lista = (ListView) getView().findViewById(R.id.listaKategorija);

            Resources res = getResources();
            adapter = new KategorijeAdapter(getActivity(), kategorije, res);
            lista.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }
    }

}
