package com.plantcare.ai.identifier.plantid.ui.component.remind.adapter

import android.annotation.SuppressLint
import androidx.databinding.ViewDataBinding
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.databinding.ItemSoundBinding
import com.plantcare.ai.identifier.plantid.domains.SoundDomain
import com.plantcare.ai.identifier.plantid.ui.bases.BaseRecyclerViewAdapter
import com.plantcare.ai.identifier.plantid.ui.bases.ext.click

class SoundAdapter(
    private val onItemClick: (index: Int, soundDomain: SoundDomain) -> Unit
) : BaseRecyclerViewAdapter<SoundDomain>() {

    var idSoundSelected: Long = 0L
        set(value) {
            field = value
            notifyItemChanged(value.toInt())
        }

    override fun getItemLayout(): Int = R.layout.item_sound

    override fun setData(binding: ViewDataBinding, item: SoundDomain, layoutPosition: Int) {
        if (binding is ItemSoundBinding) {
            binding.tvSoundName.apply {
                text = item.name
                isSelected = true
            }
            binding.tvSoundName.isActivated = item.id == idSoundSelected
        }
    }

    override fun onClickViews(binding: ViewDataBinding, obj: SoundDomain, layoutPosition: Int) {
        if (binding is ItemSoundBinding) {
            binding.layoutContainer.click { onItemClick.invoke(layoutPosition, obj) }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun submitData(newData: List<SoundDomain>) {
        list.apply {
            clear()
            addAll(newData)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = list.size
}