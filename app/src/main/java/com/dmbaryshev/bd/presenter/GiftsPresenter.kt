package com.dmbaryshev.bd.presenter

import com.dmbaryshev.bd.R
import com.dmbaryshev.bd.model.dto.common.SingleResponse
import com.dmbaryshev.bd.model.repository.GiftRepo
import com.dmbaryshev.bd.model.repository.GroupRepo
import com.dmbaryshev.bd.model.view_model.GiftVM
import com.dmbaryshev.bd.network.ResponseAnswer
import com.dmbaryshev.bd.network.errors.NetworkUnavailableException
import com.dmbaryshev.bd.presenter.common.BasePresenter
import com.dmbaryshev.bd.utils.doge
import com.dmbaryshev.bd.utils.makeLogTag
import com.dmbaryshev.bd.view.gift.fragment.IGiftView
import rx.Observable
import rx.android.schedulers.AndroidSchedulers

class GiftsPresenter : BasePresenter<IGiftView, GiftVM>() {
    private var mOffset = 0
    private val OFFSET_STEP = 100
    private var mStopAutoloading = false
    private val giftRepo = GiftRepo()

    override fun showAnswer(answer: MutableList<GiftVM>?) {
        mView?.showData(answer ?: return)
        if (mStopAutoloading) mView?.removeListLoader()
    }

    override fun initNetworkObservable(): Observable<ResponseAnswer<GiftVM>>? {
        mOffset = count
        return giftRepo.load(mView?.getGroup()?.id ?: return null, mOffset)
    }

    override fun initDnObservable(): Observable<ResponseAnswer<GiftVM>>? {
        return giftRepo.getGiftsFromDb(mView?.getGroup()?.id ?: return null)
    }

    override fun load() {
        loadFromRes() {
            loadFromDb()
            loadFromNetwork()
        }
    }

    override fun onNextNetwork(responseAnswer: ResponseAnswer<GiftVM>) {
        isLoading = false
        mView?.stopLoad()
        val answer = responseAnswer.answer
        if (answer == null || answer.size == 0) {
            mStopAutoloading = true
            mView?.removeListLoader()
        }
    }

    override fun onErrorNetwork(e: Throwable) {
        doge(makeLogTag(GiftsPresenter::class.java), "error = ", e)
        mView?.stopLoad()
        when (e) {
            is NetworkUnavailableException -> mView?.showError(R.string.error_network_unavailable)
            else -> mView?.showError(R.string.error_common)
        }
    }

    fun getFilteredGifts(name: String,
                         minPrice: Float,
                         maxPrice: Float) {
        val filteredGiftsByGroup = giftRepo.getFilteredGiftsByGroup(mView?.getGroup()?.id ?: return,
                                                                    name,
                                                                    minPrice,
                                                                    maxPrice)
        mAnswerList.clear()
        mAnswerList.addAll(filteredGiftsByGroup)
        mView?.showFilteredGifts(filteredGiftsByGroup)
    }

    fun loadMore() {
        if (mStopAutoloading) return
        mNetworkObservable = null
        mOffset = count
        mOffset += OFFSET_STEP
        loadFromNetwork()
    }

    fun joinGroup(isMember: Boolean) {
        val groupRepo = GroupRepo()
        val groupId = mView?.getGroup()?.id ?: return
        val observable: Observable<SingleResponse<Int>> = if (isMember) groupRepo.leaveGroup(groupId) else groupRepo.joinGroup(
                groupId)
        addSubscription(observable.observeOn(AndroidSchedulers.mainThread())
                                .subscribe ({
                                                mView?.changeMemberStatus(!isMember)
                                                groupRepo.changeMemberStatus(!isMember, groupId)
                                            }, { onErrorNetwork(it) }))
    }
}