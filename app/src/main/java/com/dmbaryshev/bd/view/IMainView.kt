
package com.dmbaryshev.bd.view

import com.dmbaryshev.bd.model.view_model.UserVM
import com.dmbaryshev.bd.view.common.IView

interface IMainView: IView<UserVM> {
    fun openFriendsFragment()
}