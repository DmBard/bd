package com.dmbaryshev.bd.presenter

import com.dmbaryshev.bd.R
import com.dmbaryshev.bd.model.dto.common.SingleResponse
import com.dmbaryshev.bd.model.dto.likes.VkLikes
import com.dmbaryshev.bd.model.repository.GiftRepo
import com.dmbaryshev.bd.model.repository.GroupRepo
import com.dmbaryshev.bd.model.repository.LikesRepo
import com.dmbaryshev.bd.model.view_model.GiftVM
import com.dmbaryshev.bd.network.ResponseAnswer
import com.dmbaryshev.bd.network.errors.NetworkUnavailableException
import com.dmbaryshev.bd.presenter.common.BasePresenter
import com.dmbaryshev.bd.utils.doge
import com.dmbaryshev.bd.utils.makeLogTag
import com.dmbaryshev.bd.view.gift_details.IGiftDetailsView
import rx.Observable
import rx.android.schedulers.AndroidSchedulers

class GiftDetailsPresenter : BasePresenter<IGiftDetailsView, GiftVM>() {
    private val MARKET_CODE = "market"
    private val mGiftRepo = GiftRepo()
    private val mGroupRepo = GroupRepo()
    override fun showAnswer(answer: MutableList<GiftVM>?) {
        mView?.showData(answer ?: return)
    }

    override fun initDnObservable(): Observable<ResponseAnswer<GiftVM>>? {
        return mGiftRepo.getGiftFromDb(mView?.getGift()?.id ?: return null)
    }

    override fun initNetworkObservable(): Observable<ResponseAnswer<GiftVM>>? {
        return null
    }

    override fun onNextNetwork(responseAnswer: ResponseAnswer<GiftVM>) {
    }

    override fun onErrorNetwork(e: Throwable) {
        doge(makeLogTag(GiftDetailsPresenter::class.java), "error = ", e)
        mView?.stopLoad()
        when (e) {
            is NetworkUnavailableException -> mView?.showError(R.string.error_network_unavailable)
            else -> mView?.showError(R.string.error_common)
        }
    }

    override fun load() {
        loadFromRes() {
            loadFromDb()
        }
    }

    fun buy() {
        val group = mGroupRepo.getGroupById(mView?.getGift()?.ownerId ?: return)
        mView?.openMessagesFragment(group)
    }

    fun like(isLiked: Boolean) {
        val likesRepo = LikesRepo()
        val itemId = mView?.getGift()?.id ?: return
        val ownerId = mView?.getGift()?.ownerId ?: return
        val observable: Observable<SingleResponse<VkLikes>> = if (isLiked) likesRepo.deleteLike(MARKET_CODE, ownerId, itemId)
                                                              else likesRepo.addLike(MARKET_CODE, ownerId, itemId)
        addSubscription(observable.observeOn(AndroidSchedulers.mainThread())
                                .subscribe ({
                                                mView?.showLike(!isLiked)
                                                mGiftRepo.changeGiftLike(!isLiked, itemId)
                                            },
                                            { onErrorNetwork(it) })
        )
    }
}