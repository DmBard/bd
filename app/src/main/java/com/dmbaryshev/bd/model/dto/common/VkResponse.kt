package com.dmbaryshev.bd.model.dto.common

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

data class VkResponse<T>(
        @SerializedName("count")
        @Expose
        val count: Int,
        @SerializedName("items")
        @Expose
        val items: List<T> = ArrayList()
                        )
