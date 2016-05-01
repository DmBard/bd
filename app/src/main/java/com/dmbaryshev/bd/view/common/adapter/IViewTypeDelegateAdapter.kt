package com.dmbaryshev.bd.view.common.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

interface IViewTypeDelegateAdapter {

    fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder

    fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: IViewType?)
}