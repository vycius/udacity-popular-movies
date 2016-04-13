package lt.vycius.movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.vycius.movies.adapter.MoviesPagerAdapter;
import lt.vycius.movies.adapter.MoviesRecyclerViewAdapter;
import lt.vycius.movies.entity.Movie;
import lt.vycius.movies.fragment.MovieDetailFragment;

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MovieGridActivity extends AppCompatActivity implements MoviesRecyclerViewAdapter.OnMovieClickedListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;


    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.tabs)
    TabLayout tabLayout;

    @Bind(R.id.movies_grid_view_pager)
    ViewPager moviesViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());


        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
        }

        setupTabs();
    }

    protected void setupTabs() {
        MoviesPagerAdapter adapter = new MoviesPagerAdapter(this, getSupportFragmentManager());

        moviesViewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(moviesViewPager);
    }

    @Override
    public void onMovieClicked(Movie movie) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putLong(MovieDetailFragment.ARG_ITEM_ID, movie.getId());
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtra(MovieDetailFragment.ARG_ITEM_ID, movie.getId());

            startActivity(intent);
        }
    }
}
