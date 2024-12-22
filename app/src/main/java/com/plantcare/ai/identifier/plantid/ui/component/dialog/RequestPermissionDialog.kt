package com.plantcare.ai.identifier.plantid.ui.component.dialog

import android.content.Context
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.databinding.DialogRequestPermissionBinding
import com.plantcare.ai.identifier.plantid.ui.bases.BaseDialog
import com.plantcare.ai.identifier.plantid.ui.bases.ext.click

class RequestPermissionDialog(
    context: Context,
    private val titlePermission: String,
    private val desPermission: String,
    private val onGoToSetting: () -> Unit
): BaseDialog<DialogRequestPermissionBinding>(context) {

    override fun getLayoutDialog(): Int = R.layout.dialog_request_permission

    override fun initViews() {
        mBinding.tvTitlePermission.text = titlePermission
        mBinding.tvDescription.text = desPermission
    }

    override fun onClickViews() {
        mBinding.btnGoToSetting.click {
            onGoToSetting.invoke()
            dismiss()
        }
    }

}