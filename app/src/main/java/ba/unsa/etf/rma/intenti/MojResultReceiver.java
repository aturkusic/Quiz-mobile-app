package ba.unsa.etf.rma.intenti;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;

public class MojResultReceiver extends ResultReceiver {
    private Receiver mReceiver;
    public MojResultReceiver(Handler handler) {
        super(handler);
    }
    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }
    /* Deklaracija interfejsa koji će se trebati implementirati */
    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData);
    }
    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }
}