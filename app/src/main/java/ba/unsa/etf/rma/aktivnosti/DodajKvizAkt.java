package ba.unsa.etf.rma.aktivnosti;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.common.collect.Lists;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import ba.unsa.etf.rma.adapteri.SpinerAdapter;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.adapteri.ListaPitanjaAdapter;
import ba.unsa.etf.rma.klase.Pitanje;
import ba.unsa.etf.rma.R;

public class DodajKvizAkt extends AppCompatActivity {
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;
    private static final int SECOND_ACTIVITY_REQUEST_CODE1 = 1;
    ArrayList<Pitanje> pitanja = new ArrayList<>();
    ListaPitanjaAdapter adapter;
    ListaPitanjaAdapter adapter1;
    String naziv;
    Kategorija kategorija;
    Kategorija dodanaKategorija = null;
    Kviz kviz = new Kviz(); //odabrani kviz sa glavne aktivnosti
    ArrayList<Kategorija> kategorije;
    SpinnerAdapter spinnerAdapter;
    Spinner spinner;
    ArrayList<Kviz> kvizovi; //svi kvizovi
    String svrha;
    String token;
    ListView listaPitanja;
    ListView listaMogucihPitanja;
    EditText imeKviza;
    Button dugme;
    Button importujBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dodaj_kviz);

        listaPitanja = (ListView) findViewById(R.id.lvDodanaPitanja);
        listaMogucihPitanja = (ListView) findViewById(R.id.lvMogucaPitanja);
        imeKviza = (EditText) findViewById(R.id.etNaziv);
        spinner = (Spinner) findViewById(R.id.spKategorije);
        dugme = (Button) findViewById(R.id.btnDodajKviz);
        importujBtn = (Button) findViewById(R.id.btnImportKviz);

        new DobaviTokenKlasa().execute();
        new DobaviKviz().execute("https://firestore.googleapis.com/v1/projects/rma19turkusicarslan73/databases/(default)/documents/Kvizovi?access_token=");


        //dobavljanje podataka putem intenta i postavljanje default vrijednosti kao eleent liste za dodavanje pitanja
        Intent intent = getIntent();
        kategorija = (Kategorija) intent.getSerializableExtra("kategorija");
        kategorije = (ArrayList<Kategorija>) intent.getSerializableExtra("kategorije");
        pitanja = (ArrayList<Pitanje>) intent.getSerializableExtra("pitanja");
        kvizovi = (ArrayList<Kviz>) intent.getSerializableExtra("kvizovi");
        naziv = intent.getStringExtra("naziv");
        kviz = (Kviz) intent.getSerializableExtra("kviz");
        svrha = intent.getStringExtra("svrha");
        Pitanje dodajPitanje = new Pitanje();
        dodajPitanje.setNaziv("Dodaj Pitanje");
        if(pitanja.size() == 0 || !pitanja.get(pitanja.size() - 1).getNaziv().equalsIgnoreCase("Dodaj pitanje"))
            pitanja.add(dodajPitanje);
        if(!kategorije.get(kategorije.size() - 1).getNaziv().equalsIgnoreCase("Dodaj kategoriju")) {
            Kategorija addKat = new Kategorija();
            addKat.setNaziv("Dodaj kategoriju");
            addKat.setId("addkviz");
            kategorije.add(addKat);
        }


        if(svrha.equals("izmjena")) dugme.setText("Izmijeni kviz");

        final ArrayList<Pitanje> listaMogucih = new ArrayList<Pitanje>();

        //postavljanje adaptera i vrijednosti polja ako je u pitanju izmjena
        Resources resources = getResources();
        adapter = new ListaPitanjaAdapter(this, pitanja, resources );
        listaPitanja.setAdapter( adapter );
        adapter1 = new ListaPitanjaAdapter(this, listaMogucih, resources );
        listaMogucihPitanja.setAdapter( adapter1 );
        spinnerAdapter = new SpinerAdapter(this, kategorije);
        spinner.setAdapter(spinnerAdapter);
        imeKviza.setText(naziv);

        spinner.setSelection(((SpinerAdapter) spinnerAdapter).getPozicija(kategorija));

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
                    intent.putExtra("pitanja", pitanja);
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
                    spinner.setSelection(0);
                    Intent intent = new Intent(DodajKvizAkt.this, DodajKategorijuAkt.class);
                    intent.putExtra("kategorije", kategorije);
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
                } else if(imeKviza.getText().toString().equalsIgnoreCase("Dodaj kategoriju") || imeKviza.getText().toString().equalsIgnoreCase("Svi")) {
                    imeKviza.setBackgroundColor(Color.RED);
                    imeKviza.setHint("Odaberite drugo ime");
                    return;
                }
                if(daLiPostojiKviz(imeKviza.getText().toString())) {
                    imeKviza.setBackgroundColor(Color.RED);
                    imeKviza.setHint("Odaberite drugo ime");
                    return;
                }
                imeKviza.setHint("");
                imeKviza.setBackgroundColor(0x00000000);
                kategorija = (Kategorija) spinner.getSelectedItem();
                kviz = new Kviz(imeKviza.getText().toString(), pitanja, kategorija);
                Intent povratni = new Intent();
                povratni.putExtra("povratniKviz", kviz);
                povratni.putExtra("dodaneKategorije", kategorije);
                povratni.putExtra("tip", svrha);
                setResult(Activity.RESULT_OK, povratni);
                finish();

            }
        });

        importujBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFileSearch();
            }
        });
    }
    private static final int READ_REQUEST_CODE = 42;

    public void performFileSearch() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        intent.addCategory(Intent.CATEGORY_OPENABLE);

        intent.setType("text/*");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onBackPressed() {
            Intent povratni = new Intent();
            povratni.putExtra("dodaneKategorije", kategorije);
            povratni.putExtra("tip", svrha);
            setResult(Activity.RESULT_OK, povratni);
            finish();
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
                spinner.setSelection(kategorije.size() - 2);
            }
        } else if(requestCode == READ_REQUEST_CODE) {//dodavanje kviza iz txt datoteke
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                //provjera da li je sve uredu
                if(!daLiJeSveUreduUDatoteci(uri)) return;
                try {
                    InputStream is = getContentResolver().openInputStream(uri);
                    String str = "";
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                        if (is != null) {
                            int i = 0;
                            while ((str = reader.readLine()) != null) {
                                if(str.equals("")) continue;
                                String[] niz = str.split(",");
                                if(i == 0) {
                                    imeKviza.setText(niz[0]);
                                    Kategorija kat = new Kategorija(niz[1], "13");
                                    if(kategorije.contains(kat)) spinner.setSelection(kategorije.indexOf(kat));
                                    else {
                                        kategorije.add(kategorije.size() - 1, kat);
                                        spinner.setSelection(kategorije.size() - 2);
                                        dodanaKategorija = kat;
                                    }
                                } else {
                                    Pitanje pitanje = new Pitanje();
                                    pitanje.setNaziv(niz[0]);
                                    pitanje.setTekstPitanja(niz[0]);
                                    for(int j = 3; j < 3 + Integer.parseInt(niz[1]); j++) {
                                        pitanje.getOdgovori().add(niz[j]);
                                    }
                                    pitanje.setTacanOdgovor(pitanje.getOdgovori().get(Integer.parseInt(niz[2])));
                                    pitanja.add(pitanja.size() - 1, pitanje);
                                    adapter.notifyDataSetChanged();
                                }
                                i++;
                            }
                        }
                    } finally {
                        try { is.close();
                        } catch (Throwable ignore) {}
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean daLiJeSveUreduUDatoteci(Uri uri) {
        InputStream is = null;
        try {
            is = getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        String str = "";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            if (is != null) {
                int i = 0;
                ArrayList<Pitanje> pitanja = new ArrayList<>();
                while ((str = reader.readLine()) != null) {
                    if(str.equals("")) continue;
                    String[] niz = str.split(",");
                    if(i == 0) {
                        if(niz.length < 3)  { dialogAkcija("Neispravani podaci za kviz"); return false; }
                        if(niz.length != 3) { dialogAkcija("Neispravan format datoteke"); return false; }
                        else if(daLiPostojiKviz(niz[0])) { dialogAkcija("Kviz kojeg importujete već postoji!"); return false; }
                        else if(brojLinijaUDatoteci(uri) - 1 != Integer.parseInt(niz[2])) { dialogAkcija("Kviz kojeg imporujete ima neispravan broj pitanja!"); return false;}
                    } else {
                        if(niz.length < 4)  { dialogAkcija("Neispravani podaci za pitanja"); return false; }
                        if(Integer.parseInt(niz[1]) != niz.length - 3) { dialogAkcija("Kviz kojeg importujete ima neispravan broj odgovora!"); return false; }
                        else if(Integer.parseInt(niz[2]) < 0 || Integer.parseInt(niz[2]) >  niz.length - 4) { dialogAkcija("Kviz kojeg importujete ima neispravan index tacnog odgovora!"); return false; }
                        Pitanje pitanje = new Pitanje();
                        pitanje.setNaziv(niz[0]);
                        if(pitanja.contains(pitanje)) { dialogAkcija("Kviz kojeg importujete ima vise istih pitanja!"); return false; }
                        pitanja.add(pitanje);
                        for(int j = 3; j < 3 + Integer.parseInt(niz[1]); j++) {
                            if(pitanje.getOdgovori().contains(niz[j]))  { dialogAkcija("Kviz kojeg importujete ima vise istih odgovora!"); return false; }
                            pitanje.getOdgovori().add(niz[j]);
                        }
                    }
                    i++;
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Throwable ignore) {
            }
        }
        return true;
    }

    private int brojLinijaUDatoteci(Uri uri) {
        InputStream is = null;
        try {
            is = getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        int lines = 0;
        String str = "";
        try {
            while ((str = reader.readLine())!= null)
                if(!str.equals("")) lines++;
            reader.close();
        } catch (IOException e) {

        }
        return lines;
    }

    private void dialogAkcija(String poruka) {
        AlertDialog alertDialog = new AlertDialog.Builder(DodajKvizAkt.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(poruka);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private boolean daLiPostojiKviz(String  s) {
        for(Kviz k : kvizovi) {
            if(k.getNaziv().equalsIgnoreCase(s)) {
                if(s.equalsIgnoreCase(kviz.getNaziv())) {

                } else {
                    return true;
                }
            }
        }
        return false;
    }

    public Kviz getKviz() {
        return kviz;
    }

    public void setKviz(Kviz kviz) {
        this.kviz = kviz;
    }

    private class DobaviTokenKlasa extends AsyncTask<URL, Integer, String> {
        protected String doInBackground(URL... urls) {
            InputStream is = getResources().openRawResource(R.raw.secret);
            GoogleCredential credentials = null;
            try {
                credentials = GoogleCredential.fromStream(is).createScoped(Lists.newArrayList("https://www.googleapis.com/auth/datastore"));
                credentials.refreshToken();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String TOKEN = credentials.getAccessToken();
            return TOKEN;

        }

        protected void onPostExecute(String result) {
            token = result;
        }
    }

    private class DobaviKviz extends AsyncTask<String, Integer, Void> {
        protected Void doInBackground(String... urls) {
            String url1 = urls[0] + token;
            URL url;
            try {
                url = new URL(url1);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String rezultat = convertStreamToString(in);
                JSONObject jo = null;

                jo = new JSONObject(rezultat);
                JSONObject kvizovi = jo.getJSONObject("Kvizovi");


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }  catch (JSONException e) {
                e.printStackTrace();
            }

            return null ;
        }


    }

    public String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new
                InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
        } finally {
            try {
                is.close();
            } catch (IOException e) {
            }
        }
        return sb.toString();
    }



}
