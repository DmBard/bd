package com.dmbaryshev.bd.model.dto.likes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class VkLiked(
        @SerializedName("liked")
        @Expose
        val liked: Int,
        @SerializedName("copied")
        @Expose
        val copied: Int
        )
