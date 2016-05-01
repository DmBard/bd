package com.dmbaryshev.bd.network

import com.dmbaryshev.bd.utils.dogi
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import com.vk.sdk.VKAccessToken
import io.realm.RealmObject
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiHelper {
    private val TAG:String = ApiHelper::class.java.simpleName

    private val TIMEOUT:Int = 25

    fun createService(): ApiService {
        dogi(TAG, "createService: ")
        return ApiHelper.getRetrofit().create(ApiService::class.java)
    }

    fun getRetrofit(): Retrofit {
            val okHttpClient = with(OkHttpClient().newBuilder()) {
                readTimeout(ApiHelper.TIMEOUT.toLong(), TimeUnit.SECONDS)
                connectTimeout(ApiHelper.TIMEOUT.toLong(), TimeUnit.SECONDS)
                addInterceptor(ApiHelper.addConstantlyParamsInterceptor())
                addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                build()
            }

            val gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().setExclusionStrategies(object: ExclusionStrategy {
                override fun shouldSkipClass(clazz: Class<*>?): Boolean {
                    return clazz?.declaringClass?.equals(RealmObject::class.java) ?: false
                }
                override fun shouldSkipField(attr: FieldAttributes?): Boolean {
                    return false
                }
            }).create()

            return Retrofit.Builder().baseUrl(NetworkConstants.BASE_URL).client(okHttpClient).addConverterFactory(
                    GsonConverterFactory.create(gson)).addCallAdapterFactory(
                    RxJavaCallAdapterFactory.create()).build()
        }

    fun addConstantlyParamsInterceptor(): Interceptor {
        return Interceptor { it ->
            val token = VKAccessToken.currentToken().accessToken
            var request: Request? = it.request()
            val url = request?.url()
                    ?.newBuilder()
                    ?.addQueryParameter(NetworkConstants.PARAM_ACCESS_TOKEN, token)
                    ?.addQueryParameter(NetworkConstants.PARAM_API_VERSION,
                                        NetworkConstants.API_VERSION)
                    ?.build()
            request = request?.newBuilder()?.url(url)?.build()
            it.proceed(request)
        }
    }
}
