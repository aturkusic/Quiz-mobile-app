package ba.unsa.etf.rma.klase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Objects;

public class Kategorija implements Serializable {

    private String idUBazi;
    private String naziv = "";
    private String id;

    public Kategorija() {
    }

    public Kategorija(String naziv, String id) {
        this.naziv = naziv;
        this.id = id;
    }

    public Kategorija(String idUBazi, String naziv, String id) {
        this.naziv = naziv;
        this.idUBazi = idUBazi;
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUBazi() {
        return idUBazi;
    }

    public void setIdUBazi(String idUBazi) {
        this.idUBazi = idUBazi;
    }

    @Override
    public boolean equals(Object o) {
        Kategorija kat = (Kategorija) o;
        return naziv.equalsIgnoreCase(kat.naziv);
    }

    public String toJSON(){
        JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put("naziv", getNaziv());
            jsonObject.put("idKategorije", getId());
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }

    }

    @Override
    public int hashCode() {
        idUBazi = String.valueOf(Objects.hash(naziv));
        return Integer.parseInt(idUBazi);
    }
}
