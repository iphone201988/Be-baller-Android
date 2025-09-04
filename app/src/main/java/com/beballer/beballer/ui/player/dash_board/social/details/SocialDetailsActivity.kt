package com.beballer.beballer.ui.player.dash_board.social.details

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.beballer.beballer.BR
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseActivity
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.base.SimpleRecyclerViewAdapter
import com.beballer.beballer.data.api.Constants
import com.beballer.beballer.data.model.CommentData
import com.beballer.beballer.data.model.CommonResponse
import com.beballer.beballer.data.model.GetPostCommentResponse
import com.beballer.beballer.data.model.GetUserPostData
import com.beballer.beballer.data.model.PostCommentData
import com.beballer.beballer.data.model.PostCommentResponse
import com.beballer.beballer.databinding.ActivitySocialDetailsBinding
import com.beballer.beballer.databinding.SocialRvChatItemBinding
import com.beballer.beballer.ui.interfacess.CommonPostInterface
import com.beballer.beballer.utils.BindingUtils
import com.beballer.beballer.utils.BindingUtils.DateHelper
import com.beballer.beballer.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class SocialDetailsActivity : BaseActivity<ActivitySocialDetailsBinding>() {
    private val viewModel: SocialDetailsActivityVM by viewModels()
    private lateinit var chatAdapter: SimpleRecyclerViewAdapter<PostCommentData, SocialRvChatItemBinding>
    private var player: ExoPlayer? = null
    private var commentDataSet: CommentData? = null
    private var userLikeId = ""
    private var userPostCommentId = ""
    var userLikeCount: Int = 0
    var userCommentLikeCount: Int = 0
    var currentUserLikeCount: Int = 0
    var currentCommentUserLikeCount: Int = 0
    var postCommentCount = 0
    var currentPage: Int = 1
    var chatList = ArrayList<PostCommentData>()
    private var postPosition = -1
    private var nextPosition = -1
    private var userCommentId = ""
    private var scroll: Int = 0

    companion object {
        var commonPostInterface: CommonPostInterface? = null
    }

    override fun getLayoutResource(): Int {
        return R.layout.activity_social_details
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        // get intent data
        val intentType = intent.getStringExtra("socialDetails")
        val item = intent.getParcelableExtra<GetUserPostData>("socialData")
        nextPosition = intent.getIntExtra("socialPos", -1)
        if (item != null) {
            binding.bean = item
            userLikeId = item._id.toString()
            userPostCommentId = item.id.toString()
            userLikeCount = item.likesCount ?: 0
            postCommentCount = item.commentCount ?: 0
            currentUserLikeCount = item.currentUserLikeCount ?: 0

            binding.tvLike.text = userLikeCount.toString()
            binding.tvComment.text = postCommentCount.toString()


            when (item.currentUserLikeCount) {
                0 -> binding.postLikeImageImage.setImageResource(R.drawable.ic_like_0_24)
                1 -> binding.postLikeImageImage.setImageResource(R.drawable.like_icon1)
                2 -> binding.postLikeImageImage.setImageResource(R.drawable.like_icon2)
                3 -> binding.postLikeImageImage.setImageResource(R.drawable.like_icon3)
            }

            val rawDate = item.date
            if (rawDate?.isNotEmpty() == true) {
                val date = BindingUtils.convertToDate(rawDate)
                val relative = DateHelper.formatRelativeDate(date)
                binding.tvHour.text = relative
                binding.tvSecondHour.text = relative
            }

            if (intentType.equals("video")) {
                val width = item.postContentWidth?.toIntOrNull() ?: 0
                val height = item.postContentHeight?.toIntOrNull() ?: 0
                (binding.cardPostVideoPlayer.layoutParams as? ConstraintLayout.LayoutParams)?.apply {
                    dimensionRatio = when {
                        width > height -> "16:9"
                        else -> "3:4"
                    }
                    binding.cardPostVideoPlayer.layoutParams = this
                }
            }
            // user data set
            commentDataSet = CommentData(
                item._id,
                item.publisherData?.firstName,
                item.publisherData?.id,
                item.publisherData?.lastName,
                item.publisherData?.profilePicture,
                item.publisherData?.username,
                item.publisherData?.verified
            )


        }

        when (intentType) {
            "image" -> {
                binding.clImageType.visibility = View.VISIBLE
                binding.clPostHeader.visibility = View.VISIBLE
                binding.clGameType.visibility = View.GONE
                binding.clVideoType.visibility = View.GONE
                binding.clCourtType.visibility = View.GONE
                binding.clTypeFive.visibility = View.GONE
                binding.tvTextDescType.visibility = View.GONE
                binding.clSecond.visibility = View.GONE
                binding.tvPublisherSector.visibility = View.GONE

            }

            "court" -> {
                binding.clImageType.visibility = View.GONE
                binding.clGameType.visibility = View.GONE
                binding.clVideoType.visibility = View.GONE
                binding.clCourtType.visibility = View.VISIBLE
                binding.clPostHeader.visibility = View.VISIBLE
                binding.clTypeFive.visibility = View.GONE
                binding.tvTextDescType.visibility = View.GONE
                binding.clSecond.visibility = View.GONE
                binding.tvPublisherSector.visibility = View.VISIBLE
                if (item?.court?.coordinates?.size == 2) {
                    val lat1 = item.court.coordinates.getOrNull(1)
                    val lon1 = item.court.coordinates.getOrNull(0)
                    val lat2 = BindingUtils.lat
                    val lon2 = BindingUtils.long
                    if (lat1 != null && lon1 != null) {
                        val distance = BindingUtils.formattedDistance(lat1, lon1, lat2, lon2)
                        binding.tvCourtDistance.text = distance
                    }
                }
            }

            "video" -> {
                binding.clImageType.visibility = View.GONE
                binding.clGameType.visibility = View.GONE
                binding.clVideoType.visibility = View.VISIBLE
                binding.clCourtType.visibility = View.GONE
                binding.clTypeFive.visibility = View.GONE
                binding.tvTextDescType.visibility = View.GONE
                binding.clPostHeader.visibility = View.INVISIBLE
                binding.clSecond.visibility = View.VISIBLE
                item?.video?.takeIf { it.isNotEmpty() }?.let { videoPath ->
                    val playerView = binding.playerView
                    playerView.visibility = View.VISIBLE

                    player = ExoPlayer.Builder(this).build()
                    playerView.player = player

                    val videoUrl = Constants.IMAGE_URL + videoPath
                    val mediaItem = MediaItem.fromUri(videoUrl)
                    player?.setMediaItem(mediaItem)
                    player?.prepare()
                    player?.playWhenReady = true
                }


            }

            "game" -> {
                binding.clImageType.visibility = View.GONE
                binding.clGameType.visibility = View.VISIBLE
                binding.clPostHeader.visibility = View.VISIBLE
                binding.clVideoType.visibility = View.GONE
                binding.clCourtType.visibility = View.GONE
                binding.clTypeFive.visibility = View.GONE
                binding.tvTextDescType.visibility = View.GONE
                binding.clSecond.visibility = View.GONE
            }

            "fiveType" -> {
                binding.clImageType.visibility = View.GONE
                binding.clGameType.visibility = View.GONE
                binding.clVideoType.visibility = View.GONE
                binding.clCourtType.visibility = View.GONE
                binding.clTypeFive.visibility = View.VISIBLE
                binding.tvTextDescType.visibility = View.GONE
                binding.clPostHeader.visibility = View.INVISIBLE
                binding.clSecond.visibility = View.VISIBLE
            }

            "textOnly" -> {
                binding.clImageType.visibility = View.GONE
                binding.clGameType.visibility = View.GONE
                binding.clVideoType.visibility = View.GONE
                binding.clCourtType.visibility = View.GONE
                binding.clTypeFive.visibility = View.GONE
                binding.tvTextDescType.visibility = View.VISIBLE
                binding.clPostHeader.visibility = View.VISIBLE
                binding.clSecond.visibility = View.GONE
                binding.tvPublisherSector.visibility = View.GONE
            }

        }
        // initOnCLick
        initOnClick()
        // adapter
        initChatAdapter()
        // observer
        initObserver()
        // api call
        if (item?._id?.isNotEmpty() == true) {
            userCommentId = item.id.toString()
            val data = HashMap<String, Any>()
            data["page"] = currentPage
            data["limit"] = 10
            viewModel.getPostComment(Constants.USER_GET_POST_COMMENT + "/${item.id}", data)
        }
        // pagination
        paginationRecyclerView()
    }

    /*** all click handel in this method  ***/
    private fun initOnClick() {
        viewModel.onClick.observe(this@SocialDetailsActivity) {
            when (it?.id) {
                R.id.ivBack -> {
                    finish()
                }

                // message send button  click
                R.id.ivSend -> {
                    val data = HashMap<String, Any>()
                    val message = binding.etSendMessage.text.toString()
                    if (message.isEmpty()) {
                        showInfoToast("Please enter message")
                    } else {
                        val message = binding.etSendMessage.text.toString().trim()
                        data["postId"] = userPostCommentId
                        data["comment"] = message

                        chatList.add(
                            0, PostCommentData(
                                null,
                                message,
                                SimpleDateFormat(
                                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US
                                ).apply { timeZone = TimeZone.getTimeZone("UTC") }.format(Date()),
                                null,
                                null,
                                null,
                                null,
                                null,
                                commentDataSet
                            )
                        )

                        chatAdapter.list = chatList
                        chatAdapter.notifyItemInserted(0)

                        // Optional: scroll to top to show new comment
                        binding.rvSocialChat.scrollToPosition(0)

                        binding.etSendMessage.setText("")
                        binding.etSendMessage.clearFocus()
                    }
                    Handler(Looper.getMainLooper()).postDelayed({
                        viewModel.postComment(Constants.USER_POST_COMMENT, data)
                    }, 1000)
                }


                R.id.postCommentImage -> {
                    binding.etSendMessage.requestFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(binding.etSendMessage, InputMethodManager.SHOW_IMPLICIT)
                }

                R.id.postShareImage -> {

                }

                R.id.postLikeImageImage -> {
                    if (userLikeId.isNotEmpty()) {
                        if (currentUserLikeCount < 3) {
                            viewModel.postLikeApi("${Constants.USER_POST_LIKE}?postId=$userLikeId")
                        } else {
                            showInfoToast("You can like only up to 3 times.")
                        }
                    }
                }
            }
        }
    }


    /**  Pagination Function **/
    private fun paginationRecyclerView() {
        binding.customScrollview.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            val tolerance = 50
            if (scrollY >= (v.getChildAt(0).measuredHeight - v.measuredHeight - tolerance)) {
                if (scroll == 1) {
                    loadMoreItems()
                    scroll = 0
                }
            }
        })
    }

    /**
     *  load more function call
     **/
    private fun loadMoreItems() {
        currentPage++
        val data = HashMap<String, Any>()
        data["page"] = currentPage
        data["limit"] = 10
        viewModel.getPostComment(Constants.USER_GET_POST_COMMENT + "/${userCommentId}", data)
    }

    /** handle api response **/
    private fun initObserver() {
        viewModel.commonObserver.observe(this@SocialDetailsActivity) {
            when (it?.status) {
                Status.LOADING -> {
                    hideLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "postLikeApi" -> {
                            try {
                                val myDataModel: CommonResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel?.success == true) {
                                    showSuccessToast(myDataModel.message.toString())
                                    userLikeCount++
                                    currentUserLikeCount++
                                    binding.tvLike.text = userLikeCount.toString()
                                    when (currentUserLikeCount) {
                                        0 -> binding.postLikeImageImage.setImageResource(R.drawable.ic_like_0_24)
                                        1 -> binding.postLikeImageImage.setImageResource(R.drawable.like_icon1)
                                        2 -> binding.postLikeImageImage.setImageResource(R.drawable.like_icon2)
                                        3 -> binding.postLikeImageImage.setImageResource(R.drawable.like_icon3)
                                    }
                                    commonPostInterface?.likeCount(
                                        userLikeCount, currentUserLikeCount, nextPosition
                                    )
                                }
                            } catch (e: Exception) {
                                Log.e("error", "postLikeApi: $e")
                            } finally {
                                hideLoading()
                            }
                        }

                        "getPostComment" -> {
                            try {
                                val myDataModel: GetPostCommentResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    if (myDataModel.data != null) {
                                        if (currentPage == 1) {
                                            chatList =
                                                myDataModel.data as ArrayList<PostCommentData>
                                            chatAdapter.list = chatList
                                        } else {
                                            chatAdapter.addToList(myDataModel.data)
                                        }
                                        scroll =
                                            if (currentPage == myDataModel.pagination?.totalPages) {
                                                0
                                            } else {
                                                1
                                            }

                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("error", "getPostComment: $e")
                            } finally {
                                hideLoading()
                            }
                        }

                        "postCommentLikeApi" -> {
                            try {
                                val myDataModel: CommonResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel?.success == true) {
                                    showSuccessToast(myDataModel.message.toString())
                                    chatAdapter.getList()[postPosition]?.let { post ->
                                        post.likeCount = (post.likeCount ?: 0) + 1
                                        post.currentUserLikeCount =
                                            (post.currentUserLikeCount ?: 0) + 1
                                        chatAdapter.notifyItemChanged(postPosition)
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("error", "postCommentLikeApi: $e")
                            } finally {
                                hideLoading()
                            }
                        }


                        "postComment" -> {
                            try {
                                val myDataModel: PostCommentResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel?.success == true) {
                                    showSuccessToast(myDataModel.message.toString())
                                    postCommentCount++
                                    binding.tvComment.text = postCommentCount.toString()
                                    commonPostInterface?.commentCount(
                                        postCommentCount,
                                        nextPosition
                                    )
                                }
                            } catch (e: Exception) {
                                Log.e("error", "postComment: $e")
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

    /** handle out side adapter **/
    private fun initChatAdapter() {
        chatAdapter =
            SimpleRecyclerViewAdapter(R.layout.social_rv_chat_item, BR.bean) { v, m, pos ->
                when (v.id) {
                    R.id.ivLikeImage -> {
                        postPosition = pos
                        userCommentLikeCount = m.likeCount ?: 0
                        currentCommentUserLikeCount = m.currentUserLikeCount ?: 0
                        if (currentCommentUserLikeCount < 3) {
                            viewModel.postCommentLikeApi("${Constants.USER_POST_LIKE_COMMENT}?commentId=${m._id}")
                        } else {
                            showInfoToast("You can like only up to 3 times.")
                        }
                    }
                }
            }

        binding.rvSocialChat.adapter = chatAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }
}