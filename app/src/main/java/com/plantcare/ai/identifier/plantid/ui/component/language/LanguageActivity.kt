package com.plantcare.ai.identifier.plantid.ui.component.language

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.ads.control.ads.ITGAd
import com.ads.control.ads.wrapper.ApNativeAd
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.ads.AdsManager
import com.plantcare.ai.identifier.plantid.ads.AdsManager.loadNativeOnbPage1
import com.plantcare.ai.identifier.plantid.ads.AdsManager.loadNativeOnbPage4
import com.plantcare.ai.identifier.plantid.ads.AdsManager.nativeAdLanguage
import com.plantcare.ai.identifier.plantid.ads.AdsManager.nativeAdLanguageClick
import com.plantcare.ai.identifier.plantid.ads.PreLoadNativeListener
import com.plantcare.ai.identifier.plantid.app.AppConstants
import com.plantcare.ai.identifier.plantid.app.AppConstants.KEY_FIRST_INSTALL_APP
import com.plantcare.ai.identifier.plantid.app.AppConstants.KEY_LANGUAGE
import com.plantcare.ai.identifier.plantid.app.AppConstants.KEY_SETTING
import com.plantcare.ai.identifier.plantid.app.GlobalApp
import com.plantcare.ai.identifier.plantid.databinding.ActivityLanguageBinding
import com.plantcare.ai.identifier.plantid.ui.bases.BaseActivity
import com.plantcare.ai.identifier.plantid.ui.bases.ext.click
import com.plantcare.ai.identifier.plantid.ui.bases.ext.goneView
import com.plantcare.ai.identifier.plantid.ui.bases.ext.invisibleView
import com.plantcare.ai.identifier.plantid.ui.bases.ext.isNetwork
import com.plantcare.ai.identifier.plantid.ui.bases.ext.showToastById
import com.plantcare.ai.identifier.plantid.ui.bases.ext.visibleView
import com.plantcare.ai.identifier.plantid.utils.EasyPreferences.get
import com.plantcare.ai.identifier.plantid.utils.EasyPreferences.set
import com.plantcare.ai.identifier.plantid.utils.Routes.startMainActivity
import com.plantcare.ai.identifier.plantid.utils.Routes.startOnBoardingActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class LanguageActivity : BaseActivity<ActivityLanguageBinding>() {
    private var languageAdapter: LanguageAdapter? = null
    private var model: LanguageModel? = null

    private var isChooseLanguage = false
    private var isFromSetting = false
    private var isClickLanguage = false
    private var isFirstInstallApp = true
    private var popNativeClick = false

    override fun getLayoutActivity() = R.layout.activity_language

    override fun initViews() {
        isFromSetting = intent.getBooleanExtra(KEY_SETTING, false)
        isFirstInstallApp = prefs[KEY_FIRST_INSTALL_APP, true] == true
        if (isFromSetting){
            mBinding.icBack.visibleView()
        } else {
            mBinding.icBack.goneView()
            Log.e("VinhTQ", "initViews KEY_FIRST_INSTALL_APP: ${prefs.getBoolean(KEY_FIRST_INSTALL_APP, true)}")
            if (prefs.getBoolean(KEY_FIRST_INSTALL_APP, true)) {
                loadNativeOnbPage1(this)
                loadNativeOnbPage4(this)
            }
        }
        initRcvLanguage()
        initAdmob()
    }

    override fun onClickViews() {
        mBinding.icDone.click {
            if (model != null && isChooseLanguage) {
                prefs[KEY_LANGUAGE] = model?.isoLanguage
                setLocale()
                startMainOrOnBoarding()
            } else showToastById(R.string.please_select_language)
        }

        mBinding.icBack.click { finish() }
    }

    private fun initRcvLanguage() = mBinding.rcvLanguage.apply {
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this@LanguageActivity)
        languageAdapter = LanguageAdapter(onLanguageSelected = { language, index ->
            isClickLanguage = true
            isChooseLanguage = true
            model = language
            val indexSelectedBefore = languageAdapter?.indexSelected ?: -1
            languageAdapter?.let {
                it.apply {
                    indexSelected = index
                    notifyItemChanged(indexSelectedBefore)
                }
            }

            mBinding.icDone.visibleView()
            showNativeLanguageClick()
        }).apply { submitData(setLanguageDefault()) }
        adapter = languageAdapter
    }

    @SuppressLint("LogNotTimber")
    private fun initAdmob() {
        AdsManager.setPreLoadNativeCallback(object : PreLoadNativeListener {
            override fun onLoadNativeSuccess() =
                showNativeLanguage()

            override fun onLoadNativeFail() {
                if (nativeAdLanguage == null) {
                    mBinding.frAds.goneView()
                }
            }
        })
        showNativeLanguage()
    }

    private fun startMainOrOnBoarding() =
        try {
            if (isFromSetting) startMainActivity(this)
            else startOnBoardingActivity(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    private fun setLocale() {
        val language = prefs.getString(AppConstants.KEY_LANGUAGE, "en")

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

    private fun setLanguageDefault(): MutableList<LanguageModel> {
        val listLanguages: ArrayList<LanguageModel> = ArrayList()

        val key: String? = prefs[AppConstants.KEY_LANGUAGE, "en"]

        listLanguages.add(LanguageModel("English", "en", false, R.drawable.ic_english))
        listLanguages.add(LanguageModel("Hindi", "hi", false, R.drawable.ic_hindi))
        listLanguages.add(LanguageModel("Spanish", "es", false, R.drawable.ic_spanish))
        listLanguages.add(LanguageModel("French", "fr", false, R.drawable.ic_france))
        listLanguages.add(LanguageModel("Portuguese", "pt", false, R.drawable.ic_portugal))
        listLanguages.add(LanguageModel("Korean", "ko", false, R.drawable.ic_korean))
        listLanguages.add(LanguageModel("German", "de", false, R.drawable.ic_german))
        listLanguages.add(LanguageModel("Italian", "it", false, R.drawable.ic_italian))

        if (GlobalApp.instance.getLanguage() != null && !listLanguages.contains(GlobalApp.instance.getLanguage())) {
            listLanguages.remove(listLanguages[listLanguages.size - 1])
            val modelLanguage = GlobalApp.instance.getLanguage()
            if (modelLanguage != null) {
                listLanguages.add(0, modelLanguage)
            }

        }

        for (i in listLanguages.indices) {
            if (key == listLanguages[i].isoLanguage) {
                val data = listLanguages[i]
                // khi vào màn lagueage sẽ không chọn sẵn ngôn ngữ nào cả
                data.isCheck = false
                listLanguages.remove(listLanguages[i])
                listLanguages.add(listLanguages.size, data)
//                model = data
                break
            }
        }

        return listLanguages
    }

    private fun showNativeLanguage() =
        if (!isNetwork()) hideAdsView()
        else {
            if (nativeAdLanguage != null && !isFromSetting && !isClickLanguage) {
                showAdsView()
                populateNativeAdView(false)
            } else hideAdsView()
        }

    @SuppressLint("LogNotTimber")
    private fun showNativeLanguageClick() =
        if (!isNetwork()) hideAdsView()
        else {
            if (nativeAdLanguageClick != null && !isFromSetting && isClickLanguage) {
                showAdsView()
                populateNativeAdView(true)
            } else hideAdsView()
        }

    private fun populateNativeAdView(isClick: Boolean) {
        if (isClick) {
            if (!popNativeClick) {
                popNativeClick = true
                handlePopulateNativeAdView(nativeAdLanguageClick)
            }
        } else handlePopulateNativeAdView(nativeAdLanguage)
    }

    private fun handlePopulateNativeAdView(nativeAdLanguage: ApNativeAd?) = ITGAd.getInstance().populateNativeAdView(
        this, nativeAdLanguage, mBinding.frAds, mBinding.shimmerAds.shimmerNativeLarge
    )

    private fun showAdsView() = mBinding.frAds.visibleView()

    private fun hideAdsView() = mBinding.frAds.goneView()
}