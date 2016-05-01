package com.dmbaryshev.bd.model.dto.group_gift.gift

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

open class VkPrice(
        @SerializedName("amount")
        @Expose
        open var amount: Long = 0,
        @SerializedName("currency")
        @Expose
        open var currency: VkCurrency? = null,
        @SerializedName("text")
        @Expose
        open var text: String = ""
                  ) : RealmObject() {}
