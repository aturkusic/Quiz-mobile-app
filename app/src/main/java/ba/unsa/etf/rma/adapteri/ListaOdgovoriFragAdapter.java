package ba.unsa.etf.rma.adapteri;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;

public class ListaOdgovoriFragAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList data;
    private static LayoutInflater inflater = null;
    public Resources res;
    String tempValues = null;
    private int pozicijaKliknutog = -1;
    private int pozicijaTacnog = -1;
    private int crvena;
    private int zelena;


    public ListaOdgovoriFragAdapter(Activity a, ArrayList d, Resources resLocal) {
        activity = a;
        data = d;
        res = resLocal;
        inflater = ( LayoutInflater )activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        crvena = resLocal.getColor(R.color.crvena);
        zelena = resLocal.getColor(R.color.zelena);
    }

    @Override
    public int getCount() {
        if(data.size() <= 0)
            return 1;
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        if(data.size() == 0) return null;
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getPozicijaKliknutog() {
        return pozicijaKliknutog;
    }

    public void setPozicijaKliknutog(int pozicijaKliknutog) {
        this.pozicijaKliknutog = pozicijaKliknutog;
    }

    public int getPozicijaTacnog() {
        return pozicijaTacnog;
    }

    public void setPozicijaTacnog(int pozicijaTacnog) {
        this.pozicijaTacnog = pozicijaTacnog;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.pitanje_odgovor_lista, parent, false);
        }

        // get current item to be displayed
        String currentItem = (String) getItem(position);

        // get the TextView for item name and item description
        TextView odgovor = (TextView) convertView.findViewById(R.id.odgovorUListi);

        odgovor.setText(currentItem);

        odgovor.setBackgroundColor(0x00000000);
        if(position == pozicijaKliknutog && pozicijaKliknutog != pozicijaTacnog)
            odgovor.setBackgroundColor(crvena);
        else if (position == pozicijaTacnog) {
            odgovor.setBackgroundColor(zelena);
        }

        return convertView;

    }
}
