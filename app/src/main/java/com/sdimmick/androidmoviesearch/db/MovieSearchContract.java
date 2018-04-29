package com.sdimmick.androidmoviesearch.db;

import android.provider.BaseColumns;

public class MovieSearchContract {
    private MovieSearchContract() {
    }

    public static class MovieSearchResult implements BaseColumns {
        public static final String TABLE_NAME = "search_result";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_YEAR = "year";
        public static final String COLUMN_NAME_IMDB_ID = "imdb_id";
        public static final String COLUMN_NAME_POSTER = "poster";
        public static final String COLUMN_NAME_DIRECTOR = "director";
        public static final String COLUMN_NAME_PLOT_SUMMARY = "column_name_plot_summary";
        public static final String COLUMN_NAME_QUERY = "query_term";
        public static final String COLUMN_NAME_IS_FAVORITE = "is_favorite";
    }
}
