package ru.createtogether.fragment_photo.presenter

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.IntRange
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.createtogether.common.helpers.MainActions
import ru.createtogether.common.helpers.Status
import ru.createtogether.common.helpers.baseFragment.BaseFragment
import ru.createtogether.common.helpers.extension.*
import ru.createtogether.feature_photo.adapter.PhotoAdapter
import ru.createtogether.feature_photo.helpers.PhotoAdapterListener
import ru.createtogether.feature_photo_utils.PhotoModel
import ru.createtogether.feature_photo_utils.helpers.PhotoConstants
import ru.createtogether.fragment_photo.R
import ru.createtogether.fragment_photo.customView.SwipeBackLayout
import ru.createtogether.fragment_photo.databinding.FragmentPhotoBinding
import ru.createtogether.fragment_photo.presenter.viewModel.PhotoViewModel
import kotlin.math.roundToInt


@AndroidEntryPoint
class PhotoFragment : BaseFragment(R.layout.fragment_photo) {
    private val binding: FragmentPhotoBinding by viewBinding()

    override val viewModel: PhotoViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        configureViews()
        initListeners()
        initObservers()

        loadImage(photos[position])
    }

    private fun initObservers() {
        observePhotos()
    }

    private fun observePhotos() {
        viewModel.photos.observe(viewLifecycleOwner) {
            initPhotoSmallAdapter(images = it.map { it.copy() }.toTypedArray())
        }
    }

    private fun initData() {
        viewModel.photos.value = photos.toList()
    }

    private fun configureViews() {
        setPaddingContent()
        (requireActivity() as MainActions).changeNavigationBarColor(R.color.black)
        setBackgroundAlpha(PhotoConstants.FULLY_VISIBLE)
    }

    private fun setBackgroundAlpha(
        @IntRange(
            from = 0,
            to = PhotoConstants.FULLY_VISIBLE.toLong()
        ) alpha: Int
    ) {
        binding.root.background.alpha = alpha
    }

    private fun setPaddingContent() {
        binding.clContainer.setPaddingTop()
    }

    private fun initPhotoSmallAdapter(images: Array<PhotoModel>) {
        binding.rvPhoto.initAdapter(
            images,
            PhotoAdapter::class.java,
            object : PhotoAdapterListener {
                override fun onClick(item: PhotoModel) {
                    onPhotoClick(item)
                }
            })
    }

    private fun onPhotoClick(photo: PhotoModel) {
        viewModel.setSelectedPhoto(photo = photo)
        loadImage(photo)
    }

    private fun loadImage(image: PhotoModel) {
        lifecycleScope.launch {
            val result = binding.photoView.loadImage(image.url)
            when {
                result.isSuccess -> {
                    binding.progressBar.show()
                    binding.llErrorContainer.gone()
                }
                else -> {
                    binding.progressBar.gone()
                    binding.llErrorContainer.show()
                }
            }
        }
    }

    private fun initListeners() {
        setSwipeBack()
        setBackPressed()
    }

    private fun setBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBack()
            }
        })
    }

    private fun setSwipeBack() {
        binding.swipeBackLayout.setSwipeBackListener(object : SwipeBackLayout.OnSwipeBackListener {
            override fun onViewPositionChanged(
                mView: View,
                swipeBackFraction: Float,
                swipeBackFactor: Float
            ) {
                setBackgroundAlpha(viewModel.getStateTransparent(swipeBackFraction = swipeBackFraction))
            }

            override fun onViewSwipeFinished(mView: View, isEnd: Boolean) {
                if (isEnd)
                    onBack()
            }
        })
    }

    fun onRefreshClick() {
        loadImage(viewModel.getSelectedPhoto())
    }

    fun onCloseClick() {
        onBack()
    }

    override fun onDestroy() {
        super.onDestroy()
        (requireActivity() as MainActions).changeNavigationBarColor(R.color.white)
    }

    private val photos by lazy {
        requireArguments().getSerializable(PARAM_PHOTOS) as Array<PhotoModel>
    }

    private val position by lazy {
        requireArguments().getInt(PARAM_POSITION)
    }

    companion object {
        private const val PARAM_PHOTOS = "photos"
        private const val PARAM_POSITION = "position"
        fun getInstance(photos: Array<PhotoModel>, position: Int): PhotoFragment {
            return PhotoFragment().apply {
                arguments = bundleOf(PARAM_PHOTOS to photos, PARAM_POSITION to position)
            }
        }
    }
}