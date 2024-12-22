package com.plantcare.ai.identifier.plantid.ui.component.iap

import android.annotation.SuppressLint
import android.util.Log
import com.ads.control.billing.AppPurchase
import com.ads.control.funtion.PurchaseListener
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.app.AppConstants.DEFAULT_VALUE_BY_DOCUMENT
import com.plantcare.ai.identifier.plantid.app.AppConstants.ID_SUB
import com.plantcare.ai.identifier.plantid.app.AppConstants.ID_SUB_30_TIMES
import com.plantcare.ai.identifier.plantid.app.AppConstants.KEY_OBJ_IAP
import com.plantcare.ai.identifier.plantid.app.AppConstants.KEY_SCAN_PLANT
import com.plantcare.ai.identifier.plantid.app.AppConstants.KEY_TYPE_SUB
import com.plantcare.ai.identifier.plantid.app.AppConstants.TYPE_SUB_30_SCANS
import com.plantcare.ai.identifier.plantid.app.AppConstants.TYPE_SUB_YEARLY
import com.plantcare.ai.identifier.plantid.databinding.ActivityIapBinding
import com.plantcare.ai.identifier.plantid.ui.bases.BaseActivity
import com.plantcare.ai.identifier.plantid.ui.bases.ext.click
import com.plantcare.ai.identifier.plantid.ui.bases.ext.isNetwork
import com.plantcare.ai.identifier.plantid.ui.component.iap.model.ManageIapEntity
import com.plantcare.ai.identifier.plantid.utils.EasyPreferences.set
import com.plantcare.ai.identifier.plantid.utils.SystemUtil.convertJsonToManageIapEntity
import com.plantcare.ai.identifier.plantid.utils.SystemUtil.convertManageIapEntityToJson

class IapActivity : BaseActivity<ActivityIapBinding>() {

    private var idSubCurrent = ""

    override fun getLayoutActivity(): Int = R.layout.activity_iap

    @SuppressLint("LogNotTimber")
    override fun initViews() {
        getPriceAndShow()
        activeBestChoiceDefault()
        eventWhenBuyAipSuccess()

        Log.d("duylt", "Check Id Purchased: ${AppPurchase.getInstance().idPurchased}")
    }

    override fun onClickViews() {

        mBinding.icClose.click { finish() }

        mBinding.btnChoiceYear.click {
            // active btn clicked
            mBinding.btnChoiceYear.isActivated = true
            mBinding.icTickYearly.isActivated = true

            // un active btn 30 scans
            mBinding.btnChoice30Scans.isActivated = false
            mBinding.icTick30Scans.isActivated = false

            // save flag var
            idSubCurrent = ID_SUB
        }

        mBinding.btnChoice30Scans.click {
            // active btn clicked
            mBinding.btnChoice30Scans.isActivated = true
            mBinding.icTick30Scans.isActivated = true

            // un active btn yearly
            mBinding.btnChoiceYear.isActivated = false
            mBinding.icTickYearly.isActivated = false

            // save flag var
            idSubCurrent = ID_SUB_30_TIMES
        }

        mBinding.btnContinue.click { confirmBuyIap() }
    }

    private fun activeBestChoiceDefault() {
        mBinding.btnChoiceYear.isActivated = true
        mBinding.icTickYearly.isActivated = true
        idSubCurrent = ID_SUB
    }

    private fun getPriceAndShow() {
        mBinding.tvPrice30Scans.text = AppPurchase.getInstance().getPrice(ID_SUB_30_TIMES)
        mBinding.tvPriceYearly.text = AppPurchase.getInstance().getPriceSub(ID_SUB)
    }

    private fun confirmBuyIap() {
        if (isNetwork()) {
            AppPurchase.getInstance().subscribe(this, idSubCurrent)
        }
    }

    private fun eventWhenBuyAipSuccess() {
        AppPurchase.getInstance().setPurchaseListener(object : PurchaseListener {
            @SuppressLint("LogNotTimber")
            override fun onProductPurchased(s: String, s1: String) {
                Log.d("duylt", "Check Iap bought:\ns: ${s}\ns1: ${s1}\nID Sub: $idSubCurrent")
                when (idSubCurrent) {
                    ID_SUB_30_TIMES -> {
                        // add more 30 times scan
                        val countScanPlantCurrent = prefs.getInt(KEY_SCAN_PLANT, DEFAULT_VALUE_BY_DOCUMENT) // default value follow by document
                        prefs[KEY_SCAN_PLANT] = countScanPlantCurrent + 30
                        // change type to scan type
                        prefs[KEY_TYPE_SUB] = TYPE_SUB_30_SCANS

                        val objIapDefault = ManageIapEntity() // get obj default
                        val objIapCurrentTypeStr = prefs.getString(
                            KEY_OBJ_IAP, convertManageIapEntityToJson(objIapDefault)
                        ) ?: convertManageIapEntityToJson(objIapDefault) // get json
                        val objIapCurrent = convertJsonToManageIapEntity(objIapCurrentTypeStr) // convert json to obj
                        objIapCurrent.apply { // update information
                            totalScan += 30
                            lastTimeBought = System.currentTimeMillis()
                            countBoughtIap += 1
                        }
                        val objUpdateTypeStr = convertManageIapEntityToJson(objIapCurrent)
                        prefs[KEY_OBJ_IAP] = objUpdateTypeStr // cache again
                    }

                    else -> {
                        // clear all count scan
                        prefs[KEY_SCAN_PLANT] = 0
                        // change type to yearly type
                        prefs[KEY_TYPE_SUB] = TYPE_SUB_YEARLY
                    }
                }
            }

            override fun displayErrorMessage(s: String) = Unit

            override fun onUserCancelBilling() {

            }
        })
    }
}