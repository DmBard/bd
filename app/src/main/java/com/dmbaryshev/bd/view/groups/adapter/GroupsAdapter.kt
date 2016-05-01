package com.dmbaryshev.bd.view.groups.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.dmbaryshev.bd.R
import com.dmbaryshev.bd.model.view_model.GroupVM
import com.dmbaryshev.bd.utils.dogi
import com.dmbaryshev.bd.utils.image.loadRoundedImage
import com.dmbaryshev.bd.utils.makeLogTag
import com.dmbaryshev.bd.utils.setCountingString
import com.dmbaryshev.bd.view.common.adapter.IHolderClick
import com.dmbaryshev.bd.view.common.adapter.SearchableAdapter

class GroupsAdapter(var items: MutableList<GroupVM>, listener: IHolderClick) : SearchableAdapter<GroupVM, GroupsAdapter.ViewHolder>(items){
    private var context: Context? = null
    private var mListener: IHolderClick? = null

    init {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_group, parent, false)
        return ViewHolder(view, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        dogi(TAG, "onBindViewHolder position = $position")
        if (items.size <= 0) return

        val item = items[position]
        holder.tvName.text = item.name
        holder.ivAvatar.loadRoundedImage(context!!, item.photo100)
        holder.tvGIftCounts.text = item.giftCounts.setCountingString(context ?: return, R.plurals.goods_count)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun getGroup(adapterPosition: Int): GroupVM {
        return items[adapterPosition]
    }

    class ViewHolder(view: View, protected var listener: IHolderClick?) : RecyclerView.ViewHolder(view) {
        internal val ivAvatar: ImageView
        internal val tvName: TextView
        internal val tvGIftCounts: TextView

        init {
            ivAvatar = view.findViewById(R.id.iv_avatar) as ImageView
            tvName = view.findViewById(R.id.tv_name) as TextView
            tvGIftCounts = view.findViewById(R.id.tv_gift_counts) as TextView
            view.setOnClickListener { listener?.onItemClick(adapterPosition) }
        }
    }

    companion object {
        private val TAG = makeLogTag(GroupsAdapter::class.java)
    }
}
