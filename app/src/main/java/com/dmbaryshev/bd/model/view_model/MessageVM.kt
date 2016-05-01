package com.dmbaryshev.bd.model.view_model

import com.dmbaryshev.bd.model.dto.VkAttachment
import com.dmbaryshev.bd.view.common.adapter.IViewType

class MessageVM(val id: Int?, val body: String?, val out: Int, val attachments: List<VkAttachment>?) : IViewModel, IViewType {
    override fun getViewType(): Int {
        return out
    }

    private var photo: String? = null

    fun getPhoto(): String? {
        if (photo != null) {
            return photo
        }
        var photoUrl: String? = null
        if (attachments == null || attachments.size == 0) {
            return null
        }
        for (vkAttachment in attachments) {
            if (vkAttachment.type != "photo") {
                continue
            }
            photoUrl = vkAttachment.photo?.photo130
        }
        photo = photoUrl
        return photoUrl
    }

    override fun toString(): String {
        return "id = " + id
    }

    companion object {
        val OUT = 1
        val IN = 0
    }
}

