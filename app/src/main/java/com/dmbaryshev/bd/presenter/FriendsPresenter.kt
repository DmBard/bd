package com.dmbaryshev.bd.presenter

import com.dmbaryshev.bd.R
import com.dmbaryshev.bd.model.repository.UserRepo
import com.dmbaryshev.bd.model.view_model.UserVM
import com.dmbaryshev.bd.network.ResponseAnswer
import com.dmbaryshev.bd.network.errors.NetworkUnavailableException
import com.dmbaryshev.bd.presenter.common.BasePresenter
import com.dmbaryshev.bd.utils.doge
import com.dmbaryshev.bd.utils.getInterval
import com.dmbaryshev.bd.utils.makeLogTag
import com.dmbaryshev.bd.view.friends.fragment.IFriendsView
import rx.Observable
import java.util.*

val ACTION_SORT_BY_NAME = 1
val ACTION_SORT_BY_MAJORITY = 2
val ACTION_SORT_BY_BDATE = 3

open class FriendsPresenter : BasePresenter<IFriendsView, UserVM>() {
    var mNamingList: MutableList<UserVM> = LinkedList()
    var mBdateList: MutableList<UserVM> = ArrayList()
    val mUserRepo = UserRepo()

    override fun showAnswer(answer: MutableList<UserVM>?) {
        mView?.showData(answer ?: return)
    }

    override fun initNetworkObservable(): Observable<ResponseAnswer<UserVM>> {
        return mUserRepo.load()
    }

    override fun initDnObservable(): Observable<ResponseAnswer<UserVM>> {
        return mUserRepo.getAllFriendsFromDb()
    }

    override fun load() {
        loadFromRes() {
            loadFromDb()
            loadFromNetwork()
        }
    }

    override fun onNextNetwork(responseAnswer: ResponseAnswer<UserVM>) {
        isLoading = false
        mView?.stopLoad()
    }

    override fun onErrorNetwork(e: Throwable) {
        doge(makeLogTag(FriendsPresenter::class.java), "error = ", e)
        mView?.stopLoad()
        when (e) {
            is NetworkUnavailableException -> mView?.showError(R.string.error_network_unavailable)
            else -> mView?.showError(R.string.error_common)
        }
    }

    fun sort(action: Int) {
        when (action) {
            ACTION_SORT_BY_NAME -> {
                if (mNamingList.size == 0) {
                    mNamingList.clear()
                    mNamingList.addAll(mInitialAnswerList)
                    mNamingList.sortBy { it.name }
                }
                mAnswerList.clear()
                mAnswerList.addAll(mNamingList)
                mView?.showData(mNamingList)
            }

            ACTION_SORT_BY_MAJORITY -> {
                mAnswerList.clear()
                mAnswerList.addAll(mInitialAnswerList)
                mView?.showData(mAnswerList)
            }

            ACTION_SORT_BY_BDATE -> {
                if (mBdateList.size == 0) {
                    mBdateList.clear()
                    mBdateList.addAll(mInitialAnswerList)
                    var removedList: ArrayList<UserVM> = ArrayList()
                    mBdateList.forEach { if (it.bdate == null) removedList.add(it) }
                    mBdateList.removeAll(removedList)
                    mBdateList.sortWith(Comparator { user1, user2 ->
                        user1.getDate().getInterval().compareTo(user2.getDate().getInterval())
                    })
                    mBdateList.addAll(removedList)
                }
                mAnswerList.clear()
                mAnswerList.addAll(mBdateList)
                mView?.showData(mBdateList)
            }
        }
    }

    fun getFilteredFriends(mAllUsers: MutableList<UserVM>,
                           query: String): List<UserVM> {
        return mAllUsers.filter { it.name.toLowerCase().contains(query.toLowerCase()) }
    }
}
