package com.plantcare.ai.identifier.plantid.ui.component.main.bottom_bar

import android.content.Intent
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ads.control.ads.ITGAd
import com.ads.control.funtion.AdCallback
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.plantcare.ai.identifier.plantid.BuildConfig
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.ads.RemoteConfigUtils
import com.plantcare.ai.identifier.plantid.app.AppConstants
import com.plantcare.ai.identifier.plantid.databinding.FragmentMyPlantBinding
import com.plantcare.ai.identifier.plantid.domains.PlantDomain
import com.plantcare.ai.identifier.plantid.ui.bases.BaseFragment
import com.plantcare.ai.identifier.plantid.ui.bases.ext.goneView
import com.plantcare.ai.identifier.plantid.ui.bases.ext.isNetwork
import com.plantcare.ai.identifier.plantid.ui.bases.ext.visibleView
import com.plantcare.ai.identifier.plantid.ui.component.dialog.ConfirmDeleteRemindDialog
import com.plantcare.ai.identifier.plantid.ui.component.main.bottom_bar.myplant.MyPlantDetailActivity
import com.plantcare.ai.identifier.plantid.ui.component.main.bottom_bar.myplant.adapter.MyPlantAdapter
import com.plantcare.ai.identifier.plantid.ui.component.main.bottom_bar.viewmodel.MyPlantViewModel
import com.plantcare.ai.identifier.plantid.utils.toListPlantDomain
import com.plantcare.ai.identifier.plantid.utils.toPlantEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyPlantFragment : BaseFragment<FragmentMyPlantBinding>() {

    private val viewModel: MyPlantViewModel by viewModels()

    private var myPlantAdapter: MyPlantAdapter? = null

    override fun getLayoutFragment(): Int = R.layout.fragment_my_plant

    override fun initViews() {
        initRcvMyPlant()
    }

    private fun initAds() {
        activity?.let { act->
            if (RemoteConfigUtils.getOnNativeMyPlant() && isNetwork(act)) {
                ITGAd.getInstance().loadNativeAd(act,
                    BuildConfig.native_my_plant,
                    R.layout.layout_native_small_result,
                    mBinding.frAds,
                    mBinding.layoutShimmer.shimmerNativeLarge,
                    object : AdCallback() {

                        override fun onAdFailedToLoad(i: LoadAdError?) {
                            super.onAdFailedToLoad(i)
                            mBinding.frAds.removeAllViews()
                            mBinding.relayAds.goneView()
                        }

                        override fun onAdFailedToShow(adError: AdError?) {
                            super.onAdFailedToShow(adError)
                            mBinding.frAds.removeAllViews()
                            mBinding.relayAds.goneView()
                        }

                    })
            }else{
                mBinding.frAds.removeAllViews()
                mBinding.relayAds.goneView()
            }
        }

    }

    override fun observerData() {
        lifecycleScope.launch {
            viewModel.records.collect { plants ->
                plants?.let {
                    if (it.isNotEmpty()) {
                        hideUiNoDataAndShowRcv()
                        myPlantAdapter?.submitData(plants.toListPlantDomain())
                        mBinding.relayAds.visibleView()
                        initAds()
                    } else {
                        mBinding.relayAds.goneView()
                        showUiNoDataAndHideRcv()
                    }
                } ?: run {
                    mBinding.relayAds.goneView()
                    showUiNoDataAndHideRcv()
                }
            }
        }

    }

    private fun initRcvMyPlant() = mBinding.apply {
        myPlantAdapter = MyPlantAdapter(onClickItemPlant = { plantDomain, index ->
            onItemPlantDomainClick(plantDomain, index)
        }, onDeleteMyPlant = { plantDomain, index ->
            onDeleteMyPlant(plantDomain, index)
        })

        rvMyPlant.adapter = myPlantAdapter
    }

    private fun onItemPlantDomainClick(plantDomain: PlantDomain, index: Int) {
        val intent = Intent(requireContext(), MyPlantDetailActivity::class.java)
        intent.putExtra(AppConstants.KEY_PLANT, plantDomain)
        startActivity(intent)
    }

    private fun showUiNoDataAndHideRcv() = mBinding.apply {
        layoutNoData.visibleView()
        rvMyPlant.goneView()
    }

    private fun hideUiNoDataAndShowRcv() = mBinding.apply {
        layoutNoData.goneView()
        rvMyPlant.visibleView()
    }

    private fun onDeleteMyPlant(plantDomain: PlantDomain, index: Int) =
        ConfirmDeleteRemindDialog(
            context = requireContext(),
            onEventCancel = { },
            onEventDelete = {
                viewModel.deletePlantEntity(plantDomain.toPlantEntity())
            }
        ).show()
}