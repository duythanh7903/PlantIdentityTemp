package com.plantcare.ai.identifier.plantid.ui.bases

import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.plantcare.ai.identifier.plantid.app.AppConstants
import com.plantcare.ai.identifier.plantid.utils.EasyPreferences
import com.plantcare.ai.identifier.plantid.utils.ITGTrackingHelper
import com.plantcare.ai.identifier.plantid.utils.Routes
import java.util.Locale

private const val TAG = "BaseActivity"

abstract class BaseActivity<VB : ViewDataBinding> : AppCompatActivity() {
    lateinit var mBinding: VB
    lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = EasyPreferences.defaultPrefs(this)

        setLocal()

        requestWindow()
        val layoutView = getLayoutActivity()
        mBinding = DataBindingUtil.setContentView(this, layoutView)
        Log.d(TAG, "onCreate: name Class: ${this::class.java.simpleName}")
        ITGTrackingHelper.addScreenTrack(this::class.java.simpleName)
        if (intent.getStringExtra(AppConstants.KEY_TRACKING_SCREEN_FROM) != null) {
            Routes.addTrackingMoveScreen(
                intent.getStringExtra(AppConstants.KEY_TRACKING_SCREEN_FROM).toString(),
                this::class.java.simpleName
            )
        }
        mBinding.lifecycleOwner = this

        initViews()
        onResizeViews()
        onClickViews()
        observerData()
    }

    open fun setUpViews() {}

    abstract fun getLayoutActivity(): Int

    open fun requestWindow() {}

    open fun initViews() {}

    open fun onResizeViews() {}

    open fun onClickViews() {}

    open fun observerData() {}

    private fun setLocal() {
        val language: String? = prefs.getString(AppConstants.KEY_LANGUAGE, "")
        if (language == "") {
            val config = Configuration()
            val locale = Locale.getDefault()
            Locale.setDefault(locale)
            config.locale = locale
            resources.updateConfiguration(config, resources.displayMetrics)
        } else {
            if (language.equals("", ignoreCase = true)) return
            val locale = Locale(language)
            Locale.setDefault(locale)
            val config = Configuration()
            config.locale = locale
            resources.updateConfiguration(config, resources.displayMetrics)
        }
    }


    override fun onResume() {
        super.onResume()
        hideNavigationBar()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        hideNavigationBar()

    }

    private fun hideNavigationBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowInsetsControllerCompat(window, window.decorView).let { controller ->
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            hideSystemUIBeloR()
        }

    }

    private fun hideSystemUIBeloR() {
        val decorView: View = window.decorView
        val uiOptions = decorView.systemUiVisibility
        var newUiOptions = uiOptions
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_LOW_PROFILE
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        decorView.systemUiVisibility = newUiOptions
    }
}