package ba.unsa.etf.rma.aktivnosti;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

import ba.unsa.etf.rma.fragmenti.DetailFrag;
import ba.unsa.etf.rma.fragmenti.ListaFrag;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.adapteri.ListaAdapter;
import ba.unsa.etf.rma.klase.Pitanje;
import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.SpinnerAdapter;

public class KvizoviAkt extends AppCompatActivity implements DetailFrag.ZaKomunikacijuSaBaznom, ListaFrag.Filtriranje {
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;
    public  KvizoviAkt GlavnaKlasa = null;
    public ArrayList<Kviz> kvizovi = new ArrayList<>();
    public ArrayList<Kategorija> kategorije = new ArrayList<>();
    private Kategorija svi;
    private ListaAdapter adapter;
    private boolean daLiJeIzmjena = false;
    private int pozicija = -1;
    private Spinner spinner;
    private Boolean siriL = false;
    private ListaFrag listaFrag;
    private DetailFrag detailFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kvizovi_akt);

        GlavnaKlasa = this;

        if(kvizovi.size() == 0 || !kvizovi.get(kvizovi.size() - 1).getNaziv().equalsIgnoreCase("dodaj kviz"))
            dodajAddKvizNaKraj();
        dodajSviKategorijuUSpinner();
        Resources res = getResources();


        final ListView list = (ListView) findViewById(R.id.lvKvizovi);
        if(list == null) {
            zaFrag();

        } else {
            spinner = (Spinner) findViewById(R.id.spPostojeceKategorije);

            adapter = new ListaAdapter(GlavnaKlasa, kvizovi, res);
            list.setAdapter(adapter);

            SpinnerAdapter adapterSpinner = new SpinnerAdapter(GlavnaKlasa, kategorije);
            spinner.setAdapter(adapterSpinner);


            //listener za listu kvizova, slanje podataka u aktivnost za dodavanja/izmjenu kviza
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    pokreniKviz(position);
                }
            });

            list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    return dodajIzmijeniKviz(position);
                }
            });

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Kategorija cat = (Kategorija) spinner.getSelectedItem();
                    if (!cat.getNaziv().equals("Svi")) {
                        ((ListaAdapter) adapter).getFilter().filter(kategorije.get(position).getNaziv());
                    } else {
                        ((ListaAdapter) adapter).setKat(svi);
                        ((ListaAdapter) adapter).getFilter().filter(kategorije.get(position).getNaziv());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    private void pokreniKviz(int position) {
        Intent intent = new Intent(KvizoviAkt.this, IgrajKvizAkt.class);
        intent.putExtra("kviz", kvizovi.get(position));
        if (!(position == kvizovi.size() - 1))
            startActivity(intent);
    }

    private boolean dodajIzmijeniKviz(int position) {
        Intent intent = new Intent(KvizoviAkt.this, DodajKvizAkt.class);
        Kviz kviz;
        if(siriL) kviz = (Kviz) detailFrag.getAdapter().getItem(position);
        else  kviz = (Kviz) adapter.getItem(position);
        position = pronadjiPozicijuUListi(kviz.getNaziv());
        if (position == kvizovi.size() - 1) { // ako je kliknuto na zadnji element tj dodavanje novog kviza
            Kviz pomocni = new Kviz();
            pomocni.setNaziv("");
            daLiJeIzmjena = false;
            intent.putExtra("naziv", "");
            intent.putExtra("pitanja", new ArrayList<Pitanje>());
            intent.putExtra("kategorija", svi);
            intent.putExtra("kategorije", kategorije);
            intent.putExtra("svrha", "dodavanje");
            intent.putExtra("kviz", pomocni);
            intent.putExtra("kvizovi", kvizovi);
        } else {
            intent.putExtra("naziv", kvizovi.get(position).getNaziv());
            intent.putExtra("pitanja", kvizovi.get(position).getPitanja());
            intent.putExtra("kategorija", kvizovi.get(position).getKategorija());
            intent.putExtra("kategorije", kategorije);
            intent.putExtra("kviz", kvizovi.get(position));
            intent.putExtra("kvizovi", kvizovi);
            intent.putExtra("svrha", "izmjena");
            pozicija = position;
            daLiJeIzmjena = true;
        }
        startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
        return true;
    }

    private void zaFrag() {
         siriL = false;

        FragmentManager fm = getFragmentManager();
        FrameLayout ldetalji = (FrameLayout)findViewById(R.id.listPlace);

        if(ldetalji != null){
            listaFrag = (ListaFrag) fm.findFragmentById(R.id.listPlace);
            if(listaFrag == null) {
                siriL = true;
                listaFrag = new ListaFrag();
                Bundle argumenti = new Bundle();
                argumenti.putSerializable("kategorije", kategorije);
                listaFrag.setArguments(argumenti);
                fm.beginTransaction().replace(R.id.listPlace, listaFrag).commit();
            }
        }

        detailFrag = (DetailFrag)fm.findFragmentById(R.id.detailPlace);
        if(detailFrag == null){
            detailFrag = new DetailFrag();
            Bundle argumenti = new Bundle();
            argumenti.putSerializable("kvizovi", kvizovi);
            detailFrag.setArguments(argumenti);
            fm.beginTransaction().replace(R.id.detailPlace, detailFrag).commit();
        }else{
            fm.popBackStack(null,FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

    }

    private int pronadjiPozicijuUListi(String imeKviza) {
        int i = 0;
        for(Kviz k : kvizovi) {
            if (k.getNaziv().equals(imeKviza))
                return i;
            i++;
        }
         return  -1;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(spinner != null) spinner.setSelection(0);
        // Check that it is the SecondActivity with an OK result
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Get String data from Intent
                Kviz kviz = (Kviz) data.getSerializableExtra("povratniKviz");
                ArrayList<Kategorija> kategorijee = (ArrayList<Kategorija>) data.getSerializableExtra("dodaneKategorije"); // lista svih dodanih kategorija u dodajKvizAkt
                String tip = data.getStringExtra("tip");
                if(kategorijee != null) {
                    kategorije.clear();
                    for(Kategorija k : kategorijee) {
                        if(!k.getNaziv().equalsIgnoreCase("dodaj kategoriju")) kategorije.add(k);
                    }
                }
                if(tip.equalsIgnoreCase("izmjena"))
                    daLiJeIzmjena = true;
                else daLiJeIzmjena = false;

                if(!daLiJeIzmjena && kviz != null)
                    kvizovi.add(kvizovi.size() - 1, kviz);
                else if(pozicija != -1 && kviz != null) {
                    kvizovi.get(pozicija).setNaziv(kviz.getNaziv());
                    kvizovi.get(pozicija).setKategorija(kviz.getKategorija());
                    kvizovi.get(pozicija).setPitanja(kviz.getPitanja());
                }
                if(adapter != null)
                    adapter.notifyDataSetChanged();
                else {
                    detailFrag.dodajIzmijeniKviz(kvizovi);
                    listaFrag.azurirajKategorije(kategorije);
                }
            }
        }
    }

    private void dodajSviKategorijuUSpinner() {
        svi = new Kategorija();
        svi.setNaziv("Svi");
        svi.setId("157");
        kategorije.add(svi);
    }

    private void dodajAddKvizNaKraj() {
        Kviz addKviz = new Kviz();
        addKviz.setNaziv("Dodaj Kviz");
        Kategorija cat1 = new Kategorija();
        cat1.setNaziv("Dodaj kviz");
        cat1.setId("1");
        addKviz.setKategorija(cat1);
        kvizovi.add(addKviz);
    }

    @Override
    public void dodajKviz(int position) {
        dodajIzmijeniKviz(position);
    }

    @Override
    public void igrajKviz(int position) {
        pokreniKviz(position);
    }

    @Override
    public void filtriraj(String kategorija, int pozicija) {
        if (!kategorija.equals("Svi")) {
            detailFrag.getAdapter().getFilter().filter(kategorije.get(pozicija).getNaziv());
        } else {
            detailFrag.getAdapter().setKat(svi);
            detailFrag.getAdapter().getFilter().filter(kategorije.get(pozicija).getNaziv());
        }
    }
}
