package com.plantcare.ai.identifier.plantid.ui.component.dialog

import android.content.Context
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.databinding.DialogNoInternetBinding
import com.plantcare.ai.identifier.plantid.ui.bases.BaseDialog
import com.plantcare.ai.identifier.plantid.ui.bases.ext.click

class WrongDialog(
    context: Context,
    private val tryAgain: () -> Unit,
    private val backToHome: () -> Unit
) : BaseDialog<DialogNoInternetBinding>(context) {
    override fun getLayoutDialog(): Int = R.layout.dialog_no_internet

    override fun onClickViews() {
        mBinding.btnTryAgain.click {
            tryAgain.invoke()
            dismiss()
        }

        mBinding.btnBackToHome.click {
            backToHome.invoke()
            dismiss()
        }
    }
}