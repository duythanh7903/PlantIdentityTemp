package com.plantcare.ai.identifier.plantid.ui.component.language

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.databinding.ItemLanguageBinding
import com.plantcare.ai.identifier.plantid.ui.bases.BaseRecyclerViewAdapter
import com.plantcare.ai.identifier.plantid.ui.bases.ext.click

class LanguageAdapter(
    private val onLanguageSelected: (language: LanguageModel, index: Int) -> Unit
): BaseRecyclerViewAdapter<LanguageModel>() {

    var indexSelected: Int = -1
        set(value) {
            field = value
            notifyItemChanged(value)
        }

    override fun getItemLayout(): Int = R.layout.item_language

    override fun setData(binding: ViewDataBinding, item: LanguageModel, layoutPosition: Int) {
        if (binding is ItemLanguageBinding) {
            binding.icCountriesFlag.setImageResource(item.image ?: R.drawable.ic_vietnamese)
            binding.txtCountriesName.text = item.languageName
            binding.containerLanguage.isActivated = layoutPosition == indexSelected
        }
    }

    override fun onClickViews(binding: ViewDataBinding, obj: LanguageModel, layoutPosition: Int) {
        if (binding is ItemLanguageBinding) {
            binding.root.click { onLanguageSelected.invoke(obj, layoutPosition) }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun submitData(newData: List<LanguageModel>) {
        list.apply {
            clear()
            addAll(newData)
            notifyDataSetChanged()
        }
    }
}

