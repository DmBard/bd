package com.dmbaryshev.bd.model.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class VkPhoto(
        @SerializedName("id")
        @Expose
        @PrimaryKey
        open var id: Int = 0,
        @SerializedName("album_id")
        @Expose
        open var albumId: Int = 0,
        @SerializedName("owner_id")
        @Expose
        open var ownerId: Int = 0,
        @SerializedName("photo_75")
        @Expose
        open var photo75: String = "",
        @SerializedName("photo_130")
        @Expose
        open var photo130: String = "",
        @SerializedName("photo_604")
        @Expose
        open var photo604: String = "",
        @SerializedName("photo_807")
        @Expose
        open var photo807: String = "",
        @SerializedName("photo_1280")
        @Expose
        open var photo1280: String = "",
        @SerializedName("width")
        @Expose
        open var width: Int = 0,
        @SerializedName("height")
        @Expose
        open var height: Int = 0,
        @SerializedName("text")
        @Expose
        open var text: String = "",
        @SerializedName("date")
        @Expose
        open var date: Int = 0,
        @SerializedName("access_key")
        @Expose
        open var accessKey: String = ""
                  ):RealmObject(){}
