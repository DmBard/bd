package com.dmbaryshev.bd.network

import com.dmbaryshev.bd.model.dto.common.VkError

class ResponseAnswer<T>(var count: Int, var answer: List<T>?, var vkError: VkError?)