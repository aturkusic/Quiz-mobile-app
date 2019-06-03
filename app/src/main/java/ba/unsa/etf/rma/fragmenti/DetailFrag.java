package ba.unsa.etf.rma.fragmenti;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.common.collect.Lists;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.KategorijeAdapter;
import ba.unsa.etf.rma.adapteri.KvizAdapter;
import ba.unsa.etf.rma.aktivnosti.DodajKvizAkt;
import ba.unsa.etf.rma.aktivnosti.KvizoviAkt;
import ba.unsa.etf.rma.klase.Interfejsi;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;

import static ba.unsa.etf.rma.aktivnosti.KvizoviAkt.ucitajKategoriju;
import static ba.unsa.etf.rma.aktivnosti.KvizoviAkt.ucitajSveKategorijeIzBaze;

public class DetailFrag extends Fragment {
    private ArrayList<Kviz> kvizovi = new ArrayList<>();
    private KvizAdapter adapter;
    private GridView grid;
    private ZaKomunikacijuSaBaznom interfejs;

    public interface ZaKomunikacijuSaBaznom {
        void dodajKviz(int position);
        void igrajKviz(int position);
    }

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

            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    interfejs.igrajKviz(position);
                }
            });

            grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    interfejs.dodajKviz(position);
                    return true;
                }
            });

        }
    }

    public void dodajSveKvizove(ArrayList<Kviz> kvizo) {
        kvizovi.clear();
        kvizovi.addAll(kvizo);
        adapter.notifyDataSetChanged();
    }

    public void dodajIzmijeniKviz(ArrayList<Kviz> kviz_ovi) {
        kvizovi.clear();
        kvizovi.addAll(kviz_ovi);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ZaKomunikacijuSaBaznom) {
            interfejs = (ZaKomunikacijuSaBaznom) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement ZaKomunikacijuSaBazom");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        interfejs = null;
    }

    public KvizAdapter getAdapter() {
        return adapter;
    }
}
