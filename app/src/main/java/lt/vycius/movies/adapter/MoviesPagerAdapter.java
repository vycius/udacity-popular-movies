package lt.vycius.movies.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import lt.vycius.movies.R;
import lt.vycius.movies.fragment.MovieGridFragment;

public class MoviesPagerAdapter extends FragmentStatePagerAdapter {

    public static String[] tabNames;

    public MoviesPagerAdapter(Context context, FragmentManager fm) {
        super(fm);

        tabNames = context.getResources().getStringArray(R.array.movie_grid_tab_titles);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MovieGridFragment.getInstance(MovieGridFragment.TYPE_TOP_MOVIES);
            case 1:
                return MovieGridFragment.getInstance(MovieGridFragment.TYPE_POPULAR_MOVIES);
            case 2:
                return MovieGridFragment.getInstance(MovieGridFragment.TYPE_FAVORITES);

        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabNames[position];
    }

    @Override
    public int getCount() {
        return tabNames.length;
    }
}
