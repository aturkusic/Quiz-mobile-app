package ba.unsa.etf.rma.adapteri;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.maltaisn.icondialog.IconView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kategorija;

public class KategorijeAdapter extends BaseAdapter {
        private Activity activity;
        private ArrayList<Kategorija> data, dataCopy;
        private static LayoutInflater inflater = null;
        public Resources res;
        private Kategorija trenutnaKategorija = null;


        public KategorijeAdapter(Activity a, ArrayList<Kategorija> d, Resources resLocal) {
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


        private static class ViewHolder{

            public TextView textImeKviza;
            public IconView image;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View vi = convertView;
            ViewHolder holder;

            if (convertView == null) {

                vi = inflater.inflate(R.layout.custom_lista_kategorije, null);

                holder = new ViewHolder();
                holder.textImeKviza = (TextView) vi.findViewById(R.id.imeKvizaLW);
                holder.image = (IconView) vi.findViewById(R.id.image);

                vi.setTag(holder);
            } else
                holder = (ViewHolder) vi.getTag();

            if (data.size() <= 0) {
                holder.textImeKviza.setText("Nema podataka");

            } else {
                trenutnaKategorija = null;
                trenutnaKategorija = (Kategorija) data.get(position);

                if (trenutnaKategorija != null) {
                    holder.textImeKviza.setText(trenutnaKategorija.getNaziv());
                    holder.image.setImageResource(res.getIdentifier("ba.unsa.etf.rma:drawable/bluedot", null, null));
                }
            }
            return vi;
        }
}
