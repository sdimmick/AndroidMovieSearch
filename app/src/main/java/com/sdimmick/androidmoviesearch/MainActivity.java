package com.sdimmick.androidmoviesearch;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.sdimmick.androidmoviesearch.db.MovieSearchDbHelper;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private RecyclerView mRecyclerView;
    private MovieSearchAdapter mAdapter;
    private String mQuery;
    private BroadcastReceiver mDataChangedReceiver;
    private BroadcastReceiver mSearchResultsErrorReceiver;
    private boolean mIsFavoritesQuery;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_search:
                    mIsFavoritesQuery = false;
                    queryDatabase();
                    return true;
                case R.id.navigation_favorites:
                    mIsFavoritesQuery = true;
                    queryDatabase();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recycler_view);
        mAdapter = new MovieSearchAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mDataChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                queryDatabase();
            }
        };

        mSearchResultsErrorReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onError();
            }
        };

        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(mDataChangedReceiver, new IntentFilter(MovieSearchDbHelper.ACTION_SEARCH_RESLULTS_UPDATED));

        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(mSearchResultsErrorReceiver, new IntentFilter(MovieSearch.ACTION_ERROR_FETCHING_SEARCH_RESULTS));

        handleIntent(getIntent());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mDataChangedReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mSearchResultsErrorReceiver);
    }

    private void queryDatabase() {
        mAdapter.swapCursor(null);

        if (mIsFavoritesQuery) {
            getFavorites();
        } else {
            getAllSearchResults();
        }
    }

    private void onError() {
        Toast.makeText(this, "Error fetching search results!", Toast.LENGTH_SHORT).show();
    }

    private void getAllSearchResults() {
        new FetchSearchResultsFromDatabaseTask().execute(mQuery);
    }

    private void getFavorites() {
        new GetFavoritesFromDatabaseTask().execute();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d(TAG, "Query: " + query);

            mQuery = query;

            mAdapter.swapCursor(null);
            MovieSearchApplication.getMovieSearch().searchMovies(query);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    class FetchSearchResultsFromDatabaseTask extends AsyncTask<String, Void, Cursor> {

        @Override
        protected Cursor doInBackground(String... args) {
            String query = args[0];
            if (query == null || query.length() == 0) {
                return null;
            }

            return MovieSearchApplication.getDatabase().getSearchResultsForQuery(query);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            mAdapter.swapCursor(cursor);
        }
    }

    class GetFavoritesFromDatabaseTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... args) {
            return MovieSearchApplication.getDatabase().getFavorites();
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            mAdapter.swapCursor(cursor);
        }
    }
}
