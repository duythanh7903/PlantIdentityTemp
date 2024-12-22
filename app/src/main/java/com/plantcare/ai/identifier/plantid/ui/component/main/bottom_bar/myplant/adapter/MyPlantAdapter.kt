package com.plantcare.ai.identifier.plantid.ui.component.main.bottom_bar.myplant.adapter

import android.annotation.SuppressLint
import android.content.Context
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.data.database.entities.PlantEntity
import com.plantcare.ai.identifier.plantid.databinding.ItemMyPlantBinding
import com.plantcare.ai.identifier.plantid.domains.PlantDomain
import com.plantcare.ai.identifier.plantid.ui.bases.BaseRecyclerViewAdapter
import com.plantcare.ai.identifier.plantid.ui.bases.ext.click
import com.plantcare.ai.identifier.plantid.ui.component.dialog.ConfirmDeleteRemindDialog

class MyPlantAdapter(
    private val onClickItemPlant: (PlantDomain, Int) -> Unit,
    private val onDeleteMyPlant: (PlantDomain, Int) -> Unit
) : BaseRecyclerViewAdapter<PlantDomain>() {
    override fun getItemLayout(): Int = R.layout.item_my_plant

    override fun setData(binding: ViewDataBinding, item: PlantDomain, layoutPosition: Int) {
        if (binding is ItemMyPlantBinding) {
            binding.apply {
                tvPlantName.text = item.scientificName
                Glide.with(root.context).load(item.images[0])
                    .into(plantImg)

                tvPlantName.isSelected = true
            }
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    override fun submitData(newData: List<PlantDomain>) {
        list.clear()
        list.addAll(newData)
        notifyDataSetChanged()
    }

    override fun onClickViews(binding: ViewDataBinding, obj: PlantDomain, layoutPosition: Int) {
        if(binding is ItemMyPlantBinding){
            binding.apply {
                layoutItemPlant.click { onClickItemPlant(obj, layoutPosition) }

                icTrash.click { onDeleteMyPlant.invoke(obj, layoutPosition) }
            }
        }
    }
}