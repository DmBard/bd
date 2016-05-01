package com.dmbaryshev.bd.model.repository

import com.dmbaryshev.bd.model.dto.VkMessage
import com.dmbaryshev.bd.model.dto.common.CommonResponse
import com.dmbaryshev.bd.model.dto.common.SingleResponse
import com.dmbaryshev.bd.model.mapper.BaseMapper
import com.dmbaryshev.bd.model.mapper.MessageMapper
import com.dmbaryshev.bd.model.view_model.MessageVM
import com.dmbaryshev.bd.network.ApiHelper
import com.dmbaryshev.bd.network.ResponseAnswer
import com.dmbaryshev.bd.network.errors.NetworkUnavailableException
import com.dmbaryshev.bd.utils.isOnline
import retrofit2.Response
import rx.Observable
import rx.schedulers.Schedulers

class MessageRepo : BaseRepo<VkMessage, MessageVM>() {

    override fun save(items: List<VkMessage>?) {}

    fun sendMessage(userId: Int, messageText: String): Observable<SingleResponse<Int>> {
        return Observable.defer {
            if (isOnline()) {
                ApiHelper.createService().sendMessage(userId, messageText)
            } else Observable.error<SingleResponse<Int>>(NetworkUnavailableException())
        }.subscribeOn(Schedulers.newThread())
    }

    fun load(userId: Int,
             messagesCount: Int,
             offset: Int): Observable<ResponseAnswer<MessageVM>> {
        val networkObservable = Observable.defer {
            if (isOnline()) {
                ApiHelper.createService().getMessageHistory(userId, messagesCount, offset)
            } else Observable.error<Response<CommonResponse<VkMessage>>>(NetworkUnavailableException())
        }.subscribeOn(Schedulers.newThread())
        return load(networkObservable)
    }

    override fun initMapper(): BaseMapper<VkMessage, MessageVM> {
        return MessageMapper()
    }
}
