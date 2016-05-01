package com.dmbaryshev.bd.model.repository

import com.dmbaryshev.bd.model.dto.common.SingleResponse
import com.dmbaryshev.bd.model.dto.likes.VkLikes
import com.dmbaryshev.bd.network.ApiHelper
import com.dmbaryshev.bd.network.errors.NetworkUnavailableException
import com.dmbaryshev.bd.utils.isOnline
import rx.Observable
import rx.schedulers.Schedulers

class LikesRepo {
    fun addLike(code: String, ownerId: Int, itemId: Int): Observable<SingleResponse<VkLikes>> {
        return Observable.defer {
            if (isOnline()) {
                ApiHelper.createService().addLike(code, ownerId, itemId)
            } else Observable.error<SingleResponse<VkLikes>>(NetworkUnavailableException())
        }.subscribeOn(Schedulers.newThread())
    }

    fun deleteLike(code: String, ownerId: Int, itemId: Int): Observable<SingleResponse<VkLikes>> {
        return Observable.defer {
            if (isOnline()) {
                ApiHelper.createService().deleteLike(code, ownerId, itemId)
            } else Observable.error<SingleResponse<VkLikes>>(NetworkUnavailableException())
        }.subscribeOn(Schedulers.newThread())

    }
}