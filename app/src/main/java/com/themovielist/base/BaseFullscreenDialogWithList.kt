package com.themovielist.base

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import com.themovielist.PopularMovieApplication
import com.themovielist.R
import com.themovielist.injector.components.ApplicationComponent
import kotlinx.android.synthetic.main.fullscreen_fragment_dialog_with_list.*
import java.util.*


abstract class BaseFullscreenDialogWithList<TModel : Parcelable, TView> : DialogFragment(), CommonMethods {

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

        arguments?.let {
            mList = it.getParcelableArrayList<TModel>(LIST_BUNDLE_KEY)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fullscreen_fragment_dialog_with_list, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let {
            configureToolbarBackButton(it, toolbarMovieReviewDialog) {
                dismiss()
            }

            val applicationComponent = PopularMovieApplication.getApplicationComponent(it)
            onInjectDependencies(applicationComponent)

            presenterImplementation.setView(viewImplementation)
        }
    }

    protected fun setTitle(@StringRes titleResId: Int) {
        tvListFragmentDialogTitle.setText(titleResId)
    }

    protected abstract fun onInjectDependencies(applicationComponent: ApplicationComponent)

    protected abstract val presenterImplementation: BasePresenter<TView>
    protected abstract val viewImplementation: TView

    companion object {
        private val LIST_BUNDLE_KEY = "list"

        fun <TFragmentDialog : BaseFullscreenDialogWithList<TModel, *>, TModel : Parcelable> createNewInstance(clazz: Class<TFragmentDialog>, items: List<TModel>): TFragmentDialog {
            val baseFullscreenDialogWithList = clazz.newInstance()
            val bundle = Bundle()
            bundle.putParcelableArrayList(LIST_BUNDLE_KEY, ArrayList(items))
            baseFullscreenDialogWithList.arguments = bundle
            baseFullscreenDialogWithList.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme_DialogFullscreen)

            return baseFullscreenDialogWithList
        }
    }
}
