package ba.unsa.etf.rma.klase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Kviz implements Serializable {
    private String id;
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

    public Kviz(String id, String naziv, ArrayList<Pitanje> pitanja, Kategorija kategorija) {
        this.id = id;
        this.naziv = naziv;
        this.pitanja = pitanja;
        this.kategorija = kategorija;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        id = String.valueOf(Objects.hash(naziv.toLowerCase()));
        return Integer.parseInt(id);
    }

    public String dajPitanjaKaoString() {
        String rez = "";
        int i = 0;
        for(Pitanje o : pitanja) {
            if(i++ != pitanja.size() - 1) rez += o.getId() + ",";
            else rez += o.getId();
        }
        return rez;
    }
}
