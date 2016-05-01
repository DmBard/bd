package com.dmbaryshev.bd.presenter

import com.dmbaryshev.bd.R
import com.dmbaryshev.bd.model.repository.GiftRepo
import com.dmbaryshev.bd.model.repository.GroupRepo
import com.dmbaryshev.bd.model.view_model.GroupVM
import com.dmbaryshev.bd.network.ResponseAnswer
import com.dmbaryshev.bd.network.errors.NetworkUnavailableException
import com.dmbaryshev.bd.network.errors.StopLoadingException
import com.dmbaryshev.bd.presenter.common.BasePresenter
import com.dmbaryshev.bd.utils.PreferencesHelper
import com.dmbaryshev.bd.utils.doge
import com.dmbaryshev.bd.utils.makeLogTag
import com.dmbaryshev.bd.utils.now
import com.dmbaryshev.bd.view.groups.fragment.IGroupView
import rx.Observable

class GroupPresenter : BasePresenter<IGroupView, GroupVM>() {
    private val DAY_IN_MILLIS: Long = 24 * 60 * 60 * 1000
    private val mGiftRepo = GiftRepo()
    private val mGroupRepo = GroupRepo()

    override fun showAnswer(answer: MutableList<GroupVM>?) {
        mView?.showData(answer ?: return)
    }

    override fun initDnObservable(): Observable<ResponseAnswer<GroupVM>>? {
        return mGroupRepo.getGroupGiftsFromDb(mView?.getUser()?.id ?: return null)
    }

    override fun initNetworkObservable(): Observable<ResponseAnswer<GroupVM>>? {
        return mGroupRepo.load(mView?.getUser()?.id ?: return null)
    }

    override fun onNextDb(it: ResponseAnswer<GroupVM>?) {
        super.onNextDb(it)
        if (!isExpired()) {
            isLoading = false
            mView?.stopLoad()
        }
    }

    override fun onNextNetwork(responseAnswer: ResponseAnswer<GroupVM>) {
    }

    override fun onCompleteNetwork() {
        super.onCompleteNetwork()
        val userId = mView?.getUser()?.id ?: return
        PreferencesHelper.setUpdateGroupsTime(userId.toString(), now())
    }

    override fun onErrorNetwork(e: Throwable) {
        doge(makeLogTag(FriendsPresenter::class.java), "error = ", e)
        mView?.stopLoad()
        when (e) {
            is NetworkUnavailableException -> mView?.showError(R.string.error_network_unavailable)
            is StopLoadingException -> return
            else -> mView?.showError(R.string.error_common)
        }
    }

    override fun load() {
        loadFromRes() {
            loadFromDb()
            if (isExpired()) {
                loadFromNetwork()
            }
        }
    }

    private fun isExpired(): Boolean {
        val userId = mView?.getUser()?.id ?: return true
        val time = PreferencesHelper.getUpdateGroupsTime(userId.toString()) ?: return true
        return time + DAY_IN_MILLIS <= now()
    }

    override fun forceLoad() {
        mNetworkObservable = null
        mCompositeSubscription.clear()
        isLoading = true
        mAnswerList.clear()
        loadFromNetwork()
    }

    fun getFilteredGroups(mAllGroups: MutableList<GroupVM>, query: String): List<GroupVM> {
        val filteredGroups = mAllGroups.filter { it.name.toLowerCase().contains(query.toLowerCase()) }
        mAnswerList.clear()
        mAnswerList.addAll(filteredGroups)
        return filteredGroups
    }

    fun getFilteredGifts(name: String, minPrice: Float, maxPrice: Float): Unit {
        mView?.showFilteredGifts(mGiftRepo.getFilteredGiftsByUser(mView?.getUser()?.id ?: return,
                                                                  name,
                                                                  minPrice,
                                                                  maxPrice))
    }
}