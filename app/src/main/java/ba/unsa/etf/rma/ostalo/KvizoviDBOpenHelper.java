package ba.unsa.etf.rma.ostalo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;

public class KvizoviDBOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "mojaBaza.db";
    public static final int DATABASE_VERSION = 7;

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

    public static final String TABELA_IGRACI_RANGLISTE = "igraciRangliste";
    public static final String IGRAC_ID = "_id";
    public static final String IGRAC_NAZIV = "naziv";
    public static final String IGRAC_POZICIJA = "pozicija";
    public static final String IGRAC_POSTOTAK = "postotak";


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


    public KvizoviDBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_KATEGORIJE_TABELA);
        db.execSQL(CREATE_PITANJA_TABELA);
        db.execSQL(CREATE_KVIZOVI_TABELU);
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

//    public void ucitajNEsto() {
//        String[] koloneRezulat = new String[]{ KVIZ_NAZIV, KVIZ_ID, KVIZ_KATEGORIJA, KVIZ_PITANJA};
//// Specificiramo WHERE dio upita
//        String where = KVIZ_KATEGORIJA + "=127045871";
//// Definišemo argumente u where upitu, group by, having i order po potrebi
//        String whereArgs[] = null;
//        String groupBy = null;
//        String having = null;
//        String order = null;
//// Dohvatimo referencu na bazu (poslije ćemo opisati kako se implementira helper)
//        SQLiteDatabase db = this.getReadableDatabase();
//// Izvršimo upit
//        Cursor cursor = db.query(this.TABELA_KVIZOVI, koloneRezulat, where,
//                whereArgs, groupBy, having, order);
//
//        int INDEX_KOLONE_IME = cursor.getColumnIndexOrThrow(KVIZ_NAZIV);
//        int INDEX_KOLONE_id = cursor.getColumnIndexOrThrow(KVIZ_ID);
//        int INDEX_KOLONE_kategorija = cursor.getColumnIndexOrThrow(KVIZ_KATEGORIJA);
//        int INDEX_KOLONE_pitanja = cursor.getColumnIndexOrThrow(KVIZ_PITANJA);
//        while(cursor.moveToNext()){
//            Log.d("ARSLANNN", cursor.getString(INDEX_KOLONE_IME) + "  ID  "+cursor.getInt(INDEX_KOLONE_id) + "  KATE  " + cursor.getString(INDEX_KOLONE_kategorija) + "PITANJA" + cursor.getString(INDEX_KOLONE_pitanja));
//        }
////kada završimo sa kursorom potrebno ga je zatvoriti
//        cursor.close();
//
//    }

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

}
