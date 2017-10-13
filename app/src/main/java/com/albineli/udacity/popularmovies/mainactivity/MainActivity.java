package com.albineli.udacity.popularmovies.mainactivity;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.albineli.udacity.popularmovies.R;
import com.albineli.udacity.popularmovies.enums.MovieListFilterDescriptor;
import com.albineli.udacity.popularmovies.event.TabChangeFilterEvent;
import com.albineli.udacity.popularmovies.movielist.MovieListFragment;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

import org.greenrobot.eventbus.EventBus;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {
    private static final String SELECTED_TAB_BUNDLE_KEY = "selected_tab";

   // @BindView(R.id.bb_bottom_menu)
    BottomNavigationBar mBottomBar;

    //@BindView(R.id.toolbar_title)
    TextView mToobarTitle;

    private
    @IdRes
    int mSelectedTabIndex = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);

        super.setTitle(null);

        //ButterKnife.bind(this);

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_TAB_BUNDLE_KEY)) {
            mSelectedTabIndex = savedInstanceState.getInt(SELECTED_TAB_BUNDLE_KEY);
        }

        Timber.i("SELECTED TAB " + mSelectedTabIndex);
        mBottomBar.setMode(BottomNavigationBar.MODE_FIXED)
                .addItem(new BottomNavigationItem(R.drawable.ic_popularity, R.string.popular))
                .addItem(new BottomNavigationItem(R.drawable.ic_star_half_black_24dp, R.string.rating))
                .addItem(new BottomNavigationItem(R.drawable.ic_heart_black_24dp, R.string.favorite))
                .initialise();

        mBottomBar.selectTab(mSelectedTabIndex, false);
        mBottomBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int index) {
                @MovieListFilterDescriptor.MovieListFilter int filter = getFilterBySelectedTab(index);
                EventBus.getDefault().post(new TabChangeFilterEvent(filter));

                mSelectedTabIndex = index;
            }

            @Override
            public void onTabUnselected(int i) {

            }

            @Override
            public void onTabReselected(int i) {

            }
        });

        setupMovieListFragment();

        getFragmentManager().addOnBackStackChangedListener(this);

        checkShouldDisplayBackButton();
    }

    private void setupMovieListFragment() {
        final String movieListFragmentTag = "movie_list";
        FragmentManager fragmentManager = getFragmentManager();
        MovieListFragment movieListFragment = (MovieListFragment) fragmentManager.findFragmentByTag(movieListFragmentTag);
        if (movieListFragment == null) {
            @MovieListFilterDescriptor.MovieListFilter int filter = getFilterBySelectedTab(mSelectedTabIndex);

            movieListFragment = MovieListFragment.getInstance(filter);
            fragmentManager
                    .beginTransaction()
                    .add(R.id.fl_main_content, movieListFragment, movieListFragmentTag)
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .commit();
        }
    }

    private void checkShouldDisplayBackButton() {
        boolean shouldDisplayBackButton = getFragmentManager().getBackStackEntryCount() > 0;
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(shouldDisplayBackButton);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        getFragmentManager().popBackStack();
        return true;
    }

    @Override
    public void onBackStackChanged() {
        checkShouldDisplayBackButton();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_TAB_BUNDLE_KEY, mSelectedTabIndex);
    }

    private
    @MovieListFilterDescriptor.MovieListFilter
    int getFilterBySelectedTab(@IdRes int selectedTabIndex) {
        switch (selectedTabIndex) {
            case MovieListFilterDescriptor.INSTANCE.getPOPULAR():
                return MovieListFilterDescriptor.INSTANCE.getPOPULAR();
            case MovieListFilterDescriptor.INSTANCE.getRATING():
                return MovieListFilterDescriptor.INSTANCE.getRATING();
            default:
                return MovieListFilterDescriptor.INSTANCE.getFAVORITE();
        }
    }

    @Override
    public void setTitle(int titleId) {
        setTitle(getString(titleId));
    }

    @Override
    public void setTitle(CharSequence title) {
        mToobarTitle.setText(title);
    }
}
