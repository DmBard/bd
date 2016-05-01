package com.dmbaryshev.bd.network.errors

class StopLoadingException : Throwable() {
    override val message: String?
        get() = "Stop Loading"
}
