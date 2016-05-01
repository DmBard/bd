package com.dmbaryshev.bd.view.gift_details

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.dmbaryshev.bd.R
import com.dmbaryshev.bd.utils.find

class GiftImagesPageAdapter(private val mContext: Context, private val mItems: List<String>) : PagerAdapter() {

    override fun getCount(): Int {
        return mItems.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as FrameLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = LayoutInflater.from(mContext)
        val itemView = layoutInflater.inflate(R.layout.item_gift_images_pager, container, false)

        var imageView = itemView.find<ImageView>(R.id.iv_gift_image)
        Glide.with(mContext).load(mItems[position]).into(imageView)

        container.addView(itemView)

        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as FrameLayout)
    }
}