package ba.unsa.etf.rma.ostalo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import ba.unsa.etf.rma.aktivnosti.DodajKvizAkt;
import ba.unsa.etf.rma.aktivnosti.KvizoviAkt;

public class ConnectivityReceiver extends BroadcastReceiver {
    KvizoviAkt kA;
    DodajKvizAkt dKA;
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if(context.getClass() == KvizoviAkt.class) kA = (KvizoviAkt) context;
        else {
            dKA = (DodajKvizAkt) context;
        }
        if(isConnected) {
            if(kA != null) {
                kA.setOnline(true);
            } else {
                dKA.setOnline(true);
            }
        } else {
            if(kA != null) {
                kA.setOnline(false);
            } else {
                dKA.setOnline(false);
            }
        }
    }
}
