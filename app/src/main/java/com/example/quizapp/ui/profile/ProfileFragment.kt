package com.example.quizapp.ui.profile

import android.Manifest
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.quizapp.R
import com.example.quizapp.databinding.FragmentProfileBinding
import com.example.quizapp.util.GalleryPermissionTextProvider
import com.example.quizapp.util.permissionDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.IOException

private const val TAG = "ProfileFragment"

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val viewModel: ProfileViewModel by viewModels()

    private val galleryPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            viewModel.onPermissionResult(
                permission = Manifest.permission.READ_EXTERNAL_STORAGE,
                isGranted = isGranted
            )
            if (isGranted) {
                imageUriLauncher.launch(arrayOf("image/*"))
            }
        }

    private val imageUriLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            if (uri != null) {
                try {
                    val bitmap = if (Build.VERSION.SDK_INT >= 29) {
                        val source = ImageDecoder.createSource(
                            requireContext().contentResolver,
                            uri
                        )
                        ImageDecoder.decodeBitmap(source)
                    } else {
                        MediaStore.Images.Media.getBitmap(
                            requireContext().contentResolver,
                            uri
                        )
                    }
                    viewModel.saveImageToInternalStorage(bitmap)
                } catch (e: IOException) {
                    Log.e(TAG, e.message.toString())
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentProfileBinding.bind(view)

        binding.apply {
            profileImageView.setOnClickListener {

            }
            addPhotoButton.setOnClickListener {
                galleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            exitLinearLayout.setOnClickListener {
                viewModel.signOutUser()
                val action =
                    ProfileFragmentDirections.actionProfileFragmentToLoginFragment()
                findNavController().navigate(action)
            }
            profileRefreshLayout.setOnRefreshListener {
                viewModel.updateLoadingState()
                viewModel.pageRefreshed()
                profileRefreshLayout.isRefreshing = false
            }
        }

        viewModel.visiblePermissionDialogQueue.observe(viewLifecycleOwner) {
            it.forEach { permission ->
                permissionDialog(
                    context = requireContext(),
                    permissionTextProvider = when (permission) {
                        Manifest.permission.READ_EXTERNAL_STORAGE -> {
                            GalleryPermissionTextProvider()
                        }

                        else -> return@forEach
                    },
                    isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                        permission
                    ),
                    onDismiss = viewModel::dismissDialog,
                    onOkClick = {
                        galleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    },
                    onGoToAppSettingsClick = ::openAppSettings
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                viewModel.uiState.collect { event ->
                    if (!event.isLoading && (event.imageBitmap != null || event.imageUri != null)) {
                        val image = event.imageUri ?: event.imageBitmap
                        Glide.with(requireContext()).load(image).transform(CircleCrop())
                            .into(binding.profileImageView)
                    }
                    if (event.imageUri != null) {
                        viewModel.savePhotoToInternalStorageFromFirebaseUri(requireContext().filesDir.toString())
                    }
                    event.userMessage?.let {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                        viewModel.userMessageShown()
                    }
                }
            }
        }
    }
}

fun Fragment.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", requireActivity().packageName, null)
    ).also(::startActivity)
}




