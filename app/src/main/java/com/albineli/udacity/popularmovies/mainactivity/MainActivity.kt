package com.albineli.udacity.popularmovies.mainactivity

import android.app.FragmentManager
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v7.app.AppCompatActivity
import com.albineli.udacity.popularmovies.R
import com.albineli.udacity.popularmovies.enums.MovieListFilterDescriptor
import com.albineli.udacity.popularmovies.event.TabChangeFilterEvent
import com.albineli.udacity.popularmovies.movielist.MovieListFragment
import com.ashokvarma.bottomnavigation.BottomNavigationBar
import com.ashokvarma.bottomnavigation.BottomNavigationItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

class MainActivity : AppCompatActivity(), FragmentManager.OnBackStackChangedListener {

    @IdRes
    private var mSelectedTabIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        super.setTitle(null)

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_TAB_BUNDLE_KEY)) {
            mSelectedTabIndex = savedInstanceState.getInt(SELECTED_TAB_BUNDLE_KEY)
        }

        Timber.i("SELECTED TAB " + mSelectedTabIndex)
        bb_bottom_menu.setMode(BottomNavigationBar.MODE_FIXED)
                .addItem(BottomNavigationItem(R.drawable.ic_popularity, R.string.popular))
                .addItem(BottomNavigationItem(R.drawable.ic_star_half_black_24dp, R.string.rating))
                .addItem(BottomNavigationItem(R.drawable.ic_heart_black_24dp, R.string.favorite))
                .initialise()

        bb_bottom_menu.selectTab(mSelectedTabIndex, false)
        bb_bottom_menu.setTabSelectedListener(object : BottomNavigationBar.OnTabSelectedListener {
            override fun onTabSelected(index: Int) {
                val filter = getFilterBySelectedTab(index)
                EventBus.getDefault().post(TabChangeFilterEvent(filter))

                mSelectedTabIndex = index
            }

            override fun onTabUnselected(i: Int) {

            }

            override fun onTabReselected(i: Int) {

            }
        })

        setupMovieListFragment()

        fragmentManager.addOnBackStackChangedListener(this)

        checkShouldDisplayBackButton()
    }

    private fun setupMovieListFragment() {
        val movieListFragmentTag = "movie_list"
        val fragmentManager = fragmentManager
        var movieListFragment = fragmentManager.findFragmentByTag(movieListFragmentTag) as MovieListFragment?
        if (movieListFragment == null) {
            val filter = getFilterBySelectedTab(mSelectedTabIndex)

            movieListFragment = MovieListFragment.getInstance(filter)
            fragmentManager
                    .beginTransaction()
                    .add(R.id.fl_main_content, movieListFragment, movieListFragmentTag)
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .commit()
        }
    }

    private fun checkShouldDisplayBackButton() {
        val shouldDisplayBackButton = fragmentManager.backStackEntryCount > 0
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(shouldDisplayBackButton)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        fragmentManager.popBackStack()
        return true
    }

    override fun onBackStackChanged() {
        checkShouldDisplayBackButton()
    }

    public override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putInt(SELECTED_TAB_BUNDLE_KEY, mSelectedTabIndex)
    }

    @MovieListFilterDescriptor.MovieListFilter
    private fun getFilterBySelectedTab(@IdRes selectedTabIndex: Int): Int {
        return when (selectedTabIndex) {
            MovieListFilterDescriptor.POPULAR -> MovieListFilterDescriptor.POPULAR
            MovieListFilterDescriptor.RATING -> MovieListFilterDescriptor.RATING
            else -> MovieListFilterDescriptor.FAVORITE
        }
    }

    override fun setTitle(titleId: Int) {
        title = getString(titleId)
    }

    override fun setTitle(title: CharSequence) {
        toolbar_title.text = title
    }

    companion object {
        private val SELECTED_TAB_BUNDLE_KEY = "selected_tab"
    }
}
