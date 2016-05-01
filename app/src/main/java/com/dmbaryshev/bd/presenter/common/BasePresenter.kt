package com.dmbaryshev.bd.presenter.common

import com.dmbaryshev.bd.R
import com.dmbaryshev.bd.model.view_model.IViewModel
import com.dmbaryshev.bd.network.ResponseAnswer
import com.dmbaryshev.bd.view.common.IView
import rx.Observable
import rx.Subscription
import rx.subscriptions.CompositeSubscription
import java.util.*

abstract class BasePresenter<T : IView<VM>, VM : IViewModel> {
    protected var mView: T? = null
    protected var mAnswerList: MutableList<VM> = ArrayList()
    protected var mInitialAnswerList: MutableList<VM> = ArrayList()
    protected val mCompositeSubscription = CompositeSubscription()
    protected var mDbObservable: Observable<ResponseAnswer<VM>>? = null
    protected var mNetworkObservable: Observable<ResponseAnswer<VM>>? = null
    protected var isLoading = true
    var count: Int = 0
        private set

    protected fun addSubscription(subscription: Subscription) {
        mCompositeSubscription.add(subscription)
    }

    protected fun parseData(data: ResponseAnswer<VM>?) {
        val answer = data?.answer
        if (data != null) {
            mInitialAnswerList.clear()
            mInitialAnswerList.addAll(answer ?: return)
            mAnswerList.clear()
            mAnswerList.addAll(answer)
        } else {
            mView?.showError(R.string.error_common)
            return
        }

        count = data.count
        val vkError = data.vkError

        mView?.showCount(count)

        if (vkError != null) {
            mView?.showError(vkError.errorMsg)
            return
        }
        showAnswer(mAnswerList)
    }

    fun onStop() {
        mView?.stopLoad()
        mCompositeSubscription.clear()
    }

    fun bindView(view: T) {
        this.mView = view
    }

    fun unbindView() {
        this.mView = null
    }

    open fun forceLoad() {
        mNetworkObservable = null
        mCompositeSubscription.clear()
        isLoading = true
        mAnswerList.clear()
        load()
    }

    protected fun loadFromRes(loadRes: () -> Unit) {
        if (mAnswerList.isEmpty() || isLoading) {
            mView?.startLoad()
            loadRes()
        } else {
            showAnswer(mAnswerList)
            mView?.stopLoad()
        }
    }

    protected fun loadFromDb() {
        if (mDbObservable == null) mDbObservable = initDnObservable()
        val dbSubscription = mDbObservable?.subscribe({ onNextDb(it) })
        addSubscription(checkNotNull(dbSubscription))
    }

    open protected  fun onNextDb(it: ResponseAnswer<VM>?) {
        this.parseData(it)
    }

    protected fun loadFromNetwork() {
        if (mNetworkObservable == null) mNetworkObservable = initNetworkObservable()
        val networkSubscription = mNetworkObservable?.subscribe({ onNextNetwork(it) },
                                                                { onErrorNetwork(it) },
                                                                { onCompleteNetwork() })
        addSubscription(checkNotNull(networkSubscription))
    }

    open protected fun onCompleteNetwork() {
        isLoading = false
        mView?.stopLoad()
    }

    protected abstract fun showAnswer(answer: MutableList<VM>?)
    protected abstract fun initDnObservable(): Observable<ResponseAnswer<VM>>?
    protected abstract fun initNetworkObservable(): Observable<ResponseAnswer<VM>>?
    protected abstract fun onNextNetwork(responseAnswer: ResponseAnswer<VM>)
    protected abstract fun onErrorNetwork(e: Throwable)
    abstract fun load()
}
