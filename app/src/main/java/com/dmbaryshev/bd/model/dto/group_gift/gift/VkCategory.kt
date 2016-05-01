package com.dmbaryshev.bd.model.dto.group_gift.gift

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class VkCategory(
        @SerializedName("id")
        @Expose
        @PrimaryKey
        open var id: Int = 0,
        @SerializedName("name")
        @Expose
        open var name: String = ""
                     ): RealmObject(){}