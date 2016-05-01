package com.dmbaryshev.bd.presenter

import com.dmbaryshev.bd.model.view_model.UserVM
import com.dmbaryshev.bd.network.ResponseAnswer
import com.dmbaryshev.bd.presenter.common.BasePresenter
import com.dmbaryshev.bd.view.calendar.ICalendarView
import com.prolificinteractive.materialcalendarview.CalendarDay
import rx.Observable
import java.util.*

class CalendarPresenter : BasePresenter<ICalendarView, UserVM>() {
    private val mUsers: List<UserVM> by lazy { mView!!.getUsers() }

    override fun showAnswer(answer: MutableList<UserVM>?) {
        mView?.showData(answer ?: return)
    }

    override fun initDnObservable(): Observable<ResponseAnswer<UserVM>>? {
        return null
    }

    override fun initNetworkObservable(): Observable<ResponseAnswer<UserVM>>? {
        return null
    }

    override fun onNextNetwork(responseAnswer: ResponseAnswer<UserVM>) {
    }

    override fun onErrorNetwork(e: Throwable) {
    }

    override fun load() {
    }

    fun getBirthdaysByDate(day: Int, month: Int) {
        mView?.showData(mUsers.filter { it.bdate != null && it.bdate!!.size > 1 && it.bdate!![0] == day && it.bdate!![1] == month })
    }

    fun getEvents(): Collection<CalendarDay> {
        var calendarDays: MutableList<CalendarDay> = ArrayList()
        val users = mView?.getUsers()
        users?.forEach {
            if (it.bdate != null && it.bdate!!.size > 1) {
                var calendar = Calendar.getInstance()
                calendar.set(Calendar.DAY_OF_MONTH, it.bdate!![0])
                calendar.set(Calendar.MONTH, it.bdate!![1] - 1)
                calendarDays.add(CalendarDay(calendar))
            }
        }
        return calendarDays
    }
}