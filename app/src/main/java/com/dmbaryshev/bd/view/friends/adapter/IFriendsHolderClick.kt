package com.dmbaryshev.bd.view.friends.adapter

import com.dmbaryshev.bd.view.common.adapter.IHolderClick

interface IFriendsHolderClick : IHolderClick {
    fun openMessage(adapterPosition: Int)
}
