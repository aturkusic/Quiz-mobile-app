package ba.unsa.etf.rma;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class KvizoviAkt extends AppCompatActivity {
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;
    public  KvizoviAkt GlavnaKlasa = null;
    public ArrayList<Kviz> kvizovi = new ArrayList<>();
    public ArrayList<Kategorija> kategorije = new ArrayList<>();
    Kategorija svi;
    private ListaAdapter adapter;
    private boolean daLiJeIzmjena = false;
    private int pozicija = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kvizovi_akt);

        GlavnaKlasa = this;
///////////////////////////////////////
        Kviz testni = new Kviz();
        Kategorija cat = new Kategorija();
        cat.setNaziv("Science");
        cat.setId("science");
        testni.setNaziv("Testni");
        testni.setKategorija(cat);
        //kvizovi.add(testni);
        ArrayList<String> odg = new ArrayList<>();
        odg.add("da");
        odg.add("ne");
        Pitanje pitanje = new Pitanje("Prvo pitanje", "Da li ce raditi?", odg, "da");
        Pitanje pitanje1 = new Pitanje("Drugo pitanje", "Da li ce raditi?", odg, "ne");
        testni.getPitanja().add(pitanje);
        testni.getPitanja().add(pitanje1);
//////////////////////////////////////

        dodajAddKvizNaKraj();
        dodajSviKategorijuUSpinner();


        Resources res = getResources();

        final ListView list  = ( ListView )findViewById( R.id.lvKvizovi );
        final Spinner spinner = ( Spinner )findViewById(R.id.spPostojeceKategorije);

        adapter = new ListaAdapter(GlavnaKlasa, kvizovi, res);
        list.setAdapter(adapter);

        SpinnerAdapter adapterSpinner = new SpinnerAdapter(GlavnaKlasa, kategorije );
        spinner.setAdapter(adapterSpinner);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == kvizovi.size() - 1) { // ako je kliknuto na zadnji element tj dodavanje novog kviza
                    Intent intent = new Intent(KvizoviAkt.this, DodajKvizAkt.class);
                    daLiJeIzmjena = false;
                    intent.putExtra("naziv", "");
                    intent.putExtra("pitanja", new ArrayList<Pitanje>());
                    intent.putExtra("kategorija", svi);
                    intent.putExtra("kategorije", kategorije);
                    intent.putExtra("svrha", "dodavanje");
                    startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
                } else {
                    Intent intent = new Intent(KvizoviAkt.this, DodajKvizAkt.class);
                    intent.putExtra("naziv", kvizovi.get(position).getNaziv());
                    intent.putExtra("pitanja", kvizovi.get(position).getPitanja());
                    intent.putExtra("kategorija", kvizovi.get(position).getKategorija());
                    intent.putExtra("kategorije", kategorije);
                    intent.putExtra("kviz", kvizovi.get(position));
                    intent.putExtra("svrha", "izmjena");
                    pozicija = position;
                    daLiJeIzmjena = true;
                    startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Kategorija cat = (Kategorija)  spinner.getSelectedItem();
                    if(!cat.getNaziv().equals("Svi")) {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check that it is the SecondActivity with an OK result
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Get String data from Intent
                Kviz kviz = (Kviz) data.getSerializableExtra("povratniKviz");
                Kategorija kategorija = (Kategorija) data.getSerializableExtra("novaKategorija");
                if(!daLiJeIzmjena)
                    kvizovi.add(kvizovi.size() - 1, kviz);
                else {
                    kvizovi.get(pozicija).setNaziv(kviz.getNaziv());
                    kvizovi.get(pozicija).setKategorija(kviz.getKategorija());
                    kvizovi.get(pozicija).setPitanja(kviz.getPitanja());
                }
                if(kategorija != null) {
                    kategorije.add(kategorije.size(), kategorija);
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void dodajSviKategorijuUSpinner() {
        svi = new Kategorija();
        svi.setNaziv("Svi");
        kategorije.add(svi);
    }

    private void dodajAddKvizNaKraj() {
        Kviz addKviz = new Kviz();
        addKviz.setNaziv("Dodaj Kviz");
        Kategorija cat1 = new Kategorija();
        cat1.setNaziv("Dodaj kviz");
        cat1.setId("addkviz");
        addKviz.setKategorija(cat1);
        kvizovi.add(addKviz);
    }
}
