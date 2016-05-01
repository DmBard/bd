package com.dmbaryshev.bd.view.messages.fragment

import com.dmbaryshev.bd.model.view_model.MessageVM
import com.dmbaryshev.bd.view.common.IView

interface IMessageView : IView<MessageVM> {
    fun addMessage(messageVM: MessageVM)
}
