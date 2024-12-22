package com.plantcare.ai.identifier.plantid.ui.component.main.bottom_bar.myplant

import android.os.Build
import android.util.Log
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
import com.plantcare.ai.identifier.plantid.databinding.ActivityMyPlantDetailBinding
import com.plantcare.ai.identifier.plantid.domains.HistoryDomain
import com.plantcare.ai.identifier.plantid.domains.PlantDomain
import com.plantcare.ai.identifier.plantid.ui.bases.BaseActivity
import com.plantcare.ai.identifier.plantid.ui.bases.ext.click
import com.plantcare.ai.identifier.plantid.ui.component.plan_identifier.result.adapter.CommonNameAdapter
import com.plantcare.ai.identifier.plantid.ui.component.plan_identifier.result.adapter.ImageResultAdapter
import kotlin.math.abs

class MyPlantDetailActivity : BaseActivity<ActivityMyPlantDetailBinding>() {

    private var imageResultAdapter: ImageResultAdapter? = null

    override fun getLayoutActivity(): Int = R.layout.activity_my_plant_detail
    private var commonNameAdapter: CommonNameAdapter? = null

    override fun initViews() {
        AdsManager.loadBanner(this, BuildConfig.banner_all, mBinding.frAds, RemoteConfigUtils.getOnBannerAll())
        val plant = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(
                AppConstants.KEY_PLANT,
                PlantDomain::class.java
            )
        } else {
            intent.getSerializableExtra(AppConstants.KEY_PLANT) as? PlantDomain
        }

        val history = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(
                AppConstants.KEY_HISTORY,
                HistoryDomain::class.java
            )
        } else {
            intent.getSerializableExtra(AppConstants.KEY_HISTORY) as? HistoryDomain
        }

        if (plant != null && history == null) {
            initCommonNameAdapter(plant)
            initImageResultAdapter(plant)

            mBinding.apply {
                tvPlantName.text = plant.scientificName
                tvGenusValue.text = plant.genus
                tvFamilyValue.text = plant.family
                tvPlantName.isSelected = true
            }
        } else if(plant == null && history != null) {
            initCommonNameAdapter(history)
            initImageResultAdapter(history)

            mBinding.apply {
                tvPlantName.text = history.scientificName
                tvGenusValue.text = history.genus
                tvFamilyValue.text = history.family
                tvPlantName.isSelected = true
            }
        }
    }

    override fun onClickViews() {
        mBinding.icClose.click { onBackPressed() }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun initCommonNameAdapter(plant: PlantDomain) = mBinding.rcvCommonName.apply {
        commonNameAdapter = CommonNameAdapter().apply {
            submitData(plant.commonNames)
        }

        adapter = commonNameAdapter
    }

    private fun initCommonNameAdapter(historyDomain: HistoryDomain) = mBinding.rcvCommonName.apply {
        commonNameAdapter = CommonNameAdapter().apply {
            submitData(historyDomain.commonNames)
        }

        adapter = commonNameAdapter
    }

    private fun initImageResultAdapter(plantDomain: PlantDomain) = mBinding.vpgListImage.apply {
        imageResultAdapter = ImageResultAdapter(this@MyPlantDetailActivity).apply {
            submitData(plantDomain.images)
        }

        adapter = imageResultAdapter
        orientation = ViewPager2.ORIENTATION_HORIZONTAL
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

        setupIndicators(plantDomain.images.size)
    }

    private fun initImageResultAdapter(historyDomain: HistoryDomain) = mBinding.vpgListImage.apply {
        imageResultAdapter = ImageResultAdapter(this@MyPlantDetailActivity).apply {
            submitData(historyDomain.images)
        }

        adapter = imageResultAdapter
        orientation = ViewPager2.ORIENTATION_HORIZONTAL
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

        setupIndicators(historyDomain.images.size)
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

    override fun onDestroy() {
        super.onDestroy()
        mBinding.vpgListImage.unregisterOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {})
    }

}