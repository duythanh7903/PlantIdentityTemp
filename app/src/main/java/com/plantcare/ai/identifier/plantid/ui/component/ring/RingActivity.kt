package com.plantcare.ai.identifier.plantid.ui.component.ring

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.PowerManager
import android.os.Vibrator
import android.util.Log
import android.view.WindowManager
import androidx.activity.viewModels
import com.ads.control.admob.AppOpenManager
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.app.AppConstants.KEY_DATA_TO_RING_SCREEN
import com.plantcare.ai.identifier.plantid.app.AppConstants.SOUND_DEFAULT
import com.plantcare.ai.identifier.plantid.app.AppConstants.SOUND_SILENT
import com.plantcare.ai.identifier.plantid.data.database.entities.AlarmEntity
import com.plantcare.ai.identifier.plantid.data.database.entities.AlarmEntity.TYPE_ALARM_SNOOZE
import com.plantcare.ai.identifier.plantid.databinding.ActivityRingBinding
import com.plantcare.ai.identifier.plantid.service.AlarmReceiver
import com.plantcare.ai.identifier.plantid.service.LoadAlarmsService
import com.plantcare.ai.identifier.plantid.ui.bases.BaseActivity
import com.plantcare.ai.identifier.plantid.ui.bases.ext.click
import com.plantcare.ai.identifier.plantid.ui.bases.ext.showToastById
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class RingActivity: BaseActivity<ActivityRingBinding>() {
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var ringtone: Uri? = null
    private var alarmRingtone: Ringtone? = null
    private var jobCountDown: Job? = null
    private var alarmEntity: AlarmEntity? = null

    private val viewModel: RingViewModel by viewModels()

    override fun onBackPressed() {
        AppOpenManager.getInstance().disableAppResume()
        finish()
    }

    override fun getLayoutActivity(): Int = R.layout.activity_ring

    override fun initViews() {
        initWakeLock()
        initMediaVar()
        getAlarmDomainFromReceiver()
        AppOpenManager.getInstance().disableAppResume()

        jobCountDown = viewModel.countDown1MinuteAndFinishScreenIfNotAction()

        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )
    }

    override fun onClickViews() {
        mBinding.btnLetDoIt.click { onEventLetDoIt() }

        mBinding.btnSnooze.click { onEventSnooze() }
    }

    override fun onDestroy() {
        super.onDestroy()

        mediaPlayer?.release()
        mediaPlayer = null

        vibrator?.cancel()
        vibrator = null

        alarmRingtone?.stop()
        alarmRingtone = null

        jobCountDown?.cancel()
        jobCountDown = null
    }

    override fun observerData() {
        viewModel.alarmEntity.observe(this) { alarm ->
            showUIToText(alarm)
            alarmEntity = alarm
            val typeAlarm = alarm.type
            if (typeAlarm == TYPE_ALARM_SNOOZE) {
                alarm.setIsEnabled(false)
                viewModel.updateRecord(alarm) {
                    LoadAlarmsService.launchLoadAlarmsService(this@RingActivity)
                    AlarmReceiver.setReminderAlarm(this@RingActivity, alarm)
                }
            }
        }

        viewModel.canFinishScreen.observe(this) { finish ->
            if (finish) onBackPressed()
        }
    }

    private fun initWakeLock() {
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "AlarmActivity::WakeLock"
        )
        wakeLock.acquire(3000) // Giữ WakeLock trong 3 giây
    }

    private fun onEventLetDoIt() {
        alarmEntity?.let {
            if (it.type == TYPE_ALARM_SNOOZE) {
                it.setIsEnabled(false)
                viewModel.updateRecord(it) {
                    AlarmReceiver.setReminderAlarm(this@RingActivity, it)
                }
            }
        }
        onBackPressed()
    }

    @SuppressLint("LogNotTimber")
    private fun getAlarmDomainFromReceiver() {
        val bundle = intent.extras
        if (bundle != null) {
            var alarmEntity = bundle.getSerializable(KEY_DATA_TO_RING_SCREEN)
            if (alarmEntity != null) {
                alarmEntity = alarmEntity as AlarmEntity
                viewModel.searchAlarmEntity(alarmEntity.id)
            } else eventFinishWithShowWrongMessage()
        } else eventFinishWithShowWrongMessage()
    }

    private fun eventFinishWithShowWrongMessage() {
        showToastById(R.string.some_thing_went_wrong)
        onBackPressed()
    }

    private fun initMediaVar() {
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        ringtone = RingtoneManager.getDefaultUri(
            RingtoneManager.TYPE_NOTIFICATION
        )
    }

    @SuppressLint("SetTextI18n")
    private fun showUIToText(alarmDomain: AlarmEntity) {
        mBinding.tvTitle.apply {
            text = "${getString(R.string.it_s_time)} “${alarmDomain.label}”!"
            isSelected = true
        }
        mBinding.tvDescription.text = alarmDomain.description
        mBinding.tvTime.text = getTimeStrFromTimeMili(System.currentTimeMillis())

        handlePlaySoundAndVibrator(alarmDomain)
    }

    private fun getTimeStrFromTimeMili(time: Long): String {
        val dateFormat = SimpleDateFormat("HH:mm", Locale("en"))
        return dateFormat.format(Date(time))
    }

    @SuppressLint("LogNotTimber")
    private fun handlePlaySoundAndVibrator(alarmDomain: AlarmEntity) {
        if (alarmDomain.isVibration) {
            val pattern = longArrayOf(1000, 500, 1000, 500, 1000, 500)
            vibrator?.vibrate(pattern, 0)
        }

        if (alarmDomain.soundRes != SOUND_SILENT) {
            if (alarmDomain.soundRes == SOUND_DEFAULT) playSound(ringtone, null)
            else playSound(null, alarmDomain.soundRes)
        } else Log.d("duylt", "Alarm Not Play Sound")
    }

    private fun playSound(ringtoneDefault: Uri? = null, resSound: Int? = null) {
        val isUsedSoundDefault = ringtoneDefault != null && resSound == null
        val isUsedSoundCustom = ringtoneDefault == null && resSound != null

        if (isUsedSoundDefault) {
            /*alarmRingtone = RingtoneManager.getRingtone(this, ringtoneDefault)
            alarmRingtone?.play()
            Log.d("duylt", "Check Ringtone:\nRingtone default is null: ${ringtoneDefault == null}\nAlarm Ringtone is null: ${alarmRingtone}")*/

            mediaPlayer = MediaPlayer.create(this, R.raw.voice_gentle_guitar).apply {
                isLooping = true
            }
            mediaPlayer?.start()
        }

        if (isUsedSoundCustom) {
           try {
               mediaPlayer = MediaPlayer.create(this, resSound!!).apply {
                   isLooping = true
               }
               mediaPlayer?.start()
           } catch (e: Exception) {
               val defaultSound = R.raw.voice_sound_1
               mediaPlayer = MediaPlayer.create(this, defaultSound).apply {
                   isLooping = true
               }
               mediaPlayer?.start()
           }
        }
    }

    private fun onEventSnooze() {
        val timeLongTypeAfter5M =
            addFiveMinutesToCurrentTimeLegacy().time.time
        alarmEntity?.let {
            it.setIsEnabled(false)
            viewModel.updateRecord(it) {
                AlarmReceiver.setReminderAlarm(this@RingActivity, it)
            }

            it.apply {
                id = System.currentTimeMillis()
                time = timeLongTypeAfter5M
                type = TYPE_ALARM_SNOOZE
                setIsEnabled(true)
                viewModel.saveRecordReminder(it) {
                    LoadAlarmsService.launchLoadAlarmsService(this@RingActivity)
                    AlarmReceiver.setReminderAlarm(this@RingActivity, it)
                    onBackPressed()
                }
            }
        } ?: showToastById(R.string.some_thing_went_wrong)

    }

    private fun addFiveMinutesToCurrentTimeLegacy(): Calendar {
        val calendar = Calendar.getInstance() // Lấy thời gian hiện tại
        calendar.add(Calendar.MINUTE, 5)     // Cộng thêm 5 phút
        return calendar
    }
}