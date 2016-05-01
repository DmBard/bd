package com.dmbaryshev.bd.utils

import android.content.Context
import android.content.SharedPreferences
import com.dmbaryshev.bd.App

object PreferencesHelper {
    private val PREF_NAME = "com.dmbaryshev.vkschool.utils.pref.NAME"
    private val PREF_GROUPS_UPDATE_TIME = "com.dmbaryshev.vkschool.utils.pref.GROUPS_UPDATE_TIME"

    private var mSharedPreferences: SharedPreferences? = App.appContext?.getSharedPreferences(PREF_NAME,
                                                                                            Context.MODE_PRIVATE)

    fun getUpdateGroupsTime(userId: String): Long? = mSharedPreferences?.getLong(PREF_GROUPS_UPDATE_TIME + userId, 0L)

    fun setUpdateGroupsTime(userId: String, time: Long) = mSharedPreferences?.edit()
            ?.putLong(PREF_GROUPS_UPDATE_TIME + userId, time)
            ?.apply()
}

