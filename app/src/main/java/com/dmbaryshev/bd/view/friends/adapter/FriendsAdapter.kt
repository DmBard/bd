package com.dmbaryshev.bd.view.friends.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.crashlytics.android.Crashlytics
import com.dmbaryshev.bd.BuildConfig
import com.dmbaryshev.bd.R
import com.dmbaryshev.bd.model.view_model.UserVM
import com.dmbaryshev.bd.utils.dogi
import com.dmbaryshev.bd.utils.image.loadRoundedImage
import com.dmbaryshev.bd.utils.makeLogTag
import com.dmbaryshev.bd.view.common.adapter.SearchableAdapter

class FriendsAdapter(var items: MutableList<UserVM>, listener: IFriendsHolderClick) : SearchableAdapter<UserVM, FriendsAdapter.ViewHolder>(items) {
    private var context: Context? = null
    private var mListener: IFriendsHolderClick? = null

    init {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_friend, parent, false)
        return ViewHolder(view, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        dogi(TAG, "onBindViewHolder position = $position")
        if ( mItems.size <= 0) return

        val item = mItems[position]
        holder.tvName.text = item.name
        holder.vStatus.visibility = if (item.online == 1) View.VISIBLE else View.GONE
        var text =""
        if (item.bdate != null) {
            if ( item.bdate?.size == 2) text = String.format("%02d.%02d",
                                                             (item.bdate as IntArray)[0],
                                                             (item.bdate as IntArray)[1])
            else if (item.bdate?.size == 3) text = String.format("%02d.%02d.%02d",
                                                                 (item.bdate as IntArray)[0],
                                                                 (item.bdate as IntArray)[1],
                                                                 (item.bdate as IntArray)[2])
        }

        holder.tvBdate.text = text
        holder.tvBdate.visibility = if(item.bdate == null) View.GONE else View.VISIBLE
        holder.ivBdate.visibility = if(item.bdate == null) View.GONE else View.VISIBLE
        holder.ivAvatar.loadRoundedImage(context!!, item.photo100)
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    fun getUser(adapterPosition: Int): UserVM? {
        try {
            return mItems[adapterPosition]
        } catch(e: ArrayIndexOutOfBoundsException) {
            if(!BuildConfig.DEBUG) Crashlytics.logException(e)
            return null
        }
    }

    class ViewHolder(view: View, protected var listener: IFriendsHolderClick?) : RecyclerView.ViewHolder(view) {
        internal val ivAvatar: ImageView
        internal val ivMessage: ImageView
        internal val ivBdate: ImageView
        internal val tvName: TextView
        internal val tvBdate: TextView
        internal val vStatus: View

        init {
            ivAvatar = view.findViewById(R.id.iv_avatar) as ImageView
            ivMessage = view.findViewById(R.id.iv_message) as ImageView
            ivBdate = view.findViewById(R.id.iv_bdate) as ImageView
            tvName = view.findViewById(R.id.tv_name) as TextView
            tvBdate = view.findViewById(R.id.tv_bdate) as TextView
            vStatus = view.findViewById(R.id.v_status)
            view.setOnClickListener { listener?.onItemClick(adapterPosition) }
            ivMessage.setOnClickListener { listener?.openMessage(adapterPosition) }
        }
    }

    companion object {
        private val TAG = makeLogTag(FriendsAdapter::class.java)
    }
}
