package com.github.nathan130200.qdg.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "qdg";
    public static final Integer DATABASE_VERSION = 4;
    
    public static final String DB_CREATE = "create table qdg_item ("
            + "id integer primary key autoincrement, "
            + "valor real not null, "
            + "data integer not null);";
    
    public SQLiteHelper(Context ctx){
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE);
    }
    
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists qdg");
        db.execSQL("drop table if exists qdg_item");
    }
    
    public void remover(int id){
        this.getWritableDatabase().delete("qdg_item", "id == ?", new String[]{ Integer.toString(id) });
    }
    
    public boolean cadastrar(Item item){
        ContentValues cv = new ContentValues();
        cv.put("valor", item.getValor());
        cv.put("data", item.getData());
        
        return this.getWritableDatabase().insert("qdg_item", null, cv) > 0;
    }
    
    public List<Item> getAll(){
        ArrayList<Item> ret = new ArrayList<>();
        
        try
        {
            Cursor cursor = this.getReadableDatabase().query("qdg_item", new String[] {"id", "valor", "data"}, null, null, null, null, null);
            
            while (cursor.moveToNext()){
                Item prox = new Item();
                prox.setId(cursor.getInt(0));
                prox.setValor(cursor.getDouble(1));
                prox.setTimestamp(cursor.getLong(2));
                ret.add(prox);
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        
        return ret;
    }
}
