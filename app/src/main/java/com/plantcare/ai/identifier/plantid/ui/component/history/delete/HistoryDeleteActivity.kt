package com.plantcare.ai.identifier.plantid.ui.component.history.delete

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import com.plantcare.ai.identifier.plantid.BuildConfig
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.ads.AdsManager
import com.plantcare.ai.identifier.plantid.ads.RemoteConfigUtils
import com.plantcare.ai.identifier.plantid.data.database.entities.HistoryEntity
import com.plantcare.ai.identifier.plantid.databinding.ActivityHistoryDeleteBinding
import com.plantcare.ai.identifier.plantid.ui.bases.BaseActivity
import com.plantcare.ai.identifier.plantid.ui.bases.ext.click
import com.plantcare.ai.identifier.plantid.ui.bases.ext.invisibleView
import com.plantcare.ai.identifier.plantid.ui.bases.ext.showToastById
import com.plantcare.ai.identifier.plantid.ui.bases.ext.visibleView
import com.plantcare.ai.identifier.plantid.ui.component.dialog.ConfirmDeleteRemindDialog
import com.plantcare.ai.identifier.plantid.ui.component.history.HistoryViewModel
import com.plantcare.ai.identifier.plantid.ui.component.main.MainActivity
import com.plantcare.ai.identifier.plantid.utils.toListHistoryDomain
import com.plantcare.ai.identifier.plantid.utils.toListHistoryEntity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryDeleteActivity : BaseActivity<ActivityHistoryDeleteBinding>() {

    private val viewModel: HistoryViewModel by viewModels()

    private var historyAdapter: HistoryDeleteAdapter? = null

    override fun getLayoutActivity(): Int = R.layout.activity_history_delete

    override fun initViews() {
        initRcvHistory()
        AdsManager.loadBanner(this, BuildConfig.banner_all, mBinding.frAds, RemoteConfigUtils.getOnBannerAll())
    }

    override fun onClickViews() {
        mBinding.icBack.click { finish() }

        mBinding.icChbAll.click { markAllItem() }

        mBinding.btnDelete.click { onEventDelete() }
    }

    override fun observerData() {
        viewModel.records.observe(this) { listRecord ->
           if(listRecord.isNotEmpty()){
               historyAdapter?.submitData(listRecord.toListHistoryDomain())
               updateDeleteButtonVisibility()
           } else finish()
        }
    }

    private fun initRcvHistory() = mBinding.rcvHistory.apply {
        historyAdapter = HistoryDeleteAdapter(
            contextParams = this@HistoryDeleteActivity,
            onItemSelected = { _, index ->
                historyAdapter?.changeSelected(index = index, onEventAllItemDone = {
                    mBinding.icChbAll.isActivated = true
                    showBtnDelete()
                }, onEventAllItemNotDone = {
                    mBinding.icChbAll.isActivated = false
                }, onEventMarkSomeItem = {
                    showBtnDelete()
                })
                updateDeleteButtonVisibility()
            })

        adapter = historyAdapter
    }

    private fun markAllItem() {
        val isAlreadyMarkDone = mBinding.icChbAll.isActivated
        mBinding.icChbAll.isActivated = if (isAlreadyMarkDone) {
            historyAdapter?.unMarkAllItem()
            hideBtnDelete()
            false
        } else {
            historyAdapter?.markAllItem()
            showBtnDelete()
            true
        }
        updateDeleteButtonVisibility()
    }

    private fun onEventDelete() {
        val listHistoryMarked = historyAdapter?.list?.filter { it.isSelected }
        listHistoryMarked?.let {
            if (it.isNotEmpty()) showDialogConfirm(
                listHistoryMarked.toListHistoryEntity().toMutableList()
            )
            else showToastById(R.string.please_select_the_record_to_delete)
        } ?: showToastById(R.string.some_thing_went_wrong)
    }

    private fun showDialogConfirm(
        histories: MutableList<HistoryEntity>
    ) = ConfirmDeleteRemindDialog(context = this@HistoryDeleteActivity,
        onEventCancel = {},
        onEventDelete = {
            viewModel.deleteListHistoryEntity(histories)
        }).show()

    private fun updateDeleteButtonVisibility() {
        mBinding.btnDelete.visibility =
            if (historyAdapter?.hasAnyItemSelected() == true) View.VISIBLE else View.GONE
    }

    private fun showBtnDelete() = mBinding.btnDelete.visibleView()

    private fun hideBtnDelete() = mBinding.btnDelete.invisibleView()
}