package lt.vycius.movies.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import autodagger.AutoComponent;
import autodagger.AutoInjector;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.State;
import lt.vycius.movies.MovieDetailActivity;
import lt.vycius.movies.MovieGridActivity;
import lt.vycius.movies.R;
import lt.vycius.movies.adapter.MovieVideoRecyclerViewAdapter;
import lt.vycius.movies.adapter.ReviewsRecyclerViewAdapter;
import lt.vycius.movies.entity.FavoriteMovie;
import lt.vycius.movies.entity.Movie;
import lt.vycius.movies.entity.MovieReview;
import lt.vycius.movies.entity.MovieReviewsList;
import lt.vycius.movies.entity.MovieVideo;
import lt.vycius.movies.entity.MovieVideosList;
import lt.vycius.movies.network.MovieModule;
import lt.vycius.movies.network.MovieService;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieGridActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
@AutoComponent(modules = MovieModule.class)
@AutoInjector
@Singleton
public class MovieDetailFragment extends BaseFragment implements Callback<Movie> {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    public final String TAG_LOG = getClass().getName();

    @Inject
    MovieService movieService;

    @Bind(R.id.toolbar_layout)
    CollapsingToolbarLayout appBarLayout;

    @Bind(R.id.backdrop)
    ImageView backdrop;

    @Bind(R.id.movie_detail_plot)
    TextView mMoviePlot;

    @Bind(R.id.movie_detail_release_date)
    TextView mMovieReleaseDate;

    @Bind(R.id.movie_detail_rating)
    TextView mMovieRating;

    @Bind(R.id.movie_detail_add_to_favorites)
    FloatingActionButton mAddToFavorites;

    @Bind(R.id.movie_detail_trailers_recycler_view)
    RecyclerView movieTrailersRecyclerView;

    @Bind(R.id.movie_detail_reviews_recycler_view)
    RecyclerView movieReviewsRecyclerView;

    private ShareActionProvider mShareActionProvider;

    @State
    Movie curMovie;

    @State
    long movieId;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieDetailFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        DaggerMovieDetailFragmentComponent.create().inject(this);

        ButterKnife.bind(this, getActivity());

        if (curMovie != null)
            renderMovie(curMovie);
        else if (getArguments().containsKey(ARG_ITEM_ID)) {
            movieId = getArguments().getLong(ARG_ITEM_ID);

            loadMovie(movieId);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);

        return rootView;
    }

    public void loadMovie(long movieId) {
        loadMovieFromDB(movieId);

        movieService.getdMovie(movieId).enqueue(this);

        loadMovieVideos(movieId);
        loadMovieReviews(movieId);
    }

    public void loadMovieFromDB(long movieId) {
        Single.fromCallable(() -> Movie.findById(Movie.class, movieId)).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                toObservable().
                compose(bindToLifecycle()).
                subscribe(movie -> {
                            if (movie != null && curMovie == null) {
                                curMovie = movie;

                                Log.d(TAG_LOG, "Loaded movie from DB");

                                renderMovie(movie);
                            }
                        },
                        Throwable::printStackTrace);

    }

    public void renderMovie(Movie movie) {
        curMovie = movie;

        if (getContext() == null)
            return;

        getActivity().setTitle(movie.getTitle());
        appBarLayout.setTitle(movie.getTitle());

        Glide.with(getContext()).load(movie.getBackdropUrl()).into(backdrop);

        mMovieRating.setText(movie.getVoteAverage());
        mMoviePlot.setText(movie.getOverview());
        mMovieReleaseDate.setText(movie.getReleaseDate());

        renderFavoritesButton(true);
    }

    public void loadMovieVideos(long id) {
        movieService.getMovieVideos(id).enqueue(new Callback<MovieVideosList>() {
            @Override
            public void onResponse(Response<MovieVideosList> response) {
                List<MovieVideo> trailers = response.body().getVideos();

                MovieVideoRecyclerViewAdapter adapter = new MovieVideoRecyclerViewAdapter(getContext(), trailers);

                movieTrailersRecyclerView.setAdapter(adapter);

                if (!trailers.isEmpty())
                    setShareIntent(trailers.get(0));
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getContext(), "Can't get trailers " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loadMovieReviews(long id) {
        movieService.getMovieReviews(id).enqueue(new Callback<MovieReviewsList>() {
            @Override
            public void onResponse(Response<MovieReviewsList> response) {
                ArrayList<MovieReview> reviews = response.body().getReviews();

                ReviewsRecyclerViewAdapter adapter = new ReviewsRecyclerViewAdapter(reviews);

                movieReviewsRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getContext(), "Can't get reviews " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void renderFavoritesButton(final boolean initial) {
        Single.fromCallable(() -> FavoriteMovie.findById(FavoriteMovie.class, movieId) != null).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                toObservable().
                compose(bindToLifecycle()).
                subscribe(favorite -> {
                            FavoriteMovie favoriteMovie = new FavoriteMovie(movieId);

                            if (favorite ^ initial) {
                                mAddToFavorites.setImageResource(R.drawable.ic_heart_white_24dp);
                                favoriteMovie.delete();
                            } else {
                                mAddToFavorites.setImageResource(R.drawable.ic_heart_outline_white_24dp);
                                favoriteMovie.save();
                            }
                        },
                        Throwable::printStackTrace);
    }

    @OnClick(R.id.movie_detail_add_to_favorites)
    public void onAddToFavoritesClicked() {
        renderFavoritesButton(false);
    }

    @Override
    public void onResponse(Response<Movie> response) {
        renderMovie(response.body());
    }

    @Override
    public void onFailure(Throwable t) {
        Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_detail_menu, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.movie_detail_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
    }

    // Call to update the share intent
    private void setShareIntent(MovieVideo trailer) {
        if (mShareActionProvider != null && getActivity() != null) {
            String shareBody = getString(R.string.movie_detail_check_trailer, trailer.getName(), trailer.getVideoUrl());
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

            mShareActionProvider.setShareIntent(sharingIntent);
        }
    }
}
