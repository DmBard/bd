package com.dmbaryshev.bd.view.gift.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.dmbaryshev.bd.R
import com.dmbaryshev.bd.model.view_model.GiftVM
import com.dmbaryshev.bd.utils.image.loadRoundedImage
import com.dmbaryshev.bd.utils.inflate
import com.dmbaryshev.bd.view.common.adapter.IHolderClick
import com.dmbaryshev.bd.view.common.adapter.IViewType
import com.dmbaryshev.bd.view.common.adapter.IViewTypeDelegateAdapter

class GiftsDelegateAdapter(val listener: IHolderClick?) : IViewTypeDelegateAdapter {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_gift), listener, parent.context)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: IViewType?) {
        holder as ViewHolder
        holder.bind(item as GiftVM)
    }

    class ViewHolder(view: View, val listener: IHolderClick?, val context: Context) : RecyclerView.ViewHolder(
            view) {
        internal val ivAvatar: ImageView
        internal val tvName: TextView
        internal val tvGIftCounts: TextView

        init {
            ivAvatar = view.findViewById(R.id.iv_avatar) as ImageView
            tvName = view.findViewById(R.id.tv_name) as TextView
            tvGIftCounts = view.findViewById(R.id.tv_gift_price) as TextView
            view.setOnClickListener { listener?.onItemClick(adapterPosition) }
        }

        fun bind(item: GiftVM): Any = with(item) {
            ivAvatar.loadRoundedImage(context, this.thumbPhoto)
            tvName.text = this.title
            tvGIftCounts.text = this.price
            this
        }
    }
}