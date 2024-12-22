package com.plantcare.ai.identifier.plantid.ui.component.history.delete

import android.annotation.SuppressLint
import android.content.Context
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.databinding.ItemHistoryDeleteBinding
import com.plantcare.ai.identifier.plantid.domains.HistoryDomain
import com.plantcare.ai.identifier.plantid.ui.bases.BaseRecyclerViewAdapter
import com.plantcare.ai.identifier.plantid.ui.bases.ext.click

class HistoryDeleteAdapter(
    private val contextParams: Context, private val onItemSelected: (HistoryDomain, Int) -> Unit
) : BaseRecyclerViewAdapter<HistoryDomain>() {
    override fun getItemLayout(): Int = R.layout.item_history_delete

    override fun setData(binding: ViewDataBinding, item: HistoryDomain, layoutPosition: Int) {
        if (binding is ItemHistoryDeleteBinding) {
            Glide.with(contextParams).load(item.images[0]).into(binding.imgPreviewHistory)
            binding.icChbDelete.isActivated = item.isSelected
            binding.tvPlantName.apply {
                text = item.scientificName
                isSelected = true
            }
        }
    }

    override fun onClickViews(binding: ViewDataBinding, obj: HistoryDomain, layoutPosition: Int) {
        if (binding is ItemHistoryDeleteBinding) {
            binding.root.click { onItemSelected.invoke(obj, layoutPosition) }
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

    fun markAllItem() {
        list.forEach { item -> item.isSelected = true }
        notifyItemRangeChanged(0, list.size)
    }

    fun unMarkAllItem() {
        list.forEach { item -> item.isSelected = false }
        notifyItemRangeChanged(0, list.size)
    }

    fun changeSelected(
        index: Int, onEventAllItemDone: () -> Unit, onEventAllItemNotDone: () -> Unit,
        onEventMarkSomeItem: () -> Unit
    ) {
        list[index].isSelected = !list[index].isSelected
        notifyItemChanged(index)

        if (isMarkAllItem()) onEventAllItemDone.invoke()
        else if (isNotMarkAnyItem()) onEventAllItemNotDone.invoke()
        else onEventMarkSomeItem.invoke()
    }

    private fun isMarkAllItem() = list.all { it.isSelected }

    private fun isNotMarkAnyItem() = list.all { !it.isSelected }

    fun hasAnyItemSelected(): Boolean = list.any { it.isSelected }
}