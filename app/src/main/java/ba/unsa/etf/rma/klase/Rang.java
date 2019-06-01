package ba.unsa.etf.rma.klase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

import ba.unsa.etf.rma.ostalo.Trojka;

public class Rang implements Serializable {
    private String id = "";
    private ArrayList<Trojka<Integer, String, Double>> lista = new ArrayList<>();
    private String imeKviza = "";

    public Rang() {
    }

    public Rang(ArrayList<Trojka<Integer, String, Double>> lista, String imeKviza) {
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
}
