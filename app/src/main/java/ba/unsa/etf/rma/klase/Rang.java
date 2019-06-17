package ba.unsa.etf.rma.klase;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Objects;

import ba.unsa.etf.rma.ostalo.Trojka;

public class Rang implements Serializable {
    private String id = "";
    private ArrayList<Trojka<Integer, String, Double>> lista = new ArrayList<>();
    private String imeKviza = "";

    static SecureRandom rnd = new SecureRandom();

    public Rang() {
    }

    public Rang(ArrayList<Trojka<Integer, String, Double>> lista, String imeKviza) {
        this.lista = lista;
        this.imeKviza = imeKviza;
    }

    public Rang(String id, ArrayList<Trojka<Integer, String, Double>> lista, String imeKviza) {
        this.id = id;
        this.lista = lista;
        this.imeKviza = imeKviza;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    @Override
    public int hashCode() {
        id = String.valueOf(Objects.hash(imeKviza));
        return Integer.parseInt(id);
    }

    public ArrayList<Trojka<Integer, String, Double>> getLista() {
        return lista;
    }

    public void setLista(ArrayList<Trojka<Integer, String, Double>> lista) {
        this.lista = lista;
    }

    public String getImeKviza() {
        return imeKviza;
    }

    public void setImeKviza(String imeKviza) {
        this.imeKviza = imeKviza;
    }

    public String dajIgraceKaoString() {
        String igraci = "";
        int i = 0;
        for(Trojka<Integer, String, Double> t : lista) {
            if(i++ != lista.size() - 1)  {
                igraci += t.getFirst() + "," + t.getSecond() + "," + t.getThird() + "-";
            } else {
                igraci += t.getFirst() + "," + t.getSecond() + "," + t.getThird();
            }
        }
        return igraci;
    }

    public String postaviRandomId() {
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
            StringBuilder sb = new StringBuilder( 20 );
            for( int i = 0; i < 20; i++ )
                sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
            id = sb.toString();
            return sb.toString();
    }

}
