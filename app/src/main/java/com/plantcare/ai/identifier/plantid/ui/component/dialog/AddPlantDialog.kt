package com.plantcare.ai.identifier.plantid.ui.component.dialog

import android.content.Context
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.databinding.DialogAddPlantBinding
import com.plantcare.ai.identifier.plantid.ui.bases.BaseDialog

class AddPlantDialog(
    context: Context
): BaseDialog<DialogAddPlantBinding>(context) {
    override fun getLayoutDialog(): Int = R.layout.dialog_add_plant

    override fun initViews() {
        setCancelable(false)
    }
}