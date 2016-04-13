package lt.vycius.movies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.vycius.movies.R;
import lt.vycius.movies.entity.Movie;

public class MoviesRecyclerViewAdapter
        extends RecyclerView.Adapter<MoviesRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private final List<Movie> mValues;
    protected OnMovieClickedListener onMovieClickedListener;

    public MoviesRecyclerViewAdapter(Context context, OnMovieClickedListener onMovieClickedListener, List<Movie> items) {
        this.context = context;
        mValues = items;

        this.onMovieClickedListener = onMovieClickedListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_grid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Movie movie = mValues.get(position);

        Glide.with(context).load(movie.getPosterImage())
                .into(holder.mMovieImage);

        holder.mView.setOnClickListener(v -> {
            if (onMovieClickedListener != null)
                onMovieClickedListener.onMovieClicked(movie);
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.movie_grid_item)
        public View mView;

        @Bind(R.id.movie_grid_item_image)
        public ImageView mMovieImage;


        public ViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);
        }
    }

    public interface OnMovieClickedListener {

        void onMovieClicked(Movie movie);

    }

    ;
}
