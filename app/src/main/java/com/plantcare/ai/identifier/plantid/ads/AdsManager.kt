package com.plantcare.ai.identifier.plantid.ads

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.ads.control.ads.ITGAd
import com.ads.control.ads.wrapper.ApInterstitialAd
import com.ads.control.ads.wrapper.ApNativeAd
import com.ads.control.billing.AppPurchase
import com.ads.control.funtion.AdCallback
import com.ads.control.util.AppConstant
import com.google.android.gms.ads.LoadAdError
import com.plantcare.ai.identifier.plantid.BuildConfig
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.ui.bases.ext.goneView
import com.plantcare.ai.identifier.plantid.ui.bases.ext.isNetwork

@SuppressLint("StaticFieldLeak")
object AdsManager {

    var nativeAdLanguage: ApNativeAd? = null
    var nativeAdLanguageClick: ApNativeAd? = null

    var nativeAdOnboardingPage1: ApNativeAd? = null
    var nativeAdOnboardingPage4: ApNativeAd? = null

    var nativeAdPermission: ApNativeAd? = null

    var preLoadNativeListener: PreLoadNativeListener? = null

    var mInterHome: ApInterstitialAd? = null

    fun setPreLoadNativeCallback(listener: PreLoadNativeListener) {
        preLoadNativeListener = listener
    }

    @SuppressLint("LogNotTimber")
    fun loadNativeLanguage(activity: Activity) {
        if (nativeAdLanguage == null && activity.isNetwork()) {
            if (RemoteConfigUtils.getOnNativeLanguage()) {
                Log.d("duylt", "Pre Load Native Language 1")
                ITGAd.getInstance().loadNativeAdResultCallback(
                    activity, BuildConfig.native_language,
                    R.layout.layout_native_language,
                    object: AdCallback() {
                        override fun onNativeAdLoaded(nativeAd: ApNativeAd) {
                            super.onNativeAdLoaded(nativeAd)
                            nativeAdLanguage = nativeAd
                            if (preLoadNativeListener != null) {
                                preLoadNativeListener?.onLoadNativeSuccess()
                            }
                        }

                        override fun onAdFailedToLoad(adError: LoadAdError?) {
                            super.onAdFailedToLoad(adError)
                            if (preLoadNativeListener != null) {
                                preLoadNativeListener?.onLoadNativeFail()
                            }
                        }
                    }
                )
            }
        }
    }

    @SuppressLint("LogNotTimber")
    fun loadNativeLanguageClick(activity: Activity) {
        if (nativeAdLanguageClick == null && activity.isNetwork()) {
            if (RemoteConfigUtils.getOnNativeLanguageClick()) {
                Log.d("duylt", "Pre Load Native Language Click 1")
                ITGAd.getInstance().loadNativeAdResultCallback(
                    activity, BuildConfig.native_language_click,
                    R.layout.layout_native_language,
                    object: AdCallback() {
                        override fun onNativeAdLoaded(nativeAd: ApNativeAd) {
                            super.onNativeAdLoaded(nativeAd)
                            nativeAdLanguageClick = nativeAd
                            if (preLoadNativeListener != null) {
                                preLoadNativeListener?.onLoadNativeSuccess()
                            }
                        }

                        override fun onAdFailedToLoad(adError: LoadAdError?) {
                            super.onAdFailedToLoad(adError)
                            if (preLoadNativeListener != null) {
                                preLoadNativeListener?.onLoadNativeFail()
                            }
                        }
                    }
                )
            }
        }
    }

    @SuppressLint("LogNotTimber")
    fun loadNativeOnbPage1(activity: Activity) {
        if (nativeAdOnboardingPage1 == null && activity.isNetwork()) {
            if (RemoteConfigUtils.getOnNativeOnboarding() ) {
                Log.d("duylt", "Pre Load Native Onb Page 1_1")
                ITGAd.getInstance().loadNativeAdResultCallback(
                    activity, BuildConfig.native_onboarding,
                    R.layout.layout_native_on_boarding,
                    object: AdCallback() {
                        override fun onNativeAdLoaded(nativeAd: ApNativeAd) {
                            super.onNativeAdLoaded(nativeAd)
                            nativeAdOnboardingPage1 = nativeAd
                            preLoadNativeListener?.onLoadNativeSuccess()
                        }

                        override fun onAdFailedToLoad(adError: LoadAdError?) {
                            super.onAdFailedToLoad(adError)
                            preLoadNativeListener?.onLoadNativeFail()
                        }
                    }
                )
            }
        }
    }

    @SuppressLint("LogNotTimber")
    fun loadNativeOnbPage4(activity: Activity ) {
        if (nativeAdOnboardingPage4 == null && activity.isNetwork()) {
            if (RemoteConfigUtils.getOnNativeOnboardingPage4() ) {
                Log.d("duylt", "Pre Load Native Onb Page 1_4")
                ITGAd.getInstance().loadNativeAdResultCallback(
                    activity, BuildConfig.native_onboarding_page4,
                    R.layout.layout_native_on_boarding,
                    object: AdCallback() {
                        override fun onNativeAdLoaded(nativeAd: ApNativeAd) {
                            super.onNativeAdLoaded(nativeAd)
                            nativeAdOnboardingPage4 = nativeAd
                            preLoadNativeListener?.onLoadNativeSuccess()
                        }

                        override fun onAdFailedToLoad(adError: LoadAdError?) {
                            super.onAdFailedToLoad(adError)
                            preLoadNativeListener?.onLoadNativeFail()
                        }
                    }
                )
            }
        }
    }

    fun loadNativePermission(activity: Activity) {
        if (nativeAdPermission == null) {
            if (RemoteConfigUtils.getOnNativePermission() && activity.isNetwork()) {
                ITGAd.getInstance().loadNativeAdResultCallback(activity,
                    BuildConfig.native_permission,
                    R.layout.layout_native_small_permission,
                    object : AdCallback() {
                        override fun onNativeAdLoaded(nativeAd: ApNativeAd) {
                            super.onNativeAdLoaded(nativeAd)
                            nativeAdPermission = nativeAd
                            if (preLoadNativeListener != null) {
                                preLoadNativeListener?.onLoadNativeSuccess()
                            }
                        }

                        override fun onAdFailedToLoad(adError: LoadAdError?) {
                            super.onAdFailedToLoad(adError)
                            if (preLoadNativeListener != null) {
                                preLoadNativeListener?.onLoadNativeFail()
                            }
                        }
                    })
            }
        }
    }

    fun loadInterHome(context: Context) {
        if (mInterHome == null && RemoteConfigUtils.getOnInterHome() && !AppPurchase.getInstance().isPurchased) {
            mInterHome = ITGAd.getInstance()
                .getInterstitialAds(context, BuildConfig.inter_home, object : AdCallback() {})
        }
    }

    fun loadBanner(activity: AppCompatActivity, id: String, frAds: FrameLayout, bool: Boolean) {
        if (isNetwork(activity) && bool) {
            ITGAd.getInstance().loadBanner(activity, id, object : AdCallback() {
                override fun onAdFailedToLoad(i: LoadAdError?) {
                    super.onAdFailedToLoad(i)
                    frAds.removeAllViews()
                    frAds.goneView()
                }
            })
        } else {
            frAds.removeAllViews()
            frAds.goneView()
        }
    }

    fun loadCollapsibleBanner(
        activity: AppCompatActivity,
        id: String,
        frAds: FrameLayout,
        bool: Boolean
    ) {
        if (isNetwork(activity) && bool && !AppPurchase.getInstance().isPurchased) {
            ITGAd.getInstance()
                .loadCollapsibleBanner(activity, id, AppConstant.CollapsibleGravity.BOTTOM, object :
                    AdCallback() {})
        } else {
            frAds.removeAllViews()
            frAds.goneView()
        }
    }

    internal const val TIME_RELOAD_BANNER = 35
    private var handler: Handler? = null
    private var runnable: Runnable? = null

    fun reloadCollapsibleBanner(
        activity: AppCompatActivity,
        id: String,
        frAds: FrameLayout,
        bool: Boolean
    ) {

        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            @SuppressLint("InflateParams")
            override fun run() {
                val shimmerFrameLayout =
                    LayoutInflater.from(activity).inflate(com.ads.control.R.layout.layout_banner_control, null)
                frAds.removeAllViews()
                frAds.addView(shimmerFrameLayout)

                loadCollapsibleBanner(activity, id, frAds, bool)
                handler?.postDelayed(this, (TIME_RELOAD_BANNER * 1000).toLong())
            }
        }

        handler?.postDelayed(
            runnable as Runnable,
            (TIME_RELOAD_BANNER * 1000).toLong()
        )

        loadCollapsibleBanner(activity, id, frAds, bool)

    }

    // call onDestroy off activity
    fun removeHandler() {
        if (handler != null) {
            runnable?.let { handler?.removeCallbacks(it) }
        }
    }

}
