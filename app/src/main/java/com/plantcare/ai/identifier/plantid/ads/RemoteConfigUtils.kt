package com.plantcare.ai.identifier.plantid.ads

import android.annotation.SuppressLint
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.plantcare.ai.identifier.plantid.BuildConfig

object RemoteConfigUtils {

    private const val TAG = "RemoteConfigUtils"
    private const val ON_SHOW_DIALOG_CONSENT = "on_show_dialog_consent"

    /*Open ads*/
    private const val ON_OPEN_RESUME_ADS = "open_resume"

    /*Inter ads*/
    private const val ON_INTER_SPLASH = "on_inter_splash"
    private const val ON_INTER_HOME = "on_inter_home"

    /*Native ads*/
    private const val ON_NATIVE_LANGUAGE = "native_language"
    private const val ON_NATIVE_LANGUAGE_CLICK = "native_language_click"
    private const val ON_NATIVE_ON_BOARDING = "native_onboarding"
    private const val ON_NATIVE_ON_BOARDING_PAGE_4 = "native_onboarding_page4"
    private const val ON_NATIVE_PERMISSION = "native_permission"
    private const val ON_NATIVE_HOME = "native_home"
    private const val ON_NATIVE_MY_PLANT = "native_my_plant"
    private const val ON_NATIVE_RESULT = "native_result"
    private const val ON_NATIVE_DIAGNOSE = "native_Diagnose"

    /*Banner ads*/
    private const val ON_BANNER_SPLASH = "banner_splash"
    private const val ON_BANNER_ALL = "banner_all"


    var completed = false
    private val DEFAULTS: HashMap<String, Any> = hashMapOf(
        ON_OPEN_RESUME_ADS to true,
        ON_INTER_SPLASH to true,
        ON_INTER_HOME to true,
        ON_NATIVE_LANGUAGE to true,
        ON_NATIVE_LANGUAGE_CLICK to true,
        ON_NATIVE_ON_BOARDING to true,
        ON_NATIVE_ON_BOARDING_PAGE_4 to true,
        ON_NATIVE_PERMISSION to true,
        ON_NATIVE_HOME to true,
        ON_NATIVE_MY_PLANT to true,
        ON_NATIVE_RESULT to true,
        ON_NATIVE_DIAGNOSE to true,
        ON_BANNER_SPLASH to true,
        ON_BANNER_ALL to true,
    )

    interface Listener {
        fun loadSuccess()
    }

    lateinit var listener: Listener

    @SuppressLint("StaticFieldLeak")
    private lateinit var remoteConfig: FirebaseRemoteConfig

    fun init(mListener: Listener) {
        listener = mListener
        remoteConfig = getFirebaseRemoteConfig()
    }

    private fun getFirebaseRemoteConfig(): FirebaseRemoteConfig {
        remoteConfig = Firebase.remoteConfig

        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) {
                0
            } else {
                60 * 60
            }
        }
        remoteConfig.apply {
            setConfigSettingsAsync(configSettings)
            setDefaultsAsync(DEFAULTS)
            fetchAndActivate().addOnCompleteListener {
                listener.loadSuccess()
                completed = true
            }
        }
        return remoteConfig
    }

    fun getOnNativeLanguage() = try {
        if (!completed) false else remoteConfig.getBoolean(ON_NATIVE_LANGUAGE)
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    fun getOnNativeLanguageClick() = try {
        if (!completed) false else remoteConfig.getBoolean(ON_NATIVE_LANGUAGE_CLICK)
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    fun getOnNativeOnboarding() = try {
        if (!completed) false else remoteConfig.getBoolean(ON_NATIVE_ON_BOARDING)
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    fun getOnNativeOnboardingPage4() = try {
        if (!completed) false else remoteConfig.getBoolean(ON_NATIVE_ON_BOARDING_PAGE_4)
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    fun getOnNativePermission() = try {
        if (!completed) false else remoteConfig.getBoolean(ON_NATIVE_PERMISSION)
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }


    fun getOnNativeHome() = try {
        if (!completed) false else remoteConfig.getBoolean(ON_NATIVE_HOME)
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    fun getOnNativeMyPlant() = try {
        if (!completed) false else remoteConfig.getBoolean(ON_NATIVE_MY_PLANT)
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    fun getOnNativeResult() = try {
        if (!completed) false else remoteConfig.getBoolean(ON_NATIVE_RESULT)
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    fun getOnNativeDiagnose(): Boolean {
        try {
            return if (!completed) {
                false
            } else {
                remoteConfig.getBoolean(ON_NATIVE_DIAGNOSE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }


    fun getOnInterSplash(): Boolean {
        try {
            return if (!completed) {
                false
            } else {
                remoteConfig.getBoolean(ON_INTER_SPLASH)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun getOnInterHome(): Boolean {
        try {
            return if (!completed) {
                false
            } else {
                remoteConfig.getBoolean(ON_INTER_HOME)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun getOnBannerAll(): Boolean {
        try {
            return if (!completed) {
                false
            } else {
                remoteConfig.getBoolean(ON_BANNER_ALL)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun getOnBannerSplash(): Boolean {
        try {
            return if (!completed) {
                false
            } else {
                remoteConfig.getBoolean(ON_BANNER_SPLASH)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun getOnOpenResume(): Boolean {
        try {
            return if (!completed) {
                false
            } else {
                remoteConfig.getBoolean(ON_OPEN_RESUME_ADS)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun getOnShowDialogConsent(): Boolean {
        try {
            return if (!completed) {
                return true
            } else {
                remoteConfig.getBoolean(ON_SHOW_DIALOG_CONSENT)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return false
    }
}