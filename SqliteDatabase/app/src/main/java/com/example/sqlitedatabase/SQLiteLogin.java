package com.example.sqlitedatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SQLiteLogin extends SQLiteOpenHelper {

    SQLiteDatabase sqLiteDatabase;
    SQLiteLogin cxt;

    public SQLiteLogin(@Nullable Context context) {
        super(context, "Seller.db", null, 1);

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

    public long insertversion(String s_AppName,String s_Version,String s_Status,String s_UploadedDate,String s_ExpiryDate,String s_AuditUser,String s_AuditDate){

        ContentValues contentValues = new ContentValues();
        contentValues.put("AppName",s_AppName);
        contentValues.put("Version",s_Version);
        contentValues.put("Status",s_Status);
        contentValues.put("UploadedDate",s_UploadedDate);
        contentValues.put("ExpiryDate",s_ExpiryDate);
        contentValues.put("AuditUser",s_AuditUser);
        contentValues.put("AuditDate",s_AuditDate);
        long k = sqLiteDatabase.insert("AndroidAppVersion",null,contentValues);
        return k;
    }


    public Cursor checkAppVersion(String s_AppName,String s_Version){

        Cursor cursor;
        SQLiteDatabase sqlitedatabase = this.getReadableDatabase();
        cursor = sqlitedatabase.rawQuery("SELECT        AppName, Version, Status, Remarks, Description, UpLoadedDate, ExpiryDate, AuditUser, AuditDate, CASE WHEN ExpiryDate > datetime('now') \n" +
                "THEN 'true' ELSE 'false' END AS Life, JulianDay(ExpiryDate) - JulianDay(datetime('now')) AS ExpireDayCount\n" +
                "FROM AndroidAppVersion\n" +
                "WHERE        AppName = ? AND Version = ?", new String[]{s_AppName,s_Version});
        cursor.moveToFirst();
        return cursor;
    }

    public long saveEmpid(String s_email,String s_password,String s_IMEINO){

        ContentValues contentValues = new ContentValues();
        contentValues.put("Username",s_email);
        contentValues.put("Password",s_password);
        contentValues.put("IMEINO",s_IMEINO);
        long k = sqLiteDatabase.insert("RFIDLogin",null,contentValues);
        return k;
    }


    public String checkUsers(String sUserName, String sPassword) {

        Cursor cursor;
        cursor=sqLiteDatabase.query("RFIDLogin",null,"Username=?",new String[]{sUserName},null,null,null);

        if (cursor != null){
            if (cursor.moveToFirst()){

                String temp=cursor.getString(cursor.getColumnIndex("Password"));

                if(sPassword.equals(temp)){
                    return "1";
                }else{
                    return "0";
                }
            }
            cursor.close();
        }
        return "";
    }


    public long saveEmpDetail(String s_email,String s_LineNumber, String s_Unitname, String s_IMEI,String s_Auditdate){

        ContentValues contentValues = new ContentValues();
        contentValues.put("Username",s_email);
        contentValues.put("LineNo",s_LineNumber);
        contentValues.put("Unit",s_Unitname);
        contentValues.put("IMEINO",s_IMEI);
        contentValues.put("AuditDate",s_Auditdate);
        long k = sqLiteDatabase.insert("LineWiseSystemIp",null,contentValues);
        return k;
    }

    public  Cursor Directlogin(String s_IMEINo){
        Cursor cursor;
        SQLiteDatabase sqlitedatabase = this.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery("SELECT Username,IMEINO FROM RFIDLogin WHERE IMEINO = ?",new String[]{s_IMEINo});
        cursor.moveToFirst();
        return cursor;
    }


    public long insertLineIssue(String s_Email,String s_Issueno, String s_Cutinwno, String s_Issuedate,String s_Prodline,String s_Comprefno,String s_Mstyleno,String s_Color,String s_Size,String s_Bundleno,String s_IssueQty,String s_BalanceFGQty,String s_Eancode,String s_RFCode){
        ContentValues contentValues = new ContentValues();
        contentValues.put("UnitName",s_Email);
        contentValues.put("Issueno",s_Issueno);
        contentValues.put("CutInwno",s_Cutinwno);
        contentValues.put("IssueDate",s_Issuedate);
        contentValues.put("ProdLine",s_Prodline);
        contentValues.put("Comprefno",s_Comprefno);
        contentValues.put("Mstyleno",s_Mstyleno);
        contentValues.put("Color",s_Color);
        contentValues.put("Size",s_Size);
        contentValues.put("Bundleno",s_Bundleno);
        contentValues.put("IssueQty",s_IssueQty);
        contentValues.put("BalanceFGQty",s_BalanceFGQty);
        contentValues.put("eancode",s_Eancode);
        contentValues.put("RFcode",s_RFCode);
        long k = sqLiteDatabase.insert("RFIDLineIssue",null,contentValues);
        return k;
    }


    public long insertRFITScan(String s_RFCode,String s_Eancode,String s_Bundleno,String s_Styleno,String s_Refno,String s_Color,String s_Size,String s_LineNumber,String s_ReadType,String s_DefectType,String s_DefectSubType,String s_DefectZone,String s_ScanDatetime,String s_AduitUser,String s_AuditDate,Integer s_Sync,String s_UnitName){
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        ContentValues contentValues = new ContentValues();
        contentValues.put("RFCode",s_RFCode);
        contentValues.put("Eancode",s_Eancode);
        contentValues.put("Bundleno",s_Bundleno);
        contentValues.put("Styleno",s_Styleno);
        contentValues.put("Refno",s_Refno);
        contentValues.put("Color",s_Color);
        contentValues.put("Size",s_Size);
        contentValues.put("UnitName",s_UnitName);
        contentValues.put("LineNumber",s_LineNumber);
        contentValues.put("ReadType",s_ReadType);
        contentValues.put("DefectType",s_DefectType);
        contentValues.put("DefectSubType",s_DefectSubType);
        contentValues.put("DefectZone",s_DefectZone);
        contentValues.put("ScanDatetime",currentDateandTime);
        contentValues.put("AuditUser",s_AduitUser);
        contentValues.put("AuditDate",currentDateandTime);
        contentValues.put("Sync",s_Sync);
        long k = sqLiteDatabase.insert("RFIDProScan",null,contentValues);
        return k;
    }

    public  Cursor fetchData(){

        Cursor cursor;
        cursor=sqLiteDatabase.query("LineWiseSystemIp",null,"IMEINO",new String[]{},null,null,null);
        cursor.moveToFirst();
        return cursor;
    }


    public  Cursor fetchunit(String s_IMEI){

        Cursor cursor;
        cursor=sqLiteDatabase.query("LineWiseSystemIp",null,"IMEINO=?",new String[]{s_IMEI},null,null,null);
        cursor.moveToFirst();
        return cursor;
    }

    public  Cursor fetchEANno(String s_RFcode){

        Cursor cursor;
        cursor=sqLiteDatabase.rawQuery("select eancode from RFIDLineIssue where RFCode=?   COLLATE NOCASE",new String[]{s_RFcode});
        cursor.moveToFirst();
        return cursor;
    }

    public  Cursor fetchEanDetail(String s_RFcode,String s_Prodline,String s_Unitname,String s_eancode){

        Cursor cursor;
        SQLiteDatabase sqlitedatabase = this.getReadableDatabase();

        cursor = sqlitedatabase.rawQuery("SELECT Mstyleno,Color,Size,Bundleno,Comprefno FROM RFIDLineIssue \n" +
                "WHERE RFCode = ?  COLLATE NOCASE AND ProdLine = ? COLLATE NOCASE AND UnitName = ? COLLATE NOCASE AND eancode = ? COLLATE NOCASE", new String[]{s_RFcode,s_Prodline,s_Unitname,s_eancode});

        cursor.moveToFirst();
        return cursor;
    }


    public Cursor checkRFCode(String s_RFcode,String readtype,String bundle) {

        Cursor cursor;
        if(readtype=="") {
            cursor = sqLiteDatabase.rawQuery("SELECT * FROM RFIDProScan \n" +
                    "WHERE RFCode = ? Collate nocase and bundleno=? COLLATE NOCASE ", new String[]{s_RFcode,bundle});
        }else {
            cursor = sqLiteDatabase.rawQuery("SELECT * FROM RFIDProScan \n" +
                    "WHERE RFCode = ? COLLATE NOCASE AND ReadType = ? COLLATE NOCASE and bundleno=? COLLATE NOCASE ", new String[]{s_RFcode, readtype,bundle});
        }
        cursor.moveToFirst();
        return cursor;
    }


    public  Cursor fetchAllDetailCount(){

        Cursor cursor;
        SQLiteDatabase sqlitedatabase = this.getReadableDatabase();
        cursor = sqlitedatabase.rawQuery(" \n" +
                "              select a.LineNo,a.Unit,OK,RewOk,RewoldOk,Rew,Rejok,Rejoldok,Rej,TotQty ,cast(TotQty as REAL),\n" +
                "\t\t\t  cast(cast(OK as REAL)/cast(TotQty as REAL) as  REAL)  asd\n" +
                "\t\t\t  ,\n" +
                "               \n" +
                "               cast(cast(cast(OK as REAL)/cast(TotQty as REAL) as REAL)*100 as decimal(18,2))   OKPercentage,\n" +
                "               \n" +
                "               cast(cast(cast(Rew as REAL)/cast(TotQty as REAL) as REAL)*100 as decimal(18,2))   ReworkPercentage,\n" +
                "               \n" +
                "               cast(cast(cast(RewOk as REAL)/cast(TotQty as REAL) as REAL)*100 as decimal(18,2))   ReworkOPPercentage,\n" +
                "               cast(cast(cast(RewoldOk as REAL)/cast(TotQty as REAL) as REAL)*100 as decimal(18,2))   ReworkOldOPPercentage,\n" +
                "               cast(cast(cast(Rej as REAL)/cast(TotQty as REAL) as REAL)*100  as decimal(18,2))  RejnPercentage,\n" +
                "               \n" +
                "               cast(cast(cast(Rejok as REAL)/cast(TotQty as REAL) as REAL)*100  as decimal(18,2))  RejnOPPercentage,\n" +
                "               cast(cast(cast(Rejoldok as REAL)/cast(TotQty as REAL) as REAL)*100  as decimal(18,2))  RejnOldOPPercentage,\n" +
                "               cast(cast(cast(TotQty as REAL)/cast(TotQty as REAL) as REAL)*100 as decimal(18,2))  TotalPercentage\n" +
                "               \n" +
                "               from (\n" +
                "               select * from  LineWiseSystemIP \n" +
                "               )as a \n" +
                "               \n" +
                "                JOIN (\n" +
                "               \n" +
                "               SELECT coalesce(count(distinct RFCode),0)OK FROM  RFIDProScan where readtype='OK' \n" +
                "               and  date(ScanDatetime)=date()\n" +
                "               \n" +
                "               )ok  \n" +
                "               \n" +
                "                JOIN (\n" +
                "               SELECT coalesce(count(distinct RFCode),0)RewOk FROM RFIDProScan where readtype='OK' and date(ScanDatetime)=date()\n" +
                "               and RFCode in (SELECT RFCode FROM RFIDProScan where readtype='REWORK' and date(ScanDatetime)=date())  COLLATE nocase \n" +
                "               )Rwok\n" +
                "                JOIN (\n" +
                "               \n" +
                "               SELECT coalesce(count(distinct RFCode),0)RewoldOk FROM RFIDProScan where readtype='OK' and date(ScanDatetime)=date()\n" +
                "               and RFCode in (SELECT RFCode FROM RFIDProScan where readtype='REWORK' and date(ScanDatetime)<>date())  COLLATE nocase\n" +
                "               )Rwoldok \n" +
                "               \n" +
                "                JOIN (\n" +
                "               \n" +
                "               SELECT coalesce(count(distinct RFCode),0)Rew FROM RFIDProScan where readtype='REWORK' and date(ScanDatetime)=date()\n" +
                "               )Rw\n" +
                "                JOIN (\n" +
                "               \n" +
                "               SELECT coalesce(count(distinct RFCode),0)Rejok FROM RFIDProScan where readtype='OK' and date(ScanDatetime)=date()\n" +
                "               and RFCode in (SELECT RFCode FROM RFIDProScan where readtype='REJECTION' and date(ScanDatetime)=date())  COLLATE nocase\n" +
                "               )Rejok\n" +
                "                JOIN (\n" +
                "               \n" +
                "               SELECT coalesce(count(distinct RFCode),0)Rejoldok FROM RFIDProScan where readtype='OK' and date(ScanDatetime)=date()\n" +
                "               and RFCode in (SELECT RFCode FROM RFIDProScan where readtype='REJECTION' and date(ScanDatetime)<>date())  COLLATE nocase\n" +
                "               )Rejoldok\n" +
                "                JOIN (\n" +
                "               \n" +
                "               SELECT coalesce(count(distinct RFCode),0)Rej FROM RFIDProScan where readtype='REJECTION' and date(ScanDatetime)=date()\n" +
                "               )Rej\n" +
                "                JOIN (\n" +
                "               \n" +
                "               SELECT coalesce(count(distinct RFCode),0)TotQty FROM RFIDProScan where date(ScanDatetime)=date()\n" +
                "               )TotQty", new String[]{});
        cursor.moveToFirst();
        return cursor;
    }


    public void updateSyncRF(String rfcode,String  Bundleno,String Refno,String ReadType,String LineNumber,String UnitName) {
        sqLiteDatabase.execSQL("update RFIDProScan set Sync=1 where Sync=0 and rfcode='"+rfcode+"' and Bundleno='"+Bundleno+"' and Refno='"+Refno+"' and ReadType='"+ReadType+"' and LineNumber='"+LineNumber+"' and UnitName='"+UnitName+"'");
    }

    public Cursor fetchNotSyncRF() {
        SQLiteDatabase sqlitedatabase = this.getReadableDatabase();
        Cursor cursor;
        cursor=sqLiteDatabase.rawQuery("SELECT * FROM RFIDProScan where Sync=0",null);
        return cursor;
    }

    public void u_BluetoothMac(String IMEI,String BluetoothMac) {
        sqLiteDatabase.execSQL("update LineWiseSystemIp set BluetoothMac='"+BluetoothMac+"' where IMEIno='"+IMEI+"'");
    }
}

