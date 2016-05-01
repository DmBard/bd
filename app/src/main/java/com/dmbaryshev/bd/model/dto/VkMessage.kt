package com.dmbaryshev.bd.model.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.annotations.PrimaryKey

open class VkMessage (
    @SerializedName("id")
    @Expose
    @PrimaryKey
    open var id: Int = 0,
    @SerializedName("body")
    @Expose
    open var body: String ="",
    @SerializedName("user_id")
    @Expose
    open var userId: Int =0,
    @SerializedName("from_id")
    @Expose
    open var fromId: Int =0,
    @SerializedName("date")
    @Expose
    open var date: Int = 0,
    @SerializedName("read_state")
    @Expose
    open var readState: Int = 0,
    @SerializedName("out")
    @Expose
    open var out: Int = 0,
    @SerializedName("attachments")
    @Expose
    open var attachments: RealmList<VkAttachment> ?= null
                     ){}
