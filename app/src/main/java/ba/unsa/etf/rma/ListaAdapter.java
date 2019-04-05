package ba.unsa.etf.rma;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.maltaisn.icondialog.IconView;

import java.util.ArrayList;

public class ListaAdapter extends BaseAdapter implements Filterable {
    private Activity activity;
    private ArrayList<Kviz> data, dataCopy;
    private static LayoutInflater inflater = null;
    public Resources res;
    CustomFilter cf;
    Kategorija kat = new Kategorija();
    Kviz trenutniKviz =null;


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
        public IconView image;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            vi = inflater.inflate(R.layout.custom_lista, null);

            holder = new ViewHolder();
            holder.textImeKviza = (TextView) vi.findViewById(R.id.imeKvizaLW);
            holder.image = (IconView)vi.findViewById(R.id.image);

            vi.setTag( holder );
        }
        else
            holder=(ViewHolder)vi.getTag();

        if(data.size()<=0)
        {
            holder.textImeKviza.setText("Nema podataka");

        }
        else {
            trenutniKviz = null;
            trenutniKviz = (Kviz) data.get(position);

            if (trenutniKviz != null) {
                holder.textImeKviza.setText(trenutniKviz.getNaziv());
                if (!trenutniKviz.getKategorija().getId().equals("addkviz"))
                    holder.image.setIcon(Integer.parseInt(trenutniKviz.getKategorija().getId()));
                else
                    holder.image.setImageResource(res.getIdentifier("ba.unsa.etf.rma:drawable/" + trenutniKviz.getKategorija().getId(), null, null));
            }
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

