package com.plantcare.ai.identifier.plantid.ui.component.plan_identifier.camera

import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.databinding.ItemImageBinding
import com.plantcare.ai.identifier.plantid.domains.ImageDomain
import com.plantcare.ai.identifier.plantid.ui.bases.BaseRecyclerViewAdapter
import com.plantcare.ai.identifier.plantid.ui.bases.ext.click

class ImageAdapter(
    private val onItemClick: (ImageDomain, Int) -> Unit
) : BaseRecyclerViewAdapter<ImageDomain>() {
    override fun getItemLayout(): Int = R.layout.item_image

    override fun setData(binding: ViewDataBinding, item: ImageDomain, layoutPosition: Int) {
        if (binding is ItemImageBinding) {
            Glide.with(binding.root.context).load(item.path).into(binding.imgImage)
        }
    }

    override fun onClickViews(binding: ViewDataBinding, obj: ImageDomain, layoutPosition: Int) {
        if (binding is ItemImageBinding) {
            binding.root.click { onItemClick.invoke(obj, layoutPosition) }
        }
    }

    override fun submitData(newData: List<ImageDomain>) {
        list.apply {
            clear()
            addAll(newData)
            notifyDataSetChanged()
        }
    }
}