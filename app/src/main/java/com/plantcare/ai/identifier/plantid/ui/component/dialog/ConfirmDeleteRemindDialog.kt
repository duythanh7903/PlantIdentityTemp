package com.plantcare.ai.identifier.plantid.ui.component.dialog

import android.content.Context
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.databinding.DialogConfirmDeleteRemindBinding
import com.plantcare.ai.identifier.plantid.ui.bases.BaseDialog
import com.plantcare.ai.identifier.plantid.ui.bases.ext.click

class ConfirmDeleteRemindDialog(
    context: Context,
    private val onEventCancel: () -> Unit,
    private val onEventDelete: () -> Unit
) : BaseDialog<DialogConfirmDeleteRemindBinding>(context) {

    override fun getLayoutDialog(): Int = R.layout.dialog_confirm_delete_remind

    override fun onClickViews() {
        mBinding.btnDelete.click {
            onEventDelete.invoke()
            dismiss()
        }

        mBinding.btnCancel.click {
            onEventCancel.invoke()
            dismiss()
        }
    }
}