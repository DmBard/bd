package com.dmbaryshev.bd.view.common

import android.app.Fragment
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import com.dmbaryshev.bd.model.view_model.IViewModel
import com.dmbaryshev.bd.presenter.common.BasePresenter
import com.dmbaryshev.bd.presenter.common.PresenterCache
import com.dmbaryshev.bd.utils.snack

abstract class BaseFragment<P : BasePresenter<out IView<out IViewModel>, out IViewModel>> : Fragment() {
    protected  val mPresenter: P by lazy {PresenterCache.getPresenter(tag, presenter)}
    private var isDestroyedBySystem: Boolean = false
    private var mErrorSnackbar: Snackbar? = null

    protected fun showErrorSnackbar(view: View, errorText: Int) {
        mErrorSnackbar = view.snack(errorText){mPresenter.forceLoad()}
    }

    protected fun showErrorSnackbar(view: View, errorText: String) {
        mErrorSnackbar = view.snack(errorText){mPresenter.forceLoad()}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        isDestroyedBySystem = false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        isDestroyedBySystem = true
    }

    override fun onStop() {
        super.onStop()
        mPresenter.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mErrorSnackbar?.dismiss()
        mPresenter.unbindView()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isDestroyedBySystem) {
            PresenterCache.removePresenter(tag)
        }
    }

    protected abstract val presenter: P
}
