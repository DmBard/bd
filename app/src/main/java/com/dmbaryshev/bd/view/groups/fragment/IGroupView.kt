package com.dmbaryshev.bd.view.groups.fragment

import com.dmbaryshev.bd.model.view_model.GiftVM
import com.dmbaryshev.bd.model.view_model.GroupVM
import com.dmbaryshev.bd.model.view_model.UserVM
import com.dmbaryshev.bd.view.common.IView

interface IGroupView: IView<GroupVM> {
    fun getUser(): UserVM

    fun showFilteredGifts(filteredGifts: List<GiftVM>)
}