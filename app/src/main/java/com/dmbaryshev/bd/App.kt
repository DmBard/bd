package com.dmbaryshev.bd

import android.app.Application
import android.content.Context
import android.content.Intent
import com.crashlytics.android.Crashlytics
import com.dmbaryshev.bd.utils.dogi
import com.dmbaryshev.bd.utils.makeLogTag
import com.dmbaryshev.bd.view.MainActivity
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKAccessTokenTracker
import com.vk.sdk.VKSdk
import io.fabric.sdk.android.Fabric
import io.realm.Realm
import io.realm.RealmConfiguration

class App : Application() {
    private val TAG = makeLogTag(App::class.java)

    internal var vkAccessTokenTracker: VKAccessTokenTracker = object : VKAccessTokenTracker() {
        override fun onVKAccessTokenChanged(oldToken: VKAccessToken?, newToken: VKAccessToken?) {
            if (newToken == null) {
                dogi(TAG, "onVKAccessTokenChanged: ")
                startActivity(Intent(this@App, MainActivity::class.java))
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        if (!BuildConfig.DEBUG) Fabric.with(this, Crashlytics());
        vkAccessTokenTracker.startTracking()
        App.appContext = applicationContext
        VKSdk.initialize(this)
        val realmConfiguration = RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    companion object {
        var appContext: Context? = null
            private set
    }
}
