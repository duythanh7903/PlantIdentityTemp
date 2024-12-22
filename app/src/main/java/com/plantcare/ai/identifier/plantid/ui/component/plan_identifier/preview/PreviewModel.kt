package com.plantcare.ai.identifier.plantid.ui.component.plan_identifier.preview

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.otaliastudios.cameraview.PictureResult
import com.plantcare.ai.identifier.plantid.app.AppConstants.MESSAGE_RES_PLANT_ERROR
import com.plantcare.ai.identifier.plantid.data.database.entities.HistoryEntity
import com.plantcare.ai.identifier.plantid.data.database.repository.HistoryRepository
import com.plantcare.ai.identifier.plantid.data.network.dto.ResponsePlantDto
import com.plantcare.ai.identifier.plantid.data.network.repository.PlantRepository
import com.plantcare.ai.identifier.plantid.domains.FolderDomain
import com.plantcare.ai.identifier.plantid.domains.ImageDomain
import com.plantcare.ai.identifier.plantid.ui.bases.BaseViewModel
import com.plantcare.ai.identifier.plantid.ui.component.plan_identifier.camera.PlantIdentifierActivity.Companion.bitmapImageTaken
import com.plantcare.ai.identifier.plantid.utils.network_adapter_factory.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PreviewModel @Inject constructor(
    private val plantRepository: PlantRepository, private val historyRepository: HistoryRepository,
    @ApplicationContext
    private val context: Context
): BaseViewModel() {

    private var listFolder: MutableList<FolderDomain> = mutableListOf()
    private var listImage: MutableList<ImageDomain> = mutableListOf()
    private var listAllImage: MutableList<ImageDomain> = mutableListOf()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isLoadingImages = MutableLiveData<Boolean>()
    val isLoadingImages: LiveData<Boolean> = _isLoadingImages

    private val _responsePlantDto = MutableLiveData<ResponsePlantDto?>()
    val responsePlantDto: LiveData<ResponsePlantDto?> = _responsePlantDto

    private val _folders = MutableLiveData<MutableList<FolderDomain>>(mutableListOf())

    private val _images = MutableLiveData<MutableList<ImageDomain>>(mutableListOf())
    val images: LiveData<MutableList<ImageDomain>> = _images

    @SuppressLint("LogNotTimber")
    fun fetchInformationPlant(
        packageName : String,
        versionCode : String,
        internalKey: String,
        secretKey: String,
        image: MultipartBody.Part
    ) = bgScope.launch {
        _isLoading.postValue(true)

        val response = plantRepository.fetchInformationPlant(
            packageName,versionCode,internalKey, secretKey, image
        )
        when (response) {
            is ResultWrapper.Success -> {
                val data = response.value
                _responsePlantDto.postValue(data)
                Log.d("duylt", "Data: $data")
            }

            is ResultWrapper.GenericError -> {
                val responseNotFound = ResponsePlantDto(
                    statusCode = 404,
                    code = MESSAGE_RES_PLANT_ERROR,
                    message = MESSAGE_RES_PLANT_ERROR,
                    path = "",
                    timestamp = System.currentTimeMillis().toString(),
                    data = null
                )
                _responsePlantDto.postValue(responseNotFound)
            }

            else -> {
                _responsePlantDto.postValue(null)
                Log.d("duylt", "Something went wrong: $response")
            }
        }

        _isLoading.postValue(false)
    }

    fun cacheResultBitmap(
        result: PictureResult, goToPreviewImageScreen: () -> Unit
    ) = uiScope.launch {
        result.toBitmap { bitmap ->
            // cache bitmap
            bitmapImageTaken = bitmap
            goToPreviewImageScreen.invoke()
            _isLoading.postValue(false)
        }
    }

    fun showLoading() = _isLoading.postValue(true)

    fun hideLoading() = _isLoading.postValue(false)

    fun saveHistory(history: HistoryEntity) =
        bgScope.launch {
            historyRepository.saveHistory(history)
        }

    @SuppressLint("Recycle")
    fun getAllFolders() = bgScope.launch {
        _isLoadingImages.postValue(true)

        // start progress
        listFolder.clear()
        listImage.clear()
        listAllImage.clear()

        // handle action progress
        var position = 0
        val cursor: Cursor?
        var columnIndexData = 0
        var columnIndexFolderName = 0
        var absolutePathOfImage: String
        var folder = false
        val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection =
            arrayOf(
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            )
        val orderBy = MediaStore.Images.Media.DATE_ADDED
        cursor = context.contentResolver.query(
            uri, projection, null, null,
            "$orderBy DESC"
        )
        if (cursor != null) {
            columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        }
        if (cursor != null) {
            columnIndexFolderName =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        }
        if (cursor != null) {
            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(columnIndexData)
                for (i in 0 until listFolder.size) {
                    if (listFolder[i].folderName == cursor.getString(columnIndexFolderName)
                    ) {
                        folder = true
                        position = i
                        break
                    } else {
                        folder = false
                    }
                }
                if (folder) {
                    val alPath: MutableList<ImageDomain> = mutableListOf()
                    alPath.addAll(listFolder[position].images)
                    val file = File(absolutePathOfImage)

                    cursor.getString(columnIndexFolderName)?.let {
                        alPath.add(
                            ImageDomain(
                                path = file.path,
                                it,
                                1
                            )
                        )
                    }

                    listFolder[position].images = alPath
                } else {
                    val alPath: MutableList<ImageDomain> = mutableListOf()
                    val file = File(absolutePathOfImage)

                    cursor.getString(columnIndexFolderName)?.let {
                        alPath.add(
                            ImageDomain(
                                path = file.path,
                                it,
                                1
                            )
                        )
                    }

                    cursor.getString(columnIndexFolderName)?.let {
                        if (cursor.getType(columnIndexFolderName) == Cursor.FIELD_TYPE_STRING) {
                            val objModel =
                                FolderDomain(
                                    cursor.getString(columnIndexFolderName),
                                    alPath
                                )
                            listFolder.add(objModel)
                        }
                    }

                }
            }

            listFolder.forEach { item ->
                if (item.images.size > 0) {
                    listImage.addAll(item.images)
                    listAllImage.addAll(item.images)
                }
            }

            listFolder.add(
                0,
                FolderDomain(
                    "All Images",
                    listAllImage
                )
            )
        }

        // end progress
        _images.postValue(listImage)
        _folders.postValue(listFolder)

        _isLoadingImages.postValue(false)
    }
}