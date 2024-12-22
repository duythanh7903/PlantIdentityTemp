package com.plantcare.ai.identifier.plantid.ui.component.plan_identifier.result.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.databinding.ItemResultImageBinding
import com.plantcare.ai.identifier.plantid.ui.bases.BaseRecyclerViewAdapter
import com.plantcare.ai.identifier.plantid.ui.bases.ext.goneView
import com.plantcare.ai.identifier.plantid.ui.bases.ext.visibleView

class ImageResultAdapter(
    private val contextParams: Context
): BaseRecyclerViewAdapter<String>() {
    override fun getItemLayout(): Int = R.layout.item_result_image

    override fun setData(binding: ViewDataBinding, item: String, layoutPosition: Int) {
        if (binding is ItemResultImageBinding) {
            // item is url
            Log.e("VinhTQ", "setData: $item", )
            Glide.with(contextParams).load(item).listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.animLoading.visibleView()

                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.animLoading.goneView()

                    return false
                }
            }).into(binding.imgPlantResult)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun submitData(newData: List<String>) {
        list.apply {
            clear()
            addAll(newData)
            notifyDataSetChanged()
        }
    }
}