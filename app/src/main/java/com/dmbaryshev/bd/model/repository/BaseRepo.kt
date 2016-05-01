package com.dmbaryshev.bd.model.repository

import com.dmbaryshev.bd.model.dto.common.CommonResponse
import com.dmbaryshev.bd.model.dto.common.VkError
import com.dmbaryshev.bd.model.mapper.BaseMapper
import com.dmbaryshev.bd.model.view_model.IViewModel
import com.dmbaryshev.bd.network.ResponseAnswer
import com.dmbaryshev.bd.network.errors.ErrorUtils
import retrofit2.Response
import rx.Observable
import rx.android.schedulers.AndroidSchedulers

abstract class BaseRepo<V, VM : IViewModel> {

    protected fun load(networkConcreteObservable: Observable<Response<CommonResponse<V>>>): Observable<ResponseAnswer<VM>> {
        return networkConcreteObservable.map({ initNetworkResponseAnswer(it) }).observeOn( AndroidSchedulers.mainThread()).cache()
    }

    private fun initNetworkResponseAnswer(response: Response<CommonResponse<V>>?): ResponseAnswer<VM> {
        var answer: List<V>? = null
        var vkError: VkError? = null
        var responseAnswer = ResponseAnswer(0, answer, vkError)

        if (response == null) {
            return initMapper().execute(responseAnswer)
        }
        val commonResponse = response.body() ?: return initMapper().execute(responseAnswer)

        val vkResponse = commonResponse.mVkResponse
        if (vkResponse == null) {
            val commonError = ErrorUtils.parseError(response)
            responseAnswer.vkError = commonError.mVkError
        } else {
            if(isSaved) save(vkResponse.items)
            responseAnswer.count = vkResponse.count
            responseAnswer.answer = vkResponse.items
        }
        return initMapper().execute(responseAnswer)
    }

    protected abstract fun save(items: List<V>?)
    protected abstract fun initMapper(): BaseMapper<V, VM>
    protected open var isSaved:Boolean = false
}