package com.dmbaryshev.bd.model.dto

import com.dmbaryshev.bd.model.dto.common.RealmCachedObject
import com.dmbaryshev.bd.model.dto.group_gift.gift.VkGift
import com.dmbaryshev.bd.model.dto.group_gift.group.VkGroup
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class VkUser(
        @SerializedName("id")
        @Expose
        @PrimaryKey
        override  var id: Int = 0,
        @SerializedName("first_name")
        @Expose
        open var firstName: String = "",
        @SerializedName("last_name")
        @Expose
        open var lastName: String = "",
        @SerializedName("photo_100")
        @Expose
        open var photo100: String = "",
        @SerializedName("photo_max_orig")
        @Expose
        open var photoMax: String = "",
        @SerializedName("online")
        @Expose
        open var online: Int = 0,
        @SerializedName("last_seen")
        @Expose
        open var lastSeen: VkLastSeen? = null,
        @SerializedName("bdate")
        @Expose
        open var bdate: String? = "",
        open var gifts: RealmList<VkGift>  = RealmList(),
        open var groups: RealmList<VkGroup>  = RealmList(),
        override var updateTime: Long? = 0
) : RealmObject(), RealmCachedObject {}