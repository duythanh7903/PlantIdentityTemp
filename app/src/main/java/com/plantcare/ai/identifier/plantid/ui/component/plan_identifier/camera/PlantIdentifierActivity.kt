package com.plantcare.ai.identifier.plantid.ui.component.plan_identifier.camera

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PointF
import android.net.Uri
import android.provider.Settings
import androidx.activity.viewModels
import com.ads.control.admob.AppOpenManager
import com.otaliastudios.cameraview.CameraException
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraOptions
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.controls.Flash
import com.otaliastudios.cameraview.controls.Grid
import com.otaliastudios.cameraview.size.SizeSelectors
import com.plantcare.ai.identifier.plantid.BuildConfig
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.ads.AdsManager
import com.plantcare.ai.identifier.plantid.ads.RemoteConfigUtils
import com.plantcare.ai.identifier.plantid.app.AppConstants.KEY_COUNT_LOCATION_PERMISSION_DENIED
import com.plantcare.ai.identifier.plantid.app.AppConstants.KEY_COUNT_STORAGE_PERMISSION_DENIED
import com.plantcare.ai.identifier.plantid.databinding.ActivityPlantIdentifierBinding
import com.plantcare.ai.identifier.plantid.ui.bases.BaseActivity
import com.plantcare.ai.identifier.plantid.ui.bases.ext.click
import com.plantcare.ai.identifier.plantid.ui.bases.ext.goneView
import com.plantcare.ai.identifier.plantid.ui.bases.ext.showToastById
import com.plantcare.ai.identifier.plantid.ui.bases.ext.visibleView
import com.plantcare.ai.identifier.plantid.ui.component.bottom_sheet.AlbumsBottomSheetFragment
import com.plantcare.ai.identifier.plantid.ui.component.bottom_sheet.AlbumsBottomSheetFragment.Companion.TAG
import com.plantcare.ai.identifier.plantid.ui.component.dialog.LoadingDialog
import com.plantcare.ai.identifier.plantid.ui.component.dialog.RequestPermissionDialog
import com.plantcare.ai.identifier.plantid.ui.component.permision.PermissionUtils.STORAGE_PERMISSION_REQUEST_CODE
import com.plantcare.ai.identifier.plantid.ui.component.permision.PermissionUtils.getPermissionStorage
import com.plantcare.ai.identifier.plantid.ui.component.permision.PermissionUtils.isCameraPermissionGranted
import com.plantcare.ai.identifier.plantid.ui.component.permision.PermissionUtils.isStoragePermissionGranted
import com.plantcare.ai.identifier.plantid.ui.component.permision.PermissionUtils.mustGoToSettingAcceptPermissions
import com.plantcare.ai.identifier.plantid.ui.component.permision.PermissionUtils.requestStoragePermission
import com.plantcare.ai.identifier.plantid.ui.component.plan_identifier.preview.PreviewIdentifyActivity
import com.plantcare.ai.identifier.plantid.ui.component.plan_identifier.preview.PreviewModel
import com.plantcare.ai.identifier.plantid.utils.EasyPreferences.set
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PlantIdentifierActivity : BaseActivity<ActivityPlantIdentifierBinding>() {

    companion object {
        var bitmapImageTaken: Bitmap? = null
    }

    private val viewModel: PreviewModel by viewModels()

    private var loadingDialog: LoadingDialog? = null

    private var isDisableAdsResume = false
    private var countStoragePermission = 0

    override fun getLayoutActivity(): Int = R.layout.activity_plant_identifier

    override fun initViews() {
        initLoadingDialog()
        initCamera()
        countStoragePermission = prefs.getInt(KEY_COUNT_STORAGE_PERMISSION_DENIED, 0)
        AdsManager.loadBanner(this, BuildConfig.banner_all, mBinding.frAds, RemoteConfigUtils.getOnBannerAll())
    }

    override fun observerData() {
        viewModel.isLoading.observe(this) { loading ->
            if (loading) loadingDialog?.show() else loadingDialog?.cancel()
        }

        viewModel.isLoadingImages.observe(this) { loading ->
            if (loading) loadingDialog?.show() else loadingDialog?.cancel()
        }
    }

    override fun onResume() {
        super.onResume()

        if (!isCameraPermissionGranted(this)) onBackPressed()
        if (isDisableAdsResume) enableAdsResume()
    }

    override fun onBackPressed() {
        releaseCamera()
        finish()
    }

    @SuppressLint("LogNotTimber")
    override fun onClickViews() {
        mBinding.imgTakePhoto.click {
            takePhoto()
        }

        mBinding.icBack.click { onBackPressed() }

        mBinding.icFlash.click { onEventFlashCamera() }

        mBinding.btnSnapTips.click { onEventSnapTips() }

        mBinding.btnAlbum.click { onEventClickAlbums() }

        mBinding.btnUnderStand.click {
            mBinding.layoutSnapTips.goneView()
            mBinding.frAds.visibleView()
        }
    }

    private fun takePhoto() {
        viewModel.showLoading()
        mBinding.camera.takePicture()
    }

    private fun initCamera() {
        mBinding.camera.apply {
            setLifecycleOwner(this@PlantIdentifierActivity)
            setPictureSize(SizeSelectors.maxWidth(1920))
            playSounds = false
            isSoundEffectsEnabled = false
            flash = Flash.OFF
            grid = Grid.OFF
            addCameraListener(object : CameraListener() {
                override fun onCameraOpened(options: CameraOptions) = Unit

                override fun onCameraError(exception: CameraException) {
                    super.onCameraError(exception)

                    showToastById(R.string.some_thing_went_wrong)
                    finish()
                }

                @SuppressLint("LogNotTimber")
                override fun onPictureTaken(result: PictureResult) {
                    super.onPictureTaken(result)
                    viewModel.cacheResultBitmap(result, goToPreviewImageScreen = {
                        goToPreviewImageScreen()
                    })
                }

                override fun onExposureCorrectionChanged(
                    newValue: Float, bounds: FloatArray, fingers: Array<PointF>?
                ) {
                    super.onExposureCorrectionChanged(newValue, bounds, fingers)
                }

                override fun onZoomChanged(
                    newValue: Float, bounds: FloatArray, fingers: Array<PointF>?
                ) {
                    super.onZoomChanged(newValue, bounds, fingers)
                }

            })
        }
    }

    private fun initLoadingDialog() {
        loadingDialog = LoadingDialog(this)
    }

    private fun releaseCamera() = mBinding.camera.destroy()

    private fun onEventFlashCamera() {
        when (mBinding.camera.flash) {
            Flash.OFF -> {
                mBinding.icFlash.isActivated = true
                mBinding.camera.flash = Flash.TORCH
            }

            else -> {
                mBinding.icFlash.isActivated = false
                mBinding.camera.flash = Flash.OFF
            }
        }
    }

    private fun goToPreviewImageScreen() =
        startActivity(Intent(this, PreviewIdentifyActivity::class.java))

    private fun onEventSnapTips() {
        mBinding.layoutSnapTips.visibleView()
        mBinding.frAds.goneView()
    }

    private fun onEventClickAlbums() {
        val isStoragePermissionGranted = isStoragePermissionGranted(this)
        if (isStoragePermissionGranted) {
            AlbumsBottomSheetFragment(
                onCancel = { },
                onItemImageSelected = { imageDomain, _ ->
                    val bitmapResult = getBitmapFromFile(imageDomain.path)
                    bitmapResult?.let {
                        bitmapImageTaken = it
                        goToPreviewImageScreen()
                    } ?: showToastById(R.string.some_thing_went_wrong)
                }
            ).show(supportFragmentManager, TAG)
        } else {
            if (countStoragePermission < 2) {
                requestStoragePermission(this)
                countStoragePermission++
                prefs[KEY_COUNT_LOCATION_PERMISSION_DENIED] = countStoragePermission
            } else RequestPermissionDialog(
                context = this,
                titlePermission = getString(R.string.read_external_storage_needed),
                desPermission = getString(R.string.des_storage_permission),
                onGoToSetting = {
                    onGoToSettingApp()
                }
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            STORAGE_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewModel.getAllFolders()
                } else {
                    val permissionStorage = getPermissionStorage()
                    if (!mustGoToSettingAcceptPermissions(
                            activity = this, arrayPermissions = permissionStorage
                        )
                    ) RequestPermissionDialog(context = this@PlantIdentifierActivity,
                        titlePermission = getString(R.string.read_external_storage_needed),
                        desPermission = getString(R.string.des_storage_permission),
                        onGoToSetting = {
                            onGoToSettingApp()
                        }).show()
                }
            }
        }
    }

    private fun onGoToSettingApp() {
        disableAdsResume()
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", this.packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun disableAdsResume() {
        AppOpenManager.getInstance().disableAppResume()
        isDisableAdsResume = true
    }

    private fun enableAdsResume() {
        AppOpenManager.getInstance().enableAppResume()
        isDisableAdsResume = false
    }

    private fun getBitmapFromFile(filePath: String): Bitmap? {
        val bitmap = BitmapFactory.decodeFile(filePath)
        return bitmap
    }
}