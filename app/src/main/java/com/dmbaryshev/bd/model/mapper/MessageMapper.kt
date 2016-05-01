package com.dmbaryshev.bd.model.mapper

import com.dmbaryshev.bd.model.dto.VkMessage
import com.dmbaryshev.bd.model.view_model.MessageVM

class MessageMapper : BaseMapper<VkMessage, MessageVM>() {
    override fun createViewModel(v: VkMessage): MessageVM {
        return MessageVM(v.id, v.body, v.out, v.attachments)
    }
}
