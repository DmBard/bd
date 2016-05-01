package com.dmbaryshev.bd.model.view_model

import android.os.Parcel
import android.os.Parcelable

data class GroupVM(
        val id: Int,
        val name: String,
        val screenName: String,
        val isClosed: Int,
        val type: String,
        val isAdmin: Int,
        var isMember: Int,
        val photo50: String,
        val photo100: String,
        val photo200: String,
        val giftCounts: Int
) : IViewModel, Parcelable {
    constructor(source: Parcel) : this(source.readInt(),
                                       source.readString(),
                                       source.readString(),
                                       source.readInt(),
                                       source.readString(),
                                       source.readInt(),
                                       source.readInt(),
                                       source.readString(),
                                       source.readString(),
                                       source.readString(),
                                       source.readInt() )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeInt(id)
        dest?.writeString(name)
        dest?.writeString(screenName)
        dest?.writeInt(isClosed)
        dest?.writeString(type)
        dest?.writeInt(isAdmin)
        dest?.writeInt(isMember)
        dest?.writeString(photo50)
        dest?.writeString(photo100)
        dest?.writeString(photo200)
        dest?.writeInt(giftCounts)
    }

    companion object {
        @JvmField final val CREATOR: Parcelable.Creator<GroupVM> = object : Parcelable.Creator<GroupVM> {
            override fun createFromParcel(source: Parcel): GroupVM {
                return GroupVM(source)
            }

            override fun newArray(size: Int): Array<GroupVM?> {
                return arrayOfNulls(size)
            }
        }
    }
}