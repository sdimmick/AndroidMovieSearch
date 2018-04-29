package com.sdimmick.androidmoviesearch;

import android.app.Application;

import com.sdimmick.androidmoviesearch.db.MovieSearchDbHelper;

public class MovieSearchApplication extends Application {
    private static MovieSearchApplication sInstance;
    private static MovieSearch sMovieSearch;
    private static MovieSearchDbHelper sDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        sMovieSearch = new MovieSearch(this);
        sDatabase = new MovieSearchDbHelper(this);
    }

    public static MovieSearchApplication getInstance() {
        return sInstance;
    }

    public static MovieSearch getMovieSearch() {
        return sMovieSearch;
    }

    public static MovieSearchDbHelper getDatabase() {
        return sDatabase;
    }

}
