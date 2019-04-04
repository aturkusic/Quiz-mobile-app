package ba.unsa.etf.rma;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ba.unsa.etf.rma.Kviz;

public class ListaAdapter extends BaseAdapter implements Filterable {
    private Activity activity;
    private ArrayList<Kviz> data, dataCopy;
    private static LayoutInflater inflater = null;
    public Resources res;
    CustomFilter cf;
    Kategorija kat = new Kategorija();
    Kviz tempValues=null;


    public ListaAdapter(Activity a, ArrayList<Kviz> d, Resources resLocal) {
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
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public Kategorija getKat() {
        return kat;
    }

    public void setKat(Kategorija kat) {
        this.kat = kat;
    }

    private static class ViewHolder{

        public TextView textImeKviza;
        public ImageView image;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            vi = inflater.inflate(R.layout.custom_lista, null);

            holder = new ViewHolder();
            holder.textImeKviza = (TextView) vi.findViewById(R.id.imeKvizaLW);
            holder.image = (ImageView)vi.findViewById(R.id.image);

            vi.setTag( holder );
        }
        else
            holder=(ViewHolder)vi.getTag();

        if(data.size()<=0)
        {
            holder.textImeKviza.setText("No Data");

        }
        else
        {
            tempValues = null;
            tempValues = (Kviz) data.get( position );

            holder.textImeKviza.setText( tempValues.getNaziv() );
            holder.image.setImageResource(res.getIdentifier("ba.unsa.etf.rma:drawable/" + tempValues.getKategorija().getId(),null,null));

        }
        return vi;
    }

    @Override
    public Filter getFilter() {
        if(cf == null)  {
            cf = new CustomFilter();
        }
        return cf;
    }

    private class CustomFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0 && !kat.getNaziv().equals("Svi")) {
                constraint = constraint.toString().toLowerCase();

                ArrayList<Kviz> filters = new ArrayList<>();

                for (int i = 0; i < dataCopy.size(); i++) {
                    if (dataCopy.get(i).getKategorijaNaziv().contains(constraint) || dataCopy.get(i).getKategorijaNaziv().equals("dodaj kviz")) {
                        Kviz kviz = new Kviz(dataCopy.get(i).getNaziv(), dataCopy.get(i).getPitanja(), dataCopy.get(i).getKategorija());
                        filters.add(kviz);
                    }

                }
                results.count = filters.size();
                results.values = filters;
            } else {
                results.count = dataCopy.size();
                results.values = dataCopy;
            }
            kat = new Kategorija();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            data = (ArrayList<Kviz>)results.values;
            notifyDataSetChanged();
        }
    }

}

