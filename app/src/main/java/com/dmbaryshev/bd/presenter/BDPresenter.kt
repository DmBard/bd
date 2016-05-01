package com.dmbaryshev.bd.presenter

import com.dmbaryshev.bd.model.repository.MyProfileRepo
import com.dmbaryshev.bd.model.repository.UserRepo
import com.dmbaryshev.bd.model.view_model.UserVM
import com.dmbaryshev.bd.network.ResponseAnswer
import com.dmbaryshev.bd.presenter.common.BasePresenter
import com.dmbaryshev.bd.utils.makeLogTag
import com.dmbaryshev.bd.view.IMainView
import rx.Observable
import java.util.*

class BDPresenter : BasePresenter<IMainView, UserVM>() {
    val TAG = makeLogTag(BDPresenter::class.java)
    private val mMyRepo = MyProfileRepo()
    private val mUserRepo = UserRepo()

    override fun showAnswer(answer: MutableList<UserVM>?) {
        mView?.showData(answer ?: return)
    }

    override fun initDnObservable(): Observable<ResponseAnswer<UserVM>>? {
        return  mMyRepo.getMyProfileFromDb()
    }

    override fun initNetworkObservable(): Observable<ResponseAnswer<UserVM>>? {
        return mMyRepo.load()
    }

    override fun onNextNetwork(responseAnswer: ResponseAnswer<UserVM>) {}

    override fun onErrorNetwork(e: Throwable) {}

    override fun load() {
        loadFromRes() {
            loadFromDb()
            loadFromNetwork()
        }
    }

    fun getFriends(): ArrayList<UserVM> {
        return ArrayList(mUserRepo.getListFriendsFromDb())
    }
}