package com.plantcare.ai.identifier.plantid.ui.component.history.list

import android.content.Intent
import androidx.activity.viewModels
import com.plantcare.ai.identifier.plantid.BuildConfig
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.ads.AdsManager
import com.plantcare.ai.identifier.plantid.ads.RemoteConfigUtils
import com.plantcare.ai.identifier.plantid.app.AppConstants
import com.plantcare.ai.identifier.plantid.databinding.ActivityHistoryBinding
import com.plantcare.ai.identifier.plantid.domains.HistoryDomain
import com.plantcare.ai.identifier.plantid.ui.bases.BaseActivity
import com.plantcare.ai.identifier.plantid.ui.bases.ext.click
import com.plantcare.ai.identifier.plantid.ui.bases.ext.goneView
import com.plantcare.ai.identifier.plantid.ui.bases.ext.invisibleView
import com.plantcare.ai.identifier.plantid.ui.bases.ext.showToastById
import com.plantcare.ai.identifier.plantid.ui.bases.ext.visibleView
import com.plantcare.ai.identifier.plantid.ui.component.history.HistoryViewModel
import com.plantcare.ai.identifier.plantid.ui.component.history.delete.HistoryDeleteActivity
import com.plantcare.ai.identifier.plantid.ui.component.main.bottom_bar.myplant.MyPlantDetailActivity
import com.plantcare.ai.identifier.plantid.utils.toListHistoryDomain
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryActivity : BaseActivity<ActivityHistoryBinding>() {

    private var historyAdapter: HistoryAdapter? = null

    private val viewModel: HistoryViewModel by viewModels()

    override fun getLayoutActivity(): Int = R.layout.activity_history

    override fun initViews() {
        initRcvHistory()
    }

    override fun onClickViews() {
        mBinding.icBack.click { finish() }

        mBinding.icTrash.click {
            if (historyAdapter?.list?.isEmpty() == true) showToastById(R.string.no_item_here)
            else goToDeleteHistoryScreen()
        }
    }

    override fun observerData() {
        viewModel.records.observe(this) { listRecord ->
            if (listRecord.isEmpty()) {
                mBinding.rcvHistory.goneView()
                mBinding.layoutNoData.visibleView()
                mBinding.icTrash.invisibleView()
                mBinding.frAds.goneView()
            } else {
                mBinding.rcvHistory.visibleView()
                mBinding.layoutNoData.goneView()
                mBinding.icTrash.visibleView()

                historyAdapter?.submitData(listRecord.toListHistoryDomain())
                mBinding.frAds.visibleView()
                AdsManager.loadBanner(this, BuildConfig.banner_all, mBinding.frAds, RemoteConfigUtils.getOnBannerAll())
            }
        }
    }

    private fun initRcvHistory() = mBinding.rcvHistory.apply {
        historyAdapter =
            HistoryAdapter(contextParams = this@HistoryActivity, onItemClick = { history, index ->
                onItemClick(history, index)
            })

        adapter = historyAdapter
    }

    private fun goToDeleteHistoryScreen() =
        startActivity(Intent(this, HistoryDeleteActivity::class.java))

    private fun onItemClick(history: HistoryDomain, index: Int) {
        val intent = Intent(this, MyPlantDetailActivity::class.java)
        intent.putExtra(AppConstants.KEY_HISTORY, history)
        startActivity(intent)
    }
}