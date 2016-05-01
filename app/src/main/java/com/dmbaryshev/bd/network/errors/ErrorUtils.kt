package com.dmbaryshev.bd.network.errors

import com.dmbaryshev.bd.model.dto.common.CommonError
import com.dmbaryshev.bd.network.ApiHelper
import retrofit2.Response
import java.io.IOException

object ErrorUtils {

    fun parseError(response: Response<*>): CommonError {
        val converter = ApiHelper.getRetrofit().responseBodyConverter<CommonError>(CommonError::class.java,
                                                                                   arrayOfNulls<Annotation>(0))

        var error: CommonError

        try {
            error = converter.convert(response.errorBody())
        } catch (e: IOException) {
            return CommonError(null)
        }

        return error
    }
}
