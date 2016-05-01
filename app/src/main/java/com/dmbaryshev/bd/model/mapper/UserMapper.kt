package com.dmbaryshev.bd.model.mapper

import com.dmbaryshev.bd.model.dto.VkUser
import com.dmbaryshev.bd.model.view_model.UserVM

class UserMapper : BaseMapper<VkUser, UserVM>() {

    override fun createViewModel(v: VkUser): UserVM {
        val bdateArray: List<String>? = v.bdate?.split(".")
        var bdateVM: IntArray? = null
        if (bdateArray != null && bdateArray.size >= 2) {
            bdateVM= IntArray(bdateArray.size)
                for ((index,value) in bdateArray.withIndex())
                     bdateVM[index] = if (value == "") 0 else Integer.parseInt(value)
        }

        return UserVM(v.id,
                      "${v.firstName} ${v.lastName}",
                      v.photo100,
                      v.photoMax,
                      v.online,
                      bdateVM,
                      v.lastSeen,
                      v.updateTime ?: 0)
    }
}