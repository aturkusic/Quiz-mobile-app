package ba.unsa.etf.rma.ostalo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

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
import java.util.concurrent.Executor;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.aktivnosti.DodajKvizAkt;
import ba.unsa.etf.rma.aktivnosti.IgrajKvizAkt;
import ba.unsa.etf.rma.aktivnosti.KvizoviAkt;
import ba.unsa.etf.rma.klase.Rang;

public class ConnectivityReceiver extends BroadcastReceiver {
    KvizoviAkt kA;
    DodajKvizAkt dKA;
    IgrajKvizAkt iKA;
    Context context;
    KvizoviDBOpenHelper baza;
    ArrayList<Rang> updatovaneRangListe = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        baza = KvizoviDBOpenHelper.getInstance(context);

        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if(context.getClass() == KvizoviAkt.class)
            kA = (KvizoviAkt) context;
        else if(context.getClass() == DodajKvizAkt.class) {
            dKA = (DodajKvizAkt) context;
        } else if(context.getClass() == IgrajKvizAkt.class) {
            iKA = (IgrajKvizAkt) context;
        }
        if(isConnected) {

            updatovaneRangListe = baza.dajSvePromijenjeneRangListe();
            new DobaviRanglisteIzBaze().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            if(kA != null) {
                kA.setOnline(true);
                kA.azurirajPodatkeULokalnojBazi();
            } else if (dKA != null) {
                dKA.setOnline(true);
            } else if (iKA != null) {
                iKA.setOnline(true);
            }
        } else {
            if(kA != null) {
                kA.setOnline(false);
            } else if (dKA != null) {
                dKA.setOnline(false);
            } else  if (iKA != null) {
                iKA.setOnline(false);
            }
        }
    }

    public String dajToken() {
        InputStream is = context.getResources().openRawResource(R.raw.secret);
        GoogleCredential credentials = null;
        try {
            credentials = GoogleCredential.fromStream(is).createScoped(Lists.newArrayList("https://www.googleapis.com/auth/datastore"));
            credentials.refreshToken();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return credentials.getAccessToken();
    }

    private class DobaviRanglisteIzBaze extends AsyncTask<String, Integer, ArrayList<Rang>> {

        protected  ArrayList<Rang> doInBackground(String... urls) {
            String TOKEN = dajToken();
            String url1  = "https://firestore.googleapis.com/v1/projects/rma19turkusicarslan73/databases/(default)/documents:runQuery?access_token=" + TOKEN;
            ArrayList<Rang> rangovi = new ArrayList<>();
            try {
                URL url = new URL(url1);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");

                for(Rang r : updatovaneRangListe) {
                    String query = "{\n" +
                            "    \"structuredQuery\": {\n" +
                            "        \"where\" : {\n" +
                            "            \"fieldFilter\" : { \n" +
                            "                \"field\": {\"fieldPath\": \"nazivKviza\"}, \n" +
                            "                \"op\":\"EQUAL\", \n" +
                            "                \"value\": {\"stringValue\": \"" + r.getImeKviza() + "\"}\n" +
                            "            }\n" +
                            "        },\n" +
                            "        \"select\": { \"fields\": [ {\"fieldPath\": \"nazivKviza\"}, {\"fieldPath\": \"lista\"}] },\n" +
                            "        \"from\": [{\"collectionId\": \"Rangliste\"}],\n" +
                            "       \"limit\": 1000 \n" +
                            "    }\n" +
                            "}";
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
                    rangovi.add(ucitajRang(items));
                }

            }  catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return rangovi ;
        }

        @Override
        protected void onPostExecute(ArrayList<Rang> ranks) {
            for(Rang r : updatovaneRangListe) {
                if(ranks.get(0) == null) break;
              for(Rang r2 : ranks) {
                  if(r.getImeKviza().equals(r2.getImeKviza())) {
                      int vel1 = r.getLista().size();
                      int vel2 = r2.getLista().size();
                      for(int i = 0; i < vel2; i++) {
                          boolean sadrzi = false;
                          for(int j = 0; j < vel1; j++) {
                              if(r.getLista().get(j).getSecond().equals(r2.getLista().get(i).getSecond()) && r.getLista().get(j).getThird().equals(r2.getLista().get(i).getThird())) {
                                  sadrzi = true;
                                  break;
                              }
                          }
                          if(!sadrzi) {
                              r.getLista().add(r2.getLista().get(i));
                          }
                      }
                      break;
                  }
              }
            }
            for(Rang r : updatovaneRangListe) {
                Collections.sort(r.getLista(), new RangComparator());
            }
            new DodajRangoveUBazu().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public  Rang ucitajRang(JSONArray items) {
        try {
            for (int i = 0; i < items.length(); i++) {
                JSONObject name = items.getJSONObject(i);
                JSONObject dokument = new JSONObject();
                JSONObject rang = new JSONObject();
                dokument = name.getJSONObject("document");
                rang = dokument.getJSONObject("fields");
                String id = dokument.getString("name");
                String idRanga ="";
                int duzina = 0;
                for(int j = id.length() - 1; j > 0; j--) {
                    if(id.charAt(j) == '/') break;
                    else duzina++;
                }
                idRanga = id.substring(id.length() - duzina);
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
                        igraci.add(new Trojka<Integer, String, Double>(j, nazivTakmicara, procenat));
                        j++;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return new Rang(idRanga, igraci, nazivKviza);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private class DodajRangoveUBazu extends AsyncTask<String, Integer, Void> {

        protected  Void doInBackground(String... urls) {//prvi param kolekcija drugi sta ubacujemo
            String url1, token;
            token = dajToken();
            URL url;
            try {
                for(Rang r : updatovaneRangListe) {
                    if(!r.getId().equals("")) url1 = "https://firestore.googleapis.com/v1/projects/rma19turkusicarslan73/databases/(default)/documents/Rangliste/" + r.getId() + "?access_token=" + token;
                    else {
                        url1 = "https://firestore.googleapis.com/v1/projects/rma19turkusicarslan73/databases/(default)/documents/Rangliste/" + r.postaviRandomId() + "?access_token=" + token;
                    }
                    url = new URL(url1);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setDoInput(true);
                    urlConnection.setRequestMethod("PATCH");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setRequestProperty("Accept", "application/json");
                    String dokument = "";
                    int i = 0;
                    dokument = "{ \"fields\": { \"lista\": { \"mapValue\": { \"fields\": {\n";
                    for (Trojka<Integer, String, Double> t : r.getLista()) {
                        if (i != r.getLista().size() - 1)
                            dokument += "\"" + t.getFirst() + "\": { \"mapValue\": { \"fields\": { \"" + t.getSecond() + "\": { \n" +
                                    "\"doubleValue\": \"" + t.getThird() + "\" } } } },";
                        else
                            dokument += "\"" + t.getFirst() + "\": { \"mapValue\": { \"fields\": { \"" + t.getSecond() + "\": { \n" +
                                    "\"doubleValue\": \"" + t.getThird() + "\" } } } }";
                    }
                    dokument += "} } }, \"nazivKviza\": { \"stringValue\": \"" + r.getImeKviza() + "\" } } }";


                    try (OutputStream os = urlConnection.getOutputStream()) {
                        byte[] input = dokument.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    int code = urlConnection.getResponseCode();
                    InputStream odgovor = urlConnection.getInputStream();
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(odgovor, "utf-8"))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine = null;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                        Log.d("ODGOVOR", response.toString());
                    }
                }
            }  catch (IOException e) {
                e.printStackTrace();
            }

            return null ;
        }

    }

}
