package ba.unsa.etf.rma.adapteri;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.maltaisn.icondialog.IconView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Rang;
import ba.unsa.etf.rma.ostalo.Trojka;

public class RangListaAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<Trojka<Integer, String, Double>> data, dataCopy;
    private static LayoutInflater inflater = null;
    public Resources res;
    private int indexOdabranog = 0;
    private Trojka<Integer, String, Double> trenutniRang = null;


    public RangListaAdapter(Activity a, ArrayList<Trojka<Integer, String, Double>> d, Resources resLocal) {
        activity = a;
        data = d;
        dataCopy = d;
        res = resLocal;
        inflater = ( LayoutInflater )activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if(data.size()<=0)
            return 1;
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getIndexOdabranog() {
        return indexOdabranog;
    }

    public void setIndexOdabranog(int indexOdabranog) {
        this.indexOdabranog = indexOdabranog;
    }


    private static class ViewHolder{

        public TextView pozicija;
        public TextView imeIgraca;
        public TextView postotakTacnih;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if (convertView == null) {

            vi = inflater.inflate(R.layout.custom_ranglista_red, null);

            holder = new ViewHolder();
            holder.pozicija = (TextView) vi.findViewById(R.id.pozicijaRangListaLW);
            holder.imeIgraca = (TextView) vi.findViewById(R.id.imeIgracaLW);
            holder.postotakTacnih = (TextView) vi.findViewById(R.id.procenatTacnihLW);

            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();

        if (data.size() <= 0) {
            holder.imeIgraca.setText("Nema podataka");

        } else {
            trenutniRang = null;
            trenutniRang = (Trojka<Integer, String, Double>) data.get(position);

            if (trenutniRang != null) {
                holder.pozicija.setText(String.valueOf(trenutniRang.getFirst() + "."));
                holder.imeIgraca.setText(trenutniRang.getSecond());
                holder.postotakTacnih.setText(String.valueOf(trenutniRang.getThird()));
            }
        }

        return vi;
    }
}
