package com.plantcare.ai.identifier.plantid.ui.component.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.duylt.lib.wheel_picker.WheelAdapter
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.app.AppConstants.SOUND_DEFAULT
import com.plantcare.ai.identifier.plantid.app.AppConstants.SOUND_SILENT
import com.plantcare.ai.identifier.plantid.data.database.entities.AlarmEntity
import com.plantcare.ai.identifier.plantid.data.database.entities.AlarmEntity.FRI
import com.plantcare.ai.identifier.plantid.data.database.entities.AlarmEntity.MON
import com.plantcare.ai.identifier.plantid.data.database.entities.AlarmEntity.SAT
import com.plantcare.ai.identifier.plantid.data.database.entities.AlarmEntity.SUN
import com.plantcare.ai.identifier.plantid.data.database.entities.AlarmEntity.THURS
import com.plantcare.ai.identifier.plantid.data.database.entities.AlarmEntity.TUES
import com.plantcare.ai.identifier.plantid.data.database.entities.AlarmEntity.WED
import com.plantcare.ai.identifier.plantid.databinding.DialogUpdateRemindBinding
import com.plantcare.ai.identifier.plantid.domains.SoundDomain
import com.plantcare.ai.identifier.plantid.ui.bases.BaseDialog
import com.plantcare.ai.identifier.plantid.ui.bases.ext.click
import com.plantcare.ai.identifier.plantid.ui.bases.ext.goneView
import com.plantcare.ai.identifier.plantid.ui.bases.ext.visibleView
import com.plantcare.ai.identifier.plantid.ui.component.remind.SoundUtils.getAllSound
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UpdateRemindDialog(
    private val activity: Activity,
    private val alarmEntity: AlarmEntity,
    private val soundRes: Int,
    private val onCancel: () -> Unit, private val onSave: (
        hour: Int, minute: Int, sound: Int, isVibrationOn: Boolean, title: String, des: String,
        isMChecked: Boolean,
        isTChecked: Boolean,
        isWChecked: Boolean,
        isThChecked: Boolean,
        isFChecked: Boolean,
        isSaChecked: Boolean,
        isSChecked: Boolean,
    ) -> Unit
) : BaseDialog<DialogUpdateRemindBinding>(activity) {

    private var soundDomainCurrent: SoundDomain = SoundDomain()

    override fun getLayoutDialog(): Int = R.layout.dialog_update_remind

    @SuppressLint("LogNotTimber")
    override fun initViews() {
        initTimePicker()
        setDefaultColorForTxtDayOfWeek()
        initSoundCurrent()
        setDefaultDayOfWeek()
        setCanceledOnTouchOutside(false)
        setCancelable(false)
    }

    private fun setDefaultDayOfWeek() {
        if (alarmEntity.getDay(MON)) onEventChangeUiBtnDayOfWeek(mBinding.btnM, mBinding.tvM)
        if (alarmEntity.getDay(TUES)) onEventChangeUiBtnDayOfWeek(mBinding.btnT, mBinding.tvT)
        if (alarmEntity.getDay(WED)) onEventChangeUiBtnDayOfWeek(mBinding.btnW, mBinding.tvW)
        if (alarmEntity.getDay(THURS)) onEventChangeUiBtnDayOfWeek(mBinding.btnTh, mBinding.tvTh)
        if (alarmEntity.getDay(FRI)) onEventChangeUiBtnDayOfWeek(mBinding.btnF, mBinding.tvF)
        if (alarmEntity.getDay(SAT)) onEventChangeUiBtnDayOfWeek(mBinding.btnSa, mBinding.tvSa)
        if (alarmEntity.getDay(SUN)) onEventChangeUiBtnDayOfWeek(mBinding.btnS, mBinding.tvS)

        val soundName = when (alarmEntity.soundRes) {
            SOUND_DEFAULT -> context.getString(R.string.txt_default)
            SOUND_SILENT -> context.getString(R.string.silent)
            R.raw.voice_air_horn -> context.getString(R.string.voice_sound_1)
            R.raw.voice_alarm_clock -> context.getString(R.string.voice_sound_2)
            R.raw.voice_cal_ringtone_1 -> context.getString(R.string.voice_sound_3)
            R.raw.voice_cell_ringtone_2 -> context.getString(R.string.voice_sound_4)
            R.raw.voice_classic_alarm -> context.getString(R.string.voice_sound_5)
            R.raw.voice_discord_call -> context.getString(R.string.voice_sound_6)
            R.raw.voice_gentle_guitar -> context.getString(R.string.voice_sound_7)
            R.raw.voice_kalimba -> context.getString(R.string.voice_sound_8)
            R.raw.voice_marimba_ringtone_5 -> context.getString(R.string.voice_sound_9)
            R.raw.voice_marimba_ringtone_9 -> context.getString(R.string.voice_sound_10)
            R.raw.voice_original_phone_ringtone -> context.getString(R.string.voice_sound_11)
            R.raw.voice_ringtone -> context.getString(R.string.voice_sound_12)
            R.raw.voice_ringtone_2 -> context.getString(R.string.voice_sound_13)
            R.raw.voice_ringtone_3 -> context.getString(R.string.voice_sound_14)
            R.raw.voice_ringtone_6 -> context.getString(R.string.voice_sound_15)
            R.raw.voice_ringtone_bubbly_bubbles -> context.getString(R.string.voice_sound_16)
            R.raw.voice_ringtone_for_mobile_phone_with_cheerful_mood -> context.getString(R.string.voice_sound_17)
            R.raw.voice_ringtone_jungle -> context.getString(R.string.voice_sound_18)
            R.raw.voice_rooster -> context.getString(R.string.voice_sound_19)
            else -> context.getString(R.string.voice_sound_20)
        }
        mBinding.tvSoundName.text = soundName
        mBinding.tvSoundName.isSelected = true

        mBinding.icSwVibration.isActivated = alarmEntity.isEnabled
        mBinding.edtTitle.setText(alarmEntity.label)
        mBinding.edtDescription.setText(alarmEntity.description)
    }

    override fun onClickViews() {
        mBinding.btnM.click {
            onEventChangeUiBtnDayOfWeek(it, mBinding.tvM)
        }

        mBinding.btnT.click {
            onEventChangeUiBtnDayOfWeek(it, mBinding.tvT)
        }

        mBinding.btnW.click {
            onEventChangeUiBtnDayOfWeek(it, mBinding.tvW)
        }

        mBinding.btnTh.click {
            onEventChangeUiBtnDayOfWeek(it, mBinding.tvTh)
        }

        mBinding.btnF.click {
            onEventChangeUiBtnDayOfWeek(it, mBinding.tvF)
        }

        mBinding.btnSa.click {
            onEventChangeUiBtnDayOfWeek(it, mBinding.tvSa)
        }

        mBinding.btnS.click {
            onEventChangeUiBtnDayOfWeek(it, mBinding.tvS)
        }

        mBinding.btnVibration.click {
            mBinding.icSwVibration.isActivated = !mBinding.icSwVibration.isActivated
        }

        mBinding.btnCancel.click {
            onCancel.invoke()
            dismiss()
        }

        mBinding.btnSave.click {
            onEventSave()
        }

        mBinding.layoutContainer.click {
            hideKeyboard(context, mBinding.edtDescription)
            hideKeyboard(context, mBinding.edtTitle)
        }

        mBinding.btnSound.click {
            Log.d("duylt", "Sound Domain Current: $soundDomainCurrent")

            ChooseSoundDialog(context = context,
                activity = activity,
                soundDomainCurrent = soundDomainCurrent,
                onItemSoundChosen = { sound, _ ->
                    soundDomainCurrent = sound
                    mBinding.tvSoundName.text = soundDomainCurrent.name
                }).show()
        }

        mBinding.edtDescription.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard(context, mBinding.edtDescription)
                Log.d("duylt", "Hide Keyboard!")
            }

            Log.d("duylt", "Id Action Edt: ${actionId}")

            true
        }
    }

    private fun hideKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        view.clearFocus()
    }

    private fun initTimePicker() = mBinding.apply {
        numberPickerHour.apply {
            // Set rounded wrap enable
            setWrapSelectorWheel(true)
            // Set wheel item count
            setWheelItemCount(3)
            // Set wheel max index
            setMaxValue(23)
            // Set wheel min index
            setMinValue(0)
            // Set selected text color
            setSelectedTextColor(R.color.txt_dark)
            // Set unselected text color
            setUnselectedTextColor(R.color.txt_description)
            // Set user defined adapter
            setAdapter(TimePickerAdapter())
        }

        numberPickerMinute.apply {
            // Set rounded wrap enable
            setWrapSelectorWheel(true)
            // Set wheel item count
            setWheelItemCount(3)
            // Set wheel max index
            setMaxValue(59)
            // Set wheel min index
            setMinValue(0)
            // Set selected text color
            setSelectedTextColor(R.color.txt_dark)
            // Set unselected text color
            setUnselectedTextColor(R.color.txt_description)
            // Set user defined adapter
            setAdapter(TimePickerAdapter())
        }

        setDefaultValueSpinWheelTime()
    }

    private fun setDefaultValueSpinWheelTime() {
        // this is 08:30 am

        val time = alarmEntity.time
        val timePair = convertLongToTimeString(time)

        val hourDefaultByDocument = timePair.first.toInt()
        val minuteDefaultByDocument = timePair.second.toInt()
        mBinding.numberPickerHour.scrollTo(hourDefaultByDocument)
        mBinding.numberPickerMinute.scrollTo(minuteDefaultByDocument)
    }

    private fun convertLongToTimeString(time: Long): Pair<String, String> {
        val date = Date(time) // Tạo đối tượng Date từ timestamp
        val format = SimpleDateFormat("HH:mm", Locale.getDefault()) // Định dạng HH:mm
        val arr = format.format(date).split(":")
        return Pair(arr[0], arr[1]) // Trả về chuỗi định dạng
    }

    private fun initSoundCurrent() {
        soundDomainCurrent = getAllSound(context).find { it.src == soundRes } ?: soundDomainCurrent
    }

    private fun onEventChangeUiBtnDayOfWeek(view: View?, tv: TextView) {
        if (view != null) {
            if (view.isActivated) {
                view.isActivated = false
                changeTxtDayOfWeekColorUnActive(tv)
            } else {
                view.isActivated = true
                changeTxtDayOfWeekColorActive(tv)
            }
        }
    }

    private fun changeTxtDayOfWeekColorActive(tv: TextView) =
        tv.setTextColor(Color.parseColor("#FFFFFF"))

    private fun changeTxtDayOfWeekColorUnActive(tv: TextView) =
        tv.setTextColor(Color.parseColor("#7DC448"))

    private fun setDefaultColorForTxtDayOfWeek() = mBinding.apply {
        tvM.setTextColor(Color.parseColor("#7DC448"))
        tvT.setTextColor(Color.parseColor("#7DC448"))
        tvW.setTextColor(Color.parseColor("#7DC448"))
        tvTh.setTextColor(Color.parseColor("#7DC448"))
        tvF.setTextColor(Color.parseColor("#7DC448"))
        tvSa.setTextColor(Color.parseColor("#7DC448"))
        tvS.setTextColor(Color.parseColor("#7DC448"))
    }

    private fun onEventSave() {
        val hour = mBinding.numberPickerHour.getCurrentItem()
        val minute = mBinding.numberPickerMinute.getCurrentItem()
        val soundIdFromRawFolder = soundDomainCurrent.src
        val isVibrationOn = mBinding.icSwVibration.isActivated
        val title = mBinding.edtTitle.text.toString().trim()
        val des = mBinding.edtDescription.text.toString().trim()

        val isEmptyInput = !isSelectedDayOfWeek() || title.isEmpty()
        if (isEmptyInput) {
            if (!isSelectedDayOfWeek()) {
                mBinding.tvPleaseChooseAtLeast1Day.visibleView()
            } else mBinding.tvPleaseChooseAtLeast1Day.goneView()

            if (title.isEmpty()) {
                mBinding.tvPleasaeTypeYourTitle.visibleView()
            } else mBinding.tvPleasaeTypeYourTitle.goneView()
        } else {

            mBinding.tvPleaseChooseAtLeast1Day.goneView()
            mBinding.tvPleasaeTypeYourTitle.goneView()

            onSave.invoke(
                hour.toInt(),
                minute.toInt(),
                soundIdFromRawFolder,
                isVibrationOn,
                title,
                des,
                mBinding.tvM.isActivated,
                mBinding.tvT.isActivated,
                mBinding.tvW.isActivated,
                mBinding.tvTh.isActivated,
                mBinding.tvF.isActivated,
                mBinding.tvSa.isActivated,
                mBinding.tvS.isActivated,
            )
            dismiss()
        }
    }

    private fun isSelectedDayOfWeek() = mBinding.tvM.isActivated ||
            mBinding.tvT.isActivated ||
            mBinding.tvF.isActivated ||
            mBinding.tvW.isActivated ||
            mBinding.tvTh.isActivated ||
            mBinding.tvF.isActivated ||
            mBinding.tvSa.isActivated ||
            mBinding.tvS.isActivated

    class TimePickerAdapter : WheelAdapter() {
        //get item value based on item position in wheel
        override fun getValue(position: Int): String = position.toString()

        //get item position based on item string value
        override fun getPosition(vale: String): Int = 0

        //return a string with the approximate longest text width, for supporting WRAP_CONTENT
        override fun getTextWithMaximumLength(): String = "00"

        //return the maximum index
        override fun getMaxValidIndex(): Int = Integer.MAX_VALUE

        //return the minimum index
        override fun getMinValidIndex(): Int = Integer.MIN_VALUE
    }

}