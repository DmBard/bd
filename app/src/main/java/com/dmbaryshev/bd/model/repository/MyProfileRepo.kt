package com.dmbaryshev.bd.model.repository

import com.dmbaryshev.bd.model.dto.VkUser
import com.dmbaryshev.bd.model.dto.common.RealmCachedObject
import com.dmbaryshev.bd.model.dto.common.SingleResponse
import com.dmbaryshev.bd.model.dto.common.VkError
import com.dmbaryshev.bd.model.mapper.BaseMapper
import com.dmbaryshev.bd.model.mapper.UserMapper
import com.dmbaryshev.bd.model.view_model.UserVM
import com.dmbaryshev.bd.network.ApiHelper
import com.dmbaryshev.bd.network.ResponseAnswer
import com.dmbaryshev.bd.network.errors.ErrorUtils
import com.dmbaryshev.bd.network.errors.NetworkUnavailableException
import com.dmbaryshev.bd.utils.isOnline
import com.vk.sdk.VKAccessToken
import io.realm.Realm
import io.realm.RealmResults
import retrofit2.Response
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class MyProfileRepo : BaseCachedRepo<VkUser, UserVM>() {
    override fun getDeletedItems(realm: Realm, now: Long): RealmResults<VkUser> {
        return realm.where(VkUser::class.java)
                .equalTo("id", VKAccessToken.currentToken().userId.toInt())
                .lessThan("updateTime", now).findAll()
    }

    override fun getUpdatedItem(realm: Realm, it: RealmCachedObject): VkUser {
        var updatedItem = it as VkUser
        val oldItem = realm.where(VkUser::class.java).equalTo("id", updatedItem.id).findAll()
        if (oldItem.size > 0) {
            updatedItem.gifts.addAll(oldItem[0].gifts)
            updatedItem.groups.addAll(oldItem[0].groups)
        }
        return updatedItem
    }

    override fun initMapper(): BaseMapper<VkUser, UserVM> {
        return UserMapper()
    }

    fun getMyProfileFromDb():Observable<ResponseAnswer<UserVM>> {
        return getItemsFromDb(Realm.getDefaultInstance()
                .where(VkUser::class.java)
                .equalTo("id", VKAccessToken.currentToken().userId.toInt())
                .findAllAsync()
                .asObservable()
                .doOnCompleted { Realm.getDefaultInstance().close() })
    }

    fun load(): Observable<ResponseAnswer<UserVM>> {
        return Observable.defer {
            if (isOnline()) {
                ApiHelper.createService().getMyProfile("photo_100,photo_max_orig,last_seen,bdate")
            } else Observable.error<Response<SingleResponse<List<VkUser>>>>(
                    NetworkUnavailableException())
        }.subscribeOn(Schedulers.newThread())
        .map({ convertAnswer(it) }).observeOn(AndroidSchedulers.mainThread())
    }

    private fun convertAnswer(it: Response<SingleResponse<List<VkUser>>>): ResponseAnswer<UserVM> {
        var answer: List<VkUser>? = null
        var vkError: VkError? = null
        var responseAnswer = ResponseAnswer(0, answer, vkError)
        val singleResponse: SingleResponse<List<VkUser>> = it.body() ?: return initMapper().execute(responseAnswer)

        val vkResponse = singleResponse.mVkResponse
        if (vkResponse == null) {
            val commonError = ErrorUtils.parseError(it)
            responseAnswer.vkError = commonError.mVkError
        } else {
            if (isSaved) save(vkResponse)
            responseAnswer.count = vkResponse.count()
            responseAnswer.answer = vkResponse
        }
        return initMapper().execute(responseAnswer)
    }
}