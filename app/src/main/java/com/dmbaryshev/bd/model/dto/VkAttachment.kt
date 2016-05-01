package com.dmbaryshev.bd.model.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

open class VkAttachment(
        @SerializedName("type")
        @Expose
        open var type: String ="",
        @SerializedName("photo")
        @Expose
        open var photo: VkPhoto? = null
                       ):RealmObject(){}
