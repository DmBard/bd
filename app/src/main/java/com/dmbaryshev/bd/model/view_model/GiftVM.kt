package com.dmbaryshev.bd.model.view_model

import android.os.Parcel
import android.os.Parcelable
import com.dmbaryshev.bd.view.common.adapter.AdapterConstants
import com.dmbaryshev.bd.view.common.adapter.IViewType

data class GiftVM(
        val id: Int,
        val ownerId: Int,
        val title: String,
        val description: String,
        val price: String,
        val category: String?,
        val date: Int,
        val thumbPhoto: String,
        var photos:MutableList<String>,
        var liked:Boolean
                 ) : Parcelable, IViewType {
    override fun getViewType(): Int {
        return  AdapterConstants.GIFT
    }

    constructor(source: Parcel) : this(source.readInt(),
                                       source.readInt(),
                                       source.readString(),
                                       source.readString(),
                                       source.readString(),
                                       source.readString(),
                                       source.readInt(),
                                       source.readString(),
                                       source.createStringArrayList(),
                                       source.readInt() == 1)

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeInt(id)
        dest?.writeInt(ownerId)
        dest?.writeString(title)
        dest?.writeString(description)
        dest?.writeString(price)
        dest?.writeString(category)
        dest?.writeInt(date)
        dest?.writeString(thumbPhoto)
        dest?.writeStringList(photos)
        dest?.writeInt(if(liked) 1 else 0)
    }

    companion object {
        @JvmField final val CREATOR: Parcelable.Creator<GiftVM> = object : Parcelable.Creator<GiftVM> {
            override fun createFromParcel(source: Parcel): GiftVM {
                return GiftVM(source)
            }

            override fun newArray(size: Int): Array<GiftVM?> {
                return arrayOfNulls(size)
            }
        }
    }
}