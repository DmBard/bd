package com.dmbaryshev.bd.model.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

open class VkLastSeen(
        @SerializedName("time")
        @Expose
        open var time: Long? = 0,
        @SerializedName("platform")
        @Expose
        open var platform: Int? = 0
                     ) : RealmObject() {}
