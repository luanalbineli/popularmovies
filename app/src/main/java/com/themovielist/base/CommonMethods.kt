package com.themovielist.base

import android.app.Fragment
import android.app.FragmentManager
import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import com.themovielist.R
import com.themovielist.util.UIUtil
import timber.log.Timber

interface CommonMethods {
    fun <T: Fragment> addFragmentIfNotExists(fragmentManager: FragmentManager, containerId: Int, tag: String, newFragmentInitializer: () -> T): T {
        return tryToRetrieveFragmentInstance(fragmentManager, tag) ?: {
            val newFragment = newFragmentInitializer.invoke()

            fragmentManager.beginTransaction()
                    .add(containerId, newFragment, tag)
                    .commit()

            newFragment
        }()
    }

    fun <T: Fragment> replaceFragment(fragmentManager: FragmentManager, containerId: Int, tag: String, newFragmentInitializer: () -> T): T {
        val newFragment = newFragmentInitializer.invoke()

        fragmentManager.beginTransaction()
                .replace(containerId, newFragment, tag)
                .commit()

        return newFragment
    }

    fun configureToolbarBackButton(context: Context, toolbar: Toolbar, onBackPressed: () -> Unit) {
        AppCompatResources.getDrawable(context, R.drawable.arrow_left)?.let {
            UIUtil.paintDrawable(it, context.resources.getColor(android.R.color.white))
            toolbar.navigationIcon = it
            toolbar.setNavigationOnClickListener { _ -> onBackPressed() }
        }
    }

    private fun <T: Fragment> tryToRetrieveFragmentInstance(fragmentManager: FragmentManager, fragmentTag: String) : T? {
        val fragment: Fragment? = fragmentManager.findFragmentByTag(fragmentTag)
        Timber.i("fragment: $fragment")
        return fragment as T?
    }
}
