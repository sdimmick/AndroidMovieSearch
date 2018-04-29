package com.sdimmick.androidmoviesearch;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MovieSearch {
    public static final String ACTION_ERROR_FETCHING_SEARCH_RESULTS = "ACTION_ERROR_FETCHING_SEARCH_RESULTS";

    private static final String TAG = "MovieSearch";
    private static final String API_KEY = "d04ba374";
    private static final Uri BASE_URL = Uri.parse("http://www.omdbapi.com")
            .buildUpon()
            .appendQueryParameter("apikey", API_KEY)
            .build();

    private final RequestQueue mRequestQueue;
    private final Object mRequestTag = new Object();

    public MovieSearch(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
    }

    public static void sendSearchResultsErrorBroadcast(Context context) {
        Intent intent = new Intent(ACTION_ERROR_FETCHING_SEARCH_RESULTS);
        LocalBroadcastManager
                .getInstance(context)
                .sendBroadcast(intent);
    }

    public void searchMovies(final String query) {
        // Cancel any pending requests
        mRequestQueue.cancelAll(mRequestTag);

        String url = getMovieSearchUrl(query);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: " + response);
                ProcessSearchResultsService.PendingSearchResult result = new ProcessSearchResultsService.PendingSearchResult(query, response);
                ProcessSearchResultsService.processSearchResults(MovieSearchApplication.getInstance(), result);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: " + error);
                sendSearchResultsErrorBroadcast(MovieSearchApplication.getInstance());
            }
        });
        request.setTag(mRequestTag);

        mRequestQueue.add(request);
    }

    public void getMovieDetails(final String imdbId, final String query) {
        String url = getMovieDetailsUrl(imdbId);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: " + response);
                ProcessSearchResultsService.processDetailsResult(MovieSearchApplication.getInstance(), response, query);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: " + error);
            }
        });

        mRequestQueue.add(request);
    }

    private String getMovieSearchUrl(String query) {
        return BASE_URL.buildUpon().appendQueryParameter("s", query).build().toString();
    }

    private String getMovieDetailsUrl(String imdbId) {
        return BASE_URL.buildUpon().appendQueryParameter("i", imdbId).build().toString();
    }
}
