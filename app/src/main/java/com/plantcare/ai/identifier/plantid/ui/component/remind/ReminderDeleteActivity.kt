package com.plantcare.ai.identifier.plantid.ui.component.remind

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.data.database.entities.AlarmEntity
import com.plantcare.ai.identifier.plantid.databinding.ActivityReminderDeleteBinding
import com.plantcare.ai.identifier.plantid.service.LoadAlarmsService
import com.plantcare.ai.identifier.plantid.ui.bases.BaseActivity
import com.plantcare.ai.identifier.plantid.ui.bases.ext.click
import com.plantcare.ai.identifier.plantid.ui.bases.ext.invisibleView
import com.plantcare.ai.identifier.plantid.ui.bases.ext.showToastById
import com.plantcare.ai.identifier.plantid.ui.bases.ext.visibleView
import com.plantcare.ai.identifier.plantid.ui.component.dialog.ConfirmDeleteRemindDialog
import com.plantcare.ai.identifier.plantid.ui.component.remind.adapter.ReminderDeleteAdapter
import com.plantcare.ai.identifier.plantid.utils.toListAlarmDeleteDomain
import com.plantcare.ai.identifier.plantid.utils.toListAlarmEntity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReminderDeleteActivity : BaseActivity<ActivityReminderDeleteBinding>() {

    private val viewModel: RemindViewModel by viewModels()

    private var reminderDeleteAdapter: ReminderDeleteAdapter? = null

    override fun getLayoutActivity(): Int = R.layout.activity_reminder_delete

    override fun initViews() {
        initReminderAdapter()
    }

    override fun onClickViews() {
        mBinding.icBack.click { finish() }

        mBinding.icChbAll.click { checkAllItem() }

        mBinding.btnDelete.click { onEventDelete() }
    }

    override fun observerData() {
        viewModel.records.observe(this) { listRecord ->
            reminderDeleteAdapter?.submitData(listRecord.toListAlarmDeleteDomain())
            updateDeleteButtonVisibility()
            if (listRecord.isEmpty()) finish()
        }
    }

    private fun initReminderAdapter() = mBinding.rcvReminder.apply {
        reminderDeleteAdapter = ReminderDeleteAdapter(
            contextParams = this@ReminderDeleteActivity
        ) { _, index ->
            reminderDeleteAdapter?.changeSelected(index = index, onEventAllItemDone = {
                mBinding.icChbAll.isActivated = true
            }, onEventAllItemNotDone = {
                mBinding.icChbAll.isActivated = false
            }, onEventMarkSomeItem = {
                showBtnDelete()
            })
            updateDeleteButtonVisibility()
        }

        adapter = reminderDeleteAdapter
    }

    private fun checkAllItem() {
        val isAlreadyMarkDone = mBinding.icChbAll.isActivated
        mBinding.icChbAll.isActivated = if (isAlreadyMarkDone) {
            reminderDeleteAdapter?.unMarkAllItem()
            hideBtnDelete()
            false
        } else {
            reminderDeleteAdapter?.markAllItem()
            showBtnDelete()
            true
        }
        updateDeleteButtonVisibility()
    }

    private fun onEventDelete() {
        val listReminderMarked = reminderDeleteAdapter?.list?.filter { it.isSelected }
        listReminderMarked?.let {
            if (it.isNotEmpty()) showDialogConfirm(listReminderMarked.toListAlarmEntity().toMutableList())
            else showToastById(R.string.please_select_the_record_to_delete)
        } ?: showToastById(R.string.some_thing_went_wrong)
    }

    private fun showDialogConfirm(
        record: MutableList<AlarmEntity>
    ) = ConfirmDeleteRemindDialog(context = this,
        onEventCancel = {},
        onEventDelete = {
            viewModel.deleteListRecordRemind(record)
            LoadAlarmsService.launchLoadAlarmsService(this)
        }).show()

    private fun hideBtnDelete() = mBinding.btnDelete.invisibleView()

    private fun showBtnDelete() = mBinding.btnDelete.visibleView()

    private fun updateDeleteButtonVisibility() {
        mBinding.btnDelete.visibility =
            if (reminderDeleteAdapter?.hasAnyItemSelected() == true) View.VISIBLE else View.GONE
    }
}