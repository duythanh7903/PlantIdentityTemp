package com.plantcare.ai.identifier.plantid.ui.component.plan_identifier.preview

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.viewModels
import com.plantcare.ai.identifier.plantid.BuildConfig
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.app.AppConstants.KEY_RESULT_SEARCH
import com.plantcare.ai.identifier.plantid.app.AppConstants.KEY_SECRET_KEY
import com.plantcare.ai.identifier.plantid.databinding.ActivityPreviewIdentifyBinding
import com.plantcare.ai.identifier.plantid.ui.bases.BaseActivity
import com.plantcare.ai.identifier.plantid.ui.bases.ext.click
import com.plantcare.ai.identifier.plantid.ui.bases.ext.goneView
import com.plantcare.ai.identifier.plantid.ui.bases.ext.invisibleView
import com.plantcare.ai.identifier.plantid.ui.bases.ext.isNetwork
import com.plantcare.ai.identifier.plantid.ui.bases.ext.showToastById
import com.plantcare.ai.identifier.plantid.ui.bases.ext.visibleView
import com.plantcare.ai.identifier.plantid.ui.component.dialog.WrongDialog
import com.plantcare.ai.identifier.plantid.ui.component.main.MainActivity
import com.plantcare.ai.identifier.plantid.ui.component.plan_identifier.camera.PlantIdentifierActivity.Companion.bitmapImageTaken
import com.plantcare.ai.identifier.plantid.ui.component.plan_identifier.result.SearchPlantResultActivity
import com.plantcare.ai.identifier.plantid.utils.toHistoryEntity
import com.plantcare.ai.identifier.plantid.utils.toPlantDomain
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

@AndroidEntryPoint
class PreviewIdentifyActivity : BaseActivity<ActivityPreviewIdentifyBinding>() {

    private val viewModel: PreviewModel by viewModels()

    private var wrongDialog: WrongDialog? = null
    private var jobFetchInformationPlant: Job? = null
    private var scanAnimation: ObjectAnimator? = null

    override fun getLayoutActivity(): Int = R.layout.activity_preview_identify

    override fun initViews() {
        getDataBitmapTaken()
        initDialogNoInternet()
        initAnimation()
    }

    override fun onClickViews() {
        mBinding.btnCancel.click {
            jobFetchInformationPlant?.cancel()
            jobFetchInformationPlant = null
            viewModel.hideLoading()
            finish()
        }

        mBinding.btnPlantIdentifier.click {
            if (isNetwork()) onEventIdentify()
            else wrongDialog?.show()
        }

        mBinding.icBack.click {
            mBinding.btnCancel.performClick()
        }

        mBinding.btnRetake.click {
            finish()
        }

        mBinding.btnBackToHome.click {
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        }
    }

    @SuppressLint("LogNotTimber")
    override fun observerData() {
        viewModel.isLoading.observe(this) { loading ->
            if (loading) {
                mBinding.layoutLoading.visibleView()
                mBinding.btnPlantIdentifier.invisibleView()
                mBinding.scanLine.visibleView()
                mBinding.icBack.goneView()
                scanAnimation?.start()
            } else {
                mBinding.layoutLoading.invisibleView()
                mBinding.btnPlantIdentifier.visibleView()
                mBinding.scanLine.invisibleView()
                mBinding.icBack.visibleView()
                scanAnimation?.cancel()
            }
        }

        viewModel.responsePlantDto.observe(this) { plantDto ->
            val isError = plantDto == null
            if (isError) showWrongDialog()
            else {
                val isFoundPlant = plantDto!!.data != null
                if (isFoundPlant) {
                    viewModel.saveHistory(plantDto.toHistoryEntity())
                    val bundle = Bundle().apply {
                        putSerializable(
                            KEY_RESULT_SEARCH, plantDto.toPlantDomain()
                        )
                    }
                    val intent = Intent(this, SearchPlantResultActivity::class.java).apply {
                        putExtras(bundle)
                    }
                    startActivity(intent)
                    finish()
                } else showLayoutNotFound()
            }
        }
    }

    override fun onPause() {
        super.onPause()

        if (jobFetchInformationPlant != null) {
            jobFetchInformationPlant?.cancel()
            viewModel.hideLoading()
        }
    }

    private fun getDataBitmapTaken() {
        if (bitmapImageTaken == null) {
            showToastById(R.string.some_thing_went_wrong)
            finish()
        } else mBinding.imgResult.setImageBitmap(bitmapImageTaken)
    }

    private fun initDialogNoInternet() {
        wrongDialog = WrongDialog(context = this, tryAgain = {
            finish()
        }, backToHome = {
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        })
    }

    private fun initAnimation() {
        // Tạo một ObjectAnimator để di chuyển scanLine từ trên xuống dưới màn hình
        scanAnimation = ObjectAnimator.ofFloat(mBinding.scanLine, "translationX", 0f, 750f).apply {
            duration = 1200 // Thời gian kéo dài cho một lần quét
            repeatCount = ObjectAnimator.INFINITE // Lặp lại vô hạn
            repeatMode = ObjectAnimator.RESTART // Quay lại vị trí ban đầu
        }
    }

    private fun onEventIdentify() {
        if (bitmapImageTaken != null) {
            if (isNetwork()) uploadBitmapAsFile(bitmapImageTaken!!)
            else showWrongDialog()
        }
    }

    private fun bitmapToRequestBody(bitmap: Bitmap): MultipartBody.Part {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream) // Nén ảnh
        val byteArray = stream.toByteArray() // Chuyển thành ByteArray

        // Tạo RequestBody từ ByteArray
        val requestBody = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("image", "uploaded_image.jpg", requestBody)
    }

    private fun uploadBitmapAsFile(bitmap: Bitmap) {
        val filePath = bitmapToRequestBody(bitmap)

        jobFetchInformationPlant = viewModel.fetchInformationPlant(
            packageName,
            BuildConfig.VERSION_CODE.toString(),
            BuildConfig.INTERNAL_KEY,
            prefs.getString(KEY_SECRET_KEY, BuildConfig.SECRET_KEY_DEFAULT) ?: BuildConfig.SECRET_KEY_DEFAULT,
            filePath
        )
    }

    private fun showWrongDialog() = wrongDialog?.show()

    private fun showLayoutNotFound() = mBinding.layoutResultNotFound.visibleView()
}