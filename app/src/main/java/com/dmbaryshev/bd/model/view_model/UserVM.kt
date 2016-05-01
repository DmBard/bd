package com.dmbaryshev.bd.model.view_model

import android.os.Parcel
import android.os.Parcelable
import com.dmbaryshev.bd.model.dto.VkLastSeen
import java.util.*

data class UserVM(
        var id: Int = 0,
        var name: String,
        var photo100: String,
        var photoMax: String,
        var online: Int = 0,
        var bdate: IntArray?,
        var lastSeen: VkLastSeen?,
        var updateTime: Long = 0 ) : Parcelable, IViewModel, Comparable<UserVM> {

    override fun compareTo(other: UserVM): Int {
        val now = Calendar.getInstance();
        val calendar = other.getDate()

        val monthNow = now.get(Calendar.MONTH);
        val month = calendar.get(Calendar.MONTH);

        if (monthNow < month) return -1;
        else if (monthNow == month) return now.get(Calendar.DAY_OF_MONTH) - calendar.get(Calendar.DAY_OF_MONTH);
        else return 1;
    }

    fun getDate(): Calendar {
        if (bdate == null) return Calendar.getInstance()
        val day: Int = (bdate as IntArray)[0]
        val month: Int = (bdate as IntArray)[1]
        val calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, day)
        calendar.set(Calendar.MONTH, month-1)
        return calendar
    }

    constructor(source: Parcel) : this (
            source.readInt(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readInt(),
            source.createIntArray(),
            VkLastSeen(source.readLong(), source.readInt()),
            source.readLong()
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeInt(id)
        dest?.writeString(name)
        dest?.writeString(photo100)
        dest?.writeString(photoMax)
        dest?.writeInt(online)
        dest?.writeIntArray(bdate)
        dest?.writeLong(lastSeen?.time ?: 0)
        dest?.writeInt(lastSeen?.platform ?: 0)
        dest?. writeLong(updateTime)
    }

    companion object {
        @JvmField final val CREATOR: Parcelable.Creator<UserVM> = object : Parcelable.Creator<UserVM> {
            override fun createFromParcel(source: Parcel): UserVM {
                return UserVM(source)
            }

            override fun newArray(size: Int): Array<UserVM?> {
                return arrayOfNulls(size)
            }
        }
    }
}
