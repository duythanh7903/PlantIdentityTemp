package com.plantcare.ai.identifier.plantid.ui.component.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.databinding.DialogChooseSoundBinding
import com.plantcare.ai.identifier.plantid.domains.SoundDomain
import com.plantcare.ai.identifier.plantid.ui.bases.BaseDialog
import com.plantcare.ai.identifier.plantid.ui.bases.ext.click
import com.plantcare.ai.identifier.plantid.ui.component.remind.SoundUtils.getAllSound
import com.plantcare.ai.identifier.plantid.ui.component.remind.adapter.SoundAdapter

class ChooseSoundDialog(
    private val context: Context,
    private val activity: Activity,
    private val soundDomainCurrent: SoundDomain,
    private val onItemSoundChosen: (soundDomain: SoundDomain, index: Int) -> Unit
) : BaseDialog<DialogChooseSoundBinding>(context) {

    private var soundAdapter: SoundAdapter? = null

    override fun getLayoutDialog(): Int = R.layout.dialog_choose_sound

    @SuppressLint("LogNotTimber")
    override fun initViews() {
        initRcvSound()
        showTxtSoundCurrent()

        setDialogWidthPercent(activity, 0.75f)
    }

    override fun onClickViews() {
        mBinding.icUp.click { cancel() }
    }

    private fun initRcvSound() = mBinding.rcvSound.apply {
        soundAdapter = SoundAdapter(onItemClick = { index, sound ->
            onItemSoundChosen.invoke(sound, index)
            dismiss()
        }).apply {
            submitData(getAllSound(this@ChooseSoundDialog.context))
            idSoundSelected = soundDomainCurrent.id
        }

        adapter = soundAdapter
    }

    private fun showTxtSoundCurrent() {
        mBinding.tvSoundName.text = soundDomainCurrent.name
    }
}