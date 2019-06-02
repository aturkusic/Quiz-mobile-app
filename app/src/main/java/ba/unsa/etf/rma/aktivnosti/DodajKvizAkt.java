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
import android.util.Log;
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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import ba.unsa.etf.rma.adapteri.SpinerAdapter;
import ba.unsa.etf.rma.klase.Interfejsi;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.adapteri.ListaPitanjaAdapter;
import ba.unsa.etf.rma.klase.Pitanje;
import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Rang;
import ba.unsa.etf.rma.ostalo.Trojka;

public class DodajKvizAkt extends AppCompatActivity {
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;
    private static final int SECOND_ACTIVITY_REQUEST_CODE1 = 1;
    private DobaviMogucaPitanja dobaviMogucaPitanjaAsync = new DobaviMogucaPitanja();
    ArrayList<Pitanje> pitanja = new ArrayList<>();
    ArrayList<Pitanje> listaMogucih = new ArrayList<>();
    ListaPitanjaAdapter adapter;
    ListaPitanjaAdapter adapterMogucihPitanja;
    String naziv;
    Kategorija kategorija;
    Kategorija dodanaKategorija = null;
    Kviz kviz = new Kviz(); //odabrani kviz sa glavne aktivnosti
    String kvizPrijePromjene = ""; //odabrani kviz sa glavne aktivnosti
    ArrayList<Kategorija> kategorije;
    SpinnerAdapter spinnerAdapter;
    Spinner spinner;
    ArrayList<Kviz> kvizovi; //svi kvizovi
    String svrha;
    ListView listaPitanja;
    ListView listaMogucihPitanja;
    EditText imeKviza;
    Button dugme;
    Button importujBtn;
    String stariId;
    private Rang rangZaIzmijenit = new Rang();

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
            kategorije.add(addKat);
        }
        kvizPrijePromjene = kviz.getNaziv();
        if(!kviz.getNaziv().equals(""))stariId = String.valueOf(kviz.hashCode());

        if(svrha.equals("izmjena")) dugme.setText("Izmijeni kviz");

        //postavljanje adaptera i vrijednosti polja ako je u pitanju izmjena
        Resources resources = getResources();
        adapter = new ListaPitanjaAdapter(this, pitanja, resources );
        listaPitanja.setAdapter( adapter );
        adapterMogucihPitanja = new ListaPitanjaAdapter(this, listaMogucih, resources );
        listaMogucihPitanja.setAdapter(adapterMogucihPitanja);
        spinnerAdapter = new SpinerAdapter(this, kategorije);
        spinner.setAdapter(spinnerAdapter);
        imeKviza.setText(naziv);

        spinner.setSelection(((SpinerAdapter) spinnerAdapter).getPozicija(kategorija));


        new DobaviMogucaPitanja().execute("Pitanja");

        listaPitanja.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != pitanja.size() - 1) {
                    listaMogucih.add(pitanja.remove(position));
                    adapter.notifyDataSetChanged();
                    adapterMogucihPitanja.notifyDataSetChanged();
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
                adapterMogucihPitanja.notifyDataSetChanged();
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
                imeKviza.setHint("");
                imeKviza.setBackgroundColor(0x00000000);
                kategorija = (Kategorija) spinner.getSelectedItem();
                kviz = new Kviz(imeKviza.getText().toString(), pitanja, kategorija);
                kviz.hashCode();
                if(!kviz.getNaziv().equals(kvizPrijePromjene))
                    new ProvjeriDaLiPostojiKviz().execute("Kvizovi");
                else {
                    new DodajObrisiBaza().execute("Kvizovi", kviz.getId(), "obrisi"); // izmjena u bazi
                    new DodajObrisiBaza().execute("Kvizovi", kviz.getId()); // izmjena u bazi
                    new DobaviRanglistinIDIzBaze().execute();
                    Intent povratni = new Intent();
                    povratni.putExtra("povratniKviz", kviz);
                    povratni.putExtra("dodaneKategorije", kategorije);
                    povratni.putExtra("tip", svrha);
                    setResult(Activity.RESULT_OK, povratni);
                    finish();
                }
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
                String odgovori = "";
                int j = 0;
                for(String o : pitanje.getOdgovori()) {// odgovore stavljam u stri  ng kako bih mogao dodat u bazu
                    if (j != pitanje.getOdgovori().size() - 1)
                        odgovori += o + ",";
                    else odgovori += o;
                }
                new DodajObrisiBaza().execute("Pitanja", String.valueOf(pitanje.hashCode()), pitanje.getNaziv(), odgovori,
                        String.valueOf(pitanje.getIndexTacnog(pitanje.getTacanOdgovor())));
                pitanja.add(pitanja.size() - 1, pitanje);
                adapter.notifyDataSetChanged();
            }
        } else if(requestCode == SECOND_ACTIVITY_REQUEST_CODE1) {
            if (resultCode == RESULT_OK) {
                Kategorija kategorija = (Kategorija) data.getSerializableExtra("povratnaKategorija");
                dodanaKategorija = kategorija;
                new DodajObrisiBaza().execute("Kategorije", String.valueOf(dodanaKategorija.hashCode())); // dodajem u bazu
                kategorije.add(kategorije.size() - 1, kategorija);
                spinner.setSelection(kategorije.size() - 2);
            }
        } else if(requestCode == READ_REQUEST_CODE) {//dodavanje kviza iz txt datoteke
            pitanja.clear();
            adapter.notifyDataSetChanged();
            Pitanje dodajPitanje = new Pitanje();
            dodajPitanje.setNaziv("Dodaj Pitanje");
            pitanja.add(dodajPitanje);
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
                                        new DodajObrisiBaza().execute("Kategorije", String.valueOf(dodanaKategorija.hashCode())); // dodajem u bazu
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
                                    String odgovori = "";
                                    int j = 0;
                                    for(String o : pitanje.getOdgovori()) {// odgovore stavljam u string kako bih mogao dodat u bazu
                                        if (j != pitanje.getOdgovori().size() - 1)
                                            odgovori += o + ",";
                                        else odgovori += o;
                                    }
                                    listaMogucih.remove(pitanje);
                                    new DodajObrisiBaza().execute("Pitanja", String.valueOf(pitanje.hashCode()), pitanje.getNaziv(), odgovori,
                                            String.valueOf(pitanje.getIndexTacnog(pitanje.getTacanOdgovor())));
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

    public void dialogAkcija(String poruka) {
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

    private class DobaviMogucaPitanja extends AsyncTask<String, Integer, ArrayList<Pitanje>> {
        protected  ArrayList<Pitanje> doInBackground(String... urls) {//prvi param kolekcija drugi id dokumenta
            String token = dajToken();
            String url1;
            ArrayList<Pitanje> listaMogucih = new ArrayList<>();
            if(urls.length == 1)
                url1 = "https://firestore.googleapis.com/v1/projects/rma19turkusicarslan73/databases/(default)/documents/" + urls[0] + "?access_token=" + token;
            else
                url1 = "https://firestore.googleapis.com/v1/projects/rma19turkusicarslan73/databases/(default)/documents/" + urls[0] + "/" + urls[1] + "?access_token=" + token;
            URL url;
            try {
                url = new URL(url1);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String rezultat = convertStreamToString(in);
                JSONObject jo = null;
                jo = new JSONObject(rezultat);
                JSONArray items = new JSONArray();
                if(urls[0].equalsIgnoreCase("Pitanja")) {
                    items = jo.getJSONArray("documents");
                    ArrayList<Pitanje> pitanja = ucitajSvaPitanjaIzBaze(items);
                    for(Pitanje p : pitanja){
                        if (!kviz.getPitanja().contains(p))
                            listaMogucih.add(p);
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }  catch (JSONException e) {
                e.printStackTrace();
            }
            return listaMogucih;
        }


        @Override
        protected void onPostExecute( ArrayList<Pitanje> lista) {
            listaMogucih.addAll(lista);
            adapterMogucihPitanja.notifyDataSetChanged();
        }
    }


    private class DodajObrisiBaza extends AsyncTask<String, Integer, Void> {

        protected  Void doInBackground(String... urls) {//prvi param kolekcija drugi sta ubacujemo
            String token = dajToken();
            String url1;
            url1 = "https://firestore.googleapis.com/v1/projects/rma19turkusicarslan73/databases/(default)/documents/" + urls[0] + "?documentId=" + urls[1] + "&access_token=" + token;
            if(svrha.equals("izmjena") && urls[0].equals("Kvizovi"))
                url1 = "https://firestore.googleapis.com/v1/projects/rma19turkusicarslan73/databases/(default)/documents/" + urls[0] + "/" + urls[1] + "?access_token=" + token;
            URL url;
            try {
                url = new URL(url1);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                if (urls.length > 2 && svrha.equals("izmjena") && urls[0].equals("Kvizovi"))
                    urlConnection.setRequestMethod("DELETE");
                else if(svrha.equals("izmjena") && urls[0].equals("Kvizovi"))
                    urlConnection.setRequestMethod("PATCH");
                else urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                String dokument = "";
                if(urls.length > 2 && svrha.equals("izmjena") && urls[0].equals("Kvizovi")) {
                    //nothing -- brisanje ne zahtjeva tijelo
                } else if(urls[0].equalsIgnoreCase("Kategorije")) {
                    dokument = "{ \"fields\": { \"idIkonice\": { \"integerValue\": \"" + dodanaKategorija.getId() + "\"}, \"naziv\": { \"stringValue\": \"" + dodanaKategorija.getNaziv() + "\"}}}";
                } else if(urls[0].equalsIgnoreCase("Kvizovi")) {
                    int i = 0;
                    dokument = "{ \"fields\": { \"pitanja\": { \"arrayValue\": { \"values\": [" ;
                    for(Pitanje p : kviz.getPitanja()) {
                        if(i < kviz.getPitanja().size() - 2)
                            dokument += "{ \"stringValue\": \"" + p.getId() + "\"}, ";
                        else if(i == kviz.getPitanja().size() - 2)
                            dokument += "{ \"stringValue\": \"" + p.getId() + "\"} ";
                        i++;
                    }
                    dokument += "]}}, \"naziv\": { \"stringValue\": \"" + kviz.getNaziv() + "\"}, \"idKategorije\": { \"stringValue\": \"" + kviz.getKategorija().getIdUBazi() + "\"}}}";
                } else if(urls[0].equalsIgnoreCase("Pitanja")) {
                    String[] odgovori = urls[3].split(",");
                    int i = 0;
                    dokument = "{ \"fields\": { \"naziv\": { \"stringValue\": \"" + urls[2] + "\"}, \"odgovori\": { \"arrayValue\": { \"values\": [" ;
                    for(String p : odgovori) {
                        if(i++ != odgovori.length - 1)
                            dokument += "{ \"stringValue\": \"" + p + "\"}, ";
                        else dokument += "{ \"stringValue\": \"" + p + "\"} ";
                    }
                    dokument += "]}}, \"indexTacnog\": { \"integerValue\": \"" + urls[4] + "\"}}}";
                }

                try (OutputStream os = urlConnection.getOutputStream()) {
                    byte[] input = dokument.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int code = urlConnection.getResponseCode();
                InputStream odgovor = urlConnection.getInputStream();
                try(BufferedReader br = new BufferedReader(new InputStreamReader(odgovor, "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    Log.d("ODGOVOR", response.toString());
                }
            }  catch (IOException e) {
                e.printStackTrace();
            }

            return null ;
        }

    }

    public static ArrayList<Pitanje> ucitajSvaPitanjaIzBaze(JSONArray items) {
        ArrayList<Pitanje> pitanjaIzBaze = new ArrayList<>();
         try {
            for(int i = 0; i < items.length(); i++) {
                JSONObject name = null;
                name = items.getJSONObject(i);
                JSONObject kviz = name.getJSONObject("fields");
                String naziv = kviz.getJSONObject("naziv").getString("stringValue");
                int indexTacnog = Integer.parseInt(kviz.getJSONObject("indexTacnog").getString("integerValue"));
                ArrayList<String> odgovori = new ArrayList<String>();
                JSONArray jArray = kviz.getJSONObject("odgovori").getJSONObject("arrayValue").getJSONArray("values");
                for (int j = 0; j < jArray.length(); j++){
                    odgovori.add(jArray.getJSONObject(j).getString("stringValue"));
                }
                Pitanje pitanje = new Pitanje(naziv, naziv, odgovori, odgovori.get(indexTacnog));
                pitanje.hashCode();
                pitanjaIzBaze.add(pitanje);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return pitanjaIzBaze;
    }


    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
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


    private class ProvjeriDaLiPostojiKviz extends AsyncTask<String, Integer,  Integer> {
        protected Integer doInBackground(String... urls) {
            Integer povratni = 0;
            try {
                String TOKEN = dajToken();

                String url1 = "https://firestore.googleapis.com/v1/projects/rma19turkusicarslan73/databases/(default)/documents/" + urls[0] + "/" + kviz.getId() + "?access_token=" + TOKEN;
                URL url = new URL(url1);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                return -1;
            }
            return povratni;
        }

        @Override
        protected void onPostExecute(Integer aVoid) {
            if(aVoid == -1) {
                if(svrha.equalsIgnoreCase("izmjena")) {
                    new DodajObrisiBaza().execute("Kvizovi", stariId, "obrisi"); // izmjena u bazi
                    new DodajObrisiBaza().execute("Kvizovi", kviz.getId()); // izmjena u bazi
                    new DobaviRanglistinIDIzBaze().execute();
                }
                else new DodajObrisiBaza().execute("Kvizovi", kviz.getId()); // dodajem u bazu
                Intent povratni = new Intent();
                povratni.putExtra("povratniKviz", kviz);
                povratni.putExtra("dodaneKategorije", kategorije);
                povratni.putExtra("tip", svrha);
                setResult(Activity.RESULT_OK, povratni);
                finish();
            } else {
                dialogAkcija("Uneseni kviz vec postoji!");
            }
        }
    }

    private class IzmijeniImeKvizaURanglisti extends AsyncTask<String, Integer,  ArrayList<Kviz>> {
        @Override
        protected ArrayList<Kviz> doInBackground(String... urls) {
            String token = dajToken();
            String url1;
            url1 = "https://firestore.googleapis.com/v1/projects/rma19turkusicarslan73/databases/(default)/documents/" + urls[0] + "/" + urls[1] + "?access_token=" + token;
            URL url;
            try {
                url = new URL(url1);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("PATCH");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");

                String dokument = "";
                int i = 0;
                dokument = "{ \"fields\": { \"lista\": { \"mapValue\": { \"fields\": {\n";
                for(Trojka<Integer, String, Double> t: rangZaIzmijenit.getLista()) {
                    if(i != rangZaIzmijenit.getLista().size() - 1)
                        dokument += "\"" + t.getFirst() + "\": { \"mapValue\": { \"fields\": { \"" + t.getSecond() + "\": { \n" +
                                "\"doubleValue\": \"" + t.getThird() +"\" } } } },";
                    else dokument += "\"" + t.getFirst() + "\": { \"mapValue\": { \"fields\": { \"" + t.getSecond() + "\": { \n" +
                            "\"doubleValue\": \"" + t.getThird() +"\" } } } }";
                }
                dokument += "} } }, \"nazivKviza\": { \"stringValue\": \"" + kviz.getNaziv() + "\" } } }";

                try (OutputStream os = urlConnection.getOutputStream()) {
                    byte[] input = dokument.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int code = urlConnection.getResponseCode();
                InputStream odgovor = urlConnection.getInputStream();
                try(BufferedReader br = new BufferedReader(new InputStreamReader(odgovor, "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    Log.d("ODGOVOR", response.toString());
                }
            }  catch (IOException e) {
                e.printStackTrace();
            }

            return null ;
        }
    }

    public String dajToken() {
        InputStream is = getResources().openRawResource(R.raw.secret);
        GoogleCredential credentials = null;
        try {
            credentials = GoogleCredential.fromStream(is).createScoped(Lists.newArrayList("https://www.googleapis.com/auth/datastore"));
            credentials.refreshToken();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return credentials.getAccessToken();
    }


    private class DobaviRanglistinIDIzBaze extends AsyncTask<String, Integer, ArrayList<Trojka<Integer, String, Double>>> {

        protected ArrayList<Trojka<Integer, String, Double>> doInBackground(String... urls) {
            String TOKEN = dajToken();
            String query = "{\n" +
                    "    \"structuredQuery\": {\n" +
                    "        \"where\" : {\n" +
                    "            \"fieldFilter\" : { \n" +
                    "                \"field\": {\"fieldPath\": \"nazivKviza\"}, \n" +
                    "                \"op\":\"EQUAL\", \n" +
                    "                \"value\": {\"stringValue\": \"" + kvizPrijePromjene + "\"}\n" +
                    "            }\n" +
                    "        },\n" +
                    "        \"select\": { \"fields\": [ {\"fieldPath\": \"nazivKviza\"}, {\"fieldPath\": \"lista\"}] },\n" +
                    "        \"from\": [{\"collectionId\": \"Rangliste\"}],\n" +
                    "       \"limit\": 1000 \n" +
                    "    }\n" +
                    "}";
            String url1  = "https://firestore.googleapis.com/v1/projects/rma19turkusicarslan73/databases/(default)/documents:runQuery?access_token=" + TOKEN;
            ArrayList<Trojka<Integer, String, Double>> rangID = new ArrayList<>();
            try {
                URL url = new URL(url1);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                try (OutputStream os = urlConnection.getOutputStream()) {
                    byte[] input = query.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int code = urlConnection.getResponseCode();
                InputStream in = urlConnection.getInputStream();
                String rezultat = DodajKvizAkt.convertStreamToString(in);
                rezultat = "{ \"documents\": " + rezultat + "}";
                JSONObject jo = null;
                jo = new JSONObject(rezultat);
                JSONArray items = new JSONArray();
                items = jo.getJSONArray("documents");
                rangID = ucitajRang(items);

                try(BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    Log.d("ODGOVOR", response.toString());
                }
            }  catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return rangID ;
        }

        @Override
        protected void onPostExecute(ArrayList<Trojka<Integer, String, Double>> s) {
            new IzmijeniImeKvizaURanglisti().execute("Rangliste", rangZaIzmijenit.getId());
        }
    }

    public  ArrayList<Trojka<Integer, String, Double>> ucitajRang(JSONArray items) {
        ArrayList<Trojka<Integer, String, Double>> rangoviIzBaze = new ArrayList<>();
        try {
            for (int i = 0; i < items.length(); i++) {
                JSONObject name = items.getJSONObject(i);
                JSONObject dokument = new JSONObject();
                JSONObject rang = new JSONObject();
                dokument = name.getJSONObject("document");
                rang = dokument.getJSONObject("fields");
                String id = dokument.getString("name");
                String id1 ="";
                int duzina = 0;
                for(int j = id.length() - 1; j > 0; j--) {
                    if(id.charAt(j) == '/') break;
                    else duzina++;
                }
                id1 = id.substring(id.length() - duzina);
                rangZaIzmijenit.setId(id1);
                String naziv = rang.getJSONObject("nazivKviza").getString("stringValue");
                JSONObject mapaGlavna = rang.getJSONObject("lista").getJSONObject("mapValue").getJSONObject("fields");
                try {
                    int j = 1;
                    while(true) {
                        JSONObject mapaSporedna = mapaGlavna.getJSONObject(String.valueOf(j)).getJSONObject("mapValue").getJSONObject("fields");
                        String nazivTakmicara = mapaSporedna.names().toString();
                        nazivTakmicara = nazivTakmicara.replace("[", "");
                        nazivTakmicara = nazivTakmicara.replace("]", "");
                        nazivTakmicara = nazivTakmicara.replace("\"", "");
                        Double procenat = mapaSporedna.getJSONObject(nazivTakmicara).getDouble("doubleValue");
                        rangoviIzBaze.add(new Trojka<>(j, nazivTakmicara, procenat));
                        j++;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        rangZaIzmijenit.setLista(rangoviIzBaze);
        return rangoviIzBaze;
    }

}
