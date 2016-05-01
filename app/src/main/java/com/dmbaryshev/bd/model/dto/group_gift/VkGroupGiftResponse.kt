package com.dmbaryshev.bd.model.dto.group_gift

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class VkGroupGiftResponse(
        @SerializedName("response")
        @Expose
        val mVkResponse: VkGroupGift?
)

