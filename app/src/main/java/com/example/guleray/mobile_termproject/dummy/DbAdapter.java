package com.example.guleray.mobile_termproject.dummy;

        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.SQLException;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.util.Log;

/**
 * Created by MUH-ENG on 12/9/2015.
 */
public class DbAdapter {
    static final String KEY_ROWID = "id";
    static final String KEY_MIDTERM = "midterm";
    static final String TAG = "DBAdapter";

    static final String DATABASE_NAME = "ACCOUNT_DB";
    static final String DATABASE_TABLE = "";

    static final String DB_TB_SAVED_ACCOUNTS = "SAVED_ACCOUNTS";
    static final String DB_COL_SA_IBAN = "IBAN";
    static final String DB_COL_SA_DESC = "desc";
    static final String DB_CR_SAVED_ACCOUNTS = "CREATE TABLE " + DB_TB_SAVED_ACCOUNTS +
            "(IBAN varchar(100) primary key, desc varchar(50))";


    static final int DATABASE_VERSION = 2;

    static final String DATABASE_CREATE =
            "create table students(id integer primary key, "
                    + "midterm integer not null);";

    final Context context;

    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public DbAdapter(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            try
            {
                // Try to create tables

                db.execSQL(DB_CR_SAVED_ACCOUNTS);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS contacts");
            onCreate(db);
        }
    }

    //---opens the database---
    public DbAdapter open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---
    public void close()
    {
        DBHelper.close();
    }

    public long insertNewSavedIBAN(String iban,String desc)
    {
        try
        {
            ContentValues initialValues = new ContentValues();
            initialValues.put(DB_COL_SA_IBAN,iban);
            initialValues.put(DB_COL_SA_DESC, desc);
            return db.insert(DB_TB_SAVED_ACCOUNTS, null, initialValues);
        }
        catch(Exception x)
        {
            return -1;
        }
    }

    public Cursor getAllSavedIBANS()
    {
        return db.query(DB_TB_SAVED_ACCOUNTS,
                new String[] {DB_COL_SA_IBAN, DB_COL_SA_DESC}, null, null, null, null, null);
    }

    public boolean deleteSavedIBAN(String iban)
    {
        return db.delete(DB_TB_SAVED_ACCOUNTS, DB_COL_SA_IBAN + "='" + iban + "'", null) > 0;
    }



    //---retrieves a particular contact---
    public Cursor getContact(long rowId) throws SQLException
    {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                                KEY_MIDTERM}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---updates a contact---
    public boolean updateContact(long rowId, int midterm)
    {
        ContentValues args = new ContentValues();
        args.put(KEY_MIDTERM, midterm);
        return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }


}

