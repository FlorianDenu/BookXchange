package com.denuinc.bookxchange.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

public class BookProvider extends ContentProvider {

    public static final String PROVIDER_NAME = "com.denuinc.bookxchange.db.BookProvider";
    private static final String URL = "content://" + PROVIDER_NAME + "/book";
    public static final Uri CONTENT_URI = Uri.parse(URL);

    public static final String _ID = "_id";
    public static final String GOOGLE_BOOK_ID = "id";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String IS_FAVORITE = "isFavorite";
    public static final String SMALL_THUMBNAIL = "smallThumbnail";
    public static final String THUMBNAIL = "thumbnail";
    public static final String BOOK_QUERY = "bookQuery";
    public static final String SEARCH_GOOGLE_BOOKS_ID = "googleBookId";
    public static final String TOTAL_COUNT = "totalCount";
    public static final String INDEX = "searchIndex";


    private static HashMap<String, String> BOOK_PROJECTION_MAP;

    private static final int BOOK = 1;
    private static final int BOOK_ID = 2;
    private static final int SEARCH = 3;

    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "book", BOOK);
        uriMatcher.addURI(PROVIDER_NAME, "books", BOOK_ID);
        uriMatcher.addURI(PROVIDER_NAME, "bookSearch", SEARCH);
    }

    private SQLiteDatabase db;
    private static final String DATABASE_NAME = "bookxchange";
    private static final String BOOK_TABLE_NAME = "book";
    private static final String BOOK_SEARCH_TABLE_NAME = "bookSearch";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_BOOK_TABLE =
            " CREATE TABLE IF NOT EXISTS " + BOOK_TABLE_NAME +
            " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            " id TEXT NOT NULL, " +
            " title TEXT NOT NULL, " +
            " description TEXT , " +
            " isFavorite TINYINT , " +
            " smallThumbnail TEXT, " +
            " thumbnail TEXT);";
    private static final String CREATE_BOOK_SEARCH_TABLE =
            " CREATE TABLE IF NOT EXISTS " + BOOK_SEARCH_TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " bookQuery TEXT NOT NULL, " +
                    " googleBookId TEXT NOT NULL, " +
                    " searchIndex INTEGER NOT NULL, " +
                    " totalCount INTEGER NOT NULL);";

    public BookProvider() { }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_BOOK_TABLE);
            db.execSQL(CREATE_BOOK_SEARCH_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + BOOK_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + BOOK_SEARCH_TABLE_NAME);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        com.denuinc.bookxchange.db.BookProvider.DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        db = dbHelper.getWritableDatabase();
        return db != null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (uriMatcher.match(uri)) {
            case BOOK:
                qb.setTables(BOOK_TABLE_NAME);
                qb.setProjectionMap(BOOK_PROJECTION_MAP);
                break;
            case BOOK_ID:
                qb.setTables(BOOK_TABLE_NAME);
//                qb.appendWhere(_ID + "=" + uri.getPathSegments().get(0));

                break;
            case SEARCH:
                qb.setTables(BOOK_SEARCH_TABLE_NAME);
                qb.setProjectionMap(BOOK_PROJECTION_MAP);
                break;
                default:
        }

        Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case BOOK:
                return "com.denuinc.bookxchange.vo.book";
            case BOOK_ID:
                return "com.denuinc.bookxchange.vo.book";
            case SEARCH:
                return "com.denuinc.bookxchange.vo.bookSearch";
                default:
                    throw  new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long rowId = 0;
        if (uriMatcher.match(uri) == SEARCH) {
            rowId = db.insert (BOOK_SEARCH_TABLE_NAME, "", values);
        } else {
            rowId = db.insert (BOOK_TABLE_NAME, "", values);
        }

        if (rowId > 0 ) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(uri, null);
            return _uri;
        }

        throw new SQLException("Failed to ladd new record into" + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case BOOK:
                count = db.delete(BOOK_TABLE_NAME, selection, selectionArgs);
                break;
            case BOOK_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete(BOOK_TABLE_NAME, _ID + " = " + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            case SEARCH:
                count = db.delete(BOOK_SEARCH_TABLE_NAME, selection, selectionArgs);
                break;
                default:
                    throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case BOOK:
                count = db.update(BOOK_TABLE_NAME, values, selection, selectionArgs);
                break;

            case BOOK_ID:
                count = db.update(BOOK_TABLE_NAME, values, _ID + " = " + uri.getPathSegments().get(1) +
                                (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
            case SEARCH:
                count = db.update(BOOK_SEARCH_TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count; }
}
