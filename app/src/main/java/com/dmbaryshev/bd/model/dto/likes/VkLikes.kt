package com.dmbaryshev.bd.model.dto.likes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

open class VkLikes(
        @SerializedName("likes")
        @Expose
        open var likes: Int = 0,
        @SerializedName("user_likes")
        @Expose
        open var userLikes: Int = 0,
        @SerializedName("count")
        @Expose
        open var count: Int = 0
) : RealmObject(){}