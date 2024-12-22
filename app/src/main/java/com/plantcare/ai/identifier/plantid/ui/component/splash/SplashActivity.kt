package com.plantcare.ai.identifier.plantid.ui.component.splash

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.os.CountDownTimer
import com.ads.control.admob.Admob
import com.ads.control.admob.AppOpenManager
import com.ads.control.ads.ITGAd
import com.ads.control.funtion.AdCallback
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentInformation
import com.google.android.ump.FormError
import com.google.firebase.messaging.FirebaseMessaging
import com.itg.iaumodule.IAdConsentCallBack
import com.itg.iaumodule.ITGAdConsent
import com.plantcare.ai.identifier.plantid.BuildConfig
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.ads.AdsManager
import com.plantcare.ai.identifier.plantid.ads.AdsManager.loadNativeLanguage
import com.plantcare.ai.identifier.plantid.ads.AdsManager.loadNativeLanguageClick
import com.plantcare.ai.identifier.plantid.ads.AdsManager.nativeAdOnboardingPage1
import com.plantcare.ai.identifier.plantid.ads.AdsManager.nativeAdOnboardingPage4
import com.plantcare.ai.identifier.plantid.ads.RemoteConfigUtils
import com.plantcare.ai.identifier.plantid.app.AppConstants
import com.plantcare.ai.identifier.plantid.app.AppConstants.KEY_CONFIRM_CONSENT
import com.plantcare.ai.identifier.plantid.app.AppConstants.KEY_FIRST_INSTALL_APP
import com.plantcare.ai.identifier.plantid.app.AppConstants.KEY_IS_USER_GLOBAL
import com.plantcare.ai.identifier.plantid.app.AppConstants.KEY_SECRET_KEY
import com.plantcare.ai.identifier.plantid.app.GlobalApp.Companion.isShowDialogRateInThisSession
import com.plantcare.ai.identifier.plantid.databinding.ActivitySplashBinding
import com.plantcare.ai.identifier.plantid.ui.bases.BaseActivity
import com.plantcare.ai.identifier.plantid.ui.bases.ext.goneView
import com.plantcare.ai.identifier.plantid.ui.bases.ext.isNetwork
import com.plantcare.ai.identifier.plantid.ui.bases.ext.visibleView
import com.plantcare.ai.identifier.plantid.utils.EasyPreferences.get
import com.plantcare.ai.identifier.plantid.utils.EasyPreferences.set
import com.plantcare.ai.identifier.plantid.utils.ITGTrackingHelper
import com.plantcare.ai.identifier.plantid.utils.ITGTrackingHelper.logEvent
import com.plantcare.ai.identifier.plantid.utils.Routes.startLanguageActivity
import dagger.hilt.android.AndroidEntryPoint


@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>(), RemoteConfigUtils.Listener {
    private var getConfigSuccess = false
    private var canPersonalized = true
    private var simpleExoPlayer: ExoPlayer? = null
    private var isFirstInstallApp = true

    private val uriVideo = Uri.parse("asset:///splash_plant.mp4")
    private val isHasVideo = true

    override fun getLayoutActivity() = R.layout.activity_splash

    override fun initViews() {
        super.initViews()
        nativeAdOnboardingPage1 = null
        nativeAdOnboardingPage4 = null
        AppConstants.isShowAds = false
        MobileAds.initialize(this)
        isFirstInstallApp = prefs[KEY_FIRST_INSTALL_APP, true] == true
        isShowDialogRateInThisSession = false

        RemoteConfigUtils.init(this)
        getSecretKeyFromFirebase()
        if (prefs[KEY_CONFIRM_CONSENT, false] == false && prefs[KEY_IS_USER_GLOBAL, false] == false && isNetwork()) {
            checkNeedToLoadConsent()
        } else {
            loadingRemoteConfig()
        }

        if (isHasVideo) {
            mBinding.imgSplash.goneView()
            mBinding.playerView.visibleView()
            simpleExoPlayer = ExoPlayer.Builder(this)
                .setRenderersFactory(DefaultRenderersFactory(this).setEnableDecoderFallback(true))
                .build()
            mBinding.playerView.player = simpleExoPlayer
            val mediaItem: MediaItem = MediaItem.fromUri(uriVideo)
            simpleExoPlayer?.addMediaItem(mediaItem)
            simpleExoPlayer?.prepare()
            simpleExoPlayer?.volume = 0f
            simpleExoPlayer?.repeatMode = ExoPlayer.REPEAT_MODE_ALL
            simpleExoPlayer?.playWhenReady = true
            simpleExoPlayer?.play()
        } else {
            mBinding.imgSplash.visibleView()
            mBinding.playerView.goneView()
        }
    }

    private fun checkNeedToLoadConsent() {
        logEvent(ITGTrackingHelper.LOAD_CONSENT_1, null)
        ITGAdConsent.loadAndShowConsent(true, object : IAdConsentCallBack {
            override fun getCurrentActivity(): Activity = this@SplashActivity

            override fun isDebug(): Boolean = BuildConfig.DEBUG

            override fun isUnderAgeAd(): Boolean = false

            override fun onConsentError(formError: FormError) {
                canPersonalized = true
                logEvent(ITGTrackingHelper.CONSENT_ERROR_1, null)
                loadingRemoteConfig()
            }

            override fun onConsentStatus(consentStatus: Int) {
                canPersonalized = consentStatus != ConsentInformation.ConsentStatus.REQUIRED
            }

            override fun onConsentSuccess(b: Boolean) {
                canPersonalized = b
                handleClickConsent(canPersonalized)
            }

            override fun onNotUsingAdConsent() {
                logEvent(ITGTrackingHelper.NOT_USING_DISPLAY_CONSENT_1, null)
                prefs[KEY_IS_USER_GLOBAL] = true
                canPersonalized = true
                loadingRemoteConfig()
            }

            override fun onRequestShowDialog() {
                logEvent(ITGTrackingHelper.DISPLAY_CONSENT_1, null)
            }

            override fun testDeviceID(): String = "ED3576D8FCF2F8C52AD8E98B4CFA4005"
        })

    }

    private fun handleClickConsent(canPersonalized: Boolean) {
        if (canPersonalized) {
            logEvent(ITGTrackingHelper.AGREE_CONSENT_1, null)
            prefs[KEY_CONFIRM_CONSENT] = true
        } else {
            ITGAdConsent.resetConsentDialog()
            logEvent(ITGTrackingHelper.REFUSE_CONSENT_1, null)
        }
        loadingRemoteConfig()
    }

    private fun loadingRemoteConfig() {
        object : CountDownTimer(AppConstants.DEFAULT_TIME_SPLASH, 100) {
            override fun onTick(millisUntilFinished: Long) {
                if (getConfigSuccess && millisUntilFinished < AppConstants.DEFAULT_LIMIT_TIME_SPLASH) {
                    checkRemoteConfigResult()
                    cancel()
                }
            }

            override fun onFinish() {
                if (!getConfigSuccess) checkRemoteConfigResult()
            }
        }.start()
    }


    private fun checkRemoteConfigResult() {
        loadBannerSplash()
        loadNativeLanguage(this )
        loadNativeLanguageClick(this)

        if (RemoteConfigUtils.getOnInterSplash() && isNetwork()) {
            Admob.getInstance().setOpenActivityAfterShowInterAds(false)
            ITGAd.getInstance().loadSplashInterstitialAds(this,
                BuildConfig.inter_splash,
                30000,
                5000,
                object : AdCallback() {
                    override fun onNextAction() {
                        super.onNextAction()
                        moveActivity()
                    }
                })
        } else moveActivity()


        if (RemoteConfigUtils.getOnOpenResume()) AppOpenManager.getInstance().enableAppResume()
        else AppOpenManager.getInstance().disableAppResume()
    }

    private fun moveActivity() {
        startLanguageActivity(this, null)
        finish()
    }

    override fun onResume() {
        super.onResume()
        ITGAd.getInstance().onCheckShowSplashWhenFail(this@SplashActivity, object : AdCallback() {
            override fun onNextAction() {
                super.onNextAction()
                moveActivity()
            }
        }, 1000)
    }

    override fun loadSuccess() {
        getConfigSuccess = true
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    private fun loadBannerSplash(){
        AdsManager.loadBanner(this, BuildConfig.banner_splash, mBinding.frAds, RemoteConfigUtils.getOnBannerSplash())
    }

    private fun getSecretKeyFromFirebase() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            }
            if (task.result != null) {
                val token: String = task.result
                prefs[KEY_SECRET_KEY] = token
            }
        }
    }
}