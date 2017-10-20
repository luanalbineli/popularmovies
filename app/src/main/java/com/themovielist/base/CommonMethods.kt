package com.themovielist.base

import android.app.Fragment
import android.app.FragmentManager
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

    private fun <T: Fragment> tryToRetrieveFragmentInstance(fragmentManager: FragmentManager, fragmentTag: String) : T? {
        val fragment: Fragment? = fragmentManager.findFragmentByTag(fragmentTag)
        Timber.i("fragment: $fragment")
        return fragment as? T
    }
}
