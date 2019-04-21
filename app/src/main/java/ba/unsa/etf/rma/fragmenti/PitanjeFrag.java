package ba.unsa.etf.rma.fragmenti;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.ListaOdgovoriAdapter;
import ba.unsa.etf.rma.adapteri.ListaOdgovoriFragAdapter;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;

public class PitanjeFrag extends Fragment {
    private PitanjeFragListener listener;
    private Kviz kviz = new Kviz();
    private TextView textPitanja;
    private ListView listaOdgovora;
    private ListaOdgovoriFragAdapter adapter;
    private ArrayList<String> odgovori;
    private ArrayList<Pitanje> pitanja;
    private Integer brojTacnih = 0;
    private Double postotakTacnih = 0.0;
    private Pitanje pitanje;
    int indexPitanja = 0;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pitanje, container, false);
    }

    public interface PitanjeFragListener {
        void onInputASent(CharSequence brojTacnihChar, CharSequence brojPreostalihChar, CharSequence postotakTacnihChar);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getArguments().containsKey("kviz")){
            kviz = (Kviz)getArguments().getSerializable("kviz");
            textPitanja = (TextView) getView().findViewById(R.id.tekstPitanja);
            listaOdgovora = (ListView) getView().findViewById(R.id.odgovoriPitanja);
            pitanja = new ArrayList<>(kviz.getPitanja());
            pitanja.remove(pitanja.size() - 1);
            Collections.shuffle(pitanja);
            if(pitanja.size() != 0) {
                pitanje = pitanja.get(indexPitanja);
                odgovori = pitanje.getOdgovori();
                textPitanja.setText(pitanje.getNaziv());

                Resources res = getResources();
                adapter = new ListaOdgovoriFragAdapter(getActivity(), odgovori, res);
                listaOdgovora.setAdapter(adapter);

            }

            listaOdgovora.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int pozicijaTacnog = pitanje.getOdgovori().indexOf(pitanje.getTacanOdgovor());
                    adapter.setPozicijaKliknutog(position);
                    adapter.setPozicijaTacnog(pozicijaTacnog);
                    if(position == pozicijaTacnog) brojTacnih++;
                    adapter.notifyDataSetChanged();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {

                            Integer preostali = pitanja.size() - 2 - indexPitanja;
                            if(preostali < 0) preostali++;
                            postotakTacnih = (brojTacnih.doubleValue() / (indexPitanja + 1)) * 100;
                            listener.onInputASent(brojTacnih.toString(), preostali.toString(), postotakTacnih.toString());

                            if(indexPitanja != pitanja.size() - 1) {
                                pitanje = pitanja.get(++indexPitanja);
                                odgovori.clear();
                                odgovori.addAll(pitanja.get(indexPitanja).getOdgovori());
                                textPitanja.setText(pitanje.getNaziv());
                            } else {
                                textPitanja.setText("“Kviz je završen!");
                                listaOdgovora.setEnabled(false);
                                odgovori.clear();
                            }
                            adapter.setPozicijaKliknutog(-1);
                            adapter.setPozicijaTacnog(-1);
                            adapter.notifyDataSetChanged();
                        }
                    }, 2000);

                }
            });

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PitanjeFragListener) {
            listener = (PitanjeFragListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement PitanjeFragListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
