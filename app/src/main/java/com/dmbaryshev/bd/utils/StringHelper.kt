package com.dmbaryshev.bd.utils

import android.content.Context

fun Int.setCountingString(context: Context, idRes: Int): String {
    return "$this ${context.resources.getQuantityString(idRes, this)}"
}
