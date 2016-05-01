package com.dmbaryshev.bd.view.common

import android.content.Context
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.TextInputEditText
import android.widget.Button
import com.dmbaryshev.bd.R
import com.dmbaryshev.bd.utils.find

class FilterDialog(val ctx: Context, val theme: Int, onOkButtonListener: (String,Float, Float) -> Unit)
: BottomSheetDialog(ctx, theme) {
    init {
        val view = layoutInflater?.inflate(R.layout.dialog_gifts_filter, null)
        setContentView(view)
        view?.find<Button>(R.id.btn_ok)?.setOnClickListener {
            val minPrice = view.find<TextInputEditText>(R.id.et_min_price).text.toString()
            val maxPrice = view.find<TextInputEditText>(R.id.et_max_price).text.toString()
            onOkButtonListener(view.find<TextInputEditText>(R.id.et_name).text.toString(),
                               if(minPrice == "") 0f else minPrice.toFloat(),
                               if(maxPrice == "") 0f else maxPrice.toFloat())
            dismiss()
        }
    }
}
