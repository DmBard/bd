package com.dmbaryshev.bd.view.common.adapter

class LoadingItem : IViewType {
    override fun getViewType(): Int {
        return AdapterConstants.LOADING
    }
}