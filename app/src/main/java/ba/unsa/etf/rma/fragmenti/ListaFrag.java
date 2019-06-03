package ba.unsa.etf.rma.fragmenti;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import java.util.ArrayList;
import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.KategorijeAdapter;
import ba.unsa.etf.rma.klase.Kategorija;


public class ListaFrag extends Fragment {
    private Filtriranje filtriranje;
    private ArrayList<Kategorija> kategorije = new ArrayList<>();
    private KategorijeAdapter adapter;
    private ListView lista;

    public interface Filtriranje {
        void filtriraj(Kategorija kategorija);
    }

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


            lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    adapter.setIndexOdabranog(position);
                    adapter.notifyDataSetChanged();
                    filtriranje.filtriraj(kategorije.get(position));
                }
            });

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Filtriranje) {
            filtriranje = (Filtriranje) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement ZaKomunikacijuSaBazom");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        filtriranje = null;
    }

    public void azurirajKategorije(ArrayList<Kategorija> kategorije1) {
        kategorije.clear();
        kategorije.addAll(kategorije1);
        adapter.notifyDataSetChanged();
    }

    public void dodajSveKategorije() {
        adapter.notifyDataSetChanged();
    }


}
