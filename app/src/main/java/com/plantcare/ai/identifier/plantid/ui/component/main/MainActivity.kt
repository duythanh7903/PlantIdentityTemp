package com.plantcare.ai.identifier.plantid.ui.component.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Handler
import android.provider.Settings
import android.util.Log
import androidx.activity.viewModels
import com.ads.control.admob.AppOpenManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.ump.FormError
import com.itg.iaumodule.IAdConsentCallBack
import com.itg.iaumodule.ITGAdConsent
import com.plantcare.ai.identifier.plantid.BuildConfig
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.ads.AdsManager
import com.plantcare.ai.identifier.plantid.ads.RemoteConfigUtils
import com.plantcare.ai.identifier.plantid.app.AppConstants
import com.plantcare.ai.identifier.plantid.app.AppConstants.KEY_FIRST_INSTALL_APP
import com.plantcare.ai.identifier.plantid.databinding.ActivityMainBinding
import com.plantcare.ai.identifier.plantid.ui.bases.BaseActivity
import com.plantcare.ai.identifier.plantid.ui.bases.ext.click
import com.plantcare.ai.identifier.plantid.ui.bases.ext.invisibleView
import com.plantcare.ai.identifier.plantid.ui.bases.ext.isNetwork
import com.plantcare.ai.identifier.plantid.ui.bases.ext.visibleView
import com.plantcare.ai.identifier.plantid.ui.component.dialog.RequestPermissionDialog
import com.plantcare.ai.identifier.plantid.ui.component.iap.IapActivity
import com.plantcare.ai.identifier.plantid.ui.component.permision.PermissionUtils.isLocationPermissionGranted
import com.plantcare.ai.identifier.plantid.ui.component.plan_identifier.camera.PlantIdentifierActivity
import com.plantcare.ai.identifier.plantid.ui.component.setting.SettingActivity
import com.plantcare.ai.identifier.plantid.utils.EasyPreferences.get
import com.plantcare.ai.identifier.plantid.utils.EasyPreferences.set
import com.plantcare.ai.identifier.plantid.utils.ITGTrackingHelper
import com.plantcare.ai.identifier.plantid.utils.ITGTrackingHelper.logEvent
import com.plantcare.ai.identifier.plantid.utils.Routes
import com.plantcare.ai.identifier.plantid.utils.toLocationDomain
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val viewModel: MainViewModel by viewModels()

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var mainAdapter: MainAdapter? = null
    private var dialogRqLocation: RequestPermissionDialog? = null

    override fun onResume() {
        super.onResume()
        onRequestWeatherStatus()
        AppOpenManager.getInstance().enableAppResume()
    }

    override fun getLayoutActivity(): Int = R.layout.activity_main

    override fun initViews() {
        super.initViews()
        if (prefs[AppConstants.KEY_CONFIRM_CONSENT, false] == false && prefs[AppConstants.KEY_IS_USER_GLOBAL, false] == false) {
            delayShowConsentDialog()
        }
        AdsManager.loadInterHome(this)
        cacheFirstInstallApp()
        initLocationRequest()
        initFusedLocationClient()
        initVpgContent()
        initTabHome()
        initDialog()
    }

    override fun onClickViews() {
        mBinding.btnTabHome.click { stateBottomTab(0) }

        mBinding.btnTabMyPlant.click { stateBottomTab(1) }

        mBinding.imgAdd.click {
            startActivity(Intent(this, PlantIdentifierActivity::class.java))
        }

        mBinding.icSettingHome.click { goToSettingScreen() }

        mBinding.tvTapToEnable.click {
            dialogRqLocation?.show()
        }

        mBinding.icVip.click {
            startActivity(Intent(this, IapActivity::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        fusedLocationClient = null
        locationRequest = null
        mainAdapter = null
    }

    @SuppressLint("SetTextI18n", "LogNotTimber")
    override fun observerData() {
        viewModel.isLoading.observe(this) { loading ->

        }

        viewModel.responseLocationDto.observe(this) { locationRes ->
            if (locationRes == null) {
                if (isLocationPermissionGranted(this@MainActivity)) {
                    mBinding.tvLocationUnavailable.invisibleView()
                    mBinding.tvCityName.visibleView()
                    mBinding.tvCityName.text =
                        prefs.getString(AppConstants.LOCATION_CITY_NAME, null)

                    mBinding.tvTapToEnable.invisibleView()
                    mBinding.tvWeatherInformation.visibleView()
                    mBinding.tvWeatherInformation.text =
                        prefs.getString(AppConstants.LOCATION_WEATHER_INFO, null)
                } else {
                    mBinding.tvCityName.invisibleView()
                    mBinding.tvWeatherInformation.invisibleView()
                    mBinding.tvLocationUnavailable.visibleView()
                    mBinding.tvTapToEnable.visibleView()
                }
            } else {
                val locationDomain = locationRes.toLocationDomain()

                mBinding.tvLocationUnavailable.invisibleView()
                mBinding.tvCityName.visibleView()
                mBinding.tvCityName.text = locationDomain.cityName
                prefs[AppConstants.LOCATION_CITY_NAME] = locationDomain.cityName
                viewModel.fetchDataWeather(
                    locationDomain.countriesCode, locationDomain.lat, locationDomain.long
                )
            }
        }

        viewModel.responseWeatherDto.observe(this) { weather ->
            if (weather != null) {
                val tempCDefault = 79
                val weatherStatusRes =
                    viewModel.weatherStates.firstOrNull { it.key.toDouble() == weather.data.current.conditionCode }
                if (weatherStatusRes != null) {
                    val status = getString(weatherStatusRes.value)
                    mBinding.tvTapToEnable.invisibleView()
                    mBinding.tvWeatherInformation.visibleView()
                    prefs[AppConstants.LOCATION_WEATHER_INFO] =
                        "$status ${weather.data.current.tempC}℃/ ${weather.data.forecast[0].day?.maxtempC ?: tempCDefault}℃"
                    mBinding.tvWeatherInformation.text =
                        "$status ${weather.data.current.tempC}℃/ ${weather.data.forecast[0].day?.maxtempC ?: tempCDefault}℃"
                } else {
                    mBinding.tvTapToEnable.invisibleView()
                    mBinding.tvWeatherInformation.visibleView()
                    mBinding.tvWeatherInformation.text = getString(R.string.unknown)
                }
            } else {
                mBinding.tvTapToEnable.invisibleView()
                mBinding.tvWeatherInformation.visibleView()
                mBinding.tvWeatherInformation.text = getString(R.string.unknown)
            }
        }
    }

    private fun delayShowConsentDialog() {
        if (!RemoteConfigUtils.getOnShowDialogConsent()) {
            return
        }
        /**
         * Có đang check thiếu điều kiện không. A thấy nếu user click consent ở màn splash rồi thì vẫn vào đây thì phải
         * Và code hiện lại đang load fail -> 5s sau lại load lại đúng ko nhỉ
         */
        Handler().postDelayed({
            logEvent(ITGTrackingHelper.LOAD_CONSENT_2, null)
            ITGAdConsent.loadAndShowConsent(true, object : IAdConsentCallBack {
                override fun getCurrentActivity(): Activity = this@MainActivity

                override fun isDebug(): Boolean = BuildConfig.DEBUG

                override fun isUnderAgeAd(): Boolean = false

                @SuppressLint("LogNotTimber")
                override fun onNotUsingAdConsent() {
                    Log.v("Ynsuper", "onNotUsingAdConsent: ")
                    logEvent(ITGTrackingHelper.NOT_USING_DISPLAY_CONSENT_2, null)
                    /**
                     * Lưu biến khi user ko nằm trong khu vực EU
                     */


                    prefs[AppConstants.KEY_IS_USER_GLOBAL] = true
                }

                @SuppressLint("LogNotTimber")
                override fun onConsentSuccess(canPersonalized: Boolean) {
                    Log.v("Ynsuper", "onConsentSuccess: $canPersonalized")
                    if (canPersonalized) {
                        /**
                         * Cần lưu biến khi user ấn agree vào SharedPreferenUtils
                         */
                        /**
                         * Cần lưu biến khi user ấn agree vào SharedPreferenUtils
                         */
                        /**
                         * Cần lưu biến khi user ấn agree vào SharedPreferenUtils
                         */
                        /**
                         * Cần lưu biến khi user ấn agree vào SharedPreferenUtils
                         */
                        logEvent(ITGTrackingHelper.AGREE_CONSENT_2, null)
                        prefs[AppConstants.KEY_CONFIRM_CONSENT] = true
                        Routes.startSplashActivity(this@MainActivity)
                    } else {
                        logEvent(ITGTrackingHelper.REFUSE_CONSENT_2, null)
                        ITGAdConsent.resetConsentDialog()
                    }
                }

                @SuppressLint("LogNotTimber")
                override fun onConsentError(formError: FormError) {
                    logEvent(ITGTrackingHelper.CONSENT_ERROR_2, null)
                    Log.v("Ynsuper", "onConsentError: " + formError.message)
                }

                @SuppressLint("LogNotTimber")
                override fun onConsentStatus(consentStatus: Int) {
                    Log.v("Ynsuper", "onConsentStatus: $consentStatus")/*if (consentStatus == 2) {
                                ITGAdConsent.INSTANCE.showDialogConsent(this);
                                Log.v(TAG, "onConsentStatus: " + consentStatus);
                            }*/
                }

                @SuppressLint("LogNotTimber")
                override fun onRequestShowDialog() {
                    Log.v("Ynsuper", "onRequestShowDialog: ")
                    logEvent(ITGTrackingHelper.DISPLAY_CONSENT_2, null)
                }

                override fun testDeviceID(): String = "ED3576D8FCF2F8C52AD8E98B4CFA4005"
            })
        }, 5000L)
    }

    private fun cacheFirstInstallApp() {
        if (prefs[KEY_FIRST_INSTALL_APP, true] == true) {
            prefs[KEY_FIRST_INSTALL_APP] = false
        }
    }

    private fun initLocationRequest() {
        @Suppress("DEPRECATION") locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
            interval = 1000
        }
    }

    private fun initFusedLocationClient() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun initDialog() {
        dialogRqLocation = RequestPermissionDialog(context = this,
            titlePermission = getString(R.string.location_permission_needed),
            desPermission = getString(R.string.des_location_permission),
            onGoToSetting = {
                onGoToSettingApp()
            })
    }

    private fun initVpgContent() = mBinding.vpgHome.apply {
        mainAdapter = MainAdapter(this@MainActivity)

        adapter = mainAdapter
        isUserInputEnabled = false
    }

    @SuppressLint("MissingPermission", "LogNotTimber")
    private fun onRequestWeatherStatus() {
        if (isLocationPermissionGranted(this)) {
            fusedLocationClient?.lastLocation?.addOnSuccessListener { location ->
                location?.let {
                    viewModel.fetchDataLocation(location.latitude, location.longitude)
                } ?: eventWhenLocationPermissionDenied()
            } ?: eventWhenLocationPermissionDenied()
        } else {
            if (!isNetwork()) {
                mBinding.tvTapToEnable.invisibleView()
                mBinding.tvWeatherInformation.visibleView()
                mBinding.tvWeatherInformation.text =
                    prefs.getString(AppConstants.LOCATION_WEATHER_INFO, null)

                mBinding.tvLocationUnavailable.invisibleView()
                mBinding.tvCityName.visibleView()
                mBinding.tvCityName.text = prefs.getString(AppConstants.LOCATION_CITY_NAME, null)
            }
            eventWhenLocationPermissionDenied()
        }
    }

    private fun stateBottomTab(pos: Int) {
        val tvColorUnActive = "#AAAABB"
        val tvColorActive = "#7DC448"

        // clear all active
        mBinding.icTabHome.isActivated = false
        mBinding.icTabMyPlant.isActivated = false
        mBinding.tvTabHome.setTextColor(Color.parseColor(tvColorUnActive))
        mBinding.tvTabMyPlant.setTextColor(Color.parseColor(tvColorUnActive))

        // active ui again
        when (pos) {
            0 -> { // active tab home
                mBinding.icTabHome.isActivated = true
                mBinding.tvTabHome.setTextColor(Color.parseColor(tvColorActive))
            }

            else -> { // active tab my plant
                mBinding.icTabMyPlant.isActivated = true
                mBinding.tvTabMyPlant.setTextColor(Color.parseColor(tvColorActive))
            }
        }

        // change page current
        mBinding.vpgHome.currentItem = pos
    }

    private fun initTabHome() = stateBottomTab(0)

    private fun goToSettingScreen() = startActivity(Intent(this, SettingActivity::class.java))

    private fun eventWhenLocationPermissionDenied() {
        showUiWhenLocationPermissionDenied()
        viewModel.postResponseLocationToNull()
    }

    private fun showUiWhenLocationPermissionDenied() = mBinding.apply {
        tvLocationUnavailable.visibleView()
        tvTapToEnable.visibleView()

        tvCityName.invisibleView()
        tvWeatherInformation.invisibleView()
    }

    private fun onGoToSettingApp() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
        AppOpenManager.getInstance().disableAppResume()
    }
}
