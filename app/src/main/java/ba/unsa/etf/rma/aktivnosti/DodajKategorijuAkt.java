package ba.unsa.etf.rma.aktivnosti;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Pitanje;

import static ba.unsa.etf.rma.aktivnosti.KvizoviAkt.ucitajKategoriju;
import static ba.unsa.etf.rma.aktivnosti.KvizoviAkt.ucitajSveKategorijeIzBaze;

public class DodajKategorijuAkt extends AppCompatActivity implements IconDialog.Callback {

    private Icon[] selectedIcons;
    private EditText imeKategorije;
    private EditText imeIkone;
    private ArrayList<Kategorija> kategorije;
    private boolean postojiUBazi;
    private Kategorija kategorijaZaDodat;

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
                kategorijaZaDodat = new Kategorija(imeKategorije.getText().toString(), imeIkone.getText().toString());
                kategorijaZaDodat.hashCode();
                new DobaviKategorije().execute("Kategorije", kategorijaZaDodat.getIdUBazi());
                imeKategorije.setHint("");
                imeKategorije.setBackgroundColor(0x00000000);
                imeIkone.setBackgroundColor(0x00000000);
            }
        });
    }

    @Override
    public void onIconDialogIconsSelected(Icon[] icons) {
        selectedIcons = icons;
        imeIkone.setText(Integer.toString(selectedIcons[0].getId()));
    }

    private class DobaviKategorije extends AsyncTask<String, Integer,  ArrayList<Kategorija>> {
        protected ArrayList<Kategorija> doInBackground(String... urls) {
            ArrayList<Kategorija> listaKategorija = new ArrayList<>();
            try {
                InputStream is = getResources().openRawResource(R.raw.secret);
                GoogleCredential credentials = null;
                credentials = GoogleCredential.fromStream(is).createScoped(Lists.newArrayList("https://www.googleapis.com/auth/datastore"));
                credentials.refreshToken();
                String TOKEN = credentials.getAccessToken();
                listaKategorija = new ArrayList<>();
                String url1 = "https://firestore.googleapis.com/v1/projects/rma19turkusicarslan73/databases/(default)/documents/" + urls[0] + "/" + kategorijaZaDodat.getIdUBazi() + "?access_token=" + TOKEN;
                URL url = new URL(url1);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                return null;
            }
            return listaKategorija;
        }

        @Override
        protected void onPostExecute(ArrayList<Kategorija> aVoid) {
            if(aVoid == null) {
                Intent povratni = new Intent();
                povratni.putExtra("povratnaKategorija", kategorijaZaDodat);
                setResult(Activity.RESULT_OK, povratni);
                finish();
            } else {
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
            }
        }
    }


}
