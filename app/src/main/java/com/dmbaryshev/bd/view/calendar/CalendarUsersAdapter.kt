package com.dmbaryshev.bd.view.calendar

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.dmbaryshev.bd.R
import com.dmbaryshev.bd.model.view_model.UserVM
import com.dmbaryshev.bd.utils.dogi
import com.dmbaryshev.bd.utils.image.loadRoundedImage
import com.dmbaryshev.bd.utils.makeLogTag
import com.dmbaryshev.bd.view.common.adapter.IHolderClick

class CalendarUsersAdapter(items: List<UserVM>?, listener: IHolderClick) : RecyclerView.Adapter<CalendarUsersAdapter.ViewHolder>() {
    private var items: List<UserVM>? = null
    private var context: Context? = null
    private var mListener: IHolderClick? = null

    init {
        if (items != null) {
            this.items = items
            mListener = listener
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_calendar_user, parent, false)
        return ViewHolder(view, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        dogi(TAG, "onBindViewHolder position = $position")
        if (items == null || items!!.size <= 0) return

        val item = items!![position]
        holder.tvName.text = item.name
        holder.ivAvatar.loadRoundedImage(context!!, item.photo100)
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    fun getUser(adapterPosition: Int): UserVM {
        return items!![adapterPosition]
    }

    class ViewHolder(view: View, protected var listener: IHolderClick?) : RecyclerView.ViewHolder(view) {
        internal val ivAvatar: ImageView
        internal val tvName: TextView

        init {
            ivAvatar = view.findViewById(R.id.iv_avatar) as ImageView
            tvName = view.findViewById(R.id.tv_name) as TextView
            view.setOnClickListener { listener?.onItemClick(adapterPosition) }
        }
    }

    companion object {
        private val TAG = makeLogTag(CalendarUsersAdapter::class.java)
    }
}