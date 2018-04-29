package com.sdimmick.androidmoviesearch.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.content.LocalBroadcastManager;

import com.sdimmick.androidmoviesearch.MovieSearchApplication;

import java.util.List;

public class MovieSearchDbHelper extends SQLiteOpenHelper {

    public static final String ACTION_SEARCH_RESLULTS_UPDATED = "searchResultsUpdated";

    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "MovieSearch.db";

    private static final String SQL_CREATE_SEARCH_RESULTS =
            "CREATE TABLE " + MovieSearchContract.MovieSearchResult.TABLE_NAME + " (" +
                    MovieSearchContract.MovieSearchResult._ID + " INTEGER PRIMARY KEY," +
                    MovieSearchContract.MovieSearchResult.COLUMN_NAME_TITLE + " TEXT," +
                    MovieSearchContract.MovieSearchResult.COLUMN_NAME_YEAR + " TEXT," +
                    MovieSearchContract.MovieSearchResult.COLUMN_NAME_IMDB_ID + " TEXT UNIQUE," +
                    MovieSearchContract.MovieSearchResult.COLUMN_NAME_POSTER + " TEXT," +
                    MovieSearchContract.MovieSearchResult.COLUMN_NAME_DIRECTOR + " TEXT," +
                    MovieSearchContract.MovieSearchResult.COLUMN_NAME_PLOT_SUMMARY + " TEXT," +
                    MovieSearchContract.MovieSearchResult.COLUMN_NAME_QUERY + " TEXT," +
                    MovieSearchContract.MovieSearchResult.COLUMN_NAME_IS_FAVORITE + " INTEGER DEFAULT 0)";

    private static final String SQL_DELETE_SEARCH_RESULTS =
            "DROP TABLE IF EXISTS " + MovieSearchContract.MovieSearchResult.TABLE_NAME;

    public MovieSearchDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_SEARCH_RESULTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_SEARCH_RESULTS);
        onCreate(db);
    }

    public void insertSearchResult(SearchResult searchResult) {
        searchResult.insert(getWritableDatabase());
        sendDataChangedBroadcast();
    }

    public void insertSearchResults(List<SearchResult> searchResults) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();
            for (SearchResult searchResult : searchResults) {
                searchResult.insert(db);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        sendDataChangedBroadcast();
    }

    public void updateByImdbId(String imdbId, ContentValues contentValues) {
        String selection = MovieSearchContract.MovieSearchResult.COLUMN_NAME_IMDB_ID + " = ?";
        String[] selectionArgs = new String[]{imdbId};

        SQLiteDatabase db = getWritableDatabase();
        db.update(MovieSearchContract.MovieSearchResult.TABLE_NAME, contentValues, selection, selectionArgs);

        sendDataChangedBroadcast();
    }

    public void toggleFavorite(long id, boolean isFavorite) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MovieSearchContract.MovieSearchResult.COLUMN_NAME_IS_FAVORITE, isFavorite);

        String selection = MovieSearchContract.MovieSearchResult._ID + " = ?";
        String[] selectionArgs = new String[]{Long.toString(id)};

        db.update(MovieSearchContract.MovieSearchResult.TABLE_NAME, values, selection, selectionArgs);

        sendDataChangedBroadcast();
    }

    private void sendDataChangedBroadcast() {
        Intent intent = new Intent();
        intent.setAction(ACTION_SEARCH_RESLULTS_UPDATED);

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(MovieSearchApplication.getInstance());
        broadcastManager.sendBroadcast(intent);
    }

    public Cursor getSearchResultsForQuery(String query) {
        String selection = String.format("%s = ? AND %s IS NOT NULL", MovieSearchContract.MovieSearchResult.COLUMN_NAME_QUERY, MovieSearchContract.MovieSearchResult.COLUMN_NAME_DIRECTOR);

        return getWritableDatabase().query(
                MovieSearchContract.MovieSearchResult.TABLE_NAME,
                null,
                selection,
                new String[]{query},
                null, null, null, null);
    }

    public Cursor getSearchResultById(long id) {
        String selection = String.format("%s = ?", MovieSearchContract.MovieSearchResult._ID);
        String[] selectionArgs = new String[]{Long.toString(id)};

        return getReadableDatabase().query(
                MovieSearchContract.MovieSearchResult.TABLE_NAME,
                null, selection, selectionArgs, null, null, null);
    }

    public Cursor getFavorites() {
        String selection = String.format("%s = 1", MovieSearchContract.MovieSearchResult.COLUMN_NAME_IS_FAVORITE);

        return getWritableDatabase().query(
                MovieSearchContract.MovieSearchResult.TABLE_NAME,
                null,
                selection,
                null, null, null, null);
    }
}
