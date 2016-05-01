package com.dmbaryshev.bd.model.repository

import com.dmbaryshev.bd.model.dto.VkUser
import com.dmbaryshev.bd.model.dto.common.CommonResponse
import com.dmbaryshev.bd.model.dto.common.RealmCachedObject
import com.dmbaryshev.bd.model.mapper.BaseMapper
import com.dmbaryshev.bd.model.mapper.UserMapper
import com.dmbaryshev.bd.model.view_model.UserVM
import com.dmbaryshev.bd.network.ApiHelper
import com.dmbaryshev.bd.network.ResponseAnswer
import com.dmbaryshev.bd.network.errors.NetworkUnavailableException
import com.dmbaryshev.bd.utils.isOnline
import com.vk.sdk.VKAccessToken
import io.realm.Realm
import io.realm.RealmResults
import retrofit2.Response
import rx.Observable
import rx.schedulers.Schedulers

class UserRepo : BaseCachedRepo<VkUser, UserVM>() {
    override fun getDeletedItems(realm: Realm, now: Long):RealmResults<VkUser>{
        return realm.where(VkUser::class.java)
                .notEqualTo("id", VKAccessToken.currentToken().userId.toInt())
                .lessThan("updateTime", now)
                .findAll()
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

    fun getAllFriendsFromDb():Observable<ResponseAnswer<UserVM>> {
        return getItemsFromDb(Realm.getDefaultInstance()
                .where(VkUser::class.java)
                .notEqualTo("id", VKAccessToken.currentToken().userId.toInt())
                .findAllAsync()
                .asObservable()
                .doOnCompleted { Realm.getDefaultInstance().close() })
    }

    fun getListFriendsFromDb():List<UserVM> {
        return Realm.getDefaultInstance()
                .where(VkUser::class.java)
                .findAll()
                .toList()
                .map { UserMapper().createViewModel(it) }
    }

    fun getFriendsFromDbByBdate(day:Int, month:Int):Observable<ResponseAnswer<UserVM>> {
        return getItemsFromDb(Realm.getDefaultInstance()
                                      .where(VkUser::class.java)
                                      .equalTo("bdate", "$day.$month")
                                      .findAllAsync()
                                      .asObservable()
                                      .doOnCompleted { Realm.getDefaultInstance().close() })
    }

    fun load(): Observable<ResponseAnswer<UserVM>> {
        val networkObservable = Observable.defer {
            if (isOnline()) {
                ApiHelper.createService().getFriendList("hints", "photo_100,photo_max_orig,last_seen,bdate")
            } else Observable.error<Response<CommonResponse<VkUser>>>(NetworkUnavailableException())
        }.subscribeOn(Schedulers.newThread())
        return load(networkObservable)
    }

    override fun initMapper(): BaseMapper<VkUser, UserVM> {
        return UserMapper()
    }
}
