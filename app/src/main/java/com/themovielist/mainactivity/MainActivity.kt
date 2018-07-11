package com.themovielist.mainactivity

import android.app.Fragment
import android.app.FragmentManager
import android.os.Bundle
import com.themovielist.R
import com.themovielist.base.BaseActivity
import com.themovielist.browse.MovieBrowseFragment
import com.themovielist.favorite.FavoriteFragment
import com.themovielist.home.HomeFragment
import com.themovielist.intheaters.InTheatersFragment
import com.themovielist.ui.searchabletoolbar.OnSearchToolbarQueryChanged
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.security.InvalidParameterException


class MainActivity : BaseActivity(), FragmentManager.OnBackStackChangedListener {
    // Default tab.
    private var mSelectedItemId = R.id.bottom_menu_item_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vsMainContent.layoutResource = R.layout.content_main
        vsMainContent.inflate()

        setSupportActionBar(searchableToolbar)

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_TAB_BUNDLE_KEY)) {
            mSelectedItemId = savedInstanceState.getInt(SELECTED_TAB_BUNDLE_KEY)
        }

        bnvBottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            mSelectedItemId = menuItem.itemId
            setUpMainContentFragment()
            true
        }

        bnvBottomNavigationView.selectedItemId = mSelectedItemId

        fragmentManager.addOnBackStackChangedListener(this)

        checkShouldDisplayBackButton()
    }

    private fun setUpMainContentFragment() {
        var fragmentInstance: Fragment? = null
        var fragmentTag = ""
        when (mSelectedItemId) {
            R.id.bottom_menu_item_home -> {
                fragmentInstance = HomeFragment.getInstance()
                fragmentTag = HOME_FRAGMENT_TAG
            }
            R.id.bottom_menu_item_browse -> {
                fragmentInstance = MovieBrowseFragment.getInstance()
                fragmentTag = BROWSE_FRAGMENT_TAG
            }
            R.id.bottom_menu_item_cinema ->  {
                fragmentInstance = InTheatersFragment.getInstance()
                fragmentTag = IN_THEATERS_FRAGMENT_TAG
            }
            R.id.bottom_menu_item_favorite ->  {
                fragmentInstance = FavoriteFragment.getInstance()
                fragmentTag = FAVORITE_FRAGMENT_TAG
            }
        }

        if (fragmentInstance === null) {
            throw InvalidParameterException("mSelectedItemId: $mSelectedItemId")
        }

        checkChangeMainContent(fragmentTag) {
            fragmentInstance
        }

        searchableToolbar.onSearchToolbarQueryChanged = (fragmentInstance as? OnSearchToolbarQueryChanged)
    }

    private fun checkChangeMainContent(fragmentTag: String, fragmentInstanceInvoker: () -> Fragment) {
        replaceFragment(fragmentManager, R.id.flMainContent, fragmentTag, fragmentInstanceInvoker)
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
        outState?.putInt(SELECTED_TAB_BUNDLE_KEY, mSelectedItemId)
    }

    companion object {
        private const val SELECTED_TAB_BUNDLE_KEY = "selected_tab"
        private const val HOME_FRAGMENT_TAG = "home_fragment"
        private const val BROWSE_FRAGMENT_TAG = "browse_fragment"
        private const val IN_THEATERS_FRAGMENT_TAG = "in_theaters_fragment"
        private const val FAVORITE_FRAGMENT_TAG = "favorite_fragment"
    }
}
