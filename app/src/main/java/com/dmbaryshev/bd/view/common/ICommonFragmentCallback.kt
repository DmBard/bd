package com.dmbaryshev.bd.view.common

import com.dmbaryshev.bd.model.view_model.UserVM

interface ICommonFragmentCallback {
    fun setCollapsingToolbarImage(imageUrl:String="", expandAppBar:Boolean = true)

    fun showTitle(title: String)

    fun showSubtitle(subtitle: String)

    fun openGroupsFragment(user: UserVM, backstack: Boolean = true)

}
