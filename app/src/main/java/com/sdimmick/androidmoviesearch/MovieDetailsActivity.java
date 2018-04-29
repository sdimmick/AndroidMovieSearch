package com.sdimmick.androidmoviesearch;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdimmick.androidmoviesearch.db.MovieSearchContract;

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String KEY_SEARCH_RESULT_ID = "searchResultId";

    private ImageView mPosterImageView;
    private TextView mTitleTextView;
    private TextView mPlotTextView;

    public static void start(Context context, long searchResultId) {
        Intent intent = new Intent(context, MovieDetailsActivity.class);
        intent.putExtra(KEY_SEARCH_RESULT_ID, searchResultId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        mPosterImageView = findViewById(R.id.movie_poster);
        mTitleTextView = findViewById(R.id.movie_title);
        mPlotTextView = findViewById(R.id.movie_plot);

        new FetchMovieDetailsTask().execute();
    }

    private class FetchMovieDetailsTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            long searchResultId = getIntent().getLongExtra(KEY_SEARCH_RESULT_ID, -1);
            return MovieSearchApplication.getDatabase().getSearchResultById(searchResultId);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            try {
                if (cursor.moveToFirst()) {
                    String title = cursor.getString(cursor.getColumnIndex(MovieSearchContract.MovieSearchResult.COLUMN_NAME_TITLE));
                    String year = cursor.getString(cursor.getColumnIndex(MovieSearchContract.MovieSearchResult.COLUMN_NAME_YEAR));
                    String director = cursor.getString(cursor.getColumnIndex(MovieSearchContract.MovieSearchResult.COLUMN_NAME_DIRECTOR));

                    String titleText = String.format("%s (%s), by %s", title, year, director);

                    mTitleTextView.setText(titleText);
                    setTitle(title);

                    String plot = cursor.getString(cursor.getColumnIndex(MovieSearchContract.MovieSearchResult.COLUMN_NAME_PLOT_SUMMARY));
                    mPlotTextView.setText(plot);

                    String posterUrl = cursor.getString(cursor.getColumnIndex(MovieSearchContract.MovieSearchResult.COLUMN_NAME_POSTER));
                    GlideApp
                            .with(mPosterImageView)
                            .load(posterUrl)
                            .centerCrop()
                            .into(mPosterImageView);
                }
            } finally {
                cursor.close();
            }
        }
    }
}
