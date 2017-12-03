package com.themovielist.base

import android.app.Fragment
import android.app.FragmentManager
import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
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

    fun <T: Fragment> replaceFragmentIfNotExists(fragmentManager: FragmentManager, containerId: Int, tag: String, newFragmentInitializer: () -> T): T {
        return tryToRetrieveFragmentInstance(fragmentManager, tag) ?: {
            val newFragment = newFragmentInitializer.invoke()

            fragmentManager.beginTransaction()
                    .replace(containerId, newFragment, tag)
                    .commit()

            newFragment
        }()
    }

    fun configureToolbarBackButton(context: Context, toolbar: Toolbar, onBackPressed: () -> Unit) {
        val drawable = ContextCompat.getDrawable(context, R.drawable.arrow_left)
        UIUtil.paintDrawable(drawable, context.resources.getColor(android.R.color.white))
        toolbar.navigationIcon = drawable
        toolbar.setNavigationOnClickListener { _ -> onBackPressed() }
    }

    private fun <T: Fragment> tryToRetrieveFragmentInstance(fragmentManager: FragmentManager, fragmentTag: String) : T? {
        val fragment: Fragment? = fragmentManager.findFragmentByTag(fragmentTag)
        Timber.i("fragment: $fragment")
        return fragment as? T
    }
}
