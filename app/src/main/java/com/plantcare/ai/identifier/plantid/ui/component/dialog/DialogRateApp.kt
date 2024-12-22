package com.plantcare.ai.identifier.plantid.ui.component.dialog

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.app.AppConstants
import com.plantcare.ai.identifier.plantid.databinding.DialogRateAppBinding
import com.plantcare.ai.identifier.plantid.ui.bases.BaseDialog
import com.plantcare.ai.identifier.plantid.ui.bases.ext.click
import com.plantcare.ai.identifier.plantid.utils.EasyPreferences
import com.plantcare.ai.identifier.plantid.utils.EasyPreferences.set


class DialogRateApp(context: Context, val onRating: () -> Unit) : BaseDialog<DialogRateAppBinding>(
    context, R.style.BaseDialog
) {

    private var isCanClickRate = true

    override fun getLayoutDialog(): Int {
        return R.layout.dialog_rate_app
    }

    override fun onClickViews() {
        mBinding.icCloseDialog.click {
            dismiss()
        }

        mBinding.star1.click {
            onEventRate(1)
        }

        mBinding.star2.click {
            onEventRate(2)
        }

        mBinding.star3.click {
            onEventRate(3)
        }

        mBinding.star4.click {
            onEventRate(4)
        }

        mBinding.star5.click {
            onEventRate(5)
        }
    }

    private fun onEventRate(star: Int) {
        if (!isCanClickRate) return

        when (star) {
            1 -> {
                mBinding.star1.isActivated = true
                mBinding.star2.isActivated = false
                mBinding.star3.isActivated = false
                mBinding.star4.isActivated = false
                mBinding.star5.isActivated = false
            }

            2 -> {
                mBinding.star1.isActivated = true
                mBinding.star2.isActivated = true
                mBinding.star3.isActivated = false
                mBinding.star4.isActivated = false
                mBinding.star5.isActivated = false
            }

            3 -> {
                mBinding.star1.isActivated = true
                mBinding.star2.isActivated = true
                mBinding.star3.isActivated = true
                mBinding.star4.isActivated = false
                mBinding.star5.isActivated = false
            }

            4 -> {
                mBinding.star1.isActivated = true
                mBinding.star2.isActivated = true
                mBinding.star3.isActivated = true
                mBinding.star4.isActivated = true
                mBinding.star5.isActivated = false
            }

            5 -> {
                mBinding.star1.isActivated = true
                mBinding.star2.isActivated = true
                mBinding.star3.isActivated = true
                mBinding.star4.isActivated = true
                mBinding.star5.isActivated = true
            }
        }

        if (star < 4) {
            Toast.makeText(
                context, context.getString(R.string.txt_thanks_you_for_rating), Toast.LENGTH_SHORT
            ).show()
            Handler(Looper.getMainLooper()).postDelayed({
                dismiss()
            }, 500)
        } else {
            isCanClickRate = false
            Handler(Looper.getMainLooper()).postDelayed({
                onRating.invoke()
                dismiss()
            }, 500)
        }

        EasyPreferences.defaultPrefs(context)[AppConstants.KEY_SET_SHOW_DIALOG_RATE] = true
    }
}