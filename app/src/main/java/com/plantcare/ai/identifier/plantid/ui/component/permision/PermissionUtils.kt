package com.plantcare.ai.identifier.plantid.ui.component.permision

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionUtils {

    const val CAMERA_PERMISSION_REQUEST_CODE = 333
    const val LOCATION_PERMISSION_REQUEST_CODE = 444
    const val STORAGE_PERMISSION_REQUEST_CODE = 555

    fun isLocationPermissionGranted(context: Context) =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

    fun isCameraPermissionGranted(context: Context) =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    fun isNotificationPermissionGranted(context: Context) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED
        } else true

    fun requestLocationPermission(activity: Activity) =
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )

    fun requestCameraPermission(activity: Activity) =
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )

    fun mustGoToSettingAcceptPermissions(
        activity: AppCompatActivity,
        permission: String? = null,
        arrayPermissions: Array<String>? = null
    ): Boolean =
        if (permission != null) {
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                permission
            )
        } else if (arrayPermissions != null) {
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                arrayPermissions[0]
            )
        } else false

    fun isStoragePermissionGranted(context: Context): Boolean {
        val permissionStorage: Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        return ContextCompat.checkSelfPermission(
            context,
            permissionStorage[0]
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestStoragePermission(activity: Activity) {
        val permissionStorage: Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        ActivityCompat.requestPermissions(
            activity,
            permissionStorage,
            STORAGE_PERMISSION_REQUEST_CODE
        )
    }

    fun getPermissionStorage() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
}