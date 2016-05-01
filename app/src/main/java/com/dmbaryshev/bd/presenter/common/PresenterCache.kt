package com.dmbaryshev.bd.presenter.common

import android.support.v4.util.SimpleArrayMap
import com.dmbaryshev.bd.model.view_model.IViewModel
import com.dmbaryshev.bd.utils.dogw
import com.dmbaryshev.bd.view.common.IView

object PresenterCache {

    private var presenters: SimpleArrayMap<String, BasePresenter<out IView<out IViewModel>, out IViewModel>>? = null

    @SuppressWarnings("unchecked") // Handled internally
    fun <T : BasePresenter<out IView<out IViewModel>,  out IViewModel>> getPresenter(who: String,
                                                                                     newPresenter: T): T {
        if (presenters == null) {
            presenters = SimpleArrayMap()
        }
        var presenter: T? = null
        try {
            presenter = presenters?.get(who) as T
        } catch (e: ClassCastException) {
            dogw("PresenterActivity",
                 "Duplicate Presenter tag identified: $who. This could cause issues with state.")
        }

        if (presenter == null) {
            presenter = newPresenter
            presenters!!.put(who, presenter)
        }
        return presenter
    }

    fun removePresenter(who: String) {
        if (presenters != null) {
            presenters!!.remove(who)
        }
    }
}
