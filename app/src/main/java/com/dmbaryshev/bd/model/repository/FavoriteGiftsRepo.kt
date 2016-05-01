package com.dmbaryshev.bd.model.repository

import com.dmbaryshev.bd.model.dto.common.CommonResponse
import com.dmbaryshev.bd.model.dto.common.RealmCachedObject
import com.dmbaryshev.bd.model.dto.group_gift.gift.VkFavoriteGift
import com.dmbaryshev.bd.model.mapper.BaseMapper
import com.dmbaryshev.bd.model.mapper.FavoriteGiftMapper
import com.dmbaryshev.bd.model.view_model.GiftVM
import com.dmbaryshev.bd.network.ApiHelper
import com.dmbaryshev.bd.network.ResponseAnswer
import com.dmbaryshev.bd.network.errors.NetworkUnavailableException
import com.dmbaryshev.bd.utils.isOnline
import io.realm.Realm
import io.realm.RealmResults
import retrofit2.Response
import rx.Observable
import rx.schedulers.Schedulers

class FavoriteGiftsRepo : BaseCachedRepo<VkFavoriteGift, GiftVM>() {
    override fun getDeletedItems(realm: Realm, now: Long): RealmResults<VkFavoriteGift>? {
        return realm.where(VkFavoriteGift::class.java).lessThan("updateTime", now).findAll()
    }

    override fun getUpdatedItem(realm: Realm, it: RealmCachedObject): VkFavoriteGift {
        return it as VkFavoriteGift
    }

    fun getGiftsFromDb(): Observable<ResponseAnswer<GiftVM>> {
        return getItemsFromDb(Realm.getDefaultInstance()
                .where(VkFavoriteGift::class.java)
                .findAllAsync()
                .asObservable()
                .doOnCompleted { Realm.getDefaultInstance().close() })
    }

    fun load(): Observable<ResponseAnswer<GiftVM>> {
        val networkObservable = Observable.defer {
            if (isOnline()) {
                ApiHelper.createService().getFavoriteGifts(1)
            } else Observable.error<Response<CommonResponse<VkFavoriteGift>>>(NetworkUnavailableException())
        }.subscribeOn(Schedulers.newThread())
        return load(networkObservable)
    }

    override fun initMapper(): BaseMapper<VkFavoriteGift, GiftVM> {
        return FavoriteGiftMapper()
    }
}