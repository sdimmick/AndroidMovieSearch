package com.sdimmick.androidmoviesearch.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONObject;

// POJO w/ builder pattern to hold parsed movie search results
public class SearchResult {
    private String title;
    private String year;
    private String imdbId;
    private String poster;
    private String director;
    private String plotSummary;
    private String query;
    private boolean isFavorite;

    public static SearchResult fromResponse(JSONObject response, String query) {
        return new SearchResult()
                .setTitle(response.optString("Title"))
                .setYear(response.optString("Year"))
                .setImdbId(response.optString("imdbID"))
                .setPoster(response.optString("Poster"))
                .setDirector(response.optString("Director"))
                .setPlotSummary(response.optString("Plot"))
                .setQuery(query);
    }

    public SearchResult setTitle(String title) {
        this.title = title;
        return this;
    }

    public SearchResult setYear(String year) {
        this.year = year;
        return this;
    }

    public SearchResult setImdbId(String imdbId) {
        this.imdbId = imdbId;
        return this;
    }

    public SearchResult setPoster(String poster) {
        this.poster = poster;
        return this;
    }

    public SearchResult setDirector(String director) {
        this.director = director;
        return this;
    }

    public SearchResult setPlotSummary(String plotSummary) {
        this.plotSummary = plotSummary;
        return this;
    }

    public SearchResult setQuery(String query) {
        this.query = query;
        return this;
    }

    public SearchResult setFavorite(boolean favorite) {
        isFavorite = favorite;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getImdbId() {
        return imdbId;
    }

    public String getPoster() {
        return poster;
    }

    public String getDirector() {
        return director;
    }

    public String getPlotSummary() {
        return plotSummary;
    }

    public String getQuery() {
        return query;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public long insert(SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        values.put(MovieSearchContract.MovieSearchResult.COLUMN_NAME_TITLE, title);
        values.put(MovieSearchContract.MovieSearchResult.COLUMN_NAME_YEAR, year);
        values.put(MovieSearchContract.MovieSearchResult.COLUMN_NAME_IMDB_ID, imdbId);
        values.put(MovieSearchContract.MovieSearchResult.COLUMN_NAME_POSTER, poster);
        values.put(MovieSearchContract.MovieSearchResult.COLUMN_NAME_QUERY, query);
        values.put(MovieSearchContract.MovieSearchResult.COLUMN_NAME_DIRECTOR, director);
        values.put(MovieSearchContract.MovieSearchResult.COLUMN_NAME_PLOT_SUMMARY, plotSummary);
        values.put(MovieSearchContract.MovieSearchResult.COLUMN_NAME_IS_FAVORITE, isFavorite);

        return db.insertWithOnConflict(MovieSearchContract.MovieSearchResult.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }
}
