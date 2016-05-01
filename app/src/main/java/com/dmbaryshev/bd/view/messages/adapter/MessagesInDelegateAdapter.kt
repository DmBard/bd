package com.dmbaryshev.bd.view.messages.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.dmbaryshev.bd.R
import com.dmbaryshev.bd.model.view_model.MessageVM
import com.dmbaryshev.bd.utils.inflate
import com.dmbaryshev.bd.view.common.adapter.IViewType
import com.dmbaryshev.bd.view.common.adapter.IViewTypeDelegateAdapter

class MessagesInDelegateAdapter() : IViewTypeDelegateAdapter {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_in_message))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: IViewType?) {
        holder as ViewHolder
        holder.bind(item as MessageVM)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal val ivAttachmentPhoto: ImageView
        internal val tvText: TextView

        init {
            ivAttachmentPhoto = view.findViewById(R.id.iv_attachment_photo) as ImageView
            tvText = view.findViewById(R.id.tv_text) as TextView
        }

        fun bind(item: MessageVM):Any = with(item) {
            tvText.text = this.body

            if (this.getPhoto() == null) {
                ivAttachmentPhoto.setImageDrawable(null)
            } else {
                Glide.with(ivAttachmentPhoto.context).load(this.getPhoto()).into(ivAttachmentPhoto)
            }
        }
    }
}