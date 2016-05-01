package com.dmbaryshev.bd.utils

import android.util.Log

import com.dmbaryshev.bd.BuildConfig

    val LOG_PREFIX = "BD_"
    val LOG_PREFIX_LENGTH = LOG_PREFIX.length
    val MAX_LOG_TAG_LENGTH = 23

    fun makeLogTag(str: String): String {
        if (str.length > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) {
            return LOG_PREFIX + str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH - 1)
        }

        return LOG_PREFIX + str
    }

    /**
     * Don't use this when obfuscating class names!
     */
    fun makeLogTag(cls: Class<*>): String {
        return makeLogTag(cls.simpleName)
    }

    fun dogd(tag: String, message: String) {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (BuildConfig.DEBUG) {
            Log.d(tag, message)
        }
    }

    fun dogd(tag: String, message: String, cause: Throwable) {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (BuildConfig.DEBUG) {
            Log.d(tag, message, cause)
        }
    }

    fun dogv(tag: String, message: String) {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (BuildConfig.DEBUG) {
            Log.v(tag, message)
        }
    }

    fun dogv(tag: String, message: String, cause: Throwable) {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (BuildConfig.DEBUG) {
            Log.v(tag, message, cause)
        }
    }

    fun dogi(tag: String, message: String) {
        if (BuildConfig.DEBUG) Log.i(tag, message)
    }

    fun dogi(tag: String, message: String, cause: Throwable) {
        if (BuildConfig.DEBUG) Log.i(tag, message, cause)
    }

    fun dogw(tag: String, message: String) {
        if (BuildConfig.DEBUG) Log.w(tag, message)
    }

    fun dogw(tag: String, message: String, cause: Throwable) {
        if (BuildConfig.DEBUG) Log.w(tag, message, cause)
    }

    fun doge(tag: String, message: String) {
        if (BuildConfig.DEBUG) Log.e(tag, message)
    }

    fun doge(tag: String, message: String, cause: Throwable) {
        if (BuildConfig.DEBUG) Log.e(tag, message, cause)
    }
