package ba.unsa.etf.rma.klase;

import java.util.ArrayList;

public abstract class Interfejsi {
    public interface IDobaviKvizove {
        void processFinish(ArrayList<?> output);
    }

    public interface IListaMogucihAsyncResponse {
        void processFinish(ArrayList<Pitanje> output);
    }

}
