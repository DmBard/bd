package com.dmbaryshev.bd.model.dto.group_gift

import com.dmbaryshev.bd.model.dto.common.VkResponse
import com.dmbaryshev.bd.model.dto.group_gift.gift.VkGift
import com.dmbaryshev.bd.model.dto.group_gift.group.VkGroup
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class VkGroupGift(
        @SerializedName("groups")
        @Expose
        val groups: VkResponse<VkGroup>,
        @SerializedName("gifts")
        @Expose
        val gifts: VkResponse<VkGift>,
        @SerializedName("gift_counts")
        @Expose
        val giftCounts: List<Int>
)