package com.dmbaryshev.bd.model.mapper

import com.dmbaryshev.bd.model.dto.group_gift.gift.VkFavoriteGift
import com.dmbaryshev.bd.model.view_model.GiftVM
import java.util.*

class FavoriteGiftMapper : BaseMapper<VkFavoriteGift, GiftVM>() {
    override fun createViewModel(v: VkFavoriteGift): GiftVM {
        return GiftVM(v.id,
                      v.ownerId,
                      v.title,
                      v.description,
                      v.price?.text ?: "",
                      v.category?.name,
                      v.date,
                      v.thumbPhoto,
                      v.photos.let { list ->
                          var urls: MutableList<String> = ArrayList()
                          list?.forEach { urls.add(it.photo604) }
                          urls
                      },
                      true)
    }
}
