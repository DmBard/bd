package com.dmbaryshev.bd.utils

import android.content.Context
import android.net.ConnectivityManager
import com.dmbaryshev.bd.App

fun isOnline(): Boolean = run {
    var netInfo: ConnectivityManager? = App.appContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager? ?: null
    netInfo?.activeNetworkInfo?.isConnectedOrConnecting ?: false
        }
