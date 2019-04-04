package ba.unsa.etf.rma;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListaOdgovoriAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList data;
    private static LayoutInflater inflater = null;
    public Resources res;
    String tempValues = null;
    private int pozicija = -1;


    public ListaOdgovoriAdapter(Activity a, ArrayList d, Resources resLocal) {
        activity = a;
        data = d;
        res = resLocal;
        inflater = ( LayoutInflater )activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if(data.size() <= 0)
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

    public int getPozicija() {
        return pozicija;
    }

    public void setPozicija(int pozicija) {
        this.pozicija = pozicija;
    }

    private static class ViewHolder{

        public TextView odgovor;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;
        if(convertView==null){

            vi = inflater.inflate(R.layout.pitanje_odgovor_lista, null);

            holder = new ViewHolder();
            holder.odgovor = (TextView) vi.findViewById(R.id.odgovorUListi);

            vi.setTag( holder );
        }
        else
            holder=(ViewHolder)vi.getTag();

        if(data.size()<=0)
        {
            holder.odgovor.setText("No Data");
        }
        else
        {
            tempValues = null;
            tempValues = (String) data.get( position );

            holder.odgovor.setText( tempValues );

        }
        holder.odgovor.setTextColor(Color.BLACK);
        if(position == pozicija)
            holder.odgovor.setTextColor(Color.GREEN);
        return vi;
    }
}
