package com.dmbaryshev.bd.view.common

import com.dmbaryshev.bd.model.view_model.IViewModel

interface IView<T : IViewModel> {
    fun showError(errorTextRes: Int)

    fun showError(errorText: String)

    fun stopLoad()

    fun startLoad()

    fun showData(data: Collection<T>)

    fun showCount(count: Int)
}

