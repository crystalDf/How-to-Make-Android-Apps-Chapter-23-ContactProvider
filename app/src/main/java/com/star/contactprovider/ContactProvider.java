package com.star.contactprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class ContactProvider extends ContentProvider {

    private static final String TABLE_NAME = "cpcontacts";

    private static final String AUTHORITY = "com.star.contactprovider.provider";
    private static final String PATH = TABLE_NAME;
    private static final String SCHEME = "content://";
    static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + "/" + PATH);

    static final String COLUMN_ID = "_id";
    static final String COLUMN_NAME = "name";

    private static final int CONTACTS_DIR_CODE = 0;
    private static final int CONTACTS_ITEM_CODE = 1;

    private static final UriMatcher sUriMatcher;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, PATH, CONTACTS_DIR_CODE);
    }

    private SQLiteDatabase mSQLiteDatabase;
    private static final String DATABASE_NAME = "myContacts";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
            + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NAME + " TEXT NOT NULL"
            + ");";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    @Override
    public boolean onCreate() {
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext(), DATABASE_NAME,
                null, DATABASE_VERSION);

        mSQLiteDatabase = databaseHelper.getWritableDatabase();

        if (mSQLiteDatabase != null) {
            return true;
        }

        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();

        sqLiteQueryBuilder.setTables(TABLE_NAME);

        switch (sUriMatcher.match(uri)) {
            case CONTACTS_DIR_CODE:
                sqLiteQueryBuilder.setProjectionMap(null);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Cursor cursor = sqLiteQueryBuilder.query(mSQLiteDatabase, projection,
                selection, selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case CONTACTS_DIR_CODE:
                return "vnd.android.cursor.dir/cpcontacts";

            default:
                throw new IllegalArgumentException("Unsupported URI " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowId = mSQLiteDatabase.insert(TABLE_NAME, null, values);

        if (rowId > 0) {
            Uri newUri = ContentUris.withAppendedId(uri, rowId);

            getContext().getContentResolver().notifyChange(newUri, null);

            return newUri;
        } else {
            Toast.makeText(getContext(), "Row Insert Failed", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted = 0;

        switch (sUriMatcher.match(uri)) {
            case CONTACTS_DIR_CODE:
                rowsDeleted = mSQLiteDatabase.delete(TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rowsUpdated = 0;

        switch (sUriMatcher.match(uri)) {
            case CONTACTS_DIR_CODE:
                rowsUpdated = mSQLiteDatabase.update(TABLE_NAME, values, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name,
                              SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_TABLE);
            onCreate(db);
        }
    }

}
