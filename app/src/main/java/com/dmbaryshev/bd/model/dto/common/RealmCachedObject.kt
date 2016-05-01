package com.dmbaryshev.bd.model.dto.common

interface RealmCachedObject{
    open var id: Int
    open var updateTime: Long?
}