package com.plantcare.ai.identifier.plantid.ui.component.diagnose.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.databinding.ItemPlantDiseaseBinding
import com.plantcare.ai.identifier.plantid.ui.bases.BaseRecyclerViewAdapter
import com.plantcare.ai.identifier.plantid.ui.bases.ext.click
import com.plantcare.ai.identifier.plantid.ui.component.diagnose.model.PlantDiseaseModel

class DiagnoseAdapter(
    val onClickItemDisease: (PlantDiseaseModel) -> Unit
) : BaseRecyclerViewAdapter<PlantDiseaseModel>() {
    private var searchQuery: String = ""

    override fun getItemLayout(): Int = R.layout.item_plant_disease

    override fun setData(binding: ViewDataBinding, item: PlantDiseaseModel, layoutPosition: Int) {
        if (binding is ItemPlantDiseaseBinding) {
            binding.apply {
                Glide.with(binding.root.context)
                    .load(AppCompatResources.getDrawable(binding.root.context, item.imageList[0]))
                    .into(plantDiseaseImg)

                val highlightedText = getHighlightedText(
                    binding.root.context,
                    binding.root.context.getString(item.diseaseName),
                    searchQuery
                )

                tvDiseaseName.text = highlightedText
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun submitData(newData: List<PlantDiseaseModel>) {
        list.clear()
        list.addAll(newData)
        notifyDataSetChanged()
    }

    override fun onClickViews(
        binding: ViewDataBinding,
        obj: PlantDiseaseModel,
        layoutPosition: Int
    ) {
        if (binding is ItemPlantDiseaseBinding) {
            binding.apply {
                layoutItemDisease.click { onClickItemDisease(obj) }
            }
        }
    }

    private fun getHighlightedText(
        context: Context,
        fullText: String,
        query: String
    ): SpannableString {
        val spannable = SpannableString(fullText)
        val trimmedQuery = query.trim().replace("\\s+".toRegex(), " ")
        val normalizedQuery = if (trimmedQuery.isEmpty() && query.isNotEmpty()) {
            query
        } else {
            trimmedQuery
        }

        if (normalizedQuery.isNotEmpty()) {
            val startIndex = fullText.indexOf(normalizedQuery, ignoreCase = true)
            if (startIndex != -1) {
                val endIndex = startIndex + normalizedQuery.length
                spannable.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(context, R.color.primary)),
                    startIndex,
                    endIndex,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
        return spannable
    }

    fun updateSearchQuery(context: Context, newData: List<PlantDiseaseModel>, query: String) {
        val trimmedQuery = query.trim().replace("\\s+".toRegex(), " ")
        val normalizedQuery = if (trimmedQuery.isEmpty() && query.isNotEmpty()) {
            query
        } else {
            trimmedQuery
        }

        searchQuery = normalizedQuery
        val filteredList = if (normalizedQuery.isEmpty()) {
            newData
        } else {
            newData.filter {
                context.getString(it.diseaseName).contains(normalizedQuery, ignoreCase = true)
            }
        }
        submitData(filteredList)
    }

}