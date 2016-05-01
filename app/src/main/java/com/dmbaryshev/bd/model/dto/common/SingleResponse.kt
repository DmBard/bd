package com.dmbaryshev.bd.model.dto.common

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SingleResponse<T>(
        @SerializedName("response")
        @Expose
        val mVkResponse: T?
)