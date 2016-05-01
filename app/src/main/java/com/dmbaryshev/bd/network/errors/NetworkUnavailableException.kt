package com.dmbaryshev.bd.network.errors

class NetworkUnavailableException : Throwable() {
    override val message: String?
        get() = "Network problem"
}