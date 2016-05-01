package com.dmbaryshev.bd.presenter

import com.dmbaryshev.bd.R
import com.dmbaryshev.bd.model.repository.MessageRepo
import com.dmbaryshev.bd.model.view_model.GroupVM
import com.dmbaryshev.bd.model.view_model.MessageVM
import com.dmbaryshev.bd.model.view_model.UserVM
import com.dmbaryshev.bd.network.ResponseAnswer
import com.dmbaryshev.bd.network.errors.NetworkUnavailableException
import com.dmbaryshev.bd.presenter.common.BasePresenter
import com.dmbaryshev.bd.utils.doge
import com.dmbaryshev.bd.utils.makeLogTag
import com.dmbaryshev.bd.view.messages.fragment.IMessageView
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import java.util.*

class MessagePresenter : BasePresenter<IMessageView, MessageVM>() {
    var userVM: UserVM? = null
    var groupVM: GroupVM? = null
    private val mMessageRepo = MessageRepo()
    private var mOffset = 0
    private var mCachedAnswerList: LinkedHashSet<MessageVM> = LinkedHashSet()

    fun loadMore() {
        mCompositeSubscription.clear()
        mNetworkObservable = null
        loadFromNetwork()
        mOffset += MESSAGES_COUNT
    }

    override fun showAnswer(answer: MutableList<MessageVM>?) {
        mCachedAnswerList.addAll(answer ?: return)
        mView?.showData(mCachedAnswerList)
    }

    override fun load() {
        loadFromRes() {
            loadFromNetwork()
            mOffset += MESSAGES_COUNT
        }
    }

    override fun onNextNetwork(responseAnswer: ResponseAnswer<MessageVM>) {
        super.parseData(responseAnswer)
        mView?.stopLoad()
    }

    override fun onErrorNetwork(e: Throwable) {
        doge(makeLogTag(MessagePresenter::class.java), "error = ", e)
        mView?.stopLoad()
        when (e) {
            is NetworkUnavailableException -> mView?.showError(R.string.error_network_unavailable)
            else -> mView?.showError(R.string.error_common)
        }
    }

    fun sendMessage(messageText: String) {
        var id: Int? = if (userVM != null) userVM!!.id else if (groupVM != null) -groupVM!!.id else return
        val observable = mMessageRepo.sendMessage(id!!,
                                                   messageText).observeOn(AndroidSchedulers.mainThread())

        val subscription = observable.subscribe ({ response ->
                                                     val messageVM = MessageVM(response.mVkResponse,
                                                                               messageText,
                                                                               MessageVM.OUT,
                                                                               null)
                                                     mView?.addMessage(messageVM)
                                                 }, { onErrorNetwork(it) })
        addSubscription(subscription)
    }

    override fun initDnObservable(): Observable<ResponseAnswer<MessageVM>>? {
        return null
    }

    override fun initNetworkObservable(): Observable<ResponseAnswer<MessageVM>>? {
        var id: Int? = if (userVM != null) userVM!!.id else if (groupVM != null) -groupVM!!.id else return null
        return mMessageRepo.load(id!!,
                                  MESSAGES_COUNT,
                                  mOffset)
    }

    companion object {
        private val MESSAGES_COUNT = 30
    }
}
