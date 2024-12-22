package com.plantcare.ai.identifier.plantid.ui.component.plan_identifier.result

import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.ads.control.ads.ITGAd
import com.ads.control.ads.wrapper.ApNativeAd
import com.ads.control.funtion.AdCallback
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.plantcare.ai.identifier.plantid.BuildConfig
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.ads.RemoteConfigUtils
import com.plantcare.ai.identifier.plantid.app.AppConstants.KEY_RESULT_SEARCH
import com.plantcare.ai.identifier.plantid.app.AppConstants.KEY_SET_SHOW_DIALOG_RATE
import com.plantcare.ai.identifier.plantid.app.GlobalApp.Companion.isShowDialogRateInThisSession
import com.plantcare.ai.identifier.plantid.databinding.ActivitySearchPlantResultBinding
import com.plantcare.ai.identifier.plantid.domains.PlantDomain
import com.plantcare.ai.identifier.plantid.ui.bases.BaseActivity
import com.plantcare.ai.identifier.plantid.ui.bases.ext.click
import com.plantcare.ai.identifier.plantid.ui.bases.ext.goneView
import com.plantcare.ai.identifier.plantid.ui.bases.ext.isNetwork
import com.plantcare.ai.identifier.plantid.ui.bases.ext.showRateDialog
import com.plantcare.ai.identifier.plantid.ui.bases.ext.showToastById
import com.plantcare.ai.identifier.plantid.ui.component.dialog.AddPlantDialog
import com.plantcare.ai.identifier.plantid.ui.component.plan_identifier.result.adapter.CommonNameAdapter
import com.plantcare.ai.identifier.plantid.ui.component.plan_identifier.result.adapter.ImageResultAdapter
import com.plantcare.ai.identifier.plantid.utils.toPlantEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs

@AndroidEntryPoint
class SearchPlantResultActivity : BaseActivity<ActivitySearchPlantResultBinding>() {
    private var imageResultAdapter: ImageResultAdapter? = null
    private var commonNameAdapter: CommonNameAdapter? = null
    private var reloadAds = 0

    private var plantDomain: PlantDomain? = null

    private val viewModel: SearchPlantResultViewModel by viewModels()

    override fun getLayoutActivity(): Int = R.layout.activity_search_plant_result

    override fun onResume() {
        super.onResume()
        reloadAds++
        if (reloadAds < 3) {
            try {
                initAds()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    private fun initAds() {
        if (RemoteConfigUtils.getOnNativeResult() && isNetwork()) {
            ITGAd.getInstance().loadNativeAd(this,
                BuildConfig.native_result,
                R.layout.layout_native_small_result,
                mBinding.frAds,
                mBinding.layoutShimmer.shimmerNativeLarge,
                object : AdCallback() {
                    override fun onAdFailedToLoad(i: LoadAdError?) {
                        super.onAdFailedToLoad(i)
                        mBinding.frAds.removeAllViews()
                        mBinding.relayAds.goneView()
                    }

                    override fun onAdFailedToShow(adError: AdError?) {
                        super.onAdFailedToShow(adError)
                        mBinding.frAds.removeAllViews()
                        mBinding.relayAds.goneView()
                    }

                })
        }else{
            mBinding.frAds.removeAllViews()
            mBinding.relayAds.goneView()
        }
    }

    override fun initViews() {
        getPlantDomainFromPreviewScreen()
        showRateDialogIfCan()
    }

    override fun onClickViews() {
        mBinding.icClose.click { finish() }

        mBinding.btnAddMyPlant.click {
            plantDomain?.let {
                viewModel.savePlantIfNotExists(
                    plant = it.toPlantEntity()
                )
                val dialogSuccess = AddPlantDialog(this)
                dialogSuccess.show()
                Handler(Looper.getMainLooper()).postDelayed({
                    dialogSuccess.cancel()
                }, 2000)
            }
        }
    }

    private fun showRateDialogIfCan() {
        val isRated = prefs.getBoolean(KEY_SET_SHOW_DIALOG_RATE, false)
        if (!isRated && !isShowDialogRateInThisSession) {
            isShowDialogRateInThisSession = true
            showRateDialog(
                activity = this, isFinish = false
            )
        }
    }

    private fun getPlantDomainFromPreviewScreen() {
        val bundle = intent.extras
        if (bundle != null) {
            plantDomain = bundle.getSerializable(KEY_RESULT_SEARCH) as PlantDomain
            if (plantDomain == null) finishScreenWithToastWrong()
            else showDataToUi(plantDomain!!)
        } else finishScreenWithToastWrong()
    }

    private fun finishScreenWithToastWrong() {
        showToastById(R.string.some_thing_went_wrong)
        finish()
    }

    private fun showDataToUi(plantDomain: PlantDomain) {
        initImageResultAdapter(plantDomain)
        initCommonNameAdapter(plantDomain)

        mBinding.tvPlantName.text = plantDomain.scientificName
        mBinding.tvGenusValue.text = plantDomain.genus
        mBinding.tvFamilyValue.text = plantDomain.family

        mBinding.tvPlantName.isSelected = true
    }

    private fun initImageResultAdapter(plantDomain: PlantDomain) = mBinding.vpgListImage.apply {
        imageResultAdapter = ImageResultAdapter(this@SearchPlantResultActivity).apply {
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

    private fun initCommonNameAdapter(plantDomain: PlantDomain) = mBinding.rcvCommonName.apply {
        commonNameAdapter = CommonNameAdapter().apply {
            submitData(plantDomain.commonNames)
        }

        adapter = commonNameAdapter
    }
}