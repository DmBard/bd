package com.dmbaryshev.bd.view.common.adapter

import android.support.v7.widget.RecyclerView
import com.dmbaryshev.bd.model.view_model.IViewModel

abstract class SearchableAdapter<T : IViewModel, A : RecyclerView.ViewHolder>(var mItems: MutableList<T>) : RecyclerView.Adapter<A>() {

    fun animateTo(items: List<T>) {
        applyAndAnimateRemovals(items)
        applyAndAnimateAdditions(items)
        applyAndAnimateMovedItems(items)
    }

    private fun applyAndAnimateRemovals(newItems: List<T>) {
        for (i in mItems.size - 1 downTo 0) {
            val item = mItems[i]
            if (!newItems.contains(item)) {
                removeItem(i)
            }
        }
    }

    private fun applyAndAnimateAdditions(newItems: List<T>) {
        newItems.forEachIndexed { i, item ->
            if (!mItems.contains(item)) addItem(i, item)
        }
    }

    private fun applyAndAnimateMovedItems(newItems: List<T>) {
        for (toPosition in newItems.indices.reversed()) {
            val item = newItems[toPosition]
            val fromPosition = mItems.indexOf(item)
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition)
            }
        }
    }

    fun removeItem(position: Int): T {
        val item = mItems.removeAt(position)
        notifyItemRemoved(position)
        return item
    }

    fun addItem(position: Int, item: T) {
        mItems.add(position, item)
        notifyItemInserted(position)
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        val item = mItems.removeAt(fromPosition)
        mItems.add(toPosition, item)
        notifyItemMoved(fromPosition, toPosition)
    }
}
