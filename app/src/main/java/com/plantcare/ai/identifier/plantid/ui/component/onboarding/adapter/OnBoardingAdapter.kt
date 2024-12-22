package com.plantcare.ai.identifier.plantid.ui.component.onboarding.adapter

import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.databinding.ItemOnBoardingBinding
import com.plantcare.ai.identifier.plantid.domains.GuideModel
import com.plantcare.ai.identifier.plantid.ui.bases.BaseRecyclerViewAdapter

class OnBoardingAdapter(
) : BaseRecyclerViewAdapter<GuideModel>() {

    override fun getItemLayout(): Int = R.layout.item_on_boarding

    override fun setData(binding: ViewDataBinding, item: GuideModel, layoutPosition: Int) {
        if (binding is ItemOnBoardingBinding) {
            binding.tvTitle.text = context?.getString(item.title)
            binding.tvSubText.text = context?.getString(item.subText)
            context?.let {
                Glide.with(it).load(context?.getDrawable(item.img))
                    .into(binding.imgGuide)
            }
        }
    }

    override fun submitData(newData: List<GuideModel>) {
        list.clear()
        list.addAll(newData)
        notifyDataSetChanged()
    }
}