package com.dmbaryshev.bd.view.gift_details

import com.dmbaryshev.bd.model.view_model.GiftVM
import com.dmbaryshev.bd.model.view_model.GroupVM
import com.dmbaryshev.bd.view.common.IView

interface IGiftDetailsView: IView<GiftVM> {
    fun getGift(): GiftVM?

    fun openMessagesFragment(group: GroupVM)

    fun showLike(isLiked: Boolean)
}