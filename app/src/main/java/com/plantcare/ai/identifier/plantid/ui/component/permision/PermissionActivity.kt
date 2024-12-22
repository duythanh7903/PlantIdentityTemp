package com.plantcare.ai.identifier.plantid.ui.component.permision

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import com.ads.control.admob.AppOpenManager
import com.ads.control.ads.ITGAd
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.ads.AdsManager
import com.plantcare.ai.identifier.plantid.ads.AdsManager.nativeAdPermission
import com.plantcare.ai.identifier.plantid.ads.PreLoadNativeListener
import com.plantcare.ai.identifier.plantid.app.AppConstants.KEY_COUNT_CAMERA_PERMISSION_DENIED
import com.plantcare.ai.identifier.plantid.app.AppConstants.KEY_COUNT_LOCATION_PERMISSION_DENIED
import com.plantcare.ai.identifier.plantid.databinding.ActivityPermissionBinding
import com.plantcare.ai.identifier.plantid.ui.bases.BaseActivity
import com.plantcare.ai.identifier.plantid.ui.bases.ext.click
import com.plantcare.ai.identifier.plantid.ui.bases.ext.goneView
import com.plantcare.ai.identifier.plantid.ui.bases.ext.invisibleView
import com.plantcare.ai.identifier.plantid.ui.bases.ext.visibleView
import com.plantcare.ai.identifier.plantid.ui.component.dialog.RequestPermissionDialog
import com.plantcare.ai.identifier.plantid.ui.component.permision.PermissionUtils.CAMERA_PERMISSION_REQUEST_CODE
import com.plantcare.ai.identifier.plantid.ui.component.permision.PermissionUtils.LOCATION_PERMISSION_REQUEST_CODE
import com.plantcare.ai.identifier.plantid.ui.component.permision.PermissionUtils.isCameraPermissionGranted
import com.plantcare.ai.identifier.plantid.ui.component.permision.PermissionUtils.isLocationPermissionGranted
import com.plantcare.ai.identifier.plantid.ui.component.permision.PermissionUtils.mustGoToSettingAcceptPermissions
import com.plantcare.ai.identifier.plantid.ui.component.permision.PermissionUtils.requestCameraPermission
import com.plantcare.ai.identifier.plantid.ui.component.permision.PermissionUtils.requestLocationPermission
import com.plantcare.ai.identifier.plantid.utils.EasyPreferences.set
import com.plantcare.ai.identifier.plantid.utils.Routes.startMainActivity


class PermissionActivity : BaseActivity<ActivityPermissionBinding>() {

    private var populateNativeAdView = false
    private var isLocationPermissionGranted = false
    private var isCameraPermissionGranted = false

    private var dialogRqCameraPermission: RequestPermissionDialog? = null
    private var dialogRqLocationPermission: RequestPermissionDialog? = null

    private var countCameraPermission = 0
    private var countLocationPermission = 0

    override fun getLayoutActivity(): Int = R.layout.activity_permission

    override fun initViews() {
        initAdmob()
        initDialog()
        countCameraPermission = prefs.getInt(KEY_COUNT_CAMERA_PERMISSION_DENIED, 0)
        countLocationPermission = prefs.getInt(KEY_COUNT_LOCATION_PERMISSION_DENIED, 0)
        if(nativeAdPermission != null){
            initAdmob()
        }
    }

    override fun onResume() {
        super.onResume()

        cachePermissionsToVar()
        setActiveSwitchPermission()
    }

    private fun cachePermissionsToVar() {
        isLocationPermissionGranted = isLocationPermissionGranted(this)
        isCameraPermissionGranted = isCameraPermissionGranted(this)

        mBinding.btnGo.isActivated = isLocationPermissionGranted && isCameraPermissionGranted
        if (isLocationPermissionGranted && isCameraPermissionGranted) {
            mBinding.btnGo.visibleView()
        } else mBinding.btnGo.invisibleView()
    }

    private fun setActiveSwitchPermission() {
        mBinding.apply {
            swLocationPermission.isActivated = isLocationPermissionGranted
            swCameraPermission.isActivated = isCameraPermissionGranted
        }
    }

    private fun initAdmob() {
        AdsManager.setPreLoadNativeCallback(object : PreLoadNativeListener {
            override fun onLoadNativeSuccess() = showNativePermission()

            override fun onLoadNativeFail() {
                if (nativeAdPermission != null) showAdsView()
                else hideAdsView()
            }
        })
        showNativePermission()
    }

    private fun showAdsView() = mBinding.frAds.visibleView()

    private fun hideAdsView() = mBinding.frAds.goneView()

    private fun showNativePermission() {
        if (nativeAdPermission != null) {
            populateNativeAdView = true

            showAdsView()
            ITGAd.getInstance().populateNativeAdView(
                this, nativeAdPermission, mBinding.frAds, mBinding.layoutShimmer.shimmerNativeLarge
            )
        } else hideAdsView()
    }

    private fun initDialog() {
        dialogRqCameraPermission = RequestPermissionDialog(context = this,
            titlePermission = getString(R.string.camera_permission_needed),
            desPermission = getString(R.string.des_camera_permission),
            onGoToSetting = {
                onGoToSettingApp()
            })

        dialogRqLocationPermission = RequestPermissionDialog(context = this,
            titlePermission = getString(R.string.location_permission_needed),
            desPermission = getString(R.string.des_location_permission),
            onGoToSetting = {
                onGoToSettingApp()
            })
    }

    override fun onClickViews() {
        mBinding.btnRqCameraPermission.click {
            if (!isCameraPermissionGranted) {
                if (countCameraPermission < 2) {
                    requestCameraPermission(this)
                    countCameraPermission++
                    prefs[KEY_COUNT_CAMERA_PERMISSION_DENIED] = countCameraPermission
                } else dialogRqCameraPermission?.show()
            }
        }

        mBinding.btnRqLocationPermission.click {
            if (!isLocationPermissionGranted) {
                if (countLocationPermission < 2) {
                    requestLocationPermission(this)
                    countLocationPermission++
                    prefs[KEY_COUNT_LOCATION_PERMISSION_DENIED] = countLocationPermission
                } else dialogRqLocationPermission?.show()
            }
        }

        mBinding.btnGo.click {
            val allPermissionAccepted = isCameraPermissionGranted && isLocationPermissionGranted
            if (allPermissionAccepted) {
                startMainActivity(this)
                finish()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                isLocationPermissionGranted =
                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        mBinding.swLocationPermission.isActivated = true

                        if (isCameraPermissionGranted) {
                            mBinding.btnGo.isActivated = true
                            mBinding.btnGo.visibleView()
                        } else mBinding.btnGo.invisibleView()

                        true
                    } else {
                        mBinding.swLocationPermission.isActivated = false
                        if (!mustGoToSettingAcceptPermissions(
                                activity = this,
                                permission = Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        ) {
                            dialogRqLocationPermission?.show()
                        }
                        false
                    }
            }

            CAMERA_PERMISSION_REQUEST_CODE -> {
                isCameraPermissionGranted =
                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        mBinding.swCameraPermission.isActivated = true

                        if (isLocationPermissionGranted) {
                            mBinding.btnGo.isActivated = true
                            mBinding.btnGo.visibleView()
                        } else mBinding.btnGo.invisibleView()

                        true
                    } else {
                        mBinding.swCameraPermission.isActivated = false
                        if (!mustGoToSettingAcceptPermissions(
                                activity = this, permission = Manifest.permission.CAMERA
                            )
                        ) {
                            dialogRqCameraPermission?.show()
                        }
                        false
                    }
            }
        }
    }


    private fun onGoToSettingApp() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }
}