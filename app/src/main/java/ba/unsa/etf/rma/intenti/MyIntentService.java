package ba.unsa.etf.rma.intenti;

import android.app.IntentService;
import android.content.Intent;

public class MyIntentService extends IntentService {
    public MyIntentService() {
        super(null);
    }
    public MyIntentService(String name) {
        super(name);
        setIntentRedelivery(true);
        // Sav posao koji treba da obavi konstruktor treba da se
        // nalazi ovdje
    }
    @Override
    public void onCreate() {
        super.onCreate();
        // Akcije koje se trebaju obaviti pri kreiranju servisa
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        // Kod koji se nalazi ovdje će se izvršavati u posebnoj niti
        // Ovdje treba da se nalazi funkcionalnost servisa koja je
        // vremenski zahtjevna
    }
}
