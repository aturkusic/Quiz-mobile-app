package ba.unsa.etf.rma;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class SpinnerAdapter extends ArrayAdapter<Kategorija> {
    ArrayList<Kategorija> lista;

    public SpinnerAdapter(Context context, ArrayList<Kategorija> countryList) {
        super(context, 0, countryList);
        lista = countryList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.custom_spinner_row, parent, false
            );
        }
        TextView textViewName = convertView.findViewById(R.id.sp_tekst_red);

        if(position == lista.size()) return convertView;
            Kategorija currentItem = getItem(position);

        if (currentItem != null) {
            textViewName.setText(currentItem.getNaziv());
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return lista != null ? lista.size() : 0;
    }

    public int getPozicija(Kategorija k) {
        int i = 0;
        for(Kategorija k1 : lista) {
            if(k1.getNaziv().equals(k.getNaziv())) return i;
            i++;
        }
        return i;
    }

}

