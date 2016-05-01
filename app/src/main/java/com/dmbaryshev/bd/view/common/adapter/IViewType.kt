package com.dmbaryshev.bd.view.common.adapter

import com.dmbaryshev.bd.model.view_model.IViewModel

interface IViewType: IViewModel {
    fun getViewType(): Int
}