package lt.vycius.movies.fragment;

import android.os.Bundle;

import com.trello.rxlifecycle.components.support.RxFragment;

import icepick.Icepick;


public class BaseFragment extends RxFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }
}
