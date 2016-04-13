package lt.vycius.movies.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.orm.util.NamingHelper;

import java.text.MessageFormat;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import autodagger.AutoComponent;
import autodagger.AutoInjector;
import butterknife.Bind;
import butterknife.ButterKnife;
import lt.vycius.movies.R;
import lt.vycius.movies.adapter.MoviesRecyclerViewAdapter;
import lt.vycius.movies.entity.FavoriteMovie;
import lt.vycius.movies.entity.Movie;
import lt.vycius.movies.entity.MovieList;
import lt.vycius.movies.network.MovieModule;
import lt.vycius.movies.network.MovieService;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@AutoComponent(modules = MovieModule.class)
@AutoInjector
@Singleton
public class MovieGridFragment extends BaseFragment implements Callback<MovieList> {

    public final String TAG_LOG = getClass().getName();

    public static final String EXTRA_MOVIE_GRID_TYPE = "EXTRA_MOVIE_GRID_TYPE";

    public static final int TYPE_POPULAR_MOVIES = 0;
    public static final int TYPE_TOP_MOVIES = 1;
    public static final int TYPE_FAVORITES = 2;

    protected static final String SQL_FAVORITE_MOVIES =
            MessageFormat.format(
                    "SELECT movie.* FROM {0} JOIN {1} AS favorites ON movie.id = favorites.id",
                    NamingHelper.toSQLName(Movie.class),
                    NamingHelper.toSQLName(FavoriteMovie.class)
            );

    public static MovieGridFragment getInstance(int gridType) {
        MovieGridFragment movieGridFragment = new MovieGridFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_MOVIE_GRID_TYPE, gridType);

        movieGridFragment.setArguments(bundle);

        return movieGridFragment;
    }

    public MovieGridFragment() {

    }

    @Bind(R.id.movie_recycler_view)
    RecyclerView recyclerView;

    @Inject
    MovieService movieService;

    MoviesRecyclerViewAdapter.OnMovieClickedListener onMovieClickedListener;

    int movieGridType;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.movies_recycler_view, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        DaggerMovieGridFragmentComponent.create().inject(this);
        movieGridType = getArguments().getInt(EXTRA_MOVIE_GRID_TYPE);

        loadMovies();
    }

    public void loadMovies() {
        switch (movieGridType) {
            case TYPE_POPULAR_MOVIES:
                movieService.getPopularMovies().enqueue(this);
                break;
            case TYPE_TOP_MOVIES:
                movieService.getTopRatedMovies().enqueue(this);
                break;
            case TYPE_FAVORITES:
                loadFavoriteMovies();
                break;

        }
    }

    protected void loadFavoriteMovies() {
        List<Movie> movies = Movie.findWithQuery(Movie.class, SQL_FAVORITE_MOVIES);

        setMovies(movies);
    }

    @Override
    public void onResponse(Response<MovieList> response) {
        MovieList movieList = response.body();

        setMovies(movieList.getMovies());

        Single.fromCallable(() -> {
                    Movie.findById(Movie.class, 1);
                    Movie.saveInTx(movieList.getMovies());
                    return true;
                }
        ).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                toObservable().
                compose(bindToLifecycle()).
                subscribe(t -> Log.d(TAG_LOG, "Movies saved to DB"),
                        Throwable::printStackTrace);

    }

    public void setMovies(@NonNull List<Movie> movies) {
        MoviesRecyclerViewAdapter adapter =
                new MoviesRecyclerViewAdapter(getContext(), onMovieClickedListener, movies);

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onFailure(Throwable t) {
        Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        onMovieClickedListener = (MoviesRecyclerViewAdapter.OnMovieClickedListener) context;
    }

    @Override
    public void onDetach() {
        onMovieClickedListener = null;

        super.onDetach();
    }
}
