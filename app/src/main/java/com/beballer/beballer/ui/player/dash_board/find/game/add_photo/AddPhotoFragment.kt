package com.beballer.beballer.ui.player.dash_board.find.game.add_photo

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.utils.BaseCustomDialog
import com.beballer.beballer.utils.BindingUtils
import com.beballer.beballer.databinding.AddPhotoDialogItemBinding
import com.beballer.beballer.databinding.FragmentAddPhotoBinding
import com.beballer.beballer.databinding.VideoImagePickerDialogBoxBinding
import com.beballer.beballer.ui.player.create_profile.choose_avtar.ChooseAvatarFragment.Companion.sendMultipartImage
import com.beballer.beballer.utils.AppUtils
import com.github.dhaval2404.imagepicker.util.FileUtil
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException

@AndroidEntryPoint
class AddPhotoFragment : BaseFragment<FragmentAddPhotoBinding>() {
    private val viewModel: AddPhotoFragmentVM by viewModels()
    private lateinit var addDialogItem: BaseCustomDialog<AddPhotoDialogItemBinding>
    private var imageDialog: BaseCustomDialog<VideoImagePickerDialogBoxBinding>? = null
    private var multipartImageFirst: MultipartBody.Part? = null
    private var photoFile2: File? = null
    private var photoURI: Uri? = null
    private var multipartImageSecond: MultipartBody.Part? = null
    private var multipartImageThird: MultipartBody.Part? = null
   private var imageType = 0
    companion object{

    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_add_photo
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnclick()

    }

    /*** click event handel **/
    private fun initOnclick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                // back button
                R.id.cancelImage -> {
                    findNavController().popBackStack()
                }
                // next button
                R.id.btnNext -> {
                    val courtName = arguments?.getString("courtName")
                    val courtAddress = arguments?.getString("courtAddress")
                    val accessibility = arguments?.getString("accessibility")
                    val hoopsCount = arguments?.getString("hoopsCount")
                    val boardType = arguments?.getString("boardType")
                    val netType = arguments?.getString("netType")
                    val floorType = arguments?.getString("floorType")
                    val linesAnd = arguments?.getString("linesAnd")
                    val waterPoint = arguments?.getString("waterPoint")
                    addPhotoDialogItem()
                }
                // first image
                R.id.cardAddImage1,R.id.ivFabFirst->{
                    imageType = 1
                    imageDialog()
                }
                // second image
                R.id.cardAddImage2,R.id.image_2_fab->{
                    imageType = 2
                    imageDialog()
                }
                // third image
                R.id.cardAddImage3,R.id.ivFabThree->{
                    imageType = 3
                    imageDialog()
                }

            }
        }


        binding.courtRatingBar.setOnRatingBarChangeListener = { rating, fromUser ->
            // rating -> Float, fromUser -> Boolean
            if (fromUser) {
                Toast.makeText(requireContext(), "Rating: $rating", Toast.LENGTH_SHORT).show()
                // Save it or update your UI
            }
        }




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
                    when(imageType){
                        1->{
                            multipartImageFirst = convertMultipartPartGal(it)
                            binding.ivFirst.setImageURI(imageUri)
                            binding.ivFabFirst.visibility = View.GONE
                        }
                        2->{
                            multipartImageSecond = convertMultipartPartGal(it)
                            binding.ivSecond.setImageURI(imageUri)
                            binding.ivFabTwo.visibility = View.GONE
                        }
                        3->{
                            multipartImageThird = convertMultipartPartGal(it)
                            binding.ivThird.setImageURI(imageUri)
                            binding.ivFabThree.visibility = View.GONE
                        }

                        else -> {

                        }
                    }


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
                        when(imageType){
                            1->{
                                multipartImageFirst = convertMultipartPart(it)
                                binding.ivFirst.setImageURI(imageUri)
                                binding.ivFabFirst.visibility = View.GONE
                                binding.ivFirst1.visibility = View.GONE
                            }
                            2->{
                                multipartImageSecond = convertMultipartPart(it)
                                binding.ivSecond.setImageURI(imageUri)
                                binding.ivFabTwo.visibility = View.GONE
                                binding.ivSecond1.visibility = View.GONE
                            }
                            3->{
                                multipartImageThird = convertMultipartPart(it)
                                binding.ivThird.setImageURI(imageUri)
                                binding.ivFabThree.visibility = View.GONE
                                binding.ivThird1.visibility = View.GONE
                            }

                            else -> {

                            }
                        }

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
            "photo", newFile.name, newFile.asRequestBody("image/*".toMediaTypeOrNull())
        )
    }

    private fun convertMultipartPart(imageUri: Uri): MultipartBody.Part? {
        val filePath = imageUri.path ?: return null
        val file = File(filePath)
        if (!file.exists()) {
            return null
        }
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("photo", file.name, requestFile)
    }

    /**** alert dialog item ****/
    private fun addPhotoDialogItem() {
        addDialogItem = BaseCustomDialog<AddPhotoDialogItemBinding>(
            requireContext(), R.layout.add_photo_dialog_item
        ) {
            when (it?.id) {
                R.id.tvBtn -> {
                    BindingUtils.navigateWithSlide(
                        findNavController(), R.id.navigateGameDetailsFragment, null
                    )
                    addDialogItem.dismiss()
                }
            }

        }
        addDialogItem.create()
        addDialogItem.show()
    }


}