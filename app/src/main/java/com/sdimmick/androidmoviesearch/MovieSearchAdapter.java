package com.sdimmick.androidmoviesearch;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdimmick.androidmoviesearch.db.MovieSearchContract;

public class MovieSearchAdapter extends RecyclerView.Adapter<MovieSearchAdapter.ViewHolder> {
    private static final String TAG = "MovieSearchAdapter";

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mItemView;
        public ImageView mPosterImageView;
        public TextView mTitleView;
        public TextView mPlotView;
        public ImageView mFavoriteView;

        public ViewHolder(View itemView) {
            super(itemView);

            mItemView = itemView;
            mPosterImageView = itemView.findViewById(R.id.search_result_poster);
            mTitleView = itemView.findViewById(R.id.search_result_title);
            mPlotView = itemView.findViewById(R.id.search_result_plot);
            mFavoriteView = itemView.findViewById(R.id.search_result_favorite);
        }
    }

    private Cursor mCursor;

    public void swapCursor(Cursor cursor) {
        Log.d(TAG, "Swapped cursor");
        mCursor = cursor;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        String title = mCursor.getString(mCursor.getColumnIndex(MovieSearchContract.MovieSearchResult.COLUMN_NAME_TITLE));
        String year = mCursor.getString(mCursor.getColumnIndex(MovieSearchContract.MovieSearchResult.COLUMN_NAME_YEAR));
        String director = mCursor.getString(mCursor.getColumnIndex(MovieSearchContract.MovieSearchResult.COLUMN_NAME_DIRECTOR));

        String titleText = String.format("%s (%s), by %s", title, year, director);

        holder.mTitleView.setText(titleText);

        String plot = mCursor.getString(mCursor.getColumnIndex(MovieSearchContract.MovieSearchResult.COLUMN_NAME_PLOT_SUMMARY));
        holder.mPlotView.setText(plot);

        final boolean isFavorite = mCursor.getInt(mCursor.getColumnIndex(MovieSearchContract.MovieSearchResult.COLUMN_NAME_IS_FAVORITE)) == 1 ? true : false;
        int drawable = isFavorite ? R.drawable.ic_favorites_gold_36dp : R.drawable.ic_favorites_white_36dp;
        holder.mFavoriteView.setImageResource(drawable);
        final long id = mCursor.getLong(mCursor.getColumnIndex(MovieSearchContract.MovieSearchResult._ID));

        holder.mFavoriteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MovieSearchApplication.getDatabase().toggleFavorite(id, !isFavorite);
                    }
                }).start();
            }
        });

        String posterUrl = mCursor.getString(mCursor.getColumnIndex(MovieSearchContract.MovieSearchResult.COLUMN_NAME_POSTER));
        GlideApp
                .with(holder.mPosterImageView)
                .load(posterUrl)
                .centerCrop()
                .into(holder.mPosterImageView);

        holder.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MovieDetailsActivity.start(MovieSearchApplication.getInstance(), id);
            }
        });
    }

    @Override
    public int getItemCount() {
        int itemCount = mCursor != null ? mCursor.getCount() : 0;
        Log.d(TAG, "Item count: " + itemCount);
        return itemCount;
    }

}
