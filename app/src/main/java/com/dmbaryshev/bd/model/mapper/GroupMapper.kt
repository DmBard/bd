package com.dmbaryshev.bd.model.mapper

import com.dmbaryshev.bd.model.dto.group_gift.group.VkGroup
import com.dmbaryshev.bd.model.view_model.GroupVM

class GroupMapper : BaseMapper<VkGroup, GroupVM>() {
    override fun createViewModel(v: VkGroup): GroupVM {
        return GroupVM(v.id,
                       v.name,
                       v.screenName,
                       v.isClosed,
                       v.type,
                       v.isAdmin,
                       v.isMember,
                       v.photo50,
                       v.photo100,
                       v.photo200,
                       v.giftCounts)
    }
}
