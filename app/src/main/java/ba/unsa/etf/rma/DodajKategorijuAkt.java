package ba.unsa.etf.rma;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.maltaisn.icondialog.Icon;
import com.maltaisn.icondialog.IconDialog;

public class DodajKategorijuAkt extends AppCompatActivity implements IconDialog.Callback {

    private Icon[] selectedIcons;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dodaj_kategoriju);

        final EditText imeKategorije = (EditText) findViewById(R.id.etNaziv);
        final EditText imeIkone = (EditText) findViewById(R.id.etIkona);
        Button dodajIkonuBtn = (Button) findViewById(R.id.btnDodajIkonu);
        Button dodajKategorijuBtn = (Button) findViewById(R.id.btnDodajKategoriju);

        final IconDialog iconDialog = new IconDialog();

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
                imeKategorije.setBackgroundColor(Color.WHITE);
                imeIkone.setBackgroundColor(Color.WHITE);
                finish();
            }
        });
    }

    @Override
    public void onIconDialogIconsSelected(Icon[] icons) {
        selectedIcons = icons;
    }
}
