package com.plantcare.ai.identifier.plantid.ui.component.diagnose

import android.annotation.SuppressLint
import android.os.Build
import android.os.Build.VERSION
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.plantcare.ai.identifier.plantid.BuildConfig
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.ads.AdsManager
import com.plantcare.ai.identifier.plantid.ads.RemoteConfigUtils
import com.plantcare.ai.identifier.plantid.app.AppConstants
import com.plantcare.ai.identifier.plantid.databinding.ActivityPlantDiseaseDetailBinding
import com.plantcare.ai.identifier.plantid.ui.bases.BaseActivity
import com.plantcare.ai.identifier.plantid.ui.bases.ext.click
import com.plantcare.ai.identifier.plantid.ui.component.diagnose.adapter.PlantDiseaseDetailAdapter
import com.plantcare.ai.identifier.plantid.ui.component.diagnose.model.PlantDiseaseModel
import kotlin.math.abs

class PlantDiseaseDetailActivity : BaseActivity<ActivityPlantDiseaseDetailBinding>() {
    private lateinit var imageViewPagerAdapter: PlantDiseaseDetailAdapter
    override fun getLayoutActivity(): Int = R.layout.activity_plant_disease_detail


    override fun initViews() {
        AdsManager.loadBanner(this, BuildConfig.banner_all, mBinding.frAds, RemoteConfigUtils.getOnBannerAll())
        val plantDisease = if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(
                AppConstants.KEY_PLANT_DISEASE,
                PlantDiseaseModel::class.java
            )
        } else {
            intent.getParcelableExtra(AppConstants.KEY_PLANT_DISEASE)
        }

        imageViewPagerAdapter = PlantDiseaseDetailAdapter()

        imageViewPagerAdapter.submitData(plantDisease?.imageList ?: emptyList())

        mBinding.vpgListImage.apply {
            adapter = imageViewPagerAdapter
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 1
            getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

            val comPosPageTarn = CompositePageTransformer()
            comPosPageTarn.addTransformer(MarginPageTransformer(20))
            comPosPageTarn.addTransformer { page, position ->
                val r: Float = 1 - abs(position)
                page.scaleY = 0.85f + r * 0.15f
            }
            setPageTransformer(comPosPageTarn)
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    updateIndicators(position)
                }
            })
        }

        setupIndicators(plantDisease?.imageList?.size ?: 0)

        mBinding.vpgListImage.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateIndicators(position)
            }
        })

        val spannableStringDisease =
            SpannableString("${getString(R.string.disease)} ${
                plantDisease?.diseaseName?.let {
                    getString(
                        it
                    )
                }
            }")
        spannableStringDisease.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.primary)),
            0,
            getString(R.string.disease).length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        mBinding.tvDisease.text = spannableStringDisease

        val spannableStringSymptoms =
            SpannableString("${getString(R.string.symptoms)} ${
                plantDisease?.diseaseSymptoms?.let {
                    getString(
                        it
                    )
                }
            }")
        spannableStringSymptoms.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.primary)),
            0,
            getString(R.string.symptoms).length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        mBinding.tvSymptoms.text = spannableStringSymptoms

        val spannableStringCause =
            SpannableString("${getString(R.string.cause)} ${
                plantDisease?.diseaseCause?.let {
                    getString(
                        it
                    )
                }
            }")
        spannableStringCause.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.primary)),
            0,
            getString(R.string.cause).length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        mBinding.tvCause.text = spannableStringCause

        val spannableStringManagement = SpannableString(
            "${getString(R.string.management)} ${
                plantDisease?.diseaseManagement?.let {
                    getString(
                        it
                    )
                }
            }"
        )
        spannableStringManagement.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.primary)),
            0,
            getString(R.string.management).length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        mBinding.tvManagement.text = spannableStringManagement

    }

    override fun onClickViews() {
        mBinding.icClose.click { onBackPressed() }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.vpgListImage.unregisterOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {})
    }


    private fun setupIndicators(count: Int) {
        mBinding.indicatorContainer.removeAllViews()

        for (i in 0 until count) {
            val indicator = View(this)
            indicator.layoutParams = LinearLayout.LayoutParams(24, 24).apply {
                marginEnd = 8
            }
            indicator.setBackgroundResource(R.drawable.slider_indicator_unselected)
            indicator.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.color_D1E7C2)
            mBinding.indicatorContainer.addView(indicator)
        }

        if (mBinding.indicatorContainer.childCount > 0) {
            (mBinding.indicatorContainer.getChildAt(0) as View).layoutParams =
                LinearLayout.LayoutParams(60, 24).apply {
                    marginEnd = 8
                }
            (mBinding.indicatorContainer.getChildAt(0) as View).setBackgroundResource(R.drawable.slider_indicator_selected)
            (mBinding.indicatorContainer.getChildAt(0) as View).backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.primary)
        }
    }

    private fun updateIndicators(position: Int) {
        for (i in 0 until mBinding.indicatorContainer.childCount) {
            val indicator = mBinding.indicatorContainer.getChildAt(i)
            if (i == position) {
                indicator.setBackgroundResource(R.drawable.slider_indicator_selected)
                indicator.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.primary)
                indicator.layoutParams = LinearLayout.LayoutParams(60, 24).apply {
                    marginEnd = 8
                }
            } else {
                indicator.setBackgroundResource(R.drawable.slider_indicator_unselected)
                indicator.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.color_D1E7C2)
                indicator.layoutParams = LinearLayout.LayoutParams(24, 24).apply {
                    marginEnd = 8
                }
            }
        }
    }

    @Deprecated("Deprecated in Java", ReplaceWith("finish()"))
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        setResult(RESULT_OK)
        finish()
    }
}