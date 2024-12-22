package com.plantcare.ai.identifier.plantid.ui.component.bottom_sheet

import androidx.fragment.app.viewModels
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.databinding.BottomSheetAlbumsBinding
import com.plantcare.ai.identifier.plantid.domains.ImageDomain
import com.plantcare.ai.identifier.plantid.ui.bases.BaseBottomSheetDialogFragment
import com.plantcare.ai.identifier.plantid.ui.bases.ext.click
import com.plantcare.ai.identifier.plantid.ui.bases.ext.goneView
import com.plantcare.ai.identifier.plantid.ui.bases.ext.visibleView
import com.plantcare.ai.identifier.plantid.ui.component.plan_identifier.camera.ImageAdapter
import com.plantcare.ai.identifier.plantid.ui.component.plan_identifier.preview.PreviewModel
import com.plantcare.ai.identifier.plantid.utils.addItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlbumsBottomSheetFragment(
    private val onCancel: () -> Unit,
    private val onItemImageSelected: (ImageDomain, Int) -> Unit,
) : BaseBottomSheetDialogFragment<BottomSheetAlbumsBinding>() {

    companion object {
        internal const val TAG = "AlbumBottomSheetFragment"
    }

    private val viewModel: PreviewModel by viewModels()

    private var imageAdapter: ImageAdapter? = null

    override fun getLayoutFragment(): Int = R.layout.bottom_sheet_albums

    override fun initViews() {
        initRcvImage()
        getAllImages()
        isCancelable = false
    }

    override fun observerData() {
        viewModel.isLoadingImages.observe(viewLifecycleOwner) { loading ->
            if (loading) showLoadingAndHideRcv() else hideLoadingAndShowRcv()
        }

        viewModel.images.observe(viewLifecycleOwner) { listData ->
            if (listData.isEmpty()) showLayoutNullDataAndHideRcv() else {
                hideLayoutNullDataAndShowRcv()
                imageAdapter?.submitData(listData)
            }
        }
    }

    override fun onClickViews() {
        mBinding.tvCancel.click {
            onCancel.invoke()
            dismiss()
        }
    }

    private fun showLoadingAndHideRcv() = mBinding.apply {
        rcvImage.goneView()
        progressLoading.visibleView()
    }

    private fun hideLoadingAndShowRcv() = mBinding.apply {
        rcvImage.visibleView()
        progressLoading.goneView()
    }

    private fun showLayoutNullDataAndHideRcv() = mBinding.apply {
        rcvImage.goneView()
        layoutNoData.visibleView()
    }

    private fun hideLayoutNullDataAndShowRcv() = mBinding.apply {
        rcvImage.visibleView()
        layoutNoData.goneView()
    }

    private fun initRcvImage() = mBinding.rcvImage.apply {
        imageAdapter = ImageAdapter(
            onItemClick = { imageDomain, index ->
                onItemImageSelected.invoke(imageDomain, index)
                dismiss()
            }
        )

        adapter = imageAdapter
        addItemDecoration(
            resources.getDimensionPixelOffset(R.dimen.size_0),
            resources.getDimensionPixelOffset(R.dimen.size_0),
            resources.getDimensionPixelOffset(R.dimen.size_8),
            resources.getDimensionPixelOffset(R.dimen.size_8),
        )
    }

    private fun getAllImages() = viewModel.getAllFolders()
}