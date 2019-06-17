package ba.unsa.etf.rma.ostalo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.common.collect.Lists;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.aktivnosti.DodajKvizAkt;
import ba.unsa.etf.rma.fragmenti.RangLista;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;
import ba.unsa.etf.rma.klase.Rang;

public class KvizoviDBOpenHelper extends SQLiteOpenHelper {

    private static KvizoviDBOpenHelper instanca;

    public static final String DATABASE_NAME = "mojaBaza.db";
    public static final int DATABASE_VERSION = 17;

    public static final String TABELA_KVIZOVI = "kvizovi";
    public static final String KVIZ_ID = "_id";
    public static final String KVIZ_NAZIV = "naziv";
    public static final String KVIZ_PITANJA = "pitanja";
    public static final String KVIZ_KATEGORIJA= "kategorija";

    public static final String TABELA_KATEGORIJE = "kategorije";
    public static final String KATEGORIJA_ID = "_id";
    public static final String KATEGORIJA_ID_SLIKA = "idSlika";
    public static final String KATEGORIJA_NAZIV = "naziv";

    public static final String TABELA_PITANJA = "pitanja";
    public static final String PITANJE_ID = "_id";
    public static final String PITANJE_NAZIV= "naziv";
    public static final String PITANJE_ODGOVORI = "odgovori";
    public static final String PITANJE_TACAN = "tacanOdgovor";

    public static final String TABELA_RANGLISTE = "rangliste";
    public static final String RANGLISTA_ID = "_id";
    public static final String RANGLISTA_KVIZ = "kviz";
    public static final String RANGLISTA_IGRACI = "igraci";
    public static final String RANGLISTA_MIJENJANA = "mijenjana";


    public static synchronized KvizoviDBOpenHelper getInstance(Context context) {
        if (instanca == null) {
            instanca = new KvizoviDBOpenHelper(context.getApplicationContext());
        }
        return instanca;
    }


    String CREATE_KVIZOVI_TABELU = "CREATE TABLE " + TABELA_KVIZOVI +
            "(" +
            KVIZ_ID + " INTEGER PRIMARY KEY, " +
            KVIZ_NAZIV + " TEXT, " +
            KVIZ_PITANJA + " TEXT, " +
            KVIZ_KATEGORIJA + " TEXT " +
            ");";
    String CREATE_PITANJA_TABELA = "CREATE TABLE " + TABELA_PITANJA +
            "(" +
            PITANJE_ID + " INTEGER PRIMARY KEY, " +
            PITANJE_NAZIV + " TEXT, " +
            PITANJE_ODGOVORI + " TEXT, " +
            PITANJE_TACAN + " TEXT " +
            ");";
    String CREATE_KATEGORIJE_TABELA = "CREATE TABLE " + TABELA_KATEGORIJE +
            "(" +
            KATEGORIJA_ID + " INTEGER PRIMARY KEY, " +
            KATEGORIJA_NAZIV + " TEXT, " +
            KATEGORIJA_ID_SLIKA + " TEXT " +
            ");";
    String CREATE_RANGLISTE_TABELA = "CREATE TABLE " + TABELA_RANGLISTE +
            "(" +
            RANGLISTA_ID + " TEXT PRIMARY KEY, " +
            RANGLISTA_KVIZ + " TEXT, " +
            RANGLISTA_IGRACI + " TEXT, " +
            RANGLISTA_MIJENJANA + " INTEGER " +
            ");";

    public KvizoviDBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_KATEGORIJE_TABELA);
        db.execSQL(CREATE_PITANJA_TABELA);
        db.execSQL(CREATE_KVIZOVI_TABELU);
        db.execSQL(CREATE_RANGLISTE_TABELA);
    }

    public void ubaciSveKvizoveUBazu(ArrayList<Kviz> kvizovi) {
        for(Kviz k : kvizovi) {
            ContentValues noveVrijednosti = new ContentValues();
            noveVrijednosti.put(KVIZ_ID, k.getId());
            noveVrijednosti.put(KVIZ_NAZIV, k.getNaziv());
            noveVrijednosti.put(KVIZ_PITANJA, k.dajPitanjaKaoString());
            noveVrijednosti.put(KVIZ_KATEGORIJA, k.getKategorija().getIdUBazi());
            SQLiteDatabase db = this.getWritableDatabase();
            int id = (int)db.insertWithOnConflict(KvizoviDBOpenHelper.TABELA_KVIZOVI,null, noveVrijednosti, SQLiteDatabase.CONFLICT_IGNORE);
            if(id == -1) {
                db.update(KvizoviDBOpenHelper.TABELA_KVIZOVI, noveVrijednosti, "_id=?", new String[] {k.getId()});
            }
        }

    }

    public void ubaciSvaPitanjaUBazu(ArrayList<Pitanje> pitanja) {
        for(Pitanje p : pitanja) {
            ContentValues noveVrijednosti = new ContentValues();
            noveVrijednosti.put(PITANJE_ID, p.getId());
            noveVrijednosti.put(PITANJE_NAZIV, p.getNaziv());
            noveVrijednosti.put(PITANJE_ODGOVORI, p.dajOdgovoreKaoString());
            noveVrijednosti.put(PITANJE_TACAN, p.getTacanOdgovor());
            SQLiteDatabase db = this.getWritableDatabase();
            db.insertWithOnConflict(KvizoviDBOpenHelper.TABELA_PITANJA, null, noveVrijednosti, SQLiteDatabase.CONFLICT_IGNORE);
        }
    }

    public void ubaciSveKategorijeUBazu(ArrayList<Kategorija> kategorije) {
        for(Kategorija p : kategorije) {
            ContentValues noveVrijednosti = new ContentValues();
            noveVrijednosti.put(KATEGORIJA_ID, p.getIdUBazi());
            noveVrijednosti.put(KATEGORIJA_NAZIV, p.getNaziv());
            noveVrijednosti.put(KATEGORIJA_ID_SLIKA, p.getId());
            SQLiteDatabase db = this.getWritableDatabase();
            db.insertWithOnConflict(KvizoviDBOpenHelper.TABELA_KATEGORIJE, null, noveVrijednosti, SQLiteDatabase.CONFLICT_IGNORE);
        }
    }

    public void ubaciSveRangListeULokalnuBazu(ArrayList<Rang> rangliste) {
        for(Rang p : rangliste) {
            ContentValues noveVrijednosti = new ContentValues();
            noveVrijednosti.put(RANGLISTA_ID, p.getId());
            noveVrijednosti.put(RANGLISTA_IGRACI, p.dajIgraceKaoString());
            noveVrijednosti.put(RANGLISTA_KVIZ, p.getImeKviza());
            noveVrijednosti.put(RANGLISTA_MIJENJANA, 0);
            SQLiteDatabase db = this.getWritableDatabase();
            int id = (int)db.insertWithOnConflict(KvizoviDBOpenHelper.TABELA_RANGLISTE,null, noveVrijednosti, SQLiteDatabase.CONFLICT_IGNORE);
            if(id == -1) {
                db.update(KvizoviDBOpenHelper.TABELA_RANGLISTE, noveVrijednosti, "_id=?", new String[] {p.getId()});
            }
        }
    }

    public void ucitajNEsto() {
        String[] koloneRezulat = new String[]{ RANGLISTA_KVIZ, RANGLISTA_ID, RANGLISTA_IGRACI, RANGLISTA_MIJENJANA};
// Specificiramo WHERE dio upita
//        String where = KVIZ_KATEGORIJA + "=127045871";
        String where = null;
// Definišemo argumente u where upitu, group by, having i order po potrebi
        String whereArgs[] = null;
        String groupBy = null;
        String having = null;
        String order = null;
// Dohvatimo referencu na bazu (poslije ćemo opisati kako se implementira helper)
        SQLiteDatabase db = this.getReadableDatabase();
// Izvršimo upit
        Cursor cursor = db.query(this.TABELA_RANGLISTE, koloneRezulat, where,
                whereArgs, groupBy, having, order);

        int INDEX_KOLONE_IME = cursor.getColumnIndexOrThrow(RANGLISTA_KVIZ);
        int INDEX_KOLONE_id = cursor.getColumnIndexOrThrow(RANGLISTA_ID);
        int INDEX_KOLONE_kategorija = cursor.getColumnIndexOrThrow(RANGLISTA_IGRACI);
        int INDEX_KOLONE_pitanja = cursor.getColumnIndexOrThrow(RANGLISTA_MIJENJANA);
        while(cursor.moveToNext()){
            Log.d("ARSLANNN", cursor.getString(INDEX_KOLONE_IME) + "  ID  "+cursor.getString(INDEX_KOLONE_id) + "  KATE  " + cursor.getString(INDEX_KOLONE_kategorija) + "   PITANJA   " + cursor.getString(INDEX_KOLONE_pitanja));
        }
//kada završimo sa kursorom potrebno ga je zatvoriti
        cursor.close();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABELA_KATEGORIJE);
            db.execSQL("DROP TABLE IF EXISTS " + TABELA_PITANJA);
            db.execSQL("DROP TABLE IF EXISTS " + TABELA_KVIZOVI);
            db.execSQL("DROP TABLE IF EXISTS " + TABELA_RANGLISTE);
            onCreate(db);
        }
    }

    public ArrayList<Kviz> dobaviSveKvizoveIzLokalneBaze() {
        ArrayList<Kviz> kategorijas = new ArrayList<>();

        String[] koloneRezulat = new String[]{ KVIZ_NAZIV, KVIZ_ID, KVIZ_PITANJA, KVIZ_KATEGORIJA};
        String where = null;
        String whereArgs[] = null;
        String groupBy = null;
        String having = null;
        String order = null;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(this.TABELA_KVIZOVI, koloneRezulat, where, whereArgs, groupBy, having, order);

        int INDEX_KOLONE_IME = cursor.getColumnIndexOrThrow(KVIZ_NAZIV);
        int INDEX_KOLONE_id = cursor.getColumnIndexOrThrow(KVIZ_ID);
        int INDEX_KOLONE_pitanja = cursor.getColumnIndexOrThrow(KVIZ_PITANJA);
        int INDEX_KOLONE_kategorija = cursor.getColumnIndexOrThrow(KVIZ_KATEGORIJA);

        while(cursor.moveToNext()){
            String[] tmp = null;
            if(!cursor.getString(INDEX_KOLONE_pitanja).equals(""))
                tmp = cursor.getString(INDEX_KOLONE_pitanja).split(",");
            kategorijas.add( new Kviz(cursor.getString(INDEX_KOLONE_id), cursor.getString(INDEX_KOLONE_IME), dajSvaPitanjaIzKviza(tmp), dajKategoriju(cursor.getString(INDEX_KOLONE_kategorija))));
        }
        cursor.close();
        return kategorijas;
    }

    private ArrayList<Pitanje> dajSvaPitanjaIzKviza(String[] pitanja) {
        ArrayList<Pitanje> pitanjes = new ArrayList<>();
        if(pitanja == null) return pitanjes;
        for(String s : pitanja) {
            String[] koloneRezulat = new String[]{ PITANJE_NAZIV, PITANJE_ID, PITANJE_ODGOVORI, PITANJE_TACAN};
            String where = PITANJE_ID + "=" + s;
            String whereArgs[] = null;
            String groupBy = null;
            String having = null;
            String order = null;

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(this.TABELA_PITANJA, koloneRezulat, where, whereArgs, groupBy, having, order);

            int INDEX_KOLONE_IME = cursor.getColumnIndexOrThrow(PITANJE_NAZIV);
            int INDEX_KOLONE_id = cursor.getColumnIndexOrThrow(PITANJE_ID);
            int INDEX_KOLONE_odgovori = cursor.getColumnIndexOrThrow(PITANJE_ODGOVORI);
            int INDEX_KOLONE_tacan = cursor.getColumnIndexOrThrow(PITANJE_TACAN);

            while(cursor.moveToNext()){
                pitanjes.add( new Pitanje(cursor.getString(INDEX_KOLONE_id), cursor.getString(INDEX_KOLONE_IME), cursor.getString(INDEX_KOLONE_IME),
                        dajOdgovore(cursor.getString(INDEX_KOLONE_odgovori).split(",")), cursor.getString(INDEX_KOLONE_tacan)));
            }
            cursor.close();
        }
        return pitanjes;
    }

    private ArrayList<String> dajOdgovore(String[] split) {
        ArrayList<String> odgovori = new ArrayList<>();
        for(String s : split) odgovori.add(s);
        return odgovori;
    }

    private Kategorija dajKategoriju(String id) {
        String[] koloneRezulat = new String[]{ KATEGORIJA_NAZIV, KATEGORIJA_ID, KATEGORIJA_ID_SLIKA};
        String where = KATEGORIJA_ID + "=" + id;
        String whereArgs[] = null;
        String groupBy = null;
        String having = null;
        String order = null;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(this.TABELA_KATEGORIJE, koloneRezulat, where, whereArgs, groupBy, having, order);

        int INDEX_KOLONE_IME = cursor.getColumnIndexOrThrow(KATEGORIJA_NAZIV);
        int INDEX_KOLONE_id = cursor.getColumnIndexOrThrow(KATEGORIJA_ID);
        int INDEX_KOLONE_kategorija = cursor.getColumnIndexOrThrow(KATEGORIJA_ID_SLIKA);

        while(cursor.moveToNext()){
            return new Kategorija(cursor.getString(INDEX_KOLONE_id), cursor.getString(INDEX_KOLONE_IME), cursor.getString(INDEX_KOLONE_kategorija));
        }
        cursor.close();
        return null;
    }

    public ArrayList<Kategorija> dobaviSveKategorijeIzLokalneBaze() {
        ArrayList<Kategorija> kategorijas = new ArrayList<>();

        String[] koloneRezulat = new String[]{ KATEGORIJA_NAZIV, KATEGORIJA_ID, KATEGORIJA_ID_SLIKA};
        String where = null;
        String whereArgs[] = null;
        String groupBy = null;
        String having = null;
        String order = null;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(this.TABELA_KATEGORIJE, koloneRezulat, where, whereArgs, groupBy, having, order);

        int INDEX_KOLONE_IME = cursor.getColumnIndexOrThrow(KATEGORIJA_NAZIV);
        int INDEX_KOLONE_id = cursor.getColumnIndexOrThrow(KATEGORIJA_ID);
        int INDEX_KOLONE_kategorija = cursor.getColumnIndexOrThrow(KATEGORIJA_ID_SLIKA);

        while(cursor.moveToNext()){
            kategorijas.add( new Kategorija(cursor.getString(INDEX_KOLONE_id), cursor.getString(INDEX_KOLONE_IME), cursor.getString(INDEX_KOLONE_kategorija)));
        }
        cursor.close();
        return kategorijas;
    }

    public Rang dajRangListuKviza(String nazivKviza) {
        String[] koloneRezulat = new String[]{ RANGLISTA_KVIZ, RANGLISTA_ID, RANGLISTA_IGRACI};
        String where = RANGLISTA_KVIZ + "='" + nazivKviza + "'";
        String whereArgs[] = null;
        String groupBy = null;
        String having = null;
        String order = null;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(this.TABELA_RANGLISTE, koloneRezulat, where, whereArgs, groupBy, having, order);

        int INDEX_KOLONE_IME_KVIZA = cursor.getColumnIndexOrThrow(RANGLISTA_KVIZ);
        int INDEX_KOLONE_id = cursor.getColumnIndexOrThrow(RANGLISTA_ID);
        int INDEX_KOLONE_igraci = cursor.getColumnIndexOrThrow(RANGLISTA_IGRACI);

        while(cursor.moveToNext()){
            return new Rang(cursor.getString(INDEX_KOLONE_id), dajNizIgracaIzStringa(cursor.getString(INDEX_KOLONE_igraci)), cursor.getString(INDEX_KOLONE_IME_KVIZA));
        }
        cursor.close();
        return null;
    }

    private ArrayList<Trojka<Integer, String, Double>> dajNizIgracaIzStringa(String string) {
        ArrayList<Trojka<Integer, String, Double>> lista = new ArrayList<>();
        String[] igraci = string.split("-");
        for(String s : igraci) {
            String[] podaci = s.split(",");
            lista.add(new Trojka<>(Integer.parseInt(podaci[0]), podaci[1], Double.parseDouble(podaci[2])));
        }
        return lista;
    }

    public void dodajIzmijeniRangListuULokalnojBazi(Rang p) {
        ContentValues noveVrijednosti = new ContentValues();
        noveVrijednosti.put(RANGLISTA_ID, p.getId());
        noveVrijednosti.put(RANGLISTA_IGRACI, p.dajIgraceKaoString());
        noveVrijednosti.put(RANGLISTA_KVIZ, p.getImeKviza());
        noveVrijednosti.put(RANGLISTA_MIJENJANA, 1);
        SQLiteDatabase db = this.getWritableDatabase();
        int id = (int)db.insertWithOnConflict(KvizoviDBOpenHelper.TABELA_RANGLISTE,null, noveVrijednosti, SQLiteDatabase.CONFLICT_IGNORE);
        if(id == -1) {
            db.update(KvizoviDBOpenHelper.TABELA_RANGLISTE, noveVrijednosti, "_id=?", new String[] {p.getId()});
        }

    }


    public ArrayList<Rang> dajSvePromijenjeneRangListe() {
        ArrayList<Rang> rangs = new ArrayList<>();
        String[] koloneRezulat = new String[]{ RANGLISTA_KVIZ, RANGLISTA_ID, RANGLISTA_IGRACI};
        String where = RANGLISTA_MIJENJANA + "=1";
        String whereArgs[] = null;
        String groupBy = null;
        String having = null;
        String order = null;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(this.TABELA_RANGLISTE, koloneRezulat, where, whereArgs, groupBy, having, order);

        int INDEX_KOLONE_IME_KVIZA = cursor.getColumnIndexOrThrow(RANGLISTA_KVIZ);
        int INDEX_KOLONE_id = cursor.getColumnIndexOrThrow(RANGLISTA_ID);
        int INDEX_KOLONE_igraci = cursor.getColumnIndexOrThrow(RANGLISTA_IGRACI);

        while(cursor.moveToNext()){
            rangs.add(new Rang(cursor.getString(INDEX_KOLONE_id), dajNizIgracaIzStringa(cursor.getString(INDEX_KOLONE_igraci)), cursor.getString(INDEX_KOLONE_IME_KVIZA)));
        }
        cursor.close();
        staviSvePromijenjeneNaNulu();
        return rangs;
    }

    private void staviSvePromijenjeneNaNulu() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABELA_RANGLISTE + " SET " + RANGLISTA_MIJENJANA + "=0" + " WHERE " + RANGLISTA_MIJENJANA + "=1" );
    }
}
