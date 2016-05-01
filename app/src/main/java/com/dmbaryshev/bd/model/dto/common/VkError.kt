package com.dmbaryshev.bd.model.dto.common

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class VkError(

        @SerializedName("error_code")
        @Expose
        val errorCode: Int,

        @SerializedName("error_msg")
        @Expose
        var errorMsg: String
                  )