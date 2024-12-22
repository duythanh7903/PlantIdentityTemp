package com.plantcare.ai.identifier.plantid.ui.component.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.duylt.lib.wheel_picker.WheelAdapter
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.databinding.DialogAddRemindBinding
import com.plantcare.ai.identifier.plantid.domains.SoundDomain
import com.plantcare.ai.identifier.plantid.ui.bases.BaseDialog
import com.plantcare.ai.identifier.plantid.ui.bases.ext.click
import com.plantcare.ai.identifier.plantid.ui.bases.ext.goneView
import com.plantcare.ai.identifier.plantid.ui.bases.ext.visibleView
import com.plantcare.ai.identifier.plantid.ui.component.remind.SoundUtils.getAllSound
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale

class AddRemindDialog(
    private val activity: Activity, private val onCancel: () -> Unit, private val onSave: (
        hour: Int, minute: Int, sound: Int, isVibrationOn: Boolean, title: String, des: String,
        isMChecked: Boolean,
        isTChecked: Boolean,
        isWChecked: Boolean,
        isThChecked: Boolean,
        isFChecked: Boolean,
        isSaChecked: Boolean,
        isSChecked: Boolean,
    ) -> Unit
) : BaseDialog<DialogAddRemindBinding>(activity) {

    private var soundDomainCurrent: SoundDomain = SoundDomain()

    override fun getLayoutDialog(): Int = R.layout.dialog_add_remind

    override fun initViews() {
        initTimePicker()
        setDefaultColorForTxtDayOfWeek()
        initSoundCurrent()
        setDefaultDayOfWeek()
        mBinding.tvSoundName.isSelected = true
        setCanceledOnTouchOutside(false)
        setCancelable(false)
    }

    private fun setDefaultDayOfWeek() {
        val dayOfWeek = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val dayOfWeekApi0 = getCurrentDayOfWeek()
            dayOfWeekApi0
        } else {
            val dayOfWeekLegacy = getCurrentDayOfWeekLegacy()
            dayOfWeekLegacy
        }

        when (dayOfWeek) {
            "Sunday" -> onEventChangeUiBtnDayOfWeek(mBinding.btnS, mBinding.tvS)
            "Monday" -> onEventChangeUiBtnDayOfWeek(mBinding.btnM, mBinding.tvM)
            "Tuesday" -> onEventChangeUiBtnDayOfWeek(mBinding.btnT, mBinding.tvT)
            "Wednesday" -> onEventChangeUiBtnDayOfWeek(mBinding.btnW, mBinding.tvW)
            "Thursday" -> onEventChangeUiBtnDayOfWeek(mBinding.btnTh, mBinding.tvTh)
            "Friday" -> onEventChangeUiBtnDayOfWeek(mBinding.btnF, mBinding.tvF)
            "Saturday" -> onEventChangeUiBtnDayOfWeek(mBinding.btnSa, mBinding.tvSa)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDayOfWeek(): String {
        val today = LocalDate.now()
        // Lấy tên thứ trong tuần (ngắn gọn hoặc đầy đủ)
        return today.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("en"))
    }

    private fun getCurrentDayOfWeekLegacy(): String {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        // Map số ngày trong tuần (1-7) sang tên ngày
        val days = arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        return days[dayOfWeek - 1]
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

        mBinding.btnSound.click {
            ChooseSoundDialog(context = context,
                activity = activity,
                soundDomainCurrent = soundDomainCurrent,
                onItemSoundChosen = { sound, _ ->
                    soundDomainCurrent = sound
                    mBinding.tvSoundName.text = soundDomainCurrent.name
                }).show()
        }

        mBinding.layoutContainer.click {
            hideKeyboard(context, mBinding.edtDescription)
            hideKeyboard(context, mBinding.edtTitle)
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
        val hourDefaultByDocument = 8
        val minuteDefaultByDocument = 30
        mBinding.numberPickerHour.scrollTo(hourDefaultByDocument)
        mBinding.numberPickerMinute.scrollTo(minuteDefaultByDocument)
    }

    private fun initSoundCurrent() {
        soundDomainCurrent = getAllSound(context)[0]
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
