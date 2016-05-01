package com.dmbaryshev.bd.view.gift.fragment

import com.dmbaryshev.bd.model.view_model.GiftVM
import com.dmbaryshev.bd.model.view_model.GroupVM
import com.dmbaryshev.bd.view.common.IView

interface IGiftView: IView<GiftVM> {
    fun showFilteredGifts(filteredGiftsByGroup: List<GiftVM>)

    fun removeListLoader()

    fun getGroup(): GroupVM?

    fun changeMemberStatus(isMember: Boolean)
}