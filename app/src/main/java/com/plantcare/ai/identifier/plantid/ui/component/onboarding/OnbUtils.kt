package com.plantcare.ai.identifier.plantid.ui.component.onboarding

import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.domains.GuideModel

object OnbUtils {

    fun getListIntro(): MutableList<GuideModel> = mutableListOf(
        GuideModel(R.drawable.img_onb_1, R.string.title_onb_1, R.string.des_onb_1),
        GuideModel(R.drawable.img_onb_2, R.string.title_onb_2, R.string.des_onb_2),
        GuideModel(R.drawable.img_onb_3, R.string.title_onb_3, R.string.des_onb_3),
        GuideModel(R.drawable.img_onb_4, R.string.title_onb_4, R.string.des_onb_4),
    )

}