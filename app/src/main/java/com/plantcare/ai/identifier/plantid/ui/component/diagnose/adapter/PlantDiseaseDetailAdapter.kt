package com.plantcare.ai.identifier.plantid.ui.component.diagnose.adapter

import android.annotation.SuppressLint
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.databinding.ItemImagePlantBinding
import com.plantcare.ai.identifier.plantid.ui.bases.BaseRecyclerViewAdapter

class PlantDiseaseDetailAdapter(): BaseRecyclerViewAdapter<Int>() {
    override fun getItemLayout(): Int = R.layout.item_image_plant


    override fun setData(binding: ViewDataBinding, item: Int, layoutPosition: Int) {
       if(binding is ItemImagePlantBinding){
           context?.let {
               Glide.with(it).load(AppCompatResources.getDrawable(binding.root.context, item))
                   .into(binding.imgPlantResult)
           }
       }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun submitData(newData: List<Int>) {
        list.clear()
        list.addAll(newData)
        notifyDataSetChanged()
    }

}