package com.dmbaryshev.bd.model.mapper

import com.dmbaryshev.bd.model.view_model.IViewModel
import com.dmbaryshev.bd.network.ResponseAnswer
import java.util.*

abstract class BaseMapper<V, VM : IViewModel> {

    fun execute(vResponseAnswer: ResponseAnswer<V>): ResponseAnswer<VM> {
        val count = vResponseAnswer.count
        val vList = vResponseAnswer.answer
        val vmList = ArrayList<VM>()
        if (vList != null && !vList.isEmpty()) {
            for (v in vList) {
                val viewModel = createViewModel(v)
                vmList.add(viewModel)
            }
        }

        return ResponseAnswer(count, vmList, vResponseAnswer.vkError)
    }

    abstract fun createViewModel(v: V): VM
}
