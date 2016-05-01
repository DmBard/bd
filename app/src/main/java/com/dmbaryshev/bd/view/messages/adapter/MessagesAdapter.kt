package com.dmbaryshev.bd.view.messages.adapter

import android.support.v4.util.SparseArrayCompat
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.dmbaryshev.bd.model.view_model.MessageVM
import com.dmbaryshev.bd.utils.makeLogTag
import com.dmbaryshev.bd.view.common.adapter.*
import java.util.*

class MessagesAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: MutableList<IViewType>
    private var delegateAdapters = SparseArrayCompat<IViewTypeDelegateAdapter>()
    private val loadingItem = LoadingItem()


    init {
        delegateAdapters.put(AdapterConstants.LOADING, LoadingDelegateAdapter())
        delegateAdapters.put(MessageVM.OUT, MessagesOutDelegateAdapter())
        delegateAdapters.put(MessageVM.IN, MessagesInDelegateAdapter())
        items = LinkedList()
        items.add(loadingItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return delegateAdapters.get(viewType).onCreateViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegateAdapters.get(getItemViewType(position))?.onBindViewHolder(holder, items[position])
    }

    override fun getItemViewType(position: Int): Int {
        return this.items[position].getViewType()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun addMessage(message: MessageVM) {
        val initPosition = items.size - 1
        items.removeAt(initPosition)
        notifyItemRemoved(initPosition)

        items.add(0, message)
        items.add(loadingItem)
                notifyDataSetChanged()
    }

    fun clearAndAddMessages(messages: Collection<MessageVM>) {
        items.clear()
        items.addAll(messages)
        items.add(loadingItem)
        notifyDataSetChanged()
    }

    private fun getLastPosition() = if (items.lastIndex == -1) 0 else items.lastIndex

    companion object {
        private val TAG = makeLogTag(MessagesAdapter::class.java)
    }

    fun removeLoader() {
        val initPosition = items.size - 1
        if (items[initPosition] !is LoadingItem) return
        items.removeAt(initPosition)
        notifyItemRemoved(initPosition)
    }
}
