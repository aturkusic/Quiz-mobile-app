package ba.unsa.etf.rma.aktivnosti;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.common.collect.Lists;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import ba.unsa.etf.rma.adapteri.ListaOdgovoriAdapter;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Pitanje;
import ba.unsa.etf.rma.R;

public class DodajPitanjeAkt extends AppCompatActivity {
    private Pitanje pitanje;
    private int pozicijaTacnog = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dodaj_pitanje);

        final EditText imePitanja = (EditText) findViewById(R.id.etNaziv);
        final EditText odgovor = (EditText) findViewById(R.id.etOdgovor);
        Button dodajBtn = (Button) findViewById(R.id.btnDodajOdgovor);
        Button dodajTacanBtn = (Button) findViewById(R.id.btnDodajTacan);
        Button dodajPitanjeBtn = (Button) findViewById(R.id.btnDodajPitanje);
        final ListView listaOdgovora = (ListView) findViewById(R.id.lvOdgovori);

        pitanje = (Pitanje)getIntent().getSerializableExtra("pitanje");
        final ArrayList<Pitanje> pitanja = (ArrayList<Pitanje>)getIntent().getSerializableExtra("pitanja");
        final ArrayList<String> odgovori = new ArrayList<>();

        Resources res = getResources();
        final ListaOdgovoriAdapter adapter = new ListaOdgovoriAdapter(this, odgovori, res);
        listaOdgovora.setAdapter(adapter);


        listaOdgovora.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                odgovori.remove(position);
                adapter.notifyDataSetChanged();
                pitanje.getOdgovori().remove(position);
                if (position < pozicijaTacnog) {
                    pozicijaTacnog--;
                    adapter.setPozicija(pozicijaTacnog);
                }
                else if (position == pozicijaTacnog) {
                    pozicijaTacnog = -1;
                    adapter.setPozicija(-1);
                    pitanje.setTacanOdgovor("");
                }
            }
        });

        dodajBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(odgovor.getText().toString().equals("")) {
                    odgovor.setBackgroundColor(Color.RED);
                    odgovor.setHint("Ne smije biti prazno");
                } else if(!(odgovori.indexOf(odgovor.getText().toString())== -1)) {
                    odgovor.setBackgroundColor(Color.RED);
                    odgovor.setHint("Odgovor vec postoji");
                    return;
                } else {
                    odgovor.setBackgroundColor(0x00000000);
                    odgovor.setHint("");
                    odgovori.add(odgovor.getText().toString());
                    adapter.notifyDataSetChanged();
                    pitanje.getOdgovori().add(odgovor.getText().toString());
                }
                odgovor.setText("");
            }
        });

        dodajTacanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!pitanje.getTacanOdgovor().equals("")) {
                    Toast.makeText(DodajPitanjeAkt.this, "Ne mogu dva tacna, obrisite stari.", Toast.LENGTH_LONG).show();
                    return;
                } else if(odgovor.getText().toString().equals("")) {
                    odgovor.setBackgroundColor(Color.RED);
                    odgovor.setHint("Ne smije biti prazno");
                    return;
                } else if(!(odgovori.indexOf(odgovor.getText().toString()) == -1)) { //isti odgovor vec postoji na to pitanje
                    odgovor.setBackgroundColor(Color.RED);
                    odgovor.setHint("Odgovor vec postoji");
                    return;
                }
                odgovor.setBackgroundColor(0x00000000);
                odgovori.add(odgovor.getText().toString());
                pozicijaTacnog = odgovori.size() - 1;
                adapter.setPozicija(pozicijaTacnog);
                adapter.notifyDataSetChanged();
                pitanje.getOdgovori().add(odgovor.getText().toString());
                pitanje.setTacanOdgovor(odgovor.getText().toString());
                odgovor.setText("");
            }
        });

        dodajPitanjeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imePitanja.getText().toString().equals("")) {
                    imePitanja.setBackgroundColor(Color.RED);
                    imePitanja.setHint("Ne smije biti prazno");
                    return;
                } else if(pitanje.getTacanOdgovor().equals("")) {
                    Toast.makeText(DodajPitanjeAkt.this, "Morate unijeti tacan odgovor", Toast.LENGTH_LONG).show();
                    return;
                }
                imePitanja.setBackgroundColor(0x00000000);
                imePitanja.setHint("");
                pitanje.setNaziv(imePitanja.getText().toString());
                pitanje.setTekstPitanja(imePitanja.getText().toString());
                pitanje.hashCode();
                new ProvjeriDaLiPostojiPitanjeUBazi().execute("Pitanja");
            }
        });

    }

    private class ProvjeriDaLiPostojiPitanjeUBazi extends AsyncTask<String, Integer,  ArrayList<Pitanje>> {
        protected ArrayList<Pitanje> doInBackground(String... urls) {
            ArrayList<Pitanje> listaPitanja = new ArrayList<>();
            try {
                InputStream is = getResources().openRawResource(R.raw.secret);
                GoogleCredential credentials = null;
                credentials = GoogleCredential.fromStream(is).createScoped(Lists.newArrayList("https://www.googleapis.com/auth/datastore"));
                credentials.refreshToken();
                String TOKEN = credentials.getAccessToken();
                listaPitanja = new ArrayList<>();
                String url1 = "https://firestore.googleapis.com/v1/projects/rma19turkusicarslan73/databases/(default)/documents/" + urls[0] + "/" + pitanje.getId() + "?access_token=" + TOKEN;
                URL url = new URL(url1);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                return null;
            }
            return listaPitanja;
        }

        @Override
        protected void onPostExecute(ArrayList<Pitanje> aVoid) {
            if(aVoid == null) {
                Intent povratni = new Intent();
                povratni.putExtra("povratnoPitanje", pitanje);
                setResult(Activity.RESULT_OK, povratni);
                finish();
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(DodajPitanjeAkt.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Uneseno pitanje vec postoji!");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        }
    }
}
