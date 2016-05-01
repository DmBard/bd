package com.dmbaryshev.bd.model.repository

import com.dmbaryshev.bd.model.dto.VkUser
import com.dmbaryshev.bd.model.dto.common.RealmCachedObject
import com.dmbaryshev.bd.model.dto.common.SingleResponse
import com.dmbaryshev.bd.model.dto.common.VkError
import com.dmbaryshev.bd.model.dto.group_gift.VkGroupGift
import com.dmbaryshev.bd.model.dto.group_gift.VkGroupGiftResponse
import com.dmbaryshev.bd.model.dto.group_gift.group.VkGroup
import com.dmbaryshev.bd.model.mapper.BaseMapper
import com.dmbaryshev.bd.model.mapper.GroupMapper
import com.dmbaryshev.bd.model.view_model.GroupVM
import com.dmbaryshev.bd.network.ApiHelper
import com.dmbaryshev.bd.network.ResponseAnswer
import com.dmbaryshev.bd.network.errors.ErrorUtils
import com.dmbaryshev.bd.network.errors.NetworkUnavailableException
import com.dmbaryshev.bd.utils.isOnline
import io.realm.Realm
import io.realm.RealmResults
import retrofit2.Response
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class GroupRepo : BaseCachedRepo<VkGroup, GroupVM>() {
    val ID_OFFSET = 24
    var countGroups = 0

    fun leaveGroup(groupId:Int): Observable<SingleResponse<Int>> {
        return Observable.defer {
            if (isOnline()) {
                ApiHelper.createService().leaveGroup(groupId)
            } else Observable.error<SingleResponse<Int>>(NetworkUnavailableException())
        }.subscribeOn(Schedulers.newThread())
    }

    fun joinGroup(groupId:Int): Observable<SingleResponse<Int>> {
        return Observable.defer {
            if (isOnline()) {
                ApiHelper.createService().joinGroup(groupId)
            } else Observable.error<SingleResponse<Int>>(NetworkUnavailableException())
        }.subscribeOn(Schedulers.newThread())
    }

    override fun getDeletedItems(realm: Realm, now: Long): RealmResults<VkGroup> {
        return realm.where(VkGroup::class.java).lessThan("updateTime", now).findAll()
    }

    override fun getUpdatedItem(realm: Realm, it: RealmCachedObject): VkGroup {
        return it as VkGroup
    }

    override fun initMapper(): BaseMapper<VkGroup, GroupVM> {
        return GroupMapper()
    }

    fun getGroupById(groupId: Int) = GroupMapper().createViewModel(Realm.getDefaultInstance()
                                                                           .where(VkGroup::class.java)
                                                                           .equalTo("id", -groupId)
                                                                           .findFirst())

    fun getGroupGiftsFromDb(userId: Int): Observable<ResponseAnswer<GroupVM>> {
        return Realm.getDefaultInstance()
                .where(VkUser::class.java)
                .equalTo("id", userId)
                .findAllAsync()
                .asObservable()
                .map({ getDbResponseAnswer(it) })
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun getDbResponseAnswer(vkUsers: RealmResults<VkUser>): ResponseAnswer<GroupVM> {
        val vkError: VkError? = null
        val groups = vkUsers[0].groups
        val responseAnswer = ResponseAnswer(groups.size, groups.orEmpty(), vkError)
        return initMapper().execute(responseAnswer)
    }

    fun load(userId: Int): Observable<ResponseAnswer<GroupVM>> {
        return Observable.defer {
            if (isOnline()) initLoadingObservable(userId)
            else Observable.error<Response<VkGroupGiftResponse>>(NetworkUnavailableException())
        }
                .subscribeOn(Schedulers.newThread())
                .map { getNetworkResponseAnswer(it, userId) }
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
    }

    private fun initLoadingObservable(userId: Int): Observable<Response<VkGroupGiftResponse>>? {
        return ApiHelper.createService().getAllGroups(userId, 1).concatMap {
            countGroups = it.body().mVkResponse?.count ?: 0
            val repeatMax = countGroups / ID_OFFSET
            Observable.range(0, repeatMax + 2)
                    .flatMap {
                        if (it > repeatMax ) {
                            return@flatMap Observable.empty<Response<VkGroupGiftResponse>>()
                        }
                        val firstId = it * ID_OFFSET
                        val lastId = if (it == repeatMax) countGroups else firstId + ID_OFFSET
                        ApiHelper.createService().getGroupsGiftsByUser(createExecuteString(userId,
                                                                                           firstId,
                                                                                           lastId))
                    }
        }
    }

    private fun getNetworkResponseAnswer(it: Response<VkGroupGiftResponse>?,
                                         userId: Int): ResponseAnswer<GroupVM> {
        var answer: List<VkGroup>? = null
        var vkError: VkError? = null
        var responseAnswer = ResponseAnswer(0, answer, vkError)
        val response: Response<VkGroupGiftResponse> = it ?: return initMapper().execute( responseAnswer)
        val vkGroupGiftResponse = response.body() ?: return initMapper().execute(responseAnswer)

        val vkResponse = vkGroupGiftResponse.mVkResponse
        if (vkResponse == null) {
            val commonError = ErrorUtils.parseError(response)
            responseAnswer.vkError = commonError.mVkError
        } else {
            vkResponse.groups.items.forEachIndexed { i, vkGroup -> vkGroup.giftCounts = vkResponse.giftCounts[i] }
            responseAnswer.count = vkResponse.groups.count
            responseAnswer.answer = vkResponse.groups.items
            if (isSaved) {
                if (!save(userId, vkResponse)) return initMapper().execute(responseAnswer)
            }
        }
        return initMapper().execute(responseAnswer)
    }

    private fun save(userId: Int, vkResponse: VkGroupGift): Boolean {
        val realm = Realm.getDefaultInstance()
        var vkUser: VkUser? = realm.where(VkUser::class.java).equalTo("id", userId).findFirst()
        realm.executeTransaction {realm->
            val gifts = vkResponse.gifts.items
            var groups = vkResponse.groups.items
            realm.copyToRealmOrUpdate(gifts)
            realm.copyToRealmOrUpdate(groups)
            if (vkUser != null ) {
                gifts.forEach { if(!vkUser.gifts.toMutableList().contains(it)) vkUser.gifts.add(it) }
                groups.forEach { if(!vkUser.groups.toMutableList().contains(it)) vkUser.groups.add(it) }
                realm.copyToRealmOrUpdate(vkUser)
            }
        }
        realm.close()
        return vkUser != null
    }

    private fun createExecuteString(userId: Int, firstId:Int, lastId:Int): String {
        return """var gr=API.groups.get({"user_id":$userId,"extended":1});
                  var id = $firstId;
                  var gifts=[];
                  var groups=[];
                  var gift_counts=[];
                  gifts.count = 0;
                  gifts.items = [];
                  groups.count = 0;
                  groups.items = [];
                  while (id<$lastId) {
                        var gr_id = gr.items[id].id;
                        var gift = API.market.get({"owner_id":-gr_id,"extended":1});
                        if (gift.count != null && gift.count > 0) {
                            gift_counts.push(gift.count);
                            groups.count = groups.count+1;
                            groups.items.push(gr.items[id]);
                            gifts.count = gifts.count + gift.count;
                            gifts.items = gifts.items + gift.items;
                        }
                        id = id+1;
                  }
                  if (gifts.count == null) {return {"count":0, "items":[]};}
                  return {"groups":groups,"gifts":gifts,"gift_counts":gift_counts};"""
    }

    fun changeMemberStatus(isMember: Boolean, groupId: Int) {
        Realm.getDefaultInstance().executeTransactionAsync {
            val group = it.where(VkGroup::class.java).equalTo("id",
                                                             groupId).findFirst()
            group?.isMember = if (isMember) 1 else 0
        }
    }
}