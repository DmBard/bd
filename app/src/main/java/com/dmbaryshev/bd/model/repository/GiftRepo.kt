package com.dmbaryshev.bd.model.repository

import com.dmbaryshev.bd.model.dto.VkUser
import com.dmbaryshev.bd.model.dto.common.CommonResponse
import com.dmbaryshev.bd.model.dto.common.RealmCachedObject
import com.dmbaryshev.bd.model.dto.group_gift.gift.VkGift
import com.dmbaryshev.bd.model.mapper.BaseMapper
import com.dmbaryshev.bd.model.mapper.GiftMapper
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

class GiftRepo() : BaseCachedRepo<VkGift, GiftVM>() {
    override fun getDeletedItems(realm: Realm, now: Long): RealmResults<VkGift>? {
        // TODO: 16.03.2016 change logic of old items removing
        return null
    }

    override fun getUpdatedItem(realm: Realm, it: RealmCachedObject): VkGift {
        return it as VkGift
    }

    fun getGiftsFromDb(groupId: Int): Observable<ResponseAnswer<GiftVM>> {
        return getItemsFromDb(Realm.getDefaultInstance().where(VkGift::class.java).equalTo("ownerId",
                                                                                           -groupId).findAllAsync().asObservable().doOnCompleted { Realm.getDefaultInstance().close() })
    }

    fun getGiftFromDb(giftId: Int): Observable<ResponseAnswer<GiftVM>> {
        return getItemsFromDb(Realm.getDefaultInstance().where(VkGift::class.java).equalTo("id",
                                                                                           giftId).findAllAsync().asObservable().doOnCompleted { Realm.getDefaultInstance().close() })
    }

    fun load(groupId: Int, offset: Int = 0): Observable<ResponseAnswer<GiftVM>> {
        val networkObservable = Observable.defer {
            if (isOnline()) {
                ApiHelper.createService().getGifts(-groupId, offset, 1)
            } else Observable.error<Response<CommonResponse<VkGift>>>(NetworkUnavailableException())
        }.subscribeOn(Schedulers.newThread())
        return load(networkObservable)
    }

    override fun initMapper(): BaseMapper<VkGift, GiftVM> {
        return GiftMapper()
    }

    fun getFilteredGiftsByUser(userId: Int,
                               name: String,
                               minPrice: Float,
                               maxPrice: Float): List<GiftVM> {
        return Realm.getDefaultInstance()
                .where(VkUser::class.java)
                .equalTo("id", userId)
                .findFirst()
                .gifts
                .filter {
                    filterGifts(it, name, minPrice, maxPrice)
                }
                .map { GiftMapper().createViewModel(it) }
    }

    private fun filterGifts(item: VkGift,
                            name: String,
                            minPrice: Float,
                            maxPrice: Float): Boolean {
        var isValidName = true
        if (name != "") {
            isValidName = item.title.toLowerCase().contains(name.toLowerCase()) || item.description.toLowerCase().contains(name.toLowerCase())
        }
        var min = if (minPrice == 0f) 0f else minPrice
        var max = if (maxPrice == 0f) Float.MAX_VALUE else maxPrice
        val price = item.price?.amount ?: -1
        val isValidPrice = price / (100) in min..max

        return isValidName && isValidPrice
    }

    fun getFilteredGiftsByGroup(ownerId: Int,
                                name: String,
                                minPrice: Float,
                                maxPrice: Float): List<GiftVM> {
        return Realm.getDefaultInstance()
                .where(VkGift::class.java)
                .equalTo("ownerId", -ownerId)
                .findAll()
                .filter {
                    filterGifts(it, name, minPrice, maxPrice)
                }
                .map { GiftMapper().createViewModel(it) }
    }

    fun changeGiftLike(isLiked: Boolean, itemId: Int) {
        Realm.getDefaultInstance().executeTransactionAsync {
            val gift = it.where(VkGift::class.java).equalTo("id",
                                                            itemId).findFirst()
            gift?.likes?.userLikes = if (isLiked) 1 else 0
        }
    }
}