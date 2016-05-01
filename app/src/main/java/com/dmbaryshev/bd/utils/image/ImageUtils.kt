package com.dmbaryshev.bd.utils.image

import android.content.Context
import android.graphics.Bitmap
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget

fun ImageView.loadRoundedImage(context: Context, photoUrl:String) {
    Glide.with(context)
            .load(photoUrl)
            .asBitmap()
            .centerCrop()
            .into(object: BitmapImageViewTarget(this) {
                override fun setResource(resource: Bitmap?) {
                    val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.resources, resource);
                    circularBitmapDrawable.isCircular = true;
                    this@loadRoundedImage.setImageDrawable(circularBitmapDrawable);
                }
            })
}