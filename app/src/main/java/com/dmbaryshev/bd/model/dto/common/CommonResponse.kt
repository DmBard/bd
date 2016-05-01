package com.dmbaryshev.bd.model.dto.common

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CommonResponse<T>(
        @SerializedName("response")
        @Expose
        val mVkResponse: VkResponse<T>?
                            )
