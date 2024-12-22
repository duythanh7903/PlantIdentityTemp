package com.plantcare.ai.identifier.plantid.ui.component.plan_identifier.result.adapter

import androidx.databinding.ViewDataBinding
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.databinding.ItemCommonNameBinding
import com.plantcare.ai.identifier.plantid.ui.bases.BaseRecyclerViewAdapter

class CommonNameAdapter : BaseRecyclerViewAdapter<String>() {
    override fun getItemLayout(): Int = R.layout.item_common_name

    override fun setData(binding: ViewDataBinding, item: String, layoutPosition: Int) {
        if (binding is ItemCommonNameBinding) {
            binding.tvCommonNameValue.text = item
        }
    }

    override fun submitData(newData: List<String>) {
        list.apply {
            clear()
            addAll(newData)
            notifyDataSetChanged()
        }
    }
}