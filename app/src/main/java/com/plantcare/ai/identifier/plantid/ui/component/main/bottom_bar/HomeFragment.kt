package com.plantcare.ai.identifier.plantid.ui.component.main.bottom_bar

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.ads.control.admob.AppOpenManager
import com.ads.control.ads.ITGAd
import com.ads.control.ads.wrapper.ApNativeAd
import com.ads.control.funtion.AdCallback
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.plantcare.ai.identifier.plantid.BuildConfig
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.ads.AdsManager
import com.plantcare.ai.identifier.plantid.ads.RemoteConfigUtils
import com.plantcare.ai.identifier.plantid.app.AppConstants
import com.plantcare.ai.identifier.plantid.app.AppConstants.KEY_COUNT_CAMERA_PERMISSION_DENIED
import com.plantcare.ai.identifier.plantid.app.AppConstants.KEY_COUNT_NOTI_PERMISSION_DENIED
import com.plantcare.ai.identifier.plantid.app.AppConstants.KEY_SET_SHOW_DIALOG_RATE
import com.plantcare.ai.identifier.plantid.app.GlobalApp.Companion.isShowDialogRateInThisSession
import com.plantcare.ai.identifier.plantid.databinding.FragmentHomeBinding
import com.plantcare.ai.identifier.plantid.service.LoadAlarmsReceiver
import com.plantcare.ai.identifier.plantid.service.LoadAlarmsService
import com.plantcare.ai.identifier.plantid.ui.bases.BaseFragment
import com.plantcare.ai.identifier.plantid.ui.bases.ext.click
import com.plantcare.ai.identifier.plantid.ui.bases.ext.goneView
import com.plantcare.ai.identifier.plantid.ui.bases.ext.isNetwork
import com.plantcare.ai.identifier.plantid.ui.bases.ext.showRateDialog
import com.plantcare.ai.identifier.plantid.ui.component.diagnose.DiagnoseActivity
import com.plantcare.ai.identifier.plantid.ui.component.dialog.RequestPermissionDialog
import com.plantcare.ai.identifier.plantid.ui.component.permision.PermissionUtils.isCameraPermissionGranted
import com.plantcare.ai.identifier.plantid.ui.component.permision.PermissionUtils.isLocationPermissionGranted
import com.plantcare.ai.identifier.plantid.ui.component.permision.PermissionUtils.isNotificationPermissionGranted
import com.plantcare.ai.identifier.plantid.ui.component.permision.PermissionUtils.requestCameraPermission
import com.plantcare.ai.identifier.plantid.ui.component.plan_identifier.camera.PlantIdentifierActivity
import com.plantcare.ai.identifier.plantid.ui.component.remind.ReminderActivity
import com.plantcare.ai.identifier.plantid.utils.EasyPreferences
import com.plantcare.ai.identifier.plantid.utils.EasyPreferences.set
import com.tbruyelle.rxpermissions3.RxPermissions


class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private var isCameraPermissionGranted = false
    private var isLocationPermissionGranted = false
    private var isEnableAdsResume = true
    private var countDeninedNotiPermission = 0
    private var countCameraPermission = 0

    private var dialogRqCameraPermission: RequestPermissionDialog? = null
    private var dialogRqNotification: RequestPermissionDialog? = null
    private var mReceiver: LoadAlarmsReceiver? = null

    private val resultLauncherReminder =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) showRateDialogIfCan()
        }

    override fun onStart() {
        super.onStart()

        val filter = IntentFilter(LoadAlarmsService.ACTION_COMPLETE)
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(mReceiver!!, filter)
        LoadAlarmsService.launchLoadAlarmsService(context)
    }

    @SuppressLint("LogNotTimber")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mReceiver = LoadAlarmsReceiver { _ ->
            Log.d(
                "message_from_duylt",
                "This lambda code doesn't handle any thing but don't delete this code! This is an important message :)))"
            )
        }

        countCameraPermission = EasyPreferences.defaultPrefs(requireContext())
            .getInt(KEY_COUNT_CAMERA_PERMISSION_DENIED, 0)
    }

    override fun onStop() {
        super.onStop()

        mReceiver?.let {
            LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(it)
        }
    }

    override fun getLayoutFragment(): Int = R.layout.fragment_home

    override fun initViews() {
        initDialog()
        countDeninedNotiPermission = EasyPreferences.defaultPrefs(requireContext())
            .getInt(KEY_COUNT_NOTI_PERMISSION_DENIED, 0)
        initAds()
    }

    private fun initAds() {
        activity?.let { act->
            if (RemoteConfigUtils.getOnNativeHome() && isNetwork(act)) {
                ITGAd.getInstance().loadNativeAd(act,
                    BuildConfig.native_home,
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

    }

    override fun onResume() {
        super.onResume()

        cachePermissionsToVar()

        if (!isEnableAdsResume) {
            isEnableAdsResume = true
            AppOpenManager.getInstance().enableAppResume()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onClickViews() {
        mBinding.btnPlantIdentifier.click { onIdentify() }

        mBinding.btnReminder.click { onEventReminder() }

        mBinding.btnDiagnose.click { showAdsFunHome { goToDiagnoseScreen() } }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun onEventReminder() {

        if (isNotificationPermissionGranted(requireContext()) && Settings.canDrawOverlays(requireContext())) {
            showAdsFunHome {
                goToRemindScreen()
            }
        } else {
            if (!isNotificationPermissionGranted(requireContext())) {
                if (countDeninedNotiPermission < 2) {
                    requestNotificationPermission()
                    countDeninedNotiPermission++
                    EasyPreferences.defaultPrefs(requireContext())[KEY_COUNT_NOTI_PERMISSION_DENIED] =
                        countDeninedNotiPermission
                } else dialogRqNotification?.show()

                return
            }

            if (!Settings.canDrawOverlays(requireContext())) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${requireContext().packageName}")
                )
                startActivity(intent)
                isEnableAdsResume = false
                AppOpenManager.getInstance().disableAppResume()
            }
        }
    }

    @SuppressLint("CheckResult")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        RxPermissions(requireActivity()).requestEach(Manifest.permission.POST_NOTIFICATIONS)
            .subscribe { permission ->
                when {
                    permission.granted -> {
                        if (Settings.canDrawOverlays(requireContext())) {
                            goToRemindScreen()
                        }
                    }

                    permission.shouldShowRequestPermissionRationale -> {
                        if (countDeninedNotiPermission >= 2) {
                            dialogRqNotification?.show()
                        }
                    }

                    else -> Unit
                }
            }
    }

    private fun initDialog() {
        dialogRqCameraPermission = RequestPermissionDialog(context = requireContext(),
            titlePermission = getString(R.string.camera_permission_needed),
            desPermission = getString(R.string.des_camera_permission),
            onGoToSetting = {
                onGoToSettingApp()
            })

        dialogRqNotification = RequestPermissionDialog(context = requireContext(),
            titlePermission = getString(R.string.notification_needed),
            desPermission = getString(R.string.des_noti_permission),
            onGoToSetting = {
                onGoToSettingApp()
            })
    }

    private fun cachePermissionsToVar() {
        isLocationPermissionGranted = isLocationPermissionGranted(requireContext())
        isCameraPermissionGranted = isCameraPermissionGranted(requireContext())
    }

    private fun onIdentify() {
        if (!isCameraPermissionGranted) {
            if (countCameraPermission < 2) {
                requestCameraPermission(requireActivity())
                countCameraPermission++
                EasyPreferences.defaultPrefs(requireContext())[KEY_COUNT_CAMERA_PERMISSION_DENIED] =
                    countCameraPermission
            } else dialogRqCameraPermission?.show()
        } else {
            showAdsFunHome {
                goToCameraScreen()
            }
        }
    }

    private fun onGoToSettingApp() {
        isEnableAdsResume = false
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireContext().packageName, null)
        intent.data = uri
        startActivity(intent)
        AppOpenManager.getInstance().disableAppResume()
    }

    private fun goToCameraScreen() =
        startActivity(Intent(requireContext(), PlantIdentifierActivity::class.java))

    private fun goToRemindScreen() =
        resultLauncherReminder.launch(Intent(requireContext(), ReminderActivity::class.java))

    private fun goToDiagnoseScreen() =
        startActivity(Intent(requireContext(), DiagnoseActivity::class.java))

    private fun showRateDialogIfCan() {
        val isRated = EasyPreferences.defaultPrefs(requireContext())
            .getBoolean(KEY_SET_SHOW_DIALOG_RATE, false)
        if (!isRated && !isShowDialogRateInThisSession) {
            isShowDialogRateInThisSession = true
            showRateDialog(
                activity = requireActivity(),
                isFinish = false
            )
        }
    }

    private fun showAdsFunHome(onFunction: () -> Unit) {
        activity?.let { act->
            if (!AppConstants.isShowAds) {
                onFunction()
                AppConstants.isShowAds = true
                return
            }

            if (isNetwork(act) && RemoteConfigUtils.getOnInterHome()) {
                ITGAd.getInstance().forceShowInterstitial(
                    act, AdsManager.mInterHome, object : AdCallback() {
                        override fun onNextAction() {
                            super.onNextAction()
                            onFunction()
                        }
                    }, true
                )
            } else {
                onFunction()
            }
        }
    }
}