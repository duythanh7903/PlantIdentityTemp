package com.plantcare.ai.identifier.plantid.ui.component.diagnose

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.ads.control.ads.ITGAd
import com.ads.control.funtion.AdCallback
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.plantcare.ai.identifier.plantid.BuildConfig
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.ads.RemoteConfigUtils
import com.plantcare.ai.identifier.plantid.app.AppConstants
import com.plantcare.ai.identifier.plantid.app.AppConstants.KEY_SET_SHOW_DIALOG_RATE
import com.plantcare.ai.identifier.plantid.app.GlobalApp.Companion.isShowDialogRateInThisSession
import com.plantcare.ai.identifier.plantid.databinding.ActivityDiagnoseBinding
import com.plantcare.ai.identifier.plantid.ui.bases.BaseActivity
import com.plantcare.ai.identifier.plantid.ui.bases.ext.click
import com.plantcare.ai.identifier.plantid.ui.bases.ext.goneView
import com.plantcare.ai.identifier.plantid.ui.bases.ext.isNetwork
import com.plantcare.ai.identifier.plantid.ui.bases.ext.showRateDialog
import com.plantcare.ai.identifier.plantid.ui.component.diagnose.adapter.DiagnoseAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiagnoseActivity : BaseActivity<ActivityDiagnoseBinding>() {
    private var diagnoseAdapter: DiagnoseAdapter? = null
    val diseaseList = DiagnoseUtils.initListPlant()
    private var reloadAds = 0

    private val resultPlantDetails =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) showRateDialogIfCan()
        }

    override fun getLayoutActivity(): Int = R.layout.activity_diagnose

    override fun onResume() {
        super.onResume()
        reloadAds++
        if (reloadAds < 3) {
            try {
                if (RemoteConfigUtils.getOnNativeDiagnose() && isNetwork()) {
                    ITGAd.getInstance().loadNativeAd(this,
                        BuildConfig.native_Diagnose,
                        R.layout.layout_native_diagnose,
                        mBinding.frAds,
                        mBinding.shimmerAds.shimmerNativeLarge,
                        object : AdCallback() {
                            override fun onAdFailedToLoad(i: LoadAdError?) {
                                super.onAdFailedToLoad(i)
                                mBinding.frAds.removeAllViews()
                                mBinding.frAds.goneView()
                            }

                            override fun onAdFailedToShow(adError: AdError?) {
                                super.onAdFailedToShow(adError)
                                mBinding.frAds.removeAllViews()
                                mBinding.frAds.goneView()
                            }
                        })
                }else{
                    mBinding.frAds.removeAllViews()
                    mBinding.frAds.goneView()
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                mBinding.frAds.removeAllViews()
                mBinding.frAds.goneView()
            }
        }
    }

    override fun initViews() {
        diagnoseAdapter = DiagnoseAdapter(onClickItemDisease = { disease ->
            val intent = Intent(this@DiagnoseActivity, PlantDiseaseDetailActivity::class.java)
            intent.putExtra(AppConstants.KEY_PLANT_DISEASE, disease)
            resultPlantDetails.launch(intent)
        })
        diagnoseAdapter?.submitData(diseaseList.sortedBy { getString(it.diseaseName) })

        mBinding.apply {
            rvDiagnoseList.adapter = diagnoseAdapter
            rvDiagnoseList.layoutManager =
                LinearLayoutManager(this@DiagnoseActivity, LinearLayoutManager.VERTICAL, false)

            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) = Unit

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) =
                    filterDisease(s.toString())

                override fun afterTextChanged(s: Editable?) = Unit

            })

            etSearch.setOnEditorActionListener { v, actionId, _ ->
                // "Search" (actionId == EditorInfo.IME_ACTION_SEARCH)
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard(this@DiagnoseActivity)

                    if (v.text.isEmpty()) {
                        Toast.makeText(
                            this@DiagnoseActivity,
                            getString(R.string.please_enter_text),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    true
                } else {
                    false
                }
            }
        }
    }

    override fun onClickViews() {
        mBinding.btnBack.click { onBackPressed() }

        mBinding.layoutContainer.click { hideKeyboard(this) }
    }

    private fun filterDisease(query: String) {
        val trimmedQuery = query.trim().replace("\\s+".toRegex(), " ")
        val normalizedQuery = if (trimmedQuery.isEmpty() && query.isNotEmpty()) {
            query
        } else {
            trimmedQuery
        }

        val filteredList = if (normalizedQuery.isEmpty()) {
            diseaseList
        } else {
            diseaseList.filter {
                mBinding.root.context.getString(it.diseaseName)
                    .contains(normalizedQuery, ignoreCase = true)
            }
        }

        if (filteredList.isEmpty()) {
            mBinding.rvDiagnoseList.visibility = View.GONE
            mBinding.layoutNoResult.visibility = View.VISIBLE
        } else {
            mBinding.rvDiagnoseList.visibility = View.VISIBLE
            mBinding.layoutNoResult.visibility = View.GONE
        }

        diagnoseAdapter?.updateSearchQuery(
            mBinding.root.context,
            diseaseList.sortedBy { mBinding.root.context.getString(it.diseaseName) },
            query
        )
    }

    private fun hideKeyboard(activity: Activity) {
        val view = activity.currentFocus
        if (view != null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            view.clearFocus()
        }
    }

    private fun showRateDialogIfCan() {
        val isRated = prefs.getBoolean(KEY_SET_SHOW_DIALOG_RATE, false)
        if (!isRated && !isShowDialogRateInThisSession) {
            isShowDialogRateInThisSession = true
            showRateDialog(
                activity = this, isFinish = false
            )
        }
    }

    @Deprecated("Deprecated in Java", ReplaceWith("finish()"))
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        finish()
    }
}