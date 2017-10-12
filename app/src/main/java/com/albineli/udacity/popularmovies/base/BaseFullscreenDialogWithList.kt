package com.albineli.udacity.popularmovies.base

import android.app.DialogFragment
import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.StringRes
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.albineli.udacity.popularmovies.PopularMovieApplication
import com.albineli.udacity.popularmovies.R
//import com.albineli.udacity.popularmovies.R
import com.albineli.udacity.popularmovies.injector.components.ApplicationComponent
import com.albineli.udacity.popularmovies.util.UIUtil
import kotlinx.android.synthetic.main.fullscreen_fragment_dialog_with_list.*
import timber.log.Timber
import java.security.InvalidParameterException
import java.util.*


abstract class BaseFullscreenDialogWithList<TModel : Parcelable, TView> : DialogFragment() {

    //@BindView(R.id.rvFullscreenFragmentDialog)
    //var mRecyclerView: RecyclerView? = null

    //@BindView(R.id.toolbarMovieReviewDialog)
    //internal var mDialogToolbar: Toolbar? = null

    //@BindView(R.id.tvListFragmentDialogTitle)
    //internal var mTitle: TextView? = null

    protected lateinit var mList: ArrayList<TModel>

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null && dialog.window != null) {
            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments == null || !arguments.containsKey(LIST_BUNDLE_KEY)) {
            throw InvalidParameterException(LIST_BUNDLE_KEY)
        }

        mList = arguments.getParcelableArrayList<TModel>(LIST_BUNDLE_KEY)

        val applicationComponent = PopularMovieApplication.getApplicationComponent(activity)
        onInjectDependencies(applicationComponent)

        presenterImplementation.setView(viewImplementation)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle): View? {
        val rootView = inflater.inflate(R.layout.fullscreen_fragment_dialog_with_list, container)

        ButterKnife.bind(this, rootView)

        val drawable = resources.getDrawable(R.drawable.arrow_left)
        UIUtil.paintDrawable(drawable, resources.getColor(android.R.color.white))
        toolbarMovieReviewDialog.navigationIcon = drawable
        toolbarMovieReviewDialog.setNavigationOnClickListener { v -> dismiss() }

        return rootView
    }

    protected fun setTitle(@StringRes titleResId: Int) {
        tvListFragmentDialogTitle.setText(titleResId)
    }

    protected abstract fun onInjectDependencies(applicationComponent: ApplicationComponent)

    protected abstract val presenterImplementation: BasePresenter<TView>
    protected abstract val viewImplementation: TView

    companion object {
        private val LIST_BUNDLE_KEY = "list"

        fun <TFragmentDialog : BaseFullscreenDialogWithList<TModel, *>, TModel : Parcelable> createNewInstance(clazz: Class<TFragmentDialog>, items: List<TModel>): TFragmentDialog? {
            try {
                val baseFullscreenDialogWithList = clazz.newInstance()
                val bundle = Bundle()
                bundle.putParcelableArrayList(LIST_BUNDLE_KEY, ArrayList(items))
                baseFullscreenDialogWithList.arguments = bundle
                baseFullscreenDialogWithList.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme_DialogFullscreen)

                return baseFullscreenDialogWithList
            } catch (e: Exception) {
                Timber.e(e, "An error occurred while tried to instantiate a new fullscreen dialog")
            }

            return null
        }
    }
}
