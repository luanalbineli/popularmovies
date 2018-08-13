package com.themovielist.mainactivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.themovielist.R
import com.themovielist.base.BaseActivity
import com.themovielist.browse.MovieBrowseFragment
import com.themovielist.favorite.FavoriteFragment
import com.themovielist.home.HomeFragment
import com.themovielist.intheaters.InTheatersFragment
import com.themovielist.ui.searchabletoolbar.OnSearchToolbarQueryChanged
import kotlinx.android.synthetic.main.home_full_movie_list_activity.*
import kotlinx.android.synthetic.main.content_main.*
import java.security.InvalidParameterException


class MainActivity : BaseActivity() {
    private var mSelectedNavItemId = Int.MIN_VALUE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_main)

        configureBottomNavigationView(savedInstanceState)
    }

    private fun configureBottomNavigationView(savedInstanceState: Bundle?) {
        var initialSelectedNavItemId = R.id.bottom_menu_item_home
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_TAB_BUNDLE_KEY)) {
            initialSelectedNavItemId = savedInstanceState.getInt(SELECTED_TAB_BUNDLE_KEY)
        }

        bnvBottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            if (mSelectedNavItemId == menuItem.itemId) {
                return@setOnNavigationItemSelectedListener true
            }
            mSelectedNavItemId = menuItem.itemId
            setUpMainContentFragment()
            true
        }


        bnvBottomNavigationView.selectedItemId = initialSelectedNavItemId
    }

    private fun setUpMainContentFragment() {
        var fragmentInstance: Fragment? = null
        var fragmentTag = ""
        when (mSelectedNavItemId) {
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
            throw InvalidParameterException("mSelectedNavItemId: $mSelectedNavItemId")
        }

        checkChangeMainContent(fragmentTag) {
            fragmentInstance
        }
    }

    private fun checkChangeMainContent(fragmentTag: String, fragmentInstanceInvoker: () -> Fragment) {
        replaceFragment(supportFragmentManager, R.id.flMainContent, fragmentTag, fragmentInstanceInvoker)
    }

    public override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt(SELECTED_TAB_BUNDLE_KEY, mSelectedNavItemId)
    }

    companion object {
        private const val SELECTED_TAB_BUNDLE_KEY = "selected_tab"
        private const val HOME_FRAGMENT_TAG = "home_fragment"
        private const val BROWSE_FRAGMENT_TAG = "browse_fragment"
        private const val IN_THEATERS_FRAGMENT_TAG = "in_theaters_fragment"
        private const val FAVORITE_FRAGMENT_TAG = "favorite_fragment"
    }
}
