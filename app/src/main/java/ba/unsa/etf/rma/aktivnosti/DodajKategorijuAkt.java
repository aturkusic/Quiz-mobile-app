package ba.unsa.etf.rma.aktivnosti;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.common.collect.Lists;
import com.maltaisn.icondialog.Icon;
import com.maltaisn.icondialog.IconDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Pitanje;

import static ba.unsa.etf.rma.aktivnosti.KvizoviAkt.ucitajSveKategorijeIzBaze;

public class DodajKategorijuAkt extends AppCompatActivity implements IconDialog.Callback {

    private Icon[] selectedIcons;
    private EditText imeKategorije;
    private EditText imeIkone;
    private ArrayList<Kategorija> kategorije;
    private boolean postojiUBazi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dodaj_kategoriju);

        imeKategorije = (EditText) findViewById(R.id.etNaziv);
        imeIkone = (EditText) findViewById(R.id.etIkona);
        Button dodajIkonuBtn = (Button) findViewById(R.id.btnDodajIkonu);
        Button dodajKategorijuBtn = (Button) findViewById(R.id.btnDodajKategoriju);
        kategorije = (ArrayList<Kategorija>) getIntent().getSerializableExtra("kategorije");

        final IconDialog iconDialog = new IconDialog();
        imeIkone.setEnabled(false);

        dodajIkonuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconDialog.setSelectedIcons(selectedIcons);
                iconDialog.show(getSupportFragmentManager(), "icon_dialog");
            }
        });

        dodajKategorijuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imeKategorije.getText().toString().equals("")) {
                    imeKategorije.setBackgroundColor(Color.RED);
                    return;
                } else if (imeIkone.getText().toString().equals("")) {
                    imeIkone.setBackgroundColor(Color.RED);
                    return;
                }
                for (Kategorija k : kategorije) {
                    if (k.getNaziv().equalsIgnoreCase(imeKategorije.getText().toString())) {
                        imeKategorije.setBackgroundColor(Color.RED);
                        imeKategorije.setHint("Odaberite drugo ime");
                        AlertDialog alertDialog = new AlertDialog.Builder(DodajKategorijuAkt.this).create();
                        alertDialog.setTitle("Alert");
                        alertDialog.setMessage("Unesena kategorija vec postoji!");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                        return;
                    }
                }
                try {
                    new DobaviKategorije().execute("Kategorije").get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(postojiUBazi) {
                    Toast.makeText(DodajKategorijuAkt.this, "“Unesena kategorija već postoji!", Toast.LENGTH_LONG).show();
                    postojiUBazi = false;
                    return;
                }
                imeKategorije.setHint("");
                imeKategorije.setBackgroundColor(0x00000000);
                imeIkone.setBackgroundColor(0x00000000);
                Intent povratni = new Intent();
                povratni.putExtra("povratnaKategorija", new Kategorija(imeKategorije.getText().toString(), imeIkone.getText().toString()));
                setResult(Activity.RESULT_OK, povratni);
                finish();
            }
        });
    }

    @Override
    public void onIconDialogIconsSelected(Icon[] icons) {
        selectedIcons = icons;
        imeIkone.setText(Integer.toString(selectedIcons[0].getId()));
    }

    private class DobaviKategorije extends AsyncTask<String, Integer, Void> {
        protected Void doInBackground(String... urls) {
            try {
                InputStream is = getResources().openRawResource(R.raw.secret);
                GoogleCredential credentials = null;
                credentials = GoogleCredential.fromStream(is).createScoped(Lists.newArrayList("https://www.googleapis.com/auth/datastore"));
                credentials.refreshToken();
                String TOKEN = credentials.getAccessToken();
                String url1;
                ArrayList<Kategorija> listaKategorija = new ArrayList<>();
                if (urls.length == 1)
                    url1 = "https://firestore.googleapis.com/v1/projects/rma19turkusicarslan73/databases/(default)/documents/" + urls[0] + "?access_token=" + TOKEN;
                else
                    url1 = "https://firestore.googleapis.com/v1/projects/rma19turkusicarslan73/databases/(default)/documents/" + urls[0] + "/" + urls[1] + "?access_token=" + TOKEN;
                URL url;
                url = new URL(url1);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String rezultat = DodajKvizAkt.convertStreamToString(in);
                JSONObject jo = null;
                jo = new JSONObject(rezultat);
                JSONArray items = new JSONArray();
                items = jo.getJSONArray("documents");
                listaKategorija = ucitajSveKategorijeIzBaze(items);
                for(Kategorija k : listaKategorija) {
                    if(k.getNaziv().equalsIgnoreCase(imeKategorije.getText().toString())) {
                        postojiUBazi = true;
                        return null;
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

    }


}
