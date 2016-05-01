package com.dmbaryshev.bd.network

import com.dmbaryshev.bd.model.dto.VkMessage
import com.dmbaryshev.bd.model.dto.VkUser
import com.dmbaryshev.bd.model.dto.common.CommonResponse
import com.dmbaryshev.bd.model.dto.common.SingleResponse
import com.dmbaryshev.bd.model.dto.group_gift.VkGroupGiftResponse
import com.dmbaryshev.bd.model.dto.group_gift.gift.VkFavoriteGift
import com.dmbaryshev.bd.model.dto.group_gift.gift.VkGift
import com.dmbaryshev.bd.model.dto.group_gift.group.VkGroup
import com.dmbaryshev.bd.model.dto.likes.VkLikes
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import rx.Observable

interface ApiService {

    @GET("/method/users.get")
    fun getMyProfile(@Query("fields") fields: String): Observable<Response<SingleResponse<List<VkUser>>>>

    @GET("/method/friends.get")
    fun getFriendList(@Query("user_id") userId: Int,
                      @Query("order") order: String,
                      @Query("fields") fields: String): Observable<Response<CommonResponse<VkUser>>>

    @GET("/method/friends.get")
    fun getFriendList(@Query("order") order: String,
                      @Query("fields") fields: String): Observable<Response<CommonResponse<VkUser>>>

    @GET("/method/messages.getHistory")
    fun getMessageHistory(@Query("user_id") userId: Int,
                          @Query("offset") offset: Int,
                          @Query("count") count: Int,
                          @Query("start_message_id") startMessageId: Int): Observable<Response<CommonResponse<VkMessage>>>

    @GET("/method/messages.getHistory")
    fun getMessageHistory(@Query("peer_id") peerId: Int,
                          @Query("count") count: Int,
                          @Query("offset") offset: Int): Observable<Response<CommonResponse<VkMessage>>>

    @GET("/method/messages.getHistory")
    fun getMessageHistory(@Query("user_id") userId: Int,
                          @Query("count") count: Int): Observable<Response<CommonResponse<VkMessage>>>

    @GET("/method/groups.get")
    fun getAllGroups(@Query("user_id") userId: Int,
                     @Query("extended") count: Int): Observable<Response<CommonResponse<VkGroup>>>

    @GET("/method/market.get")
    fun getGifts(@Query("owner_id") ownerId: Int, @Query("extended") count: Int): Observable<Response<CommonResponse<VkGift>>>

    @GET("/method/market.get")
    fun getGifts(@Query("owner_id") ownerId: Int,@Query("offset") offset: Int, @Query("extended") extended: Int): Observable<Response<CommonResponse<VkGift>>>

    @GET("/method/fave.getMarketItems")
    fun getFavoriteGifts(@Query("extended") count: Int): Observable<Response<CommonResponse<VkFavoriteGift>>>

    @POST("/method/messages.send")
    fun sendMessage(@Query("user_id") userId: Int,
                    @Query("message") message: String): Observable<SingleResponse<Int>>

    @GET("/method/execute")
    fun getGroupsGiftsByUser(@Query("code") code: String): Observable<Response<VkGroupGiftResponse>>

    @GET("/method/likes.add")
    fun addLike(@Query("type") code: String, @Query("owner_id") ownerId: Int, @Query("item_id") itemId: Int): Observable<SingleResponse<VkLikes>>

    @GET("/method/groups.leave")
    fun leaveGroup(@Query("group_id") groupId: Int): Observable<SingleResponse<Int>>

    @GET("/method/groups.join")
    fun joinGroup(@Query("group_id") groupId: Int): Observable<SingleResponse<Int>>

    @GET("/method/likes.delete")
    fun deleteLike(@Query("type") code: String, @Query("owner_id") ownerId: Int, @Query("item_id") itemId: Int): Observable<SingleResponse<VkLikes>>

}
