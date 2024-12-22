package com.plantcare.ai.identifier.plantid.ui.component.remind

import android.annotation.SuppressLint
import android.content.Intent
import androidx.activity.viewModels
import com.plantcare.ai.identifier.plantid.BuildConfig
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.ads.AdsManager
import com.plantcare.ai.identifier.plantid.ads.RemoteConfigUtils
import com.plantcare.ai.identifier.plantid.data.database.entities.AlarmEntity
import com.plantcare.ai.identifier.plantid.data.database.entities.AlarmEntity.TYPE_ALARM_NORMAL
import com.plantcare.ai.identifier.plantid.databinding.ActivityReminderBinding
import com.plantcare.ai.identifier.plantid.domains.AlarmDomain
import com.plantcare.ai.identifier.plantid.service.AlarmReceiver
import com.plantcare.ai.identifier.plantid.service.LoadAlarmsService
import com.plantcare.ai.identifier.plantid.ui.bases.BaseActivity
import com.plantcare.ai.identifier.plantid.ui.bases.ext.click
import com.plantcare.ai.identifier.plantid.ui.bases.ext.goneView
import com.plantcare.ai.identifier.plantid.ui.bases.ext.invisibleView
import com.plantcare.ai.identifier.plantid.ui.bases.ext.showToastById
import com.plantcare.ai.identifier.plantid.ui.bases.ext.visibleView
import com.plantcare.ai.identifier.plantid.ui.component.dialog.AddRemindDialog
import com.plantcare.ai.identifier.plantid.ui.component.dialog.UpdateRemindDialog
import com.plantcare.ai.identifier.plantid.ui.component.remind.adapter.ReminderAdapter
import com.plantcare.ai.identifier.plantid.utils.toAlarmEntity
import com.plantcare.ai.identifier.plantid.utils.toListAlarmDomain
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class ReminderActivity : BaseActivity<ActivityReminderBinding>() {

    private val viewModel: RemindViewModel by viewModels()

    private var reminderAdapter: ReminderAdapter? = null

    override fun getLayoutActivity(): Int = R.layout.activity_reminder

    override fun initViews() {
        AdsManager.loadBanner(this, BuildConfig.banner_all, mBinding.frAds, RemoteConfigUtils.getOnBannerAll())
        reminderAdapter = ReminderAdapter(
            contextParams = this,
            onItemClick = { reminder, index ->
                onItemClick(reminder, index)
            }, onToggleClick = { reminder, index ->
                onReminderRecordClick(reminder, index)
            }
        )
        mBinding.rcvReminder.apply {
            adapter = reminderAdapter
        }
    }

    override fun onClickViews() {
        mBinding.icBack.click { onBackPressed() }

        mBinding.btnAddReminder.click {
            onEventReminder()
        }

        mBinding.icTrash.click {
            if (reminderAdapter?.list?.isNotEmpty() == true) goToTrashScreen()
            else showToastById(R.string.no_reminder_here_click_add_reminder_to_add_item)
        }
    }

    @SuppressLint("MissingSuperCall")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        setResult(RESULT_OK)
        finish()
    }

    override fun observerData() {
        viewModel.records.observe(this) { listRecord ->
            if (listRecord.isEmpty()) {
                mBinding.rcvReminder.goneView()
                mBinding.layoutNullData.visibleView()
                mBinding.icTrash.invisibleView()
            } else {
                mBinding.rcvReminder.visibleView()
                mBinding.layoutNullData.goneView()
                mBinding.icTrash.visibleView()
            }

            reminderAdapter?.submitData(listRecord.toListAlarmDomain())
        }
    }

    private fun goToTrashScreen() =
        startActivity(Intent(this, ReminderDeleteActivity::class.java))

    private fun onEventReminder() {
        AddRemindDialog(
            activity = this,
            onCancel = {},
            onSave = { hour, minute, soundIdFromRawFolder, isVibrationOn, title, des, isM, isT, isW, isTh, isF, isSa, isS ->
                val alarmEntity = AlarmEntity()
                val time = Calendar.getInstance()
                time.set(Calendar.MINUTE, minute)
                time.set(Calendar.HOUR_OF_DAY, hour)
                alarmEntity.id = System.currentTimeMillis()
                alarmEntity.time = time.getTimeInMillis()
                alarmEntity.label = title
                alarmEntity.description = des
                alarmEntity.setIsVibration(isVibrationOn)
                alarmEntity.soundRes = soundIdFromRawFolder
                alarmEntity.setIsEnabled(true)
                alarmEntity.setDay(AlarmEntity.MON, isM)
                alarmEntity.setDay(AlarmEntity.TUES, isT)
                alarmEntity.setDay(AlarmEntity.WED, isW)
                alarmEntity.setDay(AlarmEntity.THURS, isTh)
                alarmEntity.setDay(AlarmEntity.FRI, isF)
                alarmEntity.setDay(AlarmEntity.SAT, isSa)
                alarmEntity.setDay(AlarmEntity.SUN, isS)
                alarmEntity.type = TYPE_ALARM_NORMAL
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = alarmEntity.time
                // Lùi lại một ngày
                calendar.add(Calendar.DAY_OF_MONTH, -1)
                alarmEntity.time = calendar.timeInMillis
                viewModel.saveRecord(alarmEntity) {
                    LoadAlarmsService.launchLoadAlarmsService(this)
                    AlarmReceiver.setReminderAlarm(this, alarmEntity)
                }
            }).show()
    }

    private fun onReminderRecordClick(alarmDomain: AlarmDomain, index: Int) {
        alarmDomain.isEnabled = !alarmDomain.isEnabled
        viewModel.updateRecord(alarmDomain.toAlarmEntity()) {
            LoadAlarmsService.launchLoadAlarmsService(this)
            AlarmReceiver.setReminderAlarm(this, alarmDomain.toAlarmEntity())
        }
    }

    private fun onItemClick(alarmDomain: AlarmDomain, index: Int) {
        UpdateRemindDialog(
            activity = this@ReminderActivity,
            alarmEntity = alarmDomain.toAlarmEntity(),
            soundRes = alarmDomain.soundRes,
            onCancel = {},
            onSave = { hour, minute, soundIdFromRawFolder, isVibrationOn, title, des, isM, isT, isW, isTh, isF, isSa, isS ->
                val alarmEntity = alarmDomain.toAlarmEntity()
                val time = Calendar.getInstance()
                time.set(Calendar.MINUTE, minute)
                time.set(Calendar.HOUR_OF_DAY, hour)
                alarmEntity.time = time.getTimeInMillis()
                alarmEntity.label = title
                alarmEntity.description = des
                alarmEntity.setIsVibration(isVibrationOn)
                alarmEntity.soundRes = soundIdFromRawFolder
                alarmEntity.setIsEnabled(true)
                alarmEntity.setDay(AlarmEntity.MON, isM)
                alarmEntity.setDay(AlarmEntity.TUES, isT)
                alarmEntity.setDay(AlarmEntity.WED, isW)
                alarmEntity.setDay(AlarmEntity.THURS, isTh)
                alarmEntity.setDay(AlarmEntity.FRI, isF)
                alarmEntity.setDay(AlarmEntity.SAT, isSa)
                alarmEntity.setDay(AlarmEntity.SUN, isS)
                alarmEntity.type = TYPE_ALARM_NORMAL
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = alarmEntity.time
                // Lùi lại một ngày
                calendar.add(Calendar.DAY_OF_MONTH, -1)
                alarmEntity.time = calendar.timeInMillis
                viewModel.updateRecord(alarmEntity) {
                    LoadAlarmsService.launchLoadAlarmsService(this)
                    AlarmReceiver.setReminderAlarm(this, alarmEntity)
                }
            }
        ).show()
    }
}