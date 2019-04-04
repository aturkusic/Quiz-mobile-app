package ba.unsa.etf.rma;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.util.ArrayList;

public class DodajKvizAkt extends AppCompatActivity {
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;
    private static final int SECOND_ACTIVITY_REQUEST_CODE1 = 1;
    ArrayList<Pitanje> pitanja = new ArrayList<>();
    ListaPitanjaAdapter adapter;
    ListaPitanjaAdapter adapter1;
    String naziv;
    Kategorija kategorija;
    Kategorija dodanaKategorija = null;
    Kviz kviz = new Kviz();
    ArrayList<Kategorija> kategorije;
    SpinnerAdapter spinnerAdapter;
     Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dodaj_kviz);

        final ListView listaPitanja = (ListView) findViewById(R.id.lvDodanaPitanja);
        ListView listaMogucihPitanja = (ListView) findViewById(R.id.lvMogucaPitanja);
        final EditText imeKviza = (EditText) findViewById(R.id.etNaziv);
        spinner = (Spinner) findViewById(R.id.spKategorije);
        Button dugme = (Button) findViewById(R.id.btnDodajKviz);

        //dobavljanje podataka putem intenta i postavljanje default vrijednosti kao eleent liste za dodavanje pitanja
        Intent intent = getIntent();
        kategorija = (Kategorija) intent.getSerializableExtra("kategorija");
        kategorije = (ArrayList<Kategorija>) intent.getSerializableExtra("kategorije");
        pitanja = (ArrayList<Pitanje>) intent.getSerializableExtra("pitanja");
        naziv = intent.getStringExtra("naziv");
        kviz = (Kviz) intent.getSerializableExtra("kviz");
        String svrha = intent.getStringExtra("svrha");
        Pitanje dodajPitanje = new Pitanje();
        dodajPitanje.setNaziv("Dodaj Pitanje");
        if(pitanja.size() == 0 || !pitanja.get(pitanja.size() - 1).getNaziv().equalsIgnoreCase("Dodaj pitanje"))
            pitanja.add(dodajPitanje);
            Kategorija addKat = new Kategorija();
            addKat.setNaziv("Dodaj kategoriju");
            addKat.setId("addkviz");
            kategorije.add(addKat);
            kategorije.remove(0);


        if(svrha.equals("izmjena")) dugme.setText("Izmijeni kviz");

        final ArrayList<Pitanje> listaMogucih = new ArrayList<Pitanje>();

        //postavljanje adaptera i vrijednosti polja ako je u pitanju izmjena
        Resources resources = getResources();
        adapter = new ListaPitanjaAdapter(this, pitanja, resources );
        listaPitanja.setAdapter( adapter );
        adapter1 = new ListaPitanjaAdapter(this, listaMogucih, resources );
        listaMogucihPitanja.setAdapter( adapter1 );
        spinnerAdapter = new ba.unsa.etf.rma.SpinnerAdapter(this, kategorije);
        ((ba.unsa.etf.rma.SpinnerAdapter) spinnerAdapter).setDrugaAktivnost(true);
        spinner.setAdapter(spinnerAdapter);
        imeKviza.setText(naziv);

        spinner.setSelection(((ba.unsa.etf.rma.SpinnerAdapter) spinnerAdapter).getPozicija(kategorija));

        listaPitanja.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != pitanja.size() - 1) {
                    listaMogucih.add(pitanja.remove(position));
                    adapter.notifyDataSetChanged();
                    adapter1.notifyDataSetChanged();
                } else {
                    Intent intent = new Intent(DodajKvizAkt.this, DodajPitanjeAkt.class);
                    intent.putExtra("pitanje", pitanja.get(position));
                    startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
                }
            }
        });

        listaMogucihPitanja.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pitanja.add(pitanja.size() - 1, listaMogucih.remove(position));
                adapter.notifyDataSetChanged();
                adapter1.notifyDataSetChanged();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == kategorije.size() - 1) {
                    Intent intent = new Intent(DodajKvizAkt.this, DodajKategorijuAkt.class);
                    startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dugme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Kategorija tmp = (Kategorija) spinner.getSelectedItem();
                if(imeKviza.getText().toString().equals("")) {
                    imeKviza.setHint("Ne smije biti prazno");
                    imeKviza.setBackgroundColor(Color.RED);
                    return;
                } else if(tmp.getNaziv().equals("Svi") || tmp.getNaziv().equalsIgnoreCase("Dodaj kategoriju")) {
                    Toast.makeText(DodajKvizAkt.this, "Odaberite drugu ili dodajte novu kategoriju.", Toast.LENGTH_LONG).show();
                    return;
                }
                imeKviza.setBackgroundColor(0x00000000);
                kategorija = (Kategorija) spinner.getSelectedItem();
                kviz = new Kviz(imeKviza.getText().toString(), pitanja, kategorija);
                Intent povratni = new Intent();
                povratni.putExtra("povratniKviz", kviz);
                povratni.putExtra("novaKategorija", dodanaKategorija);
                setResult(Activity.RESULT_OK, povratni);
                finish();

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
                Pitanje pitanje = (Pitanje) data.getSerializableExtra("povratnoPitanje");
                pitanja.add(pitanja.size() - 1, pitanje);
                adapter.notifyDataSetChanged();
            }
        } else if(requestCode == SECOND_ACTIVITY_REQUEST_CODE1) {
            if (resultCode == RESULT_OK) {
                Kategorija kategorija = (Kategorija) data.getSerializableExtra("povratnaKategorija");
                dodanaKategorija = kategorija;
                kategorije.add(kategorije.size() - 1, kategorija);
            }
        }
    }

    public Kviz getKviz() {
        return kviz;
    }

    public void setKviz(Kviz kviz) {
        this.kviz = kviz;
    }
}