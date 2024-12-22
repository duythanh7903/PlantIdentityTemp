package com.plantcare.ai.identifier.plantid.ui.component.history.list

import android.annotation.SuppressLint
import android.content.Context
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.databinding.ItemHistoryBinding
import com.plantcare.ai.identifier.plantid.domains.HistoryDomain
import com.plantcare.ai.identifier.plantid.ui.bases.BaseRecyclerViewAdapter
import com.plantcare.ai.identifier.plantid.ui.bases.ext.click

class HistoryAdapter(
    private val contextParams: Context,
    private val onItemClick: (historyDomain: HistoryDomain, index: Int) -> Unit
) : BaseRecyclerViewAdapter<HistoryDomain>() {
    override fun getItemLayout(): Int = R.layout.item_history

    override fun setData(binding: ViewDataBinding, item: HistoryDomain, layoutPosition: Int) {
        if (binding is ItemHistoryBinding) {
            Glide.with(contextParams).load(item.images[0]).into(binding.imgPreviewHistory)
            binding.tvPlantName.apply {
                text = item.scientificName
                isSelected = true
            }
        }
    }

    override fun onClickViews(binding: ViewDataBinding, obj: HistoryDomain, layoutPosition: Int) {
        if (binding is ItemHistoryBinding) {
            binding.root.click { onItemClick.invoke(obj, layoutPosition) }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun submitData(newData: List<HistoryDomain>) {
        list.apply {
            clear()
            addAll(newData)
            notifyDataSetChanged()
        }
    }
}