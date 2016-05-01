package com.dmbaryshev.bd.model.dto.group_gift.group

import com.dmbaryshev.bd.model.dto.common.RealmCachedObject
import com.dmbaryshev.bd.model.dto.group_gift.gift.VkGift
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class VkGroup(
        @SerializedName("id")
        @Expose
        @PrimaryKey
        override  var id: Int = 0,
        @SerializedName("name")
        @Expose
        open var name: String = "",
        @SerializedName("screen_name")
        @Expose
        open var screenName: String = "",
        @SerializedName("is_closed")
        @Expose
        open var isClosed: Int = 0,
        @SerializedName("type")
        @Expose
        open var type: String = "",
        @SerializedName("is_admin")
        @Expose
        open var isAdmin: Int = 0,
        @SerializedName("is_member")
        @Expose
        open var isMember: Int = 0,
        @SerializedName("photo_50")
        @Expose
        open var photo50: String = "",
        @SerializedName("photo_100")
        @Expose
        open var photo100: String = "",
        @SerializedName("photo_200")
        @Expose
        open var photo200: String = "",
        open var gifts: RealmList<VkGift> = RealmList(),
        open var giftCounts: Int = 0,
        override var updateTime: Long? = 0
) :RealmObject(), RealmCachedObject {

        override fun equals(other: Any?): Boolean {
                if (other == null) {
                        return false;
                }
                if (!VkGroup::class.java.isAssignableFrom(other.javaClass)) {
                        return false;
                }
                val otherGroup =  other as VkGroup;

                return otherGroup.id == id
        }

        override fun hashCode(): Int {
                return id
        }
}
