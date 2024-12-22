package com.plantcare.ai.identifier.plantid.ui.component.dialog

import android.app.Activity
import android.content.Context
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.databinding.DialogLoadingBinding
import com.plantcare.ai.identifier.plantid.ui.bases.BaseDialog

class LoadingDialog(
    private val activity: Activity
) : BaseDialog<DialogLoadingBinding>(activity) {

    override fun getLayoutDialog(): Int = R.layout.dialog_loading

    override fun initViews() {
        setCancelable(false)

        setDialogWidthPercent(activity, 0.4f)
    }
}