package com.plantcare.ai.identifier.plantid.ui.component.setting

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.widget.Toast
import com.ads.control.admob.AppOpenManager
import com.plantcare.ai.identifier.plantid.BuildConfig
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.ads.AdsManager
import com.plantcare.ai.identifier.plantid.ads.RemoteConfigUtils
import com.plantcare.ai.identifier.plantid.app.AppConstants.KEY_SETTING
import com.plantcare.ai.identifier.plantid.app.AppConstants.KEY_SET_SHOW_DIALOG_RATE
import com.plantcare.ai.identifier.plantid.app.GlobalApp.Companion.isShowDialogRateInThisSession
import com.plantcare.ai.identifier.plantid.databinding.ActivitySettingBinding
import com.plantcare.ai.identifier.plantid.ui.bases.BaseActivity
import com.plantcare.ai.identifier.plantid.ui.bases.ext.click
import com.plantcare.ai.identifier.plantid.ui.bases.ext.showRateDialog
import com.plantcare.ai.identifier.plantid.ui.bases.ext.showToastById
import com.plantcare.ai.identifier.plantid.ui.component.history.list.HistoryActivity
import com.plantcare.ai.identifier.plantid.ui.component.language.LanguageActivity

class SettingActivity : BaseActivity<ActivitySettingBinding>() {

    private var isShowAdsResume = true

    override fun getLayoutActivity(): Int = R.layout.activity_setting

    override fun onResume() {
        super.onResume()

        if (!isShowAdsResume) {
            AppOpenManager.getInstance().enableAppResume()
            isShowAdsResume = true
        }
    }

    override fun initViews() {
        super.initViews()
        AdsManager.loadBanner(this, BuildConfig.banner_all, mBinding.frAds, RemoteConfigUtils.getOnBannerAll())
    }

    override fun onBackPressed() {
        AppOpenManager.getInstance().enableAppResume()
        finish()
    }

    override fun onClickViews() {
        mBinding.icBack.click {
            onBackPressed()
        }

        mBinding.btnLanguage.click { goToLanguageScreen() }

        mBinding.btnHistory.click { goToHistoryScreen() }

        mBinding.btnShare.click {
            isShowAdsResume = false

            AppOpenManager.getInstance().disableAppResume()
            shareApp(this)
        }

        mBinding.btnPolicy.click {
            isShowAdsResume = false

            AppOpenManager.getInstance().disableAppResume()
            openLinkPolicy(this@SettingActivity)
        }

        mBinding.btnRateApp.click { onRate() }
    }

    private fun goToHistoryScreen() = startActivity(Intent(this, HistoryActivity::class.java))

    private fun goToLanguageScreen() =
        startActivity(Intent(this, LanguageActivity::class.java).apply {
            putExtra(KEY_SETTING, true)
        })

    private fun shareApp(context: Context) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
            var shareMessage =
                "${context.getString(R.string.app_name)}\n${context.getString(R.string.let_me_recommend)}"
            shareMessage =
                "$shareMessage\nhttps://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            Handler().postDelayed({
                context.startActivity(
                    Intent.createChooser(
                        shareIntent, context.getString(R.string.share_to)
                    )
                )
            }, 250)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openLinkPolicy(context: Context) {
        val privacyPolicy = "https://sites.google.com/view/anhtuannt0308mypolicy"

        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicy))
            context.startActivity(browserIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onRate() {
        val isRated = prefs.getBoolean(KEY_SET_SHOW_DIALOG_RATE, false)
        if (isRated) showToastById(R.string.txt_thanks_you_for_rating)
        else {
            showRateDialog(
                activity = this, isFinish = false
            )
            isShowDialogRateInThisSession = true
        }
    }
}