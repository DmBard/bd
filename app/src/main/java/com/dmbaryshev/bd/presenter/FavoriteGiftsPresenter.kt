package com.dmbaryshev.bd.presenter

import com.dmbaryshev.bd.R
import com.dmbaryshev.bd.model.repository.FavoriteGiftsRepo
import com.dmbaryshev.bd.model.view_model.GiftVM
import com.dmbaryshev.bd.network.ResponseAnswer
import com.dmbaryshev.bd.network.errors.NetworkUnavailableException
import com.dmbaryshev.bd.presenter.common.BasePresenter
import com.dmbaryshev.bd.utils.doge
import com.dmbaryshev.bd.utils.makeLogTag
import com.dmbaryshev.bd.view.gift.fragment.IFavoriteGiftsView
import rx.Observable

class FavoriteGiftsPresenter : BasePresenter<IFavoriteGiftsView, GiftVM>() {
    private var mOffset = 0
    private val OFFSET_STEP = 50
    private var mStopAutoloading = false
    private val mRepo = FavoriteGiftsRepo()

    override fun showAnswer(answer: MutableList<GiftVM>?) {
        mView?.showData(answer ?: return)
        if(mStopAutoloading) mView?.removeListLoader()
    }

    override fun initNetworkObservable(): Observable<ResponseAnswer<GiftVM>>? {
        mOffset = count
        return mRepo.load()
    }

    override fun initDnObservable(): Observable<ResponseAnswer<GiftVM>>? {
        return mRepo.getGiftsFromDb()
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

    fun loadMore() {
        if(mStopAutoloading) return
        mNetworkObservable = null
        mOffset = count
        mOffset += OFFSET_STEP
        loadFromNetwork()
    }
}
