package com.dmbaryshev.bd.view.common.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.dmbaryshev.bd.R
import com.dmbaryshev.bd.utils.inflate

class LoadingDelegateAdapter : IViewTypeDelegateAdapter {

    override fun onCreateViewHolder(parent: ViewGroup) = LoadingViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: IViewType?) {
    }

    class LoadingViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            parent.inflate(R.layout.item_loading)) {
    }
}
