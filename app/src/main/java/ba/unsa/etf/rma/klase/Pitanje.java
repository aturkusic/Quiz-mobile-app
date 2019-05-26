package ba.unsa.etf.rma.klase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class Pitanje implements Serializable {
    private String id;
    private String naziv = "";
    private String tekstPitanja = "";
    private ArrayList<String> odgovori = new ArrayList<>();
    private String tacanOdgovor = "";

    public Pitanje(String naziv, String tekstPitanja, ArrayList<String> odgovori, String tacanOdgovor) {
        this.naziv = naziv;
        this.tekstPitanja = tekstPitanja;
        this.odgovori = odgovori;
        this.tacanOdgovor = tacanOdgovor;
    }

    public Pitanje() {
    }

    public Pitanje(String id, String naziv, String tekstPitanja, ArrayList<String> odgovori, String tacanOdgovor) {
        this.id = id;
        this.naziv = naziv;
        this.tekstPitanja = tekstPitanja;
        this.odgovori = odgovori;
        this.tacanOdgovor = tacanOdgovor;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getTekstPitanja() {
        return tekstPitanja;
    }

    public void setTekstPitanja(String tekstPitanja) {
        this.tekstPitanja = tekstPitanja;
    }

    public ArrayList<String> getOdgovori() {
        return odgovori;
    }

    public void setOdgovori(ArrayList<String> odgovori) {
        this.odgovori = odgovori;
    }

    public String getTacanOdgovor() {
        return tacanOdgovor;
    }

    public void setTacanOdgovor(String tacanOdgovor) {
        this.tacanOdgovor = tacanOdgovor;
    }

    public ArrayList<String> dajRandomOdgovore() {
        ArrayList<String> izmjesano = new ArrayList<>(odgovori);
        Collections.shuffle(izmjesano);
        return izmjesano;
    }

    @Override
    public boolean equals(Object o) {
        Pitanje pitanje = (Pitanje) o;
        return naziv.equalsIgnoreCase(pitanje.naziv);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIndexTacnog(String tacanOdgovor) {
        return odgovori.indexOf(tacanOdgovor);
    }

    @Override
    public int hashCode() {
        id = String.valueOf(Objects.hash(naziv));
        return Integer.parseInt(id);
    }
}
