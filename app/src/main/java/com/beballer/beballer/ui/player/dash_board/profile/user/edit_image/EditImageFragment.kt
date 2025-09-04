package com.beballer.beballer.ui.player.dash_board.profile.user.edit_image

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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
import com.beballer.beballer.utils.Status
import com.beballer.beballer.data.api.Constants
import com.beballer.beballer.data.model.CommonResponse
import com.beballer.beballer.data.model.UserProfile
import com.beballer.beballer.databinding.FragmentEditImageBinding
import com.beballer.beballer.databinding.VideoImagePickerDialogBoxBinding
import com.beballer.beballer.ui.player.create_profile.choose_avtar.ChooseAvatarFragment.Companion.sendMultipartImage
import com.beballer.beballer.ui.player.dash_board.profile.ProfileFragmentVM
import com.github.dhaval2404.imagepicker.util.FileUtil
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@AndroidEntryPoint
class EditImageFragment : BaseFragment<FragmentEditImageBinding>() {
    private val viewModel: ProfileFragmentVM by viewModels()
    private var selectedItem = 0
    private var imageDialog: BaseCustomDialog<VideoImagePickerDialogBoxBinding>? = null
    private var multipartImage: MultipartBody.Part? = null
    private var photoFile2: File? = null
    private var photoURI: Uri? = null

    override fun getLayoutResource(): Int {
        return R.layout.fragment_edit_image
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // observer
        initObserver()
        // click
        initOnClick()
        // api call
        val data  = HashMap<String, Any>()
        viewModel.getProfileApi(Constants.USER_PROFILE,data)
    }

    /** handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.cancelImage -> {
                   requireActivity().finish()
                }

                R.id.btnNext -> {

                    val data = HashMap<String, RequestBody>()
                    val drawableFirst =
                        ContextCompat.getDrawable(requireContext(), R.drawable.first_avtar)
                    val drawableSecond =
                        ContextCompat.getDrawable(requireContext(), R.drawable.second_avtar)
                    val drawableThird =
                        ContextCompat.getDrawable(requireContext(), R.drawable.third_avtar)
                    val drawableFour =
                        ContextCompat.getDrawable(requireContext(), R.drawable.four_avtar)
                    val drawableFive =
                        ContextCompat.getDrawable(requireContext(), R.drawable.five_avtar)
                    val drawableSix =
                        ContextCompat.getDrawable(requireContext(), R.drawable.six_avtar)
                    when (selectedItem) {
                        1 -> {
                            val bitmap = (drawableFirst as BitmapDrawable).bitmap
                            val imageFile = bitmapToFile(requireContext(), bitmap, "profilePicture")
                            multipartImage = fileToMultipart(imageFile)
                        }

                        2 -> {
                            val bitmap = (drawableSecond as BitmapDrawable).bitmap
                            val imageFile = bitmapToFile(requireContext(), bitmap, "profilePicture")
                            multipartImage = fileToMultipart(imageFile)
                        }

                        3 -> {
                            val bitmap = (drawableThird as BitmapDrawable).bitmap
                            val imageFile = bitmapToFile(requireContext(), bitmap, "profilePicture")
                            multipartImage = fileToMultipart(imageFile)
                        }

                        4 -> {
                            val bitmap = (drawableFour as BitmapDrawable).bitmap
                            val imageFile = bitmapToFile(requireContext(), bitmap, "profilePicture")
                            multipartImage = fileToMultipart(imageFile)
                        }

                        5 -> {
                            val bitmap = (drawableFive as BitmapDrawable).bitmap
                            val imageFile = bitmapToFile(requireContext(), bitmap, "profilePicture")
                            multipartImage = fileToMultipart(imageFile)
                        }

                        6 -> {
                            val bitmap = (drawableSix as BitmapDrawable).bitmap
                            val imageFile = bitmapToFile(requireContext(), bitmap, "profilePicture")
                            multipartImage = fileToMultipart(imageFile)
                        }


                    }
                    if (multipartImage != null) {
                        viewModel.updateProfileApi(Constants.CREATE_PROFILE, data, multipartImage)
                    }

                }


                R.id.ivPersonFirst -> {
                    binding.buttonCheck = true
                    binding.ivPerson.setImageResource(R.drawable.first_avtar)
                    selectedItem = 1
                    multipartImage = null
                }

                R.id.ivPersonSecond -> {
                    binding.buttonCheck = true
                    selectedItem = 2
                    binding.ivPerson.setImageResource(R.drawable.second_avtar)
                    multipartImage = null
                }

                R.id.ivPersonThird -> {
                    binding.buttonCheck = true
                    selectedItem = 3
                    binding.ivPerson.setImageResource(R.drawable.third_avtar)
                    multipartImage = null
                }

                R.id.ivPersonFour -> {
                    binding.buttonCheck = true
                    selectedItem = 4
                    binding.ivPerson.setImageResource(R.drawable.four_avtar)
                    multipartImage = null
                }

                R.id.ivPersonFive -> {
                    binding.buttonCheck = true
                    selectedItem = 5
                    binding.ivPerson.setImageResource(R.drawable.five_avtar)
                    multipartImage = null
                }

                R.id.ivPersonSix -> {
                    binding.buttonCheck = true
                    selectedItem = 6
                    binding.ivPerson.setImageResource(R.drawable.six_avtar)
                    multipartImage = null
                }

                R.id.profileImageFab -> {
                    imageDialog()
                }
            }
        }
    }

    /*** bitmap to file convert ***/
    private fun bitmapToFile(context: Context, bitmap: Bitmap, fileName: String): File {
        val file = File(context.cacheDir, "$fileName.jpg")
        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        out.flush()
        out.close()
        return file
    }

    /*** file to multipart convert ***/
    private fun fileToMultipart(
        file: File, partName: String = "profilePicture"
    ): MultipartBody.Part {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
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
                type = "image/*"
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
                    sendMultipartImage = null
                    selectedItem = 7
                    binding.buttonCheck = true
                    multipartImage = convertMultipartPartGal(it)
                    binding.ivPerson.setImageURI(imageUri)

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
                    requireContext(), "com.beballer.beballer.fileProvider", photoFile2!!
                )
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                resultLauncherCamera.launch(pictureIntent)
            } else {
                Log.d("TAG", "openCameraIntent: ")
            }
        } else {
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
                        sendMultipartImage = null
                        selectedItem = 7
                        binding.buttonCheck = true
                        multipartImage = convertMultipartPart(it)
                        binding.ivPerson.setImageURI(imageUri)

                    }
                }
            }
        }

    /***
     * convert image multipart
     */
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


    /**  api response observer  **/
    private fun initObserver() {
        viewModel.commonObserver.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "updateProfileApi" -> {
                            try {
                                val myDataModel: CommonResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    requireActivity().finish()
                                }
                            } catch (e: Exception) {
                                Log.e("error", "updateProfileApi: $e")
                            } finally {
                                hideLoading()
                            }
                        }

                        "getProfileApi" -> {
                            try {
                                val myDataModel: UserProfile? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    if (myDataModel.data?.user != null) {
                                        binding.bean = myDataModel.data.user
                                    }

                                }

                            } catch (e: Exception) {
                                Log.e("error", "getProfileApi: $e")
                            } finally {
                                hideLoading()
                            }
                        }

                    }
                }

                Status.ERROR -> {
                    hideLoading()
                    showErrorToast(it.message.toString())
                }

                else -> {
                }
            }
        }
    }
}