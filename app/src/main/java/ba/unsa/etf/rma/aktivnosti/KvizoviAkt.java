package ba.unsa.etf.rma.aktivnosti;


import android.Manifest;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.common.collect.Lists;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;

import ba.unsa.etf.rma.fragmenti.DetailFrag;
import ba.unsa.etf.rma.fragmenti.ListaFrag;
import ba.unsa.etf.rma.fragmenti.RangLista;
import ba.unsa.etf.rma.klase.Interfejsi;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.adapteri.ListaAdapter;
import ba.unsa.etf.rma.klase.Pitanje;
import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.SpinerAdapter;
import ba.unsa.etf.rma.klase.Rang;
import ba.unsa.etf.rma.ostalo.ConnectivityReceiver;
import ba.unsa.etf.rma.ostalo.KvizoviDBOpenHelper;
import ba.unsa.etf.rma.ostalo.Trojka;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class KvizoviAkt extends AppCompatActivity implements DetailFrag.ZaKomunikacijuSaBaznom, ListaFrag.Filtriranje, Interfejsi.IDobaviKvizove {
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;
    public  KvizoviAkt GlavnaKlasa = null;
    public ArrayList<Kviz> kvizovi = new ArrayList<>();
    public ArrayList<Kategorija> kategorije = new ArrayList<>();
    private Kategorija svi;
    private Kategorija odabranaKategorijaUSpineru ;
    private ListaAdapter adapter;
    private boolean daLiJeIzmjena = false;
    private int pozicija = -1;
    private Spinner spinner;
    private Boolean siriL = false;
    private ListaFrag listaFrag;
    private DetailFrag detailFrag;
    private SpinerAdapter adapterSpinner;
    private ArrayList<Pitanje> listaSvaPitanja = new ArrayList<>();
    private Kategorija kategorijaKvizaKojiSeDodaje;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private double vrijemeDoEventa;
    boolean online = true;
    private double vrijemeDoZavrsetkaEventaKojiTraje;
    private boolean perm = true; // permisija za kalendar
    private  ConnectivityReceiver receiver;
    public static KvizoviDBOpenHelper baza;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kvizovi_akt);

        GlavnaKlasa = this;
        baza = KvizoviDBOpenHelper.getInstance(this);
        checkPermissions(PERMISSION_REQUEST_CODE, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR); // dopustenje za pristup kalendaru telefona

        if(kvizovi.size() == 0 || !kvizovi.get(kvizovi.size() - 1).getNaziv().equalsIgnoreCase("dodaj kviz"))
            dodajAddKvizNaKraj();
        dodajSviKategorijuUSpinner();
        odabranaKategorijaUSpineru = svi;
        Resources res = getResources();

        receiver = new ConnectivityReceiver();

        online = daLiImaKonekcije();
        if(online) {
            Intent intent = new Intent();
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setAction("akcija");
            sendBroadcast(intent);
        } else {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            this.registerReceiver(receiver, intentFilter);
        }

        final ListView list = (ListView) findViewById(R.id.lvKvizovi);
        if(list == null) {
            zaFrag();
            if(online) {
                DobaviIzBaze dobaviIzBaze = new DobaviIzBaze(); // kad zarotiramo da dobavimo sve kategorije
                dobaviIzBaze.delegat = this;
                dobaviIzBaze.execute("Kategorije");

                dobaviSveKvizoveizBaze();
            } else {
                kvizovi.addAll(kvizovi.size() - 1, baza.dobaviSveKvizoveIzLokalneBaze());
                kategorije.addAll(baza.dobaviSveKategorijeIzLokalneBaze());
            }
        } else {
            spinner = (Spinner) findViewById(R.id.spPostojeceKategorije);

            adapter = new ListaAdapter(GlavnaKlasa, kvizovi, res);
            list.setAdapter(adapter);

            adapterSpinner = new SpinerAdapter(GlavnaKlasa, kategorije);
            spinner.setAdapter(adapterSpinner);


            //listener za listu kvizova, slanje podataka u aktivnost za dodavanja/izmjenu kviza
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(kvizovi.get(position).getPitanja().size() != 0 && daLiIMaEventPrijeVremena((int)((kvizovi.get(position).getPitanja().size() / 2.) * 60)) && perm) {
                        AlertDialog alertDialog = new AlertDialog.Builder(KvizoviAkt.this).create();
                        alertDialog.setTitle("Alert");
                        if(vrijemeDoEventa == 0) {
                            alertDialog.setMessage("Imate događaj u kalendaru koji zavrsava za " + vrijemeDoZavrsetkaEventaKojiTraje + " minuta!");
                        } else {
                            alertDialog.setMessage("Imate događaj u kalendaru za " + vrijemeDoEventa + " minuta!");
                        }
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                    else if(perm) pokreniKviz(position);
                }
            });

            list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    if(online)
                        return dodajIzmijeniKviz(position);
                    else {
                        Toast.makeText(KvizoviAkt.this, "Offline ste!", Toast.LENGTH_LONG).show();
                        return true;
                    }
                }
            });

            if(online) {
                DobaviIzBaze dobaviIzBaze = new DobaviIzBaze();
                dobaviIzBaze.delegat = this;
                dobaviIzBaze.execute("Kategorije");
            } else {
                kvizovi.clear();
                kvizovi.addAll(baza.dobaviSveKvizoveIzLokalneBaze());
                dodajAddKvizNaKraj();
                adapter.notifyDataSetChanged();
                kategorije.addAll(baza.dobaviSveKategorijeIzLokalneBaze());
                adapterSpinner.notifyDataSetChanged();
            }

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                   odabranaKategorijaUSpineru =  (Kategorija) spinner.getSelectedItem();
                   if(online) {
                       if (odabranaKategorijaUSpineru.getNaziv().equalsIgnoreCase("Svi")) {
                           dobaviSveKvizoveizBaze();
                       } else
                           new DobaviSveKvizovePoKategoriji().execute();
                   } else {
                       if(!odabranaKategorijaUSpineru.getNaziv().equals("Svi")) {
                           ((ListaAdapter) adapter).getFilter().filter(kategorije.get(position).getNaziv());
                       } else {
                           ((ListaAdapter) adapter).setKat(svi);
                           ((ListaAdapter) adapter).getFilter().filter(kategorije.get(position).getNaziv());
                       }
                   }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    public void setOnline(boolean offline) {
        this.online = offline;
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(receiver, intentFilter);

        if(online)
            azurirajPodatkeULokalnojBazi();
    }

    public void azurirajPodatkeULokalnojBazi() {
        DobaviIzBaze dobaviIzBaze = new DobaviIzBaze();
        dobaviIzBaze.delegat = this;
        dobaviIzBaze.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "Kategorije", "prvi");

        DobaviIzBaze dobaviIzBaze1 = new DobaviIzBaze();
        dobaviIzBaze1.delegat = this;
        dobaviIzBaze1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "Kvizovi", "prvi");

        DobaviIzBaze dobaviIzBaze2 = new DobaviIzBaze();
        dobaviIzBaze2.delegat = this;
        dobaviIzBaze2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "Pitanja", "prvi");

        DobaviIzBaze dobaviIzBaze3 = new DobaviIzBaze();
        dobaviIzBaze3.delegat = this;
        dobaviIzBaze3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "Rangliste", "prvi");
    }

    private boolean daLiIMaEventPrijeVremena(int vrijemeUSekundama) {
        if (perm) {
            Cursor cursor = getContentResolver().query(
                    Uri.parse("content://com.android.calendar/events"), new String[]{"title", "dtstart", "dtend"}, "deleted != 1", null, null);
            cursor.moveToFirst();
            String CNames[] = new String[cursor.getCount()];

            long trenutnoVrijeme = System.currentTimeMillis();

            long trenutnoPlusBrojPitanja = trenutnoVrijeme + vrijemeUSekundama * 1000;


            for (int i = 0; i < CNames.length; i++) {
                Long datumPocetka = Long.parseLong(cursor.getString(1));
                Long datumKraja = Long.parseLong(cursor.getString(2));
                if(datumPocetka < trenutnoVrijeme && datumKraja > trenutnoVrijeme) {
                    vrijemeDoZavrsetkaEventaKojiTraje = ( datumKraja - trenutnoVrijeme )/ 60000.;
                    vrijemeDoEventa = 0;
                    return true;
                } else if (datumPocetka > trenutnoVrijeme && datumPocetka < trenutnoPlusBrojPitanja) {
                    vrijemeDoEventa = (datumPocetka - trenutnoVrijeme) / 60000.;
                    vrijemeDoZavrsetkaEventaKojiTraje = 0;
                    return true;
                }
                CNames[i] = cursor.getString(0);
                cursor.moveToNext();

            }
        }
        return false;
    }

    public boolean daLiImaKonekcije() {
        final ConnectivityManager connectivityManager = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        final Network network = connectivityManager.getActiveNetwork();
        final NetworkCapabilities capabilities = connectivityManager
                .getNetworkCapabilities(network);

        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    }

    private boolean checkPermissions(int callbackId, String... permissionsId) {
        boolean permissions = true;
        for (String p : permissionsId) {
            permissions = permissions && ContextCompat.checkSelfPermission(this, p) == PERMISSION_GRANTED;
        }

        if (!permissions)
            ActivityCompat.requestPermissions(this, permissionsId, callbackId);
        return permissions;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_REQUEST_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                perm = true;
            } else {
                perm = false;
            }
        }
    }

    private void dobaviSveKvizoveizBaze() {
        DobaviIzBaze dobaviIzBaze1 = new DobaviIzBaze();
        dobaviIzBaze1.delegat = this;
        kvizovi.clear();
        dodajAddKvizNaKraj();
        dobaviIzBaze1.execute("Kvizovi");
    }

    private void pokreniKviz(int position) {
        Intent intent = new Intent(KvizoviAkt.this, IgrajKvizAkt.class);
        intent.putExtra("kviz", kvizovi.get(position));
        intent.putExtra("online", online);
        if (!(position == kvizovi.size() - 1))
            startActivity(intent);
    }

    private boolean dodajIzmijeniKviz(int position) {
        Intent intent = new Intent(KvizoviAkt.this, DodajKvizAkt.class);
        Kviz kviz;
        if(adapter == null) kviz = (Kviz) detailFrag.getAdapter().getItem(position);
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
        // Check that it is the SecondActivity with an OK result
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Get String data from Intent
                Kviz kviz = (Kviz) data.getSerializableExtra("povratniKviz");
                if(kviz != null && kviz.getPitanja()!= null && kviz.getPitanja().get(kviz.getPitanja().size() - 1).getNaziv().equalsIgnoreCase("Dodaj pitanje"))
                    kviz.getPitanja().remove(kviz.getPitanja().size() - 1);
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
                    detailFrag.dodajIzmijeniKviz(new ArrayList<>(kvizovi));
                    listaFrag.azurirajKategorije(new ArrayList<>(kategorije));
                }
            }
        }
//        if(spinner != null) spinner.setSelection(0);
    }

    private void dodajSviKategorijuUSpinner() {
        svi = new Kategorija();
        svi.setNaziv("Svi");
        svi.hashCode();
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

    private void dodajAddKvizNaKraj(ArrayList<Kviz> kvizevi) {
        Kviz addKviz = new Kviz();
        addKviz.setNaziv("Dodaj Kviz");
        Kategorija cat1 = new Kategorija();
        cat1.setNaziv("Dodaj kviz");
        cat1.setId("1");
        addKviz.setKategorija(cat1);
        kvizevi.add(addKviz);
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
    public void filtriraj(Kategorija kategorija) {
        odabranaKategorijaUSpineru = kategorija;
        if(online) {
            if (kategorija.getNaziv().equals("Svi")) {
                dobaviSveKvizoveizBaze();
            } else
                new DobaviSveKvizovePoKategoriji().execute();
        } else {
            if(!odabranaKategorijaUSpineru.getNaziv().equals("Svi")) {
                (detailFrag.getAdapter()).getFilter().filter(kategorija.getNaziv());
            } else {
                (detailFrag.getAdapter()).setKat(svi);
                (detailFrag.getAdapter()).getFilter().filter(kategorija.getNaziv());
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable("kvizovi", kvizovi);
        savedInstanceState.putSerializable("kategorije", kategorije);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        kvizovi.clear();
        kvizovi.addAll((ArrayList<Kviz>) savedInstanceState.getSerializable("kvizovi"));
        kategorije.clear();
        kategorije.addAll((ArrayList<Kategorija>) savedInstanceState.getSerializable("kategorije"));

        if(adapter == null) {
            detailFrag.dodajIzmijeniKviz(new ArrayList<>(kvizovi));
            listaFrag.azurirajKategorije(new ArrayList<>(kategorije));
        }
    }

    public boolean isOnline() {
        return online;
    }


    public class DobaviIzBaze extends AsyncTask<String, Integer, ArrayList<?>> {
        public Interfejsi.IDobaviKvizove delegat = null;
        protected  ArrayList<?> doInBackground(String... urls) {//prvi param kolekcija drugi id dokumenta
            InputStream is = getResources().openRawResource(R.raw.secret);
            GoogleCredential credentials = null;
            try {
                credentials = GoogleCredential.fromStream(is).createScoped(Lists.newArrayList("https://www.googleapis.com/auth/datastore"));
                credentials.refreshToken();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String TOKEN = credentials.getAccessToken();
            String url1;
            ArrayList<?> lista = new ArrayList<>();
            if(urls.length == 1)
                url1 = "https://firestore.googleapis.com/v1/projects/rma19turkusicarslan73/databases/(default)/documents/" + urls[0] + "?access_token=" + TOKEN;
            else if(urls[1].equals("prvi"))
                url1 = "https://firestore.googleapis.com/v1/projects/rma19turkusicarslan73/databases/(default)/documents/" + urls[0] + "?access_token=" + TOKEN;
            else
                url1 = "https://firestore.googleapis.com/v1/projects/rma19turkusicarslan73/databases/(default)/documents/" + urls[0] + "/" + urls[1] + "?access_token=" + TOKEN;
            URL url;
            try {
                url = new URL(url1);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String rezultat = DodajKvizAkt.convertStreamToString(in);
                JSONObject jo = null;
                jo = new JSONObject(rezultat);
                JSONArray items = new JSONArray();
                if(urls[0].equalsIgnoreCase("Kvizovi")) {
                    items = jo.getJSONArray("documents");
                    lista = ucitajKvizoveOdabraneKategorije(items, 1);
                    if(urls.length > 1) {
                        ArrayList<Kviz> kvizovi = (ArrayList<Kviz>) lista;
                        baza.ubaciSveKvizoveUBazu(kvizovi);
                        return new ArrayList<>();
                    }
                } else if(urls.length == 3 && urls[2].equalsIgnoreCase("Kategorija")) {
                    kategorijaKvizaKojiSeDodaje = ucitajKategoriju(jo);
                } else if(urls[0].equalsIgnoreCase("Kategorije")) {
                    items = jo.getJSONArray("documents");
                    lista = ucitajSveKategorijeIzBaze(items);
                    if(urls.length > 1) {
                        ArrayList<Kategorija> kategorijas = (ArrayList<Kategorija>) lista;
                        baza.ubaciSveKategorijeUBazu(kategorijas);
                    }
                } else if(urls[0].equalsIgnoreCase("Pitanja")) {
                    items = jo.getJSONArray("documents");
                    listaSvaPitanja = DodajKvizAkt.ucitajSvaPitanjaIzBaze(items);
                    if(urls.length > 1) {
                        baza.ubaciSvaPitanjaUBazu(listaSvaPitanja);
                    }
                } else if(urls[0].equalsIgnoreCase("Rangliste")) {
                    items = jo.getJSONArray("documents");
                    if(urls.length > 1) {
                        baza.ubaciSveRangListeULokalnuBazu(ucitajSveRangListeIzBaze(items));
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }  catch (JSONException e) {
                e.printStackTrace();
            }
            return lista;
        }

        @Override
        protected void onPostExecute( ArrayList<?> lista) {
            if(lista.size() != 0)
                delegat.processFinish(lista);
        }
    }

    private ArrayList<Rang> ucitajSveRangListeIzBaze(JSONArray items) {
        ArrayList<Rang> rangoviIzBaze = new ArrayList<>();
        try {
            for (int i = 0; i < items.length(); i++) {
                JSONObject name = items.getJSONObject(i);
                JSONObject rang = new JSONObject();
                rang = name.getJSONObject("fields");
                String id = name.getString("name");
                String idRangliste ="";
                int duzina = 0;
                for(int j = id.length() - 1; j > 0; j--) {
                    if(id.charAt(j) == '/') break;
                    else duzina++;
                }
                idRangliste = id.substring(id.length() - duzina);
                String nazivKviza = rang.getJSONObject("nazivKviza").getString("stringValue");
                JSONObject mapaGlavna = rang.getJSONObject("lista").getJSONObject("mapValue").getJSONObject("fields");
                ArrayList<Trojka<Integer, String, Double>> igraci = new ArrayList<>();
                try {
                    int j = 1;
                    while(true) {
                        JSONObject mapaSporedna = mapaGlavna.getJSONObject(String.valueOf(j)).getJSONObject("mapValue").getJSONObject("fields");
                        String nazivTakmicara = mapaSporedna.names().toString();
                        nazivTakmicara = nazivTakmicara.replace("[", "");
                        nazivTakmicara = nazivTakmicara.replace("]", "");
                        nazivTakmicara = nazivTakmicara.replace("\"", "");
                        Double procenat = mapaSporedna.getJSONObject(nazivTakmicara).getDouble("doubleValue");
                        igraci.add(new Trojka<>(j, nazivTakmicara, procenat));
                        j++;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                rangoviIzBaze.add(new Rang(idRangliste, igraci, nazivKviza));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rangoviIzBaze;
    }

    @Override
    public void processFinish(ArrayList<?> output) {
        if(output.size() != 0 && output.get(0).getClass() == Kategorija.class) {
            ArrayList<Kategorija> kategorijas = (ArrayList<Kategorija>) output;
            kategorije.clear();
            dodajSviKategorijuUSpinner();
            kategorije.addAll(kategorijas);
            if(listaFrag != null) {
                listaFrag.dodajSveKategorije();
            }
            if(adapterSpinner != null) adapterSpinner.notifyDataSetChanged();
        } else if(output.size() != 0 && output.get(0).getClass() == Kviz.class) {
            ArrayList<Kviz> kviz = (ArrayList<Kviz>) output;
            kvizovi.clear();
            kvizovi.addAll( kviz);
            dodajAddKvizNaKraj();
            dodajAddKvizNaKraj(kviz);
            if(detailFrag != null) {
                detailFrag.dodajSveKvizove(kviz);
            }
            if(adapter != null) adapter.notifyDataSetChanged();
        }
    }

    private class DobaviSveKvizovePoKategoriji extends AsyncTask<String, Integer, ArrayList<Kviz>> {

        protected  ArrayList<Kviz> doInBackground(String... urls) {
            InputStream is = getResources().openRawResource(R.raw.secret);
            GoogleCredential credentials = null;
            try {
                credentials = GoogleCredential.fromStream(is).createScoped(Lists.newArrayList("https://www.googleapis.com/auth/datastore"));
                credentials.refreshToken();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String TOKEN = credentials.getAccessToken();
            String query = "{\n" +
                    "    \"structuredQuery\": {\n" +
                    "        \"where\" : {\n" +
                    "            \"fieldFilter\" : { \n" +
                    "                \"field\": {\"fieldPath\": \"idKategorije\"}, \n" +
                    "                \"op\":\"EQUAL\", \n" +
                    "                \"value\": {\"stringValue\": \"" + odabranaKategorijaUSpineru.getIdUBazi() + "\"}\n" +
                    "            }\n" +
                    "        },\n" +
                    "        \"select\": { \"fields\": [ {\"fieldPath\": \"idKategorije\"}, {\"fieldPath\": \"naziv\"}, {\"fieldPath\": \"pitanja\"}] },\n" +
                    "        \"from\": [{\"collectionId\": \"Kvizovi\"}],\n" +
                    "       \"limit\": 1000 \n" +
                    "    }\n" +
                    "}";
            String url1  = "https://firestore.googleapis.com/v1/projects/rma19turkusicarslan73/databases/(default)/documents:runQuery?access_token=" + TOKEN;
            ArrayList<Kviz> kvizovi = new ArrayList<>();
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
                kvizovi = ucitajKvizoveOdabraneKategorije(items, 2);

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

            return kvizovi ;
        }

        @Override
        protected void onPostExecute(ArrayList<Kviz> kvizs) {
            kvizovi.clear();
            dodajAddKvizNaKraj();
            kvizovi.addAll(kvizovi.size() - 1, kvizs);
            dodajAddKvizNaKraj(kvizs);
            if(adapter != null) adapter.notifyDataSetChanged();
            if(detailFrag != null) detailFrag.dodajSveKvizove(kvizs);
        }
    }

    private ArrayList<Kviz> ucitajKvizoveOdabraneKategorije(JSONArray items, int odakleJePozvano ) {
        ArrayList<Kviz> kvizoviIzBaze = new ArrayList<>();
        try {
            for(int i = 0; i < items.length(); i++) {
                JSONObject name = items.getJSONObject(i);
                JSONObject dokument = new JSONObject();
                JSONObject kviz = new JSONObject();
                if(odakleJePozvano == 2) {
                    dokument = name.getJSONObject("document");
                    kviz = dokument.getJSONObject("fields");
                } else
                    kviz = name.getJSONObject("fields");
                String naziv = kviz.getJSONObject("naziv").getString("stringValue");
                String idKategorije = kviz.getJSONObject("idKategorije").getString("stringValue");
                ArrayList<String> pitanjaIdevi = new ArrayList<String>();
                JSONArray jArray = new JSONArray();
                try {
                  jArray =  kviz.getJSONObject("pitanja").getJSONObject("arrayValue").getJSONArray("values");
                } catch (JSONException e) {

                }
                for (int j = 0; j < jArray.length(); j++){
                    pitanjaIdevi.add(jArray.getJSONObject(j).getString("stringValue"));
                }
                if(i == 0)
                    new DobaviIzBaze().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "Pitanja").get();
                for (Kategorija k : kategorije) {
                    if (k.getIdUBazi().equals(idKategorije)) {
                        kategorijaKvizaKojiSeDodaje = k;
                    }
                }
                Kviz kvizz = new Kviz(naziv, dajOdgovarajucaPitanja(pitanjaIdevi), kategorijaKvizaKojiSeDodaje);
                kvizz.hashCode();
                kvizoviIzBaze.add(kvizz);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return kvizoviIzBaze;
    }

    private ArrayList<Pitanje> dajOdgovarajucaPitanja(ArrayList<String> pitanjaIdevi) {
        ArrayList<Pitanje> pitanja = new ArrayList<>();
        for(Pitanje p : listaSvaPitanja)
            if(pitanjaIdevi.contains(p.getId()))
                pitanja.add(p);
            return  pitanja;
    }

    public static ArrayList<Kategorija> ucitajSveKategorijeIzBaze(JSONArray items) {
        ArrayList<Kategorija> kategorijeIzBaze = new ArrayList<>();
        try {
            for(int i = 0; i < items.length(); i++) {
                JSONObject name = null;
                name = items.getJSONObject(i);
                kategorijeIzBaze.add(ucitajKategoriju(name));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return kategorijeIzBaze;
    }

    public static Kategorija ucitajKategoriju(JSONObject name) {
        JSONObject kviz = null;
        try {
            kviz = name.getJSONObject("fields");
            String naziv = kviz.getJSONObject("naziv").getString("stringValue");
            String idIkonice = kviz.getJSONObject("idIkonice").getString("integerValue");
            Kategorija kategorija = new Kategorija( naziv, idIkonice);
            kategorija.hashCode();
        return  kategorija;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }



}
