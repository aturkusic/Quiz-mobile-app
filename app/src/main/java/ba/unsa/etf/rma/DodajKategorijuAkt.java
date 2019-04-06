package ba.unsa.etf.rma;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.maltaisn.icondialog.Icon;
import com.maltaisn.icondialog.IconDialog;

import java.util.ArrayList;

public class DodajKategorijuAkt extends AppCompatActivity implements IconDialog.Callback {

    private Icon[] selectedIcons;
    EditText imeKategorije;
    EditText imeIkone;
    ArrayList<Kategorija> kategorije;
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
                if(imeKategorije.getText().toString().equals("")) {
                    imeKategorije.setBackgroundColor(Color.RED);
                    return;
                }
                else if(imeIkone.getText().toString().equals("")) {
                    imeIkone.setBackgroundColor(Color.RED);
                    return;
                }
                for(Kategorija k : kategorije) {
                    if(k.getNaziv().equalsIgnoreCase(imeKategorije.getText().toString())) {
                        imeKategorije.setBackgroundColor(Color.RED);
                        imeKategorije.setHint("Odaberite drugo ime");
                        return;
                    }
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
}
