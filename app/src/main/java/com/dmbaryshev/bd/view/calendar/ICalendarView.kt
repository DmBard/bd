package com.dmbaryshev.bd.view.calendar

import com.dmbaryshev.bd.model.view_model.UserVM
import com.dmbaryshev.bd.view.common.IView

interface ICalendarView : IView<UserVM> {
    fun getUsers(): List<UserVM>
}
