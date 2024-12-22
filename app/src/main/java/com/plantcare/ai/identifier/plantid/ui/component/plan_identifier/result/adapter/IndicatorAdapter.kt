package com.plantcare.ai.identifier.plantid.ui.component.plan_identifier.result.adapter

import android.util.Log
import androidx.databinding.ViewDataBinding
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.databinding.ItemIndicatorBinding
import com.plantcare.ai.identifier.plantid.ui.bases.BaseRecyclerViewAdapter
import com.plantcare.ai.identifier.plantid.ui.component.plan_identifier.result.domain.IndicatorDomain

class IndicatorAdapter : BaseRecyclerViewAdapter<IndicatorDomain>() {

    var indexSelected: Int = 0
        set(value) {
            field = value
            notifyItemChanged(value)
        }

    override fun getItemLayout(): Int = R.layout.item_indicator

    override fun setData(binding: ViewDataBinding, item: IndicatorDomain, layoutPosition: Int) {
        if (binding is ItemIndicatorBinding) {
            binding.icIndicator.isActivated = layoutPosition == indexSelected

            Log.d("duylt", "Index Current Adapter: $indexSelected")
        }
    }

    override fun submitData(newData: List<IndicatorDomain>) {
        list.apply {
            clear()
            addAll(newData)
            notifyDataSetChanged()
        }
    }
}