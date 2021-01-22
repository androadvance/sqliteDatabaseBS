package com.example.sqlitedatabase.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import java.util.ArrayList;

public class SQLiteLogin extends SQLiteOpenHelper {

    SQLiteDatabase sqLiteDatabase;
    SQLiteLogin cxt;

    public SQLiteLogin(@Nullable Context context) {
        super(context, "seller.db", null, 1);

        sqLiteDatabase = getWritableDatabase();
        cxt = this;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table login(Email text,Password text,userid text,Name text)");

        db.execSQL("create table filter(Groups text,Category text,Styleno text,PcsPerBox text,Size text)");

        db.execSQL("create table ProductMaster(StyleItemName text   COLLATE NOCASE,size text   COLLATE NOCASE,pcsperbox text   COLLATE NOCASE," +
                "catGroup text COLLATE NOCASE,catName text COLLATE NOCASE,Sizeindex int COLLATE NOCASE)");

        db.execSQL("create table UserTempTable(userid text   COLLATE NOCASE,username text   COLLATE NOCASE,BuyerName text   COLLATE NOCASE," +
                "BuyerCode text COLLATE NOCASE,Name text COLLATE NOCASE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion) {
        String upgradeQuery = "ALTER TABLE login ADD COLUMN userid TEXT";
        if (newversion > oldversion)
            db.execSQL(upgradeQuery);
    }

    public long saveit(String s_email, String s_password, String userid, String Name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("Email", s_email);
        contentValues.put("Password", s_password);
        contentValues.put("userid", userid);
        contentValues.put("Name", Name);
        long k = sqLiteDatabase.insert("login", null, contentValues);
        return k;
    }

    public String getuser() {

        ArrayList<String> array_list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from login", null);
        res.moveToFirst();

        if (res.getCount() > 0)

            return (res.getString(res.getColumnIndex("Email")));

        return "";
    }

    public String getuserid() {

        ArrayList<String> array_list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from login", null);
        res.moveToFirst();

        if (res.getCount() > 0)

            return (res.getString(res.getColumnIndex("userid")));

        return "";
    }

    public String getusername() {

        ArrayList<String> array_list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from login", null);
        res.moveToFirst();

        if (res.getCount() > 0)

            return (res.getString(res.getColumnIndex("Name")));

        return "";
    }

    public long insertGroup(String type) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("Groups", type);
        long k = sqLiteDatabase.insert("filter", null, contentValues);
        return k;
    }

    public String deleteGroup(String type) {
        sqLiteDatabase.execSQL("delete from filter where Groups='" + type + "'");
        return "ok";
    }

    public long insertCat(String type) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("Category", type);
        long k = sqLiteDatabase.insert("filter", null, contentValues);
        return k;
    }

    public String deleteCat(String type) {
        sqLiteDatabase.execSQL("delete from filter where Category='" + type + "'");
        return "ok";
    }

    public long insertStyleNo(String type) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("Styleno", type);
        long k = sqLiteDatabase.insert("filter", null, contentValues);
        return k;
    }

    public String deleteStyleNo(String type) {
        sqLiteDatabase.execSQL("delete from filter where Styleno='" + type + "'");
        return "ok";
    }

    public long insertPcsPerBox(String type) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("PcsPerBox", type);
        long k = sqLiteDatabase.insert("filter", null, contentValues);
        return k;
    }

    public String deletePcsPerBox(String type) {
        sqLiteDatabase.execSQL("delete from filter where PcsPerBox='" + type + "'");
        return "ok";
    }

    public long insertSize(String type) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("Size", type);
        long k = sqLiteDatabase.insert("filter", null, contentValues);
        return k;
    }

    public String deleteSize(String type) {
        sqLiteDatabase.execSQL("delete from filter where Size='" + type + "'");
        return "ok";
    }

    public void deleteFilter() {
        sqLiteDatabase.execSQL("delete from filter");
    }

    public Cursor fetchListitems() {
        Cursor cursor;
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM filter", new String[]{});
        cursor.moveToFirst();
        return cursor;
    }

    public Cursor fetchCheckedGroup(String type) {
        Cursor cursor;
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM filter WHERE Groups = ?", new String[]{type});
        cursor.moveToFirst();
        return cursor;
    }

    public Cursor fetchCheckedCatName(String type) {
        Cursor cursor;
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM filter WHERE Category = ?", new String[]{type});
        cursor.moveToFirst();
        return cursor;
    }

    public Cursor fetchCheckedPcsPerBox(String type) {
        Cursor cursor;
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM filter WHERE PcsPerBox = ?", new String[]{type});
        cursor.moveToFirst();
        return cursor;
    }

    public Cursor fetchCheckedSize(String type) {
        Cursor cursor;
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM filter WHERE Size = ?", new String[]{type});
        cursor.moveToFirst();
        return cursor;
    }

    public Cursor fetchCheckedStyleno(String type) {
        Cursor cursor;
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM filter WHERE Styleno = ?", new String[]{type});
        cursor.moveToFirst();
        return cursor;
    }

    public boolean insertAllProductMaster(String StyleItemName, String Size, String PcspPerbox, String Catgroup, String CatName, String Sizeindex) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("StyleItemName", StyleItemName);
        contentValues.put("size", Size);
        contentValues.put("pcsperbox", PcspPerbox);
        contentValues.put("catGroup", Catgroup);
        contentValues.put("catName", CatName);
        contentValues.put("Sizeindex", Sizeindex);
        try {
            db.insert("ProductMaster", null, contentValues);
            db.close();
            return true;
        } catch (SQLiteConstraintException er) {
            return false;
        }
    }

    public Cursor getCatMasterCount() {
        Cursor cursor;
        cursor = sqLiteDatabase.rawQuery("SELECT count(StyleItemName)as Count FROM ProductMaster", new String[]{});
        cursor.moveToFirst();
        return cursor;
    }

    public int CursorCount(String SQL) {

        Cursor cursor;
        cursor = sqLiteDatabase.rawQuery(SQL, new String[]{});
        cursor.moveToFirst();
        return cursor.getCount();
    }

    public void deleteAllCatMaster() {
        sqLiteDatabase.execSQL("delete from ProductMaster");
    }

    public Cursor DynamicQuery(String Type) {
        Cursor cursor;
        String SQL = "";

        if (Type.equalsIgnoreCase("Group") ) {
            SQL = "SELECT distinct catGroup as list FROM ProductMaster where catGroup is not null  ";
        }
        if (Type.equalsIgnoreCase("Category")) {
            SQL = "SELECT distinct catName as list FROM ProductMaster where  catName is not null ";
        }
        if (Type.equalsIgnoreCase("PCSPerBox")) {
            SQL = "SELECT distinct pcsperbox as list FROM ProductMaster where pcsperbox is not null ";
        }
        if (Type.equalsIgnoreCase("Size")) {
            SQL = "SELECT distinct size as list FROM ProductMaster where size is not null  ";
        }
        if (Type.equalsIgnoreCase("StyleNo & ItemName")) {
            SQL = "SELECT distinct StyleItemName as list FROM ProductMaster where  StyleItemName is not null ";
        }


        int GroupsFilterCount = CursorCount("select Groups from filter where Groups is not null");
        if (GroupsFilterCount > 0  & !Type.equalsIgnoreCase("Group")) {
            SQL = SQL + "  and catGroup in(select Groups from filter) ";
        }


        int CategoryFilterCount = CursorCount("select Category from filter where Category is not null");
        if (CategoryFilterCount > 0  & !Type.equalsIgnoreCase("Category")) {
            SQL = SQL + "  and catName in(select Category from filter) ";
        }

        int pcsperboxFilterCount = CursorCount("select pcsperbox from filter where pcsperbox is not null");
        if (pcsperboxFilterCount > 0  & !Type.equalsIgnoreCase("PCSPerBox")) {
            SQL = SQL + "  and pcsperbox in(select pcsperbox from filter) ";
        }


        int SizeFilterCount = CursorCount("select size from filter where size is not null");
        if (SizeFilterCount > 0  & !Type.equalsIgnoreCase("Size")) {
            SQL = SQL + "  and Size in(select size from filter) ";
        }

        int StyleItemNameFilterCount = CursorCount("select Styleno from filter where Styleno is not null");
        if (StyleItemNameFilterCount > 0  & !Type.equalsIgnoreCase("StyleNo & ItemName")) {
            SQL = SQL + "  and StyleItemName in(select Styleno from filter) ";
        }

        cursor = sqLiteDatabase.rawQuery(SQL, new String[]{});
        cursor.moveToFirst();
        return cursor;
    }

    public String deleteLogin() {
        sqLiteDatabase.execSQL("delete from login");
        return "ok";
    }

    public long saveTempTable(String s_userid, String s_username, String s_BuyerName, String s_BuyerCode,String s_Name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("userid", s_userid);
        contentValues.put("username", s_username);
        contentValues.put("BuyerName", s_BuyerName);
        contentValues.put("BuyerCode", s_BuyerCode);
        contentValues.put("Name", s_Name);
        long k = sqLiteDatabase.insert("UserTempTable", null, contentValues);
        return k;
    }

    public Cursor TempCount() {
        Cursor cursor;
        cursor = sqLiteDatabase.rawQuery("SELECT count(BuyerCode)as Count FROM UserTempTable", new String[]{});
        cursor.moveToFirst();
        return cursor;
    }

    public Cursor getTempData() {
        Cursor cursor;
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM UserTempTable", new String[]{});
        cursor.moveToFirst();
        return cursor;
    }

    public String deleteTempData() {
        sqLiteDatabase.execSQL("delete from UserTempTable");
        return "ok";
    }
}
