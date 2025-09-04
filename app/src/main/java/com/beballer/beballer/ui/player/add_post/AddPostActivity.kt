package com.beballer.beballer.ui.player.add_post


import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.arthenica.ffmpegkit.FFmpegKit
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseActivity
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.data.api.Constants
import com.beballer.beballer.data.model.CommonResponse
import com.beballer.beballer.databinding.ActivityAddPostBinding
import com.beballer.beballer.databinding.BottomsheetForAddlinkCommunityBinding
import com.beballer.beballer.ui.interfacess.AddPostInterface
import com.beballer.beballer.ui.player.create_profile.choose_avtar.ChooseAvatarFragment.Companion.sendMultipartImage
import com.beballer.beballer.utils.AppUtils
import com.beballer.beballer.utils.BaseCustomBottomSheet
import com.beballer.beballer.utils.BindingUtils
import com.beballer.beballer.utils.Status
import com.beballer.beballer.utils.hideKeyboard
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.util.FileUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


@AndroidEntryPoint
class AddPostActivity : BaseActivity<ActivityAddPostBinding>() {
    private lateinit var addImagesBs: BaseCustomBottomSheet<BottomsheetForAddlinkCommunityBinding>
    private val viewModel: AddPostActivityVM by viewModels()
    private var multipartImage: MultipartBody.Part? = null
    private var videoMultipart: MultipartBody.Part? = null
    private var photoFile2: File? = null
    private var player: ExoPlayer? = null
    private var photoURI: Uri? = null
    private var postContentWidth = 0
    private var postContentHeight = 0


    companion object {
        var addPostInterface: AddPostInterface? = null
    }


    override fun getLayoutResource(): Int {
        return R.layout.activity_add_post
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        // click
        initOnclick()
        // dialog
        initBottomSheetAddImage()
        // set profile image
        var profilePic = sharedPrefManager.getLoginData()?.data?.user?.profilePicture
        if (profilePic?.isNotEmpty() == true) {
            Glide.with(this).asBitmap().load(Constants.IMAGE_URL + profilePic).circleCrop()
                .error(R.drawable.ic_round_account_circle_40).into(binding.ivProfileImage)
        }
        // observer
        initObserver()
        // bottom sheet behaviour
        initBottomSheet()
    }

    /**
     * handel activity like as bottom sheet
     */
    private fun initBottomSheet() {
        val bottomSheet = findViewById<ConstraintLayout?>(R.id.clBottomSheet)
        val bottomSheetBehavior = BottomSheetBehavior.from<ConstraintLayout?>(bottomSheet)
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    finish()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })
    }



    /**  api response observer  **/
    private fun initObserver() {
        viewModel.commonObserver.observe(this@AddPostActivity) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "createPostApi" -> {
                            try {
                                val myDataModel: CommonResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    if (myDataModel.message?.isNotEmpty() == true) {
                                        addPostInterface?.addPost(true)
                                        showSuccessToast(myDataModel.message.toString())
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("error", "createPostApi: $e")
                            } finally {
                                hideLoading()
                                finish()
                            }
                        }
                    }
                }

                Status.ERROR -> {
                    hideLoading()
                    addPostInterface?.addPost(false)
                    showErrorToast(it.message.toString())
                }

                else -> {
                }
            }
        }
    }


    /*** click event handel **/
    private fun initOnclick() {
        viewModel.onClick.observe(this@AddPostActivity) {
            when (it?.id) {
                // cancel button click
                R.id.tvCancel -> {
                    finish()
                }
                // publish button click
                R.id.tvPublish -> {
                    val desc = binding.etDescription.text?.toString()?.trim() ?: ""
                    if (desc.isEmpty()) {
                        showCustomToast(this@AddPostActivity, "Please enter description")
                        return@observe
                    }


                    val data = HashMap<String, RequestBody>()
                    val type = when {
                        multipartImage != null -> "image"
                        videoMultipart != null -> "video"
                        else -> "textOnly"
                    }

                    data["contentType"] = type.toRequestBody("text/plain".toMediaTypeOrNull())
                    if (type != "textOnly") {
                        data["postContentWidth"] = postContentWidth.toString()
                            .toRequestBody("text/plain".toMediaTypeOrNull())
                        data["postContentHeight"] = postContentHeight.toString()
                            .toRequestBody("text/plain".toMediaTypeOrNull())
                    }

                    data["description"] = desc.toRequestBody("text/plain".toMediaTypeOrNull())
                    hideKeyboard()

               if (multipartImage!=null){
                   viewModel.createPostApi(data, multipartImage)
               }else if (videoMultipart!=null){
                   viewModel.createPostApi(data, videoMultipart)
               }else{
                   viewModel.createPostApi(data, null)
               }


                }

                // upload image button click
                R.id.cardAddImage, R.id.btnNext -> {
                    addImagesBs.show()
                }

            }
        }

    }


    /**** choose image and gallery bottom sheet   ***/
    private fun initBottomSheetAddImage() {
        addImagesBs = BaseCustomBottomSheet(this, R.layout.bottomsheet_for_addlink_community) {
            when (it.id) {
                R.id.tvChooseFromGallery, R.id.icon_emoji_new -> {
                    if (!BindingUtils.hasPermissions(
                            this@AddPostActivity, BindingUtils.permissions
                        )
                    ) {
                        permissionResultLauncher.launch(BindingUtils.permissions)
                    } else {
                        // gallery
                        galleryImagePicker()
                    }
                    addImagesBs.dismiss()

                }

                R.id.openCamaraImage, R.id.openCamara -> {
                    if (!BindingUtils.hasPermissions(
                            this@AddPostActivity, BindingUtils.permissions
                        )
                    ) {
                        permissionResultLauncher1.launch(BindingUtils.permissions)
                    } else {
                        // camera
                        openCameraIntent()
                    }
                    addImagesBs.dismiss()
                }


            }
        }
        addImagesBs.behavior.isDraggable = true
        addImagesBs.setCancelable(true)
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

    /*** open gallery for both images and videos ***/
    private fun galleryImagePicker() {
        val pictureActionIntent = Intent(Intent.ACTION_PICK).apply {
            type = "*/*" // Accept any file, but we'll filter to image/video
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
        }
        resultLauncherGallery.launch(pictureActionIntent)
    }


    /**** Open Gallery launcher and pick photo or video ***/
    private var resultLauncherGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val fileUri = data?.data
                if (fileUri != null) {
                    val mimeType = contentResolver.getType(fileUri)
                    if (mimeType != null) {
                        when {
                            mimeType.startsWith("image/") -> {
                                videoMultipart = null
                                binding.ivEvent.visibility = View.VISIBLE
                                binding.cardPostVideoPlayer.visibility = View.GONE
                                binding.btnNext.visibility = View.VISIBLE
                                binding.cardAddImage.visibility = View.GONE
                                val destinationUri = Uri.fromFile(File(cacheDir, "cropped_${System.currentTimeMillis()}.jpg"))
                                // Start uCrop
                                UCrop.of(fileUri, destinationUri).start(this)

                            }


                            mimeType.startsWith("video/") -> {
                                lifecycleScope.launch {
                                    showLoading()
                                    // Thumbnail generate & show
                                    val thumb = getVideoThumbnail(this@AddPostActivity, fileUri)
                                    if (thumb != null) {
                                        Log.d("adddsadaas", ": $thumb")
                                        binding.cardPostVideoPlayer.visibility = View.VISIBLE
                                        binding.playerView.visibility = View.INVISIBLE
                                        binding.ivPlayerView.visibility = View.VISIBLE
                                        binding.ivEvent.visibility = View.GONE
                                        binding.cardAddImage.visibility = View.GONE
                                        binding.ivPlayerView.setImageBitmap(thumb)
                                    }

                                    val inputPath = getFileFromUri(this@AddPostActivity, fileUri)
                                    if (inputPath != null) {
                                        val outputPath = File(
                                            cacheDir, "output_${System.currentTimeMillis()}.mp4"
                                        ).absolutePath

                                        val retriever = MediaMetadataRetriever().apply {
                                            setDataSource(inputPath)
                                        }

                                        val width = retriever.extractMetadata(
                                            MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH
                                        )?.toInt() ?: 720

                                        val height = retriever.extractMetadata(
                                            MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT
                                        )?.toInt() ?: 1280

                                        val fps = retriever.extractMetadata(
                                            MediaMetadataRetriever.METADATA_KEY_CAPTURE_FRAMERATE
                                        )?.toFloat()?.toInt() ?: 30

                                        val ffmpegCommand = listOf(
                                            "-i", inputPath,
                                            "-vf", "scale=$width:$height:force_original_aspect_ratio=increase,crop=$width:$height",
                                            "-r", fps.toString(),
                                            "-c:v", "libx264",
                                            "-profile:v", "high",
                                            "-b:v", "3M",
                                            "-pix_fmt", "yuv420p",
                                            "-preset", "slow",
                                            "-movflags", "+faststart",
                                            "-c:a", "aac",
                                            "-b:a", "128k",
                                            outputPath
                                        ).joinToString(" ")

                                        val rc = withContext(Dispatchers.IO) {
                                            FFmpegKit.execute(ffmpegCommand)
                                        }
                                        hideLoading()
                                        if (rc.returnCode.isValueSuccess) {
                                            Log.d("FFmpeg", "Processing success: $outputPath")
                                            binding.ivPlayerView.visibility = View.GONE
                                            binding.btnNext.visibility = View.VISIBLE
                                            binding.cardPostVideoPlayer.visibility = View.VISIBLE
                                            binding.playerView.visibility = View.VISIBLE
                                            showVideo(outputPath)
                                            videoMultipart = createVideoMultipart(outputPath)

                                        } else {
                                            Log.e("FFmpeg", "Processing failed: ${rc.failStackTrace}")
                                        }
                                    } else {
                                        hideLoading()
                                    }
                                }
                            }


                        }
                    }
                }
            }
        }


    /**
     * play video if video compressed
     */
    private fun showVideo(path: String) {
        player = ExoPlayer.Builder(this).build().also {
            binding.playerView.player = it
            val mediaItem = MediaItem.fromUri(path)
            it.setMediaItem(mediaItem)
            it.prepare()
            it.playWhenReady = true
        }
    }

    /**
     * get video thumbnail
     */
    private fun getVideoThumbnail(context: Context, uri: Uri): Bitmap? {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, uri)
            val bitmap = retriever.getFrameAtTime(1_000_000) // 1 sec ka frame
            retriever.release()
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * get file path from uri
     */
    fun getFileFromUri(context: Context, uri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Cannot open URI")
        val tempFile = File(context.cacheDir, "temp_video.mp4")
        FileOutputStream(tempFile).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        return tempFile.absolutePath
    }


    /** on activity results crop image **/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val croppedUri = UCrop.getOutput(data!!) ?: return
            lifecycleScope.launch {
                val compressedFile =
                    Compressor.compress(this@AddPostActivity, File(croppedUri.path!!)) {
                        resolution(800, 800)
                        quality(80)
                        format(Bitmap.CompressFormat.JPEG)
                    }

                val uri: Uri = Uri.fromFile(compressedFile)
                binding.ivEvent.setImageURI(uri)
              multipartImage =   convertMultipartPartGal(uri)
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            showErrorToast("${cropError?.message}")
        }
    }

    /**
     * Camera permission
     */
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
        if (pictureIntent.resolveActivity(packageManager) != null) {
            try {
                photoFile2 = AppUtils.createImageFile1(this@AddPostActivity)
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
            if (photoFile2 != null) {
                photoURI = FileProvider.getUriForFile(
                    this@AddPostActivity, "com.beballer.beballer.fileProvider", photoFile2!!
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
                        binding.ivEvent.visibility = View.VISIBLE
                        binding.btnNext.visibility = View.VISIBLE
                        binding.cardAddImage.visibility = View.GONE
                        multipartImage = convertMultipartPart(it)
                        binding.ivEvent.setImageURI(imageUri)

                    }
                }
            }
        }

       /*** convert image to multipart body ***/
    private fun convertMultipartPart(imageUri: Uri): MultipartBody.Part? {
        val filePath = imageUri.path ?: return null
        val file = File(filePath)
        if (!file.exists()) {
            return null
        }
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("postImage", file.name, requestFile)
    }

    private fun convertMultipartPartGal(imageUri: Uri): MultipartBody.Part {
        val file = FileUtil.getTempFile(this@AddPostActivity, imageUri)
        val fileName =
            "${file!!.nameWithoutExtension}_${System.currentTimeMillis()}.${file.extension}"
        val newFile = File(file.parent, fileName)
        file.renameTo(newFile)
        return MultipartBody.Part.createFormData(
            "postImage", newFile.name, newFile.asRequestBody("image/*".toMediaTypeOrNull())
        )
    }

    private fun createVideoMultipart(filePath: String): MultipartBody.Part {
        val file = File(filePath)
        val requestFile = file.asRequestBody("video/mp4".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("postVideo", file.name, requestFile)
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }
}