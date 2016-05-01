package com.dmbaryshev.bd.view.gift.adapter

import android.support.v4.util.SparseArrayCompat
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.crashlytics.android.Crashlytics
import com.dmbaryshev.bd.model.view_model.GiftVM
import com.dmbaryshev.bd.utils.makeLogTag
import com.dmbaryshev.bd.view.common.adapter.*

class GiftsAdapter(var items: MutableList<IViewType>,val listener: IHolderClick) : SearchableAdapter<IViewType, RecyclerView.ViewHolder>(items) {
    private var delegateAdapters = SparseArrayCompat<IViewTypeDelegateAdapter>()
    private val loadingItem = LoadingItem()

    init {
        delegateAdapters.put(AdapterConstants.LOADING, LoadingDelegateAdapter())
        delegateAdapters.put(AdapterConstants.GIFT, GiftsDelegateAdapter(listener))
        items.add(loadingItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return delegateAdapters.get(viewType).onCreateViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegateAdapters.get(getItemViewType(position))?.onBindViewHolder(holder,  items[position])

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return this.items[position].getViewType()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun getGift(adapterPosition: Int): GiftVM {
        return items[adapterPosition] as GiftVM
    }

    fun clearAndAddGifts(gifts: Collection<GiftVM>) {
        items.clear()
        items.addAll(gifts)
        items.add(loadingItem)
        notifyDataSetChanged()
    }

    fun removeLoader() {
        val initPosition = items.size - 1
        try {
            if(initPosition < 0 || items[initPosition] !is LoadingItem) return
        } catch(e: ArrayIndexOutOfBoundsException) {
            Crashlytics.logException(e)
            return
        }
        items.removeAt(initPosition)
        notifyItemRemoved(initPosition)
    }

    private fun getLastPosition() = if (items.lastIndex == -1) 0 else items.lastIndex

    companion object {
        private val TAG = makeLogTag(GiftsAdapter::class.java)
    }
}