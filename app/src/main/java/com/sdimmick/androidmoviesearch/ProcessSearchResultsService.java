package com.sdimmick.androidmoviesearch;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sdimmick.androidmoviesearch.db.MovieSearchContract;
import com.sdimmick.androidmoviesearch.db.SearchResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProcessSearchResultsService extends IntentService {
    private static final String NAME = "ProcessSearchResultsService";
    private static final String TAG = "SearchResultsService";
    private static final String ACTION_PROCESS_SEARCH_RESULTS = "com.sdimmick.androidmoviesearch.action.PROCESS_SEARCH_RESULTS";
    private static final String ACTION_PROCESS_MOVIE_DETAILS = "com.sdimmick.androidmoviesearch.action.PROCESS_MOVIE_DETAILS";
    private static final String KEY_SEARCH_RESULTS = "searchResults";
    private static final String KEY_MOVIE_DETAILS = "movieDetails";
    private static final String KEY_QUERY = "query";

    public static void processSearchResults(Context context, PendingSearchResult result) {
        Intent intent = new Intent(context, ProcessSearchResultsService.class);
        intent.setAction(ACTION_PROCESS_SEARCH_RESULTS);
        intent.putExtra(KEY_SEARCH_RESULTS, result);
        context.startService(intent);
    }

    public static void processDetailsResult(Context context, String result, String query) {
        Intent intent = new Intent(context, ProcessSearchResultsService.class);
        intent.setAction(ACTION_PROCESS_MOVIE_DETAILS);
        intent.putExtra(KEY_MOVIE_DETAILS, result);
        intent.putExtra(KEY_QUERY, query);
        context.startService(intent);
    }

    public ProcessSearchResultsService() {
        super(NAME);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent: " + intent.getAction());
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PROCESS_SEARCH_RESULTS.equals(action)) {
                PendingSearchResult result = (PendingSearchResult) intent.getSerializableExtra(KEY_SEARCH_RESULTS);
                processSearchResults(result);
            } else if (ACTION_PROCESS_MOVIE_DETAILS.equals(action)) {
                String details = intent.getStringExtra(KEY_MOVIE_DETAILS);
                String query = intent.getStringExtra(KEY_QUERY);
                processMovieDetails(details, query);
            }
        }
    }

    protected ContentValues getContentValuesForResult(String result, String query) throws JSONException {
        JSONObject detailsJson = new JSONObject(result);
        SearchResult searchResult = SearchResult.fromResponse(detailsJson, query);

        ContentValues cv = new ContentValues();
        cv.put(MovieSearchContract.MovieSearchResult.COLUMN_NAME_PLOT_SUMMARY, searchResult.getPlotSummary());
        cv.put(MovieSearchContract.MovieSearchResult.COLUMN_NAME_DIRECTOR, searchResult.getDirector());

        return cv;
    }

    private void processMovieDetails(String result, String query) {
        Log.d(TAG, "Processing movie details result: " + result);
        try {
            ContentValues contentValues = getContentValuesForResult(result, query);
            String imdbId = contentValues.getAsString(MovieSearchContract.MovieSearchResult.COLUMN_NAME_IMDB_ID);

            MovieSearchApplication.getDatabase().updateByImdbId(imdbId, contentValues);
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing movie details", e);
        }
    }

    protected List<SearchResult> parseSearchResults(PendingSearchResult pendingResult) throws JSONException {
        JSONObject resultsJson = new JSONObject(pendingResult.results);
        JSONArray resultsArray = resultsJson.optJSONArray("Search");
        if (resultsArray == null) {
            Log.w(TAG, "No search results found in response");
            MovieSearch.sendSearchResultsErrorBroadcast(MovieSearchApplication.getInstance());
            return null;
        }

        List<SearchResult> parsedSearchResults = new ArrayList<>();
        for (int i = 0; i < resultsArray.length(); i++) {
            JSONObject result = resultsArray.getJSONObject(i);
            SearchResult sr = SearchResult.fromResponse(result, pendingResult.query);
            parsedSearchResults.add(sr);

            MovieSearchApplication.getMovieSearch().getMovieDetails(sr.getImdbId(), pendingResult.query);
        }

        return parsedSearchResults;
    }

    private void processSearchResults(PendingSearchResult pendingResult) {
        Log.d(TAG, "processSearchResults: " + pendingResult);

        try {
            List<SearchResult> parsedSearchResults = parseSearchResults(pendingResult);
            if (parsedSearchResults == null) {
                return;
            }
            MovieSearchApplication.getDatabase().insertSearchResults(parsedSearchResults);
            Log.d(TAG, "Processed " + parsedSearchResults.size() + " results");
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing search results", e);
        }
    }

    static class PendingSearchResult implements Serializable {
        private final String query;
        private final String results;

        public PendingSearchResult(String query, String results) {
            this.query = query;
            this.results = results;
        }
    }
}
