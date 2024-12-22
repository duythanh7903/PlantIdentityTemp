package com.plantcare.ai.identifier.plantid.ui.component.onboarding

import android.annotation.SuppressLint
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.ads.control.ads.ITGAd
import com.ads.control.ads.wrapper.ApNativeAd
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.ads.AdsManager
import com.plantcare.ai.identifier.plantid.ads.AdsManager.nativeAdOnboardingPage1
import com.plantcare.ai.identifier.plantid.ads.AdsManager.nativeAdOnboardingPage4
import com.plantcare.ai.identifier.plantid.ads.PreLoadNativeListener
import com.plantcare.ai.identifier.plantid.app.AppConstants.KEY_FIRST_INSTALL_APP
import com.plantcare.ai.identifier.plantid.databinding.ActivityOnboardingBinding
import com.plantcare.ai.identifier.plantid.ui.bases.BaseActivity
import com.plantcare.ai.identifier.plantid.ui.bases.ext.goneView
import com.plantcare.ai.identifier.plantid.ui.bases.ext.isNetwork
import com.plantcare.ai.identifier.plantid.ui.bases.ext.visibleView
import com.plantcare.ai.identifier.plantid.ui.component.onboarding.OnbUtils.getListIntro
import com.plantcare.ai.identifier.plantid.ui.component.onboarding.adapter.OnBoardingAdapter
import com.plantcare.ai.identifier.plantid.ui.component.permision.PermissionUtils.isCameraPermissionGranted
import com.plantcare.ai.identifier.plantid.ui.component.permision.PermissionUtils.isLocationPermissionGranted
import com.plantcare.ai.identifier.plantid.utils.Routes.startMainActivity
import com.plantcare.ai.identifier.plantid.utils.Routes.startPermissionActivity
import kotlin.math.abs

class OnBoardingActivity : BaseActivity<ActivityOnboardingBinding>() {
    private var tutorialAdapter: OnBoardingAdapter? = null
    private var posViewPager = 0
    private var populateNative = false
    private var checkPage = false

    override fun getLayoutActivity(): Int = R.layout.activity_onboarding

    override fun initViews() {
        if (prefs.getBoolean(KEY_FIRST_INSTALL_APP, true)) {
            AdsManager.loadNativePermission(this)
        }
        setTvGetStartToNext()
        initVpgOnb()
        Log.e("VinhTQ", "initViews nativeAdOnboardingPage1: $nativeAdOnboardingPage1" )
        initAdmob(nativeAdOnboardingPage1)
    }

    private fun setTvGetStartToNext() {
        mBinding.tvGetStart.text = getString(R.string.next_onb)
    }

    private fun initVpgOnb() =
        mBinding.viewPager2.apply {
            tutorialAdapter = OnBoardingAdapter().apply {
                submitData(getListIntro())
            }
            val compositePageTransformer = CompositePageTransformer()
            compositePageTransformer.addTransformer(MarginPageTransformer(100))
            compositePageTransformer.addTransformer { view, position ->
                val r = 1 - abs(position)
                view.scaleY = 0.8f + r * 0.2f
                val absPosition = abs(position)
                view.alpha = 1.0f - (1.0f - 0.3f) * absPosition
            }

            adapter = tutorialAdapter
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 4
            getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_ALWAYS
            setPageTransformer(compositePageTransformer)
            registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                @SuppressLint("InvalidAnalyticsName", "UseCompatLoadingForDrawables")
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    when (position) {
                        0 -> {
                            posViewPager = 0
                            mBinding.tvGetStart.text = getString(R.string.next_onb)
                            mBinding.view1.setImageResource(R.drawable.ic_view_select)
                            mBinding.view2.setImageResource(R.drawable.ic_view_un_select)
                            mBinding.view3.setImageResource(R.drawable.ic_view_un_select)
                            mBinding.view4.setImageResource(R.drawable.ic_view_un_select)

                            if (checkPage && nativeAdOnboardingPage1 != null) {
                                populateNative = false
                                initAdmob(nativeAdOnboardingPage1)
                            }

                            showAdsView()
                        }

                        1 -> {
                            checkPage = true
                            posViewPager = 1
                            mBinding.tvGetStart.text = getString(R.string.next_onb)
                            mBinding.view1.setImageResource(R.drawable.ic_view_select)
                            mBinding.view2.setImageResource(R.drawable.ic_view_select)
                            mBinding.view3.setImageResource(R.drawable.ic_view_un_select)
                            mBinding.view4.setImageResource(R.drawable.ic_view_un_select)

                            hideAdsView()
                        }

                        2 -> {
                            posViewPager = 2
                            mBinding.tvGetStart.text = getString(R.string.next_onb)
                            mBinding.view1.setImageResource(R.drawable.ic_view_select)
                            mBinding.view2.setImageResource(R.drawable.ic_view_select)
                            mBinding.view3.setImageResource(R.drawable.ic_view_select)
                            mBinding.view4.setImageResource(R.drawable.ic_view_un_select)

                            hideAdsView()
                        }

                        3 -> {
                            posViewPager = 3
                            mBinding.tvGetStart.text = getString(R.string.start_onb)
                            mBinding.view1.setImageResource(R.drawable.ic_view_select)
                            mBinding.view2.setImageResource(R.drawable.ic_view_select)
                            mBinding.view3.setImageResource(R.drawable.ic_view_select)
                            mBinding.view4.setImageResource(R.drawable.ic_view_select)

                            if (nativeAdOnboardingPage4 != null) {
                                populateNative = false
                                initAdmob(nativeAdOnboardingPage4)
                            }

                            showAdsView()
                        }
                    }
                }
            })
        }

    override fun onClickViews() {
        mBinding.tvGetStart.setOnClickListener {
            when (posViewPager) {
                0, 1, 2 -> mBinding.viewPager2.currentItem = posViewPager + 1

                3 -> gotoNextScreen()
            }
        }
    }

    private fun initAdmob(nativeAd: ApNativeAd?) {
        AdsManager.setPreLoadNativeCallback(object: PreLoadNativeListener {
            override fun onLoadNativeSuccess() =
                showNativeOnBoardingAds(nativeAd)

            override fun onLoadNativeFail() {
                if (nativeAd == null) {
                    mBinding.frAds.removeAllViews()
                    mBinding.frAds.goneView()
                }
            }
        })
        showNativeOnBoardingAds(nativeAd)
    }

    private fun showNativeOnBoardingAds(nativeAd: ApNativeAd?) {
        if (!isNetwork(this)) hideAdsView()
        else {
            if (!populateNative) {
                val isPreLoadAdsSuccess = nativeAd != null
                if (isPreLoadAdsSuccess) {
                    populateNative = true

                    showAdsView()
                    populateNativeAdView(nativeAd!!)
                } else hideAdsView()
            }
        }
    }

    private fun populateNativeAdView(nativeAd: ApNativeAd) =
        ITGAd.getInstance().populateNativeAdView(
            this,
            nativeAd,
            mBinding.frAds,
            mBinding.shimmerAds.shimmerNativeLarge
        )

    private fun gotoNextScreen() {
        if (prefs.getBoolean(KEY_FIRST_INSTALL_APP, true)) {
            startPermissionActivity(this)
        } else startMainActivity(this)
        finish()
    }

    private fun hideAdsView(){
        mBinding.frAds.removeAllViews()
        mBinding.frAds.goneView()
    }

    private fun showAdsView() = mBinding.frAds.visibleView()
}