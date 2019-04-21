package ba.unsa.etf.rma.fragmenti;

import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.KategorijeAdapter;
import ba.unsa.etf.rma.adapteri.KvizAdapter;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;

public class DetailFrag extends Fragment {
    private ArrayList<Kviz> kvizovi = new ArrayList<>();
    private KvizAdapter adapter;
    private GridView grid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.detalji_frag, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getArguments().containsKey("kvizovi")) {
            kvizovi = (ArrayList<Kviz>) getArguments().getSerializable("kvizovi");
            grid = (GridView) getView().findViewById(R.id.gridKvizovi);

            adapter = new KvizAdapter(getActivity(), kvizovi, getResources());
            grid.setAdapter(adapter);

        }
    }

}
