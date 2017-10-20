package com.themovielist.mainactivity

import android.app.Fragment
import android.app.FragmentManager
import android.os.Bundle
import com.albineli.udacity.popularmovies.R
import com.ashokvarma.bottomnavigation.BottomNavigationBar
import com.ashokvarma.bottomnavigation.BottomNavigationItem
import com.themovielist.base.BaseActivity
import com.themovielist.enums.TabTypeEnum
import com.themovielist.home.HomeFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : BaseActivity(), FragmentManager.OnBackStackChangedListener {
    @TabTypeEnum.TabType
    private var mSelectedTabIndex = TabTypeEnum.HOME

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        super.setTitle(null)

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_TAB_BUNDLE_KEY)) {
            mSelectedTabIndex = savedInstanceState.getInt(SELECTED_TAB_BUNDLE_KEY)
        }

        bb_bottom_menu.setMode(BottomNavigationBar.MODE_FIXED)
                .addItem(BottomNavigationItem(R.drawable.home, R.string.home))
                .addItem(BottomNavigationItem(R.drawable.magnify, R.string.browse))
                .addItem(BottomNavigationItem(R.drawable.theater, R.string.cinema))
                .addItem(BottomNavigationItem(R.drawable.ic_heart_black_24dp, R.string.favorite))
                .initialise()

        bb_bottom_menu.selectTab(mSelectedTabIndex, false)
        bb_bottom_menu.setTabSelectedListener(object : BottomNavigationBar.OnTabSelectedListener {
            override fun onTabSelected(index: Int) {
                mSelectedTabIndex = index
                setUpMainContentFragment()
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
        }
    }

    private fun checkChangeMainContent(fragmentTag: String, fragmentInstanceInvoker: () -> Fragment) {
        replaceFragmentIfNotExists(fragmentManager, R.id.flMainContent, fragmentTag, fragmentInstanceInvoker)
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
        outState?.putInt(SELECTED_TAB_BUNDLE_KEY, mSelectedTabIndex)
    }

    override fun setTitle(titleId: Int) {
        title = getString(titleId)
    }

    override fun setTitle(title: CharSequence) {
        toolbar_title.text = title
    }

    companion object {
        private val SELECTED_TAB_BUNDLE_KEY = "selected_tab"
        private val HOME_FRAGMENT_TAG = "home_fragment"
        private val BROWSE_FRAGMENT_TAG = "browse_fragment"
        private val CINEMA_FRAGMENT_TAG = "cinema_fragment"
        private val FRAVORITE_FRAGMENT_TAG = "favorite_fragment"
    }
}
