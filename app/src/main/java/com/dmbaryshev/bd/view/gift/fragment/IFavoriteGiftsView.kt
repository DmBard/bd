package com.dmbaryshev.bd.view.gift.fragment

import com.dmbaryshev.bd.model.view_model.GiftVM
import com.dmbaryshev.bd.view.common.IView

interface IFavoriteGiftsView : IView<GiftVM> {
    fun removeListLoader()
}