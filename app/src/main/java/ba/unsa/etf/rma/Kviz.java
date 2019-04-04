package ba.unsa.etf.rma;

import java.io.Serializable;
import java.util.ArrayList;

public class Kviz implements Serializable {
    private String naziv = "";
    private ArrayList<Pitanje> pitanja = new ArrayList<>();
    private  Kategorija kategorija = new Kategorija();

    public Kviz(String naziv, ArrayList<Pitanje> pitanja, Kategorija kategorija) {
        this.naziv = naziv;
        this.pitanja = pitanja;
        this.kategorija = kategorija;
    }

    public Kviz() {
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public ArrayList<Pitanje> getPitanja() {
        return pitanja;
    }

    public void setPitanja(ArrayList<Pitanje> pitanja) {
        this.pitanja = pitanja;
    }

    public Kategorija getKategorija() {
        return kategorija;
    }

    public void setKategorija(Kategorija kategorija) {
        this.kategorija = kategorija;
    }

    public void dodajPitanje(Pitanje pitanje) {
        pitanja.add(pitanje);
    }

    public String getKategorijaNaziv() {
        return kategorija.getNaziv().toLowerCase();
    }
}