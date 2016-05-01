package com.dmbaryshev.bd.view

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.dmbaryshev.bd.R
import com.dmbaryshev.bd.utils.dogi
import com.dmbaryshev.bd.utils.makeLogTag
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKCallback
import com.vk.sdk.VKSdk
import com.vk.sdk.api.VKError

class MainActivity : AppCompatActivity(){
    val TAG = makeLogTag(MainActivity::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dogi(TAG, "onCreate")
        val loggedIn = VKSdk.isLoggedIn()
        if (!loggedIn) {
            VKSdk.login(this,
                        "friends",
                        "messages",
                        "photos",
                        "audio",
                        "wall",
                        "groups",
                        "status",
                        "market")
        } else {
            startActivity(Intent(this, BDActivity::class.java))
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!VKSdk.onActivityResult(requestCode,
                                    resultCode,
                                    data ?: Intent(),
                                    getVkCallback())) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun getVkCallback(): VKCallback<VKAccessToken> {
        return object : VKCallback<VKAccessToken> {
            override fun onResult(res: VKAccessToken) {
                val accessToken = res.accessToken
                dogi(TAG, "onResult: accessToken = " + accessToken)
                startActivity(Intent(this@MainActivity, BDActivity::class.java))
            }

            override fun onError(error: VKError) {
                dogi(TAG, "onResult: error = " + error.errorMessage)
            }
        }
    }
}
