package com.dmbaryshev.bd.model.dto.group_gift.gift

import com.dmbaryshev.bd.model.dto.VkPhoto
import com.dmbaryshev.bd.model.dto.common.RealmCachedObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class VkFavoriteGift(
        @SerializedName("id")
        @Expose
        @PrimaryKey
        override var id: Int = 0,
        @SerializedName("owner_id")
        @Expose
        open var ownerId: Int = 0,
        @SerializedName("title")
        @Expose
        open var title: String = "",
        @SerializedName("description")
        @Expose
        open var description: String = "",
        @SerializedName("price")
        @Expose
        open var price: VkPrice? = null,
        @SerializedName("category")
        @Expose
        open var category: VkCategory? = null,
        @SerializedName("date")
        @Expose
        open var date: Int = 0,
        @SerializedName("thumb_photo")
        @Expose
        open var thumbPhoto: String ="",
        @SerializedName("photos")
        @Expose
        open var photos: RealmList<VkPhoto> ?= null,
        override var updateTime: Long? = 0
): RealmObject(), RealmCachedObject {

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false;
        }
        if (!VkGift::class.java.isAssignableFrom(other.javaClass)) {
            return false;
        }
        val otherGift =  other as VkGift;

        return otherGift.id == id
    }

    override fun hashCode(): Int {
        return id
    }
}