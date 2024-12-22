package com.plantcare.ai.identifier.plantid.app

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Build
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.ads.control.admob.Admob
import com.ads.control.admob.AppOpenManager
import com.ads.control.ads.ITGAd
import com.ads.control.application.AdsMultiDexApplication
import com.ads.control.billing.AppPurchase
import com.ads.control.config.AdjustConfig
import com.ads.control.config.ITGAdConfig
import com.plantcare.ai.identifier.plantid.BuildConfig
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.app.AppConstants.ID_SUB
import com.plantcare.ai.identifier.plantid.app.AppConstants.ID_SUB_30_TIMES
import com.plantcare.ai.identifier.plantid.ui.component.language.LanguageActivity
import com.plantcare.ai.identifier.plantid.ui.component.language.LanguageModel
import com.plantcare.ai.identifier.plantid.ui.component.onboarding.OnBoardingActivity
import com.plantcare.ai.identifier.plantid.ui.component.permision.PermissionActivity
import com.plantcare.ai.identifier.plantid.ui.component.splash.SplashActivity
import com.plantcare.ai.identifier.plantid.utils.SystemUtil
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.util.*

@HiltAndroidApp
class GlobalApp : AdsMultiDexApplication() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: GlobalApp

        var isShowDialogRateInThisSession = false
        var activityVisible = true
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        initAds()
        initBilling()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        activityVisible = false
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        activityVisible = true
    }

    private fun initAds() {
        val environment =
            if (BuildConfig.DEBUG) ITGAdConfig.ENVIRONMENT_DEVELOP else ITGAdConfig.ENVIRONMENT_PRODUCTION
        mITGAdConfig = ITGAdConfig(this, environment)

        // Optional: setup Adjust event
        val adjustConfig = AdjustConfig(true, resources.getString(R.string.adjust_token))
        mITGAdConfig.adjustConfig = adjustConfig
        mITGAdConfig.facebookClientToken = resources.getString(R.string.facebook_client_token)
        mITGAdConfig.adjustTokenTiktok = resources.getString(R.string.event_token)
        mITGAdConfig.intervalInterstitialAd = 35
        mITGAdConfig.listDeviceTest = listOf("24CF6F28E641E91BF9C45786F8F722ED", "24CF6F28E641E91BF9C45786F8F722ED")

        // Optional: setup Appsflyer event
//        AppsflyerConfig appsflyerConfig = new AppsflyerConfig(true,APPSFLYER_TOKEN);
//        aperoAdConfig.setAppsflyerConfig(appsflyerConfig);

        // Optional: enable ads resume
        if (AppPurchase.getInstance().isPurchased) {
            mITGAdConfig.idAdResume = ""
        } else {
            mITGAdConfig.idAdResume = BuildConfig.open_resume
        }

        ITGAd.getInstance().init(this, mITGAdConfig)

        // Auto disable ad resume after user click ads and back to app
        Admob.getInstance().setDisableAdResumeWhenClickAds(true)
        // If true -> onNextAction() is called right after Ad Interstitial showed
        Admob.getInstance().setOpenActivityAfterShowInterAds(true)


        AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity::class.java)
        AppOpenManager.getInstance().disableAppResumeWithActivity(LanguageActivity::class.java)
        AppOpenManager.getInstance().disableAppResumeWithActivity(OnBoardingActivity::class.java)
        AppOpenManager.getInstance().disableAppResumeWithActivity(PermissionActivity::class.java)
    }

    private fun initBilling() {
        AppPurchase.getInstance().initBilling(
            this,
            arrayListOf(ID_SUB_30_TIMES),
            arrayListOf(ID_SUB)
        )
    }

    fun getLanguage(): LanguageModel? {
        var languageModel: LanguageModel? = null
        val lang = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Resources.getSystem().configuration.locales[0].language
        } else {
            Resources.getSystem().configuration.locale.language
        }
        val key = if (!SystemUtil.languageApp.contains(lang)) {
            ""
        } else {
            lang
        }
        for (model in getListLanguageApp()) {
            if (key == model?.isoLanguage) {
                languageModel = model
                break
            }
        }
        return languageModel
    }

    private fun getListLanguageApp(): ArrayList<LanguageModel?> {
        val lists: ArrayList<LanguageModel?> = arrayListOf()
        lists.add(LanguageModel("Hindi", "hi", false, R.drawable.ic_hindi))
        lists.add(LanguageModel("Spanish", "es", false, R.drawable.ic_spanish))
        lists.add(LanguageModel("Croatian", "hr", false, R.drawable.ic_croatia))
        lists.add(LanguageModel("Czech", "cs", false, R.drawable.ic_czech_republic))
        lists.add(LanguageModel("Dutch", "nl", false, R.drawable.ic_dutch))
        lists.add(LanguageModel("English", "en", false, R.drawable.ic_english))
        lists.add(LanguageModel("Filipino", "fil", false, R.drawable.ic_filipino))
        lists.add(LanguageModel("French", "fr", false, R.drawable.ic_france))
        lists.add(LanguageModel("German", "de", false, R.drawable.ic_german))
        lists.add(LanguageModel("Indonesian", "in", false, R.drawable.ic_indonesian))
        lists.add(LanguageModel("Italian", "it", false, R.drawable.ic_italian))
        lists.add(LanguageModel("Japanese", "ja", false, R.drawable.ic_japanese))
        lists.add(LanguageModel("Korean", "ko", false, R.drawable.ic_korean))
        lists.add(LanguageModel("Malay", "ms", false, R.drawable.ic_malay))
        lists.add(LanguageModel("Polish", "pl", false, R.drawable.ic_polish))
        lists.add(LanguageModel("Portuguese", "pt", false, R.drawable.ic_portugal))
        lists.add(LanguageModel("Russian", "ru", false, R.drawable.ic_russian))
        lists.add(LanguageModel("Serbian", "sr", false, R.drawable.ic_serbian))
        lists.add(LanguageModel("Swedish", "sv", false, R.drawable.ic_swedish))
        lists.add(LanguageModel("Turkish", "tr", false, R.drawable.ic_turkish))
        lists.add(LanguageModel("Vietnamese", "vi", false, R.drawable.ic_vietnamese))
        lists.add(LanguageModel("China", "zh", false, R.drawable.ic_china))
        return lists
    }
}