package com.beballer.beballer.ui.player.create_profile.add_profile

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.utils.AppUtils
import com.beballer.beballer.utils.BaseCustomDialog
import com.beballer.beballer.utils.BindingUtils
import com.beballer.beballer.databinding.FragmentAddProfilePictureBinding
import com.beballer.beballer.databinding.VideoImagePickerDialogBoxBinding
import com.beballer.beballer.ui.player.create_profile.choose_avtar.ChooseAvatarFragment
import com.github.dhaval2404.imagepicker.util.FileUtil
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException


@AndroidEntryPoint
class AddProfilePictureFragment : BaseFragment<FragmentAddProfilePictureBinding>() {
    private val viewModel: AddProfilePictureFragmentVM by viewModels()
    private var imageDialog: BaseCustomDialog<VideoImagePickerDialogBoxBinding>? = null
    private var multipartImage: MultipartBody.Part? = null
    private var photoFile2: File? = null
    private var photoURI: Uri? = null
    private var uri: Uri? = null

    override fun getLayoutResource(): Int {
        return R.layout.fragment_add_profile_picture
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // observer
        initObserver()
        // click
        initOnClick()
    }

    /** handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) { view ->
            when (view?.id) {
                R.id.cancelImage -> findNavController().popBackStack()

                R.id.profileImage, R.id.profileImageFab -> imageDialog()

                R.id.clSkip -> {
                    val bundle = getUserBundle()
                    BindingUtils.navigateWithSlide(
                        findNavController(), R.id.navigateChooseAvatarFragment, bundle
                    )
                }

                R.id.btnNext -> {
                    if (multipartImage != null) {
                        ChooseAvatarFragment.sendMultipartImage = multipartImage
                        ChooseAvatarFragment.sendUri = uri
                        val bundle = getUserBundle()
                        BindingUtils.navigateWithSlide(
                            findNavController(), R.id.navigateChooseAvatarFragment, bundle
                        )
                    } else {
                        showInfoToast("Please pick image")
                    }
                }
            }
        }
    }
    // get bundle
    private fun getUserBundle(): Bundle {
        return Bundle().apply {
            putString("firstName", arguments?.getString("firstName") ?: "")
            putString("lastName", arguments?.getString("lastName") ?: "")
            putString("userName", arguments?.getString("userName") ?: "")
            putString("countryCode", arguments?.getString("countryCode") ?: "")
            putString("userDob", arguments?.getString("userDob") ?: "")
            putString("userGender", arguments?.getString("userGender") ?: "")
            putString("userHeight", arguments?.getString("userHeight") ?: "")
        }

    }


    /** handle api response **/
    private fun initObserver() {


    }


    /**** Edit date and time dialog  handel ***/
    private fun imageDialog() {
        imageDialog = BaseCustomDialog(requireContext(), R.layout.video_image_picker_dialog_box) {
            when (it.id) {
                R.id.tvCamera, R.id.imageCamera -> {
                    if (!BindingUtils.hasPermissions(
                            requireContext(), BindingUtils.permissions
                        )
                    ) {
                        permissionResultLauncher1.launch(BindingUtils.permissions)
                    } else {
                        // camera
                        openCameraIntent()
                    }
                    imageDialog!!.dismiss()
                }

                R.id.imageGallery, R.id.tvGallery -> {
                    if (!BindingUtils.hasPermissions(
                            requireContext(), BindingUtils.permissions
                        )
                    ) {
                        permissionResultLauncher.launch(BindingUtils.permissions)

                    } else {
                        galleryImagePicker()

                    }
                    imageDialog!!.dismiss()
                }

            }
        }
        imageDialog!!.create()
        imageDialog!!.show()

    }

    /**** Gallery permission  ***/
    private var allGranted = false
    private val permissionResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            for (it in permissions.entries) {
                it.key
                val isGranted = it.value
                allGranted = isGranted
            }
            when {
                allGranted -> {
                    galleryImagePicker()
                }

                else -> {
                    showInfoToast("Permission Denied")
                }
            }
        }

    /*** open gallery ***/
    private fun galleryImagePicker() {
        val pictureActionIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI).apply {
                type = "image/*"  // Restrict to images only
            }
        resultLauncherGallery.launch(pictureActionIntent)
    }

    /*** gallery launcher ***/
    private var resultLauncherGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val imageUri = data?.data
                imageUri?.let {
                    uri =it
                    binding.buttonCheck = true
                    multipartImage = convertMultipartPartGal(it)
                    binding.profileImage.setImageURI(imageUri)

                }
            }
        }


    private val permissionResultLauncher1: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.all { it.value }
            if (allGranted) {
                openCameraIntent()
            } else {
                showInfoToast("Permission Denied")
            }

        }

    /**** open camera ***/
    private fun openCameraIntent() {
        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (pictureIntent.resolveActivity(requireContext().packageManager) != null) {
            try {
                photoFile2 = AppUtils.createImageFile1(requireContext())
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
            if (photoFile2 != null) {
                photoURI = FileProvider.getUriForFile(
                    requireContext(),
                    "com.beballer.beballer.fileProvider",
                    photoFile2!!
                )
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                resultLauncherCamera.launch(pictureIntent)
            }else{
                Log.d("TAG", "openCameraIntent: ")
            }
        }
        else{
            Log.d("TAG", "openCameraIntent: ")
        }
    }


    /*** camera launcher ***/
    private val resultLauncherCamera: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                if (photoFile2?.exists() == true) {
                    val imagePath = photoFile2?.absolutePath.toString()
                    val imageUri = imagePath.toUri()
                    imageUri.let {
                        uri =it
                        binding.buttonCheck = true
                        multipartImage = convertMultipartPart(it)
                        binding.profileImage.setImageURI(imageUri)

                    }
                }
            }
        }


    private fun convertMultipartPartGal(imageUri: Uri): MultipartBody.Part {
        val file = FileUtil.getTempFile(requireActivity(), imageUri)
        val fileName =
            "${file!!.nameWithoutExtension}_${System.currentTimeMillis()}.${file.extension}"
        val newFile = File(file.parent, fileName)
        file.renameTo(newFile)
        return MultipartBody.Part.createFormData(
            "profilePicture", newFile.name, newFile.asRequestBody("image/*".toMediaTypeOrNull())
        )
    }


    private fun convertMultipartPart(imageUri: Uri): MultipartBody.Part? {
        val filePath = imageUri.path ?: return null
        val file = File(filePath)
        if (!file.exists()) {
            return null
        }
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("profilePicture", file.name, requestFile)
    }


}