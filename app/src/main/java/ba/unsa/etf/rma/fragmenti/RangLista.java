package ba.unsa.etf.rma.fragmenti;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.common.collect.Lists;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.RangListaAdapter;
import ba.unsa.etf.rma.aktivnosti.DodajKvizAkt;
import ba.unsa.etf.rma.aktivnosti.IgrajKvizAkt;
import ba.unsa.etf.rma.aktivnosti.KvizoviAkt;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;
import ba.unsa.etf.rma.klase.Rang;
import ba.unsa.etf.rma.ostalo.KvizoviDBOpenHelper;
import ba.unsa.etf.rma.ostalo.RangComparator;
import ba.unsa.etf.rma.ostalo.Trojka;

public class RangLista extends Fragment {
    private Kviz kviz;
    ListView listaRangova;
    RangListaAdapter adapter;
    double postotakTacnih;
    private String imeIgraca;
    IgrajKvizAkt aktivnost;
    KvizoviDBOpenHelper lokalnaBaza;

    Rang novi = new Rang();
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ranglista_frag, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        aktivnost = (IgrajKvizAkt) getActivity();
        lokalnaBaza = KvizoviDBOpenHelper.getInstance(getContext());
        if(getArguments().containsKey("kviz")){
            otvoriAlertDialog();
            kviz = (Kviz)getArguments().getSerializable("kviz");
            if(aktivnost.isOnline())
                new DobaviRanglistuIzBaze().execute();
            else {
                novi = lokalnaBaza.dajRangListuKviza(kviz.getNaziv());
                if(novi == null) {
                    novi = new Rang();
                    novi.postaviRandomId();
                }
            }
            postotakTacnih = getArguments().getDouble("postotak");
            listaRangova = (ListView) getActivity().findViewById(R.id.rangListaLW) ;
            adapter = new RangListaAdapter(getActivity(), novi.getLista(), getResources());
            listaRangova.setAdapter(adapter);
        }
    }

    private void otvoriAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Unesite Vase ime:");
        // Set up the input
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                imeIgraca = input.getText().toString();
                novi.setImeKviza(kviz.getNaziv());
                novi.getLista().add(new Trojka<Integer, String, Double>(-1, imeIgraca, postotakTacnih));
                Collections.sort(novi.getLista(), new RangComparator());
                for(int i = 1; i <= novi.getLista().size(); i++) {
                    novi.getLista().get(i - 1).setFirst(i);
                }
                if(aktivnost.isOnline()) {
                    if (novi.getLista().size() == 1)
                        new DodajRangUBazu().execute("Rangliste", novi.getId(), "dodavanje");
                    else new DodajRangUBazu().execute("Rangliste", novi.getId(), "izmjena");
                } else {
                    lokalnaBaza.dodajIzmijeniRangListuULokalnojBazi(novi);
                }
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Collections.sort(novi.getLista(), new RangComparator());
                adapter.notifyDataSetChanged();
            }
        });
        builder.show();
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

    private class DobaviRanglistuIzBaze extends AsyncTask<String, Integer, ArrayList<Trojka<Integer, String, Double>>> {

        protected  ArrayList<Trojka<Integer, String, Double>> doInBackground(String... urls) {
            String TOKEN = dajToken();
            String query = "{\n" +
                    "    \"structuredQuery\": {\n" +
                    "        \"where\" : {\n" +
                    "            \"fieldFilter\" : { \n" +
                    "                \"field\": {\"fieldPath\": \"nazivKviza\"}, \n" +
                    "                \"op\":\"EQUAL\", \n" +
                    "                \"value\": {\"stringValue\": \"" + kviz.getNaziv() + "\"}\n" +
                    "            }\n" +
                    "        },\n" +
                    "        \"select\": { \"fields\": [ {\"fieldPath\": \"nazivKviza\"}, {\"fieldPath\": \"lista\"}] },\n" +
                    "        \"from\": [{\"collectionId\": \"Rangliste\"}],\n" +
                    "       \"limit\": 1000 \n" +
                    "    }\n" +
                    "}";
            String url1  = "https://firestore.googleapis.com/v1/projects/rma19turkusicarslan73/databases/(default)/documents:runQuery?access_token=" + TOKEN;
            ArrayList<Trojka<Integer, String, Double>> rangovi = new ArrayList<>();
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
                rangovi = ucitajRang(items);

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

            return rangovi ;
        }

        @Override
        protected void onPostExecute(ArrayList<Trojka<Integer, String, Double>> ranks) {
            novi.getLista().addAll(ranks);
            novi.setImeKviza(kviz.getNaziv());
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
                novi.setId(id1);
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
                        rangoviIzBaze.add(new Trojka<Integer, String, Double>(j, nazivTakmicara, procenat));
                        j++;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rangoviIzBaze;
    }

    private class DodajRangUBazu extends AsyncTask<String, Integer, Void> {

        protected  Void doInBackground(String... urls) {//prvi param kolekcija drugi sta ubacujemo
            String url1, token;
            token = dajToken();
            url1 = "https://firestore.googleapis.com/v1/projects/rma19turkusicarslan73/databases/(default)/documents/" + urls[0]  + "?access_token=" + token;
            if(urls[2].equals("izmjena"))
                url1 = "https://firestore.googleapis.com/v1/projects/rma19turkusicarslan73/databases/(default)/documents/" + urls[0] + "/" + urls[1] + "?access_token=" + token;
            URL url;
            try {
                url = new URL(url1);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                if(urls[2].equals("dodavanje"))
                    urlConnection.setRequestMethod("POST");
                else urlConnection.setRequestMethod("PATCH");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                String dokument = "";
                int i = 0;
                dokument = "{ \"fields\": { \"lista\": { \"mapValue\": { \"fields\": {\n";
                for(Trojka<Integer, String, Double> t: novi.getLista()) {
                   if(i != novi.getLista().size() - 1)
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

}

