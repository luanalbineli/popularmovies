package com.themovielist.mainactivity

import android.app.Fragment
import android.app.FragmentManager
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v7.content.res.AppCompatResources
import com.albineli.udacity.popularmovies.R
import com.ashokvarma.bottomnavigation.BottomNavigationBar
import com.ashokvarma.bottomnavigation.BottomNavigationItem
import com.themovielist.base.BaseActivity
import com.themovielist.browse.MovieBrowseFragment
import com.themovielist.enums.TabTypeEnum
import com.themovielist.home.HomeFragment
import com.themovielist.intheaters.InTheatersFragment
import com.themovielist.util.setDisplay
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : BaseActivity(), FragmentManager.OnBackStackChangedListener {
    @TabTypeEnum.TabType
    private var mSelectedTabIndex = TabTypeEnum.HOME

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vsMainContent.layoutResource = R.layout.content_main
        vsMainContent.inflate()

        setSupportActionBar(toolbar)

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_TAB_BUNDLE_KEY)) {
            mSelectedTabIndex = savedInstanceState.getInt(SELECTED_TAB_BUNDLE_KEY)
        }

        bnbBottomBar.setMode(BottomNavigationBar.MODE_FIXED)
                .addItem(BottomNavigationItem(AppCompatResources.getDrawable(this, R.drawable.home), R.string.home))
                .addItem(BottomNavigationItem(AppCompatResources.getDrawable(this, R.drawable.magnify), R.string.browse))
                .addItem(BottomNavigationItem(AppCompatResources.getDrawable(this, R.drawable.theater), R.string.cinema))
                .addItem(BottomNavigationItem(AppCompatResources.getDrawable(this, R.drawable.heart), R.string.favorite))
                .initialise()

        bnbBottomBar.selectTab(mSelectedTabIndex, false)
        bnbBottomBar.setTabSelectedListener(object : BottomNavigationBar.OnTabSelectedListener {
            override fun onTabSelected(index: Int) {
                mSelectedTabIndex = index
                setUpMainContentFragment()
                appBarLayout.setDisplay(index != 1)
                val params = llContentContainer.layoutParams as CoordinatorLayout.LayoutParams
                params.behavior = if (index == 1) null else AppBarLayout.ScrollingViewBehavior()
                llContentContainer.requestLayout()
            }

            override fun onTabUnselected(i: Int) {

            }

            override fun onTabReselected(i: Int) {

            }
        })

        setUpMainContentFragment()

        fragmentManager.addOnBackStackChangedListener(this)

        checkShouldDisplayBackButton()
    }

    private fun setUpMainContentFragment() {
        when (mSelectedTabIndex) {
            TabTypeEnum.HOME -> checkChangeMainContent(HOME_FRAGMENT_TAG) {
                HomeFragment.getInstance()
            }
            TabTypeEnum.IN_THEATERS -> checkChangeMainContent(IN_THEATERS_FRAGMENT_TAG) {
                InTheatersFragment.getInstance()
            }
            TabTypeEnum.BROWSE -> checkChangeMainContent(BROWSE_FRAGMENT_TAG) {
                MovieBrowseFragment.getInstance()
            }
        }
    }

    private fun checkChangeMainContent(fragmentTag: String, fragmentInstanceInvoker: () -> Fragment) {
        replaceFragmentIfNotExists(fragmentManager, R.id.flMainContent, fragmentTag, fragmentInstanceInvoker)
    }

    private fun checkShouldDisplayBackButton() {
        val shouldDisplayBackButton = fragmentManager.backStackEntryCount > 0
        supportActionBar?.setDisplayHomeAsUpEnabled(shouldDisplayBackButton)
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
        outState?.putInt(SELECTED_TAB_BUNDLE_KEY, mSelectedTabIndex)
    }

    companion object {
        private val SELECTED_TAB_BUNDLE_KEY = "selected_tab"
        private val HOME_FRAGMENT_TAG = "home_fragment"
        private val BROWSE_FRAGMENT_TAG = "browse_fragment"
        private val IN_THEATERS_FRAGMENT_TAG = "in_theaters_fragment"
        private val FRAVORITE_FRAGMENT_TAG = "favorite_fragment"
    }
}
