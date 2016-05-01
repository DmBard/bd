package com.dmbaryshev.bd.model.dto.common

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CommonError(
        @SerializedName("error")
        @Expose
        val mVkError: VkError?)
