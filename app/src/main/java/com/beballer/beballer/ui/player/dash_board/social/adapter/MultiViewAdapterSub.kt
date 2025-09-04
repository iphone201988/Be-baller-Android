package com.beballer.beballer.ui.player.dash_board.social.adapter



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beballer.beballer.BR
import com.beballer.beballer.R
import com.beballer.beballer.base.SimpleRecyclerViewAdapter
import com.beballer.beballer.data.api.Constants
import com.beballer.beballer.data.model.GetUserPostData
import com.beballer.beballer.data.model.MpvModel
import com.beballer.beballer.databinding.RvMpvItemLayoutBinding
import com.beballer.beballer.ui.FeedItem
import com.beballer.beballer.utils.BindingUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.imageview.ShapeableImageView

open class MultiViewAdapterSub(
    private val listener: OnItemClickListener, private val userid: String?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    private val listItem: MutableList<FeedItem> = mutableListOf()

    companion object {
        private const val TYPE_MVP = 0
        private const val TYPE_IMAGE = 1
        private const val TYPE_VIDEO = 2
        private const val TYPE_TEXT = 3
        private const val TYPE_COURT = 4
        private const val TYPE_EVENT = 5
        private const val TYPE_GAME = 6
        private const val TYPE_LOADER = 7
    }

    internal var currentlyPlayingHolder: VideoPostViewHolderSub? = null
    internal val playbackPositions = mutableMapOf<Int, Long>()


    override fun getItemViewType(position: Int): Int {
        return when (val item = listItem[position]) {
            is FeedItem.MvpSection -> TYPE_MVP
            is FeedItem.Post -> {
                when (item.post.contentType) {
                    "image" -> TYPE_IMAGE
                    "video" -> TYPE_VIDEO
                    "textOnly" -> TYPE_TEXT
                    "court" -> TYPE_COURT
//                    "event" -> TYPE_EVENT
                    "game" -> TYPE_GAME
                    else -> TYPE_IMAGE
                }
            }

            is FeedItem.Loader -> TYPE_LOADER
            null -> {
                TYPE_IMAGE
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_MVP -> {
                val view = inflater.inflate(R.layout.item_second_social, parent, false)
                MvpViewHolder(view)
            }

            TYPE_IMAGE -> {
                val view = inflater.inflate(R.layout.item_image_post, parent, false)
                ImagePostViewHolder(view)
            }

            TYPE_VIDEO -> {
                val view = inflater.inflate(R.layout.item_video_post, parent, false)
                VideoPostViewHolderSub(view)
            }

            TYPE_TEXT -> {
                val view = inflater.inflate(R.layout.item_text_post, parent, false)
                TextPostViewHolder(view)
            }

            TYPE_COURT -> {
                val view = inflater.inflate(R.layout.item_court_post, parent, false)
                CourtPostViewHolder(view)
            }

            TYPE_EVENT -> {
                val view = inflater.inflate(R.layout.item_type_five, parent, false)
                EventPostViewHolder(view)
            }

            TYPE_GAME -> {
                val view = inflater.inflate(R.layout.item_type_game, parent, false)
                GamePostViewHolder(view)
            }

            TYPE_LOADER -> {
                val view = inflater.inflate(R.layout.item_loading, parent, false)
                LoaderViewHolder(view)
            }

            else -> throw IllegalArgumentException("Invalid viewType")
        }
    }

    override fun getItemCount() = listItem.size


    fun clearList() {
        listItem.clear()
        notifyDataSetChanged()
    }

    fun getPostAt(position: Int): GetUserPostData? {
        val item = listItem.getOrNull(position)
        return if (item is FeedItem.Post) item.post else null
    }


    fun updateLikesAt(position: Int, likeCount: Int, currentUserLikeCount: Int) {
        val item = listItem.getOrNull(position)
        if (item is FeedItem.Post) {
            item.post.likesCount = likeCount
            item.post.currentUserLikeCount = currentUserLikeCount
            notifyItemChanged(position)
        }
    }


    fun updateCommentCount(position: Int, commentCount: Int) {
        val item = listItem.getOrNull(position)
        if (item is FeedItem.Post) {
            item.post.commentCount = commentCount
            notifyItemChanged(position)
        }
    }


    fun getList(): MutableList<FeedItem> = listItem

    fun setList(posts: List<GetUserPostData?>, mvpList: List<MpvModel>) {
        listItem.clear()
        listItem.add(FeedItem.MvpSection(mvpList))
        posts.filterNotNull().forEach {
            listItem.add(FeedItem.Post(it))
        }
        notifyDataSetChanged()
    }

    fun removeAt(position: Int) {
        if (position in listItem.indices) {
            listItem.removeAt(position)
            notifyItemRemoved(position)
        }
    }



    fun showLoader() {
        if (listItem.none { it is FeedItem.Loader }) {
            listItem.add(FeedItem.Loader)
            notifyItemInserted(listItem.size - 1)
        }
    }

    fun hideLoader() {
        val loaderIndex = listItem.indexOfFirst { it is FeedItem.Loader }
        if (loaderIndex != -1) {
            listItem.removeAt(loaderIndex)
            notifyItemRemoved(loaderIndex)
        }
    }

    fun addToList(list: List<GetUserPostData?>?) {
        if (list.isNullOrEmpty()) return

        listItem.size

        // Convert incoming posts into FeedItem.Post
        val newPosts = list.filterNotNull().map { FeedItem.Post(it) }

        // Insert before the Loader if it exists
        val insertIndex =
            listItem.indexOfFirst { it is FeedItem.Loader }.takeIf { it != -1 } ?: listItem.size

        listItem.addAll(insertIndex, newPosts)

        notifyItemRangeInserted(insertIndex, newPosts.size)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = listItem[position]) {
            is FeedItem.MvpSection -> (holder as MvpViewHolder).bind(
                item.items as MutableList<MpvModel?>, listener, position
            )

            is FeedItem.Post -> {
                when (holder) {
                    is ImagePostViewHolder -> holder.bind(item.post, listener, position)
                    is VideoPostViewHolderSub -> holder.bind(item.post, listener, position)
                    is TextPostViewHolder -> holder.bind(item.post, listener, position)
                    is CourtPostViewHolder -> holder.bind(item.post, listener, position)
                    is EventPostViewHolder -> holder.bind(item.post, listener, position)
                    is GamePostViewHolder -> holder.bind(item.post, listener, position)
                }
            }

            is FeedItem.Loader -> Unit
            null -> {


            }
        }
    }


    fun pauseCurrentlyPlaying() {
        currentlyPlayingHolder?.let { holder ->
            val position = holder.adapterPosition
            playbackPositions[position] = holder.getCurrentPosition()
            holder.pauseVideo()
            currentlyPlayingHolder = null
        }
    }

    fun setCurrentlyPlayingHolder(holder: VideoPostViewHolderSub?) {
        currentlyPlayingHolder = holder
    }






    // loader type
    class LoaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    // image type
    inner class ImagePostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCommonPostName: AppCompatTextView = itemView.findViewById(R.id.tvCommonPostName)
        private val tvSubscribe: AppCompatTextView = itemView.findViewById(R.id.tvSubscribe)
        private val tvImageTypeUsername: AppCompatTextView =
            itemView.findViewById(R.id.tvImageTypeUsername)
        private val tvImageSector: AppCompatTextView = itemView.findViewById(R.id.tvImageSector)
        private val ivImage: ShapeableImageView = itemView.findViewById(R.id.ivImage)
        private val ivCommonPostProfile: ShapeableImageView = itemView.findViewById(R.id.ivCommonPostProfile)
        private val tvCommonPostDesc: AppCompatTextView = itemView.findViewById(R.id.tvCommonPostDesc)
        private val ivCommonLike: AppCompatImageView =
            itemView.findViewById(R.id.ivCommonLike)
        private val ivCommonComment: AppCompatImageView =
            itemView.findViewById(R.id.ivCommonComment)
        private val ivCommonMenu: AppCompatImageView = itemView.findViewById(R.id.ivCommonMenu)
        private val ivCommonShare: AppCompatImageView =
            itemView.findViewById(R.id.ivCommonShare)
        private val tvImageLike: AppCompatTextView = itemView.findViewById(R.id.tvImageLike)
        private val tvImageComment: AppCompatTextView = itemView.findViewById(R.id.tvImageComment)
        private val shareCount: AppCompatTextView = itemView.findViewById(R.id.share_tv)
        private val tvHour: AppCompatTextView = itemView.findViewById(R.id.tvHour)
        private val clCommon: ConstraintLayout = itemView.findViewById(R.id.clCommon)
        fun bind(item: GetUserPostData?, listener: OnItemClickListener, position: Int) {
            // subscription button handel
            if (userid.equals(item?.publisherData?._id)){
                tvSubscribe.visibility = View.GONE
            }else{
                tvSubscribe.visibility = View.VISIBLE
            }

            if (item?.isSubscribed==true){
                tvSubscribe.text = tvSubscribe.context.getString(R.string.subscribed)
                tvSubscribe.setTextColor(ContextCompat.getColor(tvSubscribe.context, R.color.black_040404))
            }else{
                tvSubscribe.text = tvSubscribe.context.getString(R.string.subscribe)
                tvSubscribe.setTextColor(ContextCompat.getColor(tvSubscribe.context, R.color.blue_00bef5))
            }

            val fullName = listOfNotNull(
                item?.publisherData?.firstName?.takeIf { it.isNotBlank() },
                item?.publisherData?.lastName?.takeIf { it.isNotBlank() }).joinToString(" ")
                .ifBlank { "User" }

            tvCommonPostName.text = fullName
            val username = item?.publisherData?.username?.takeIf { it.isNotBlank() } ?: "user"
            tvImageTypeUsername.text = "@$username"
            val location = listOfNotNull(
                item?.publisherData?.country?.takeIf { it.isNotBlank() },
                item?.publisherData?.city?.takeIf { it.isNotBlank() }).joinToString(", ")

            tvImageSector.text = location.ifBlank { "" }
            tvCommonPostDesc.text = item?.description
            tvImageLike.text = item?.likesCount.toString()
            tvImageComment.text = item?.commentCount.toString()
            val imageUrl = Constants.IMAGE_URL + item?.publisherData?.profilePicture
            val safeImageUrl = imageUrl.takeIf { it.isNotBlank() }

            Glide.with(ivCommonPostProfile.context).load(safeImageUrl)
                .placeholder(R.drawable.progress_animation_small)
                .error(R.drawable.ic_round_account_circle_40)
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(ivCommonPostProfile)
            //  commentCount.text = item?.likesCount.toString()


            item?.image?.let { image ->
                val imageUrl = Constants.IMAGE_URL + image
                Glide.with(ivImage.context).load(imageUrl)
                    .placeholder(R.drawable.progress_animation_small)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(ivImage)
            }
            val rawDate = item?.date
            if (rawDate?.isNotEmpty() == true){
                val date = BindingUtils.convertToDate(rawDate)
                val relative = BindingUtils.DateHelper.formatRelativeDate(date)
                tvHour.text = relative
            }


            when (item?.currentUserLikeCount) {
                0 -> ivCommonLike.setImageResource(R.drawable.ic_like_0_24)
                1 -> ivCommonLike.setImageResource(R.drawable.like_icon1)
                2 -> ivCommonLike.setImageResource(R.drawable.like_icon2)
                3 -> ivCommonLike.setImageResource(R.drawable.like_icon3)
            }


            ivCommonComment.setOnClickListener { listener.onItemClick(item, it.id, position) }
            ivCommonLike.setOnClickListener { listener.onItemClick(item, it.id, position) }
            ivCommonShare.setOnClickListener { listener.onItemClick(item, it.id, position) }
            ivCommonMenu.setOnClickListener { listener.onItemClick(item, it.id, position) }
            tvSubscribe.setOnClickListener { listener.onItemClick(item, it.id, position) }
            ivCommonPostProfile.setOnClickListener { listener.onItemClick(item, it.id, position) }
            tvCommonPostName.setOnClickListener { listener.onItemClick(item, it.id, position) }
            clCommon.setOnClickListener { listener.onItemClick(item, it.id, position) }

        }

    }
    // video type
    inner class VideoPostViewHolderSub(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val postVideoPlayer: PlayerView =
            itemView.findViewById(R.id.postVideoPlayer)
        private val videoThumbnail: AppCompatImageView = itemView.findViewById(R.id.videoThumbnail)
        private val tvHour: AppCompatTextView = itemView.findViewById(R.id.tvHour)
        private val ivCommonPostProfile: ShapeableImageView = itemView.findViewById(R.id.ivCommonPostProfile)
        private val clCommon: ConstraintLayout = itemView.findViewById(R.id.clCommon)
        private val tvLike: AppCompatTextView = itemView.findViewById(R.id.tvLike)
        private val tvPublisherUsername: AppCompatTextView =
            itemView.findViewById(R.id.tvPublisherUsername)
        private val tvCommonPostName: AppCompatTextView = itemView.findViewById(R.id.tvCommonPostName)
        private val tvComment: AppCompatTextView = itemView.findViewById(R.id.tvComment)
        private val ivCommonLike: AppCompatImageView = itemView.findViewById(R.id.ivCommonLike)
        private val ivCommonComment: AppCompatImageView =
            itemView.findViewById(R.id.ivCommonComment)
        private val ivCommonShare: AppCompatImageView =
            itemView.findViewById(R.id.ivCommonShare)
        private val ivCommonMenu: AppCompatImageView = itemView.findViewById(R.id.ivCommonMenu)
        private val tvVideoPost: AppCompatTextView = itemView.findViewById(R.id.tvVideoPost)
        private val tvShare: AppCompatTextView = itemView.findViewById(R.id.tvComment)
        private val tvSubscribe: AppCompatTextView = itemView.findViewById(R.id.tvSubscribe)
        private val cardPostVideoPlayer: CardView = itemView.findViewById(R.id.cardPostVideoPlayer)
        private var videoPlayer: ExoPlayer? = null
        private var currentVideoUrl: String? = null

        fun bind(item: GetUserPostData?, listener: OnItemClickListener, position: Int) {
            // subscription button handel
            if (userid.equals(item?.publisherData?._id)){
                tvSubscribe.visibility = View.GONE
            }else{
                tvSubscribe.visibility = View.VISIBLE
            }

            if (item?.isSubscribed==true){
                tvSubscribe.text = tvSubscribe.context.getString(R.string.subscribed)
                tvSubscribe.setTextColor(ContextCompat.getColor(tvSubscribe.context, R.color.black_040404))
            }else{
                tvSubscribe.text = tvSubscribe.context.getString(R.string.subscribe)
                tvSubscribe.setTextColor(ContextCompat.getColor(tvSubscribe.context, R.color.blue_00bef5))
            }
            tvLike.text = item?.likesCount.toString()
            tvComment.text = item?.commentCount.toString()
            //   tvShare.text = item?.likesCount.toString()
            val videoUrl = Constants.IMAGE_URL + (item?.video ?: "")
            currentVideoUrl = videoUrl
            val imageUrl = Constants.IMAGE_URL + item?.publisherData?.profilePicture

            val safeImageUrl = imageUrl.takeIf { it.isNotBlank() }
            Glide.with(ivCommonPostProfile.context).load(safeImageUrl)
                .placeholder(R.drawable.progress_animation_small)
                .error(R.drawable.ic_round_account_circle_40)
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(ivCommonPostProfile)
            val rawDate = item?.date
            if (rawDate?.isNotEmpty() == true){
                val date = BindingUtils.convertToDate(rawDate)
                val relative = BindingUtils.DateHelper.formatRelativeDate(date)
                tvHour.text = relative
            }
            when (item?.currentUserLikeCount) {
                0 -> ivCommonLike.setImageResource(R.drawable.ic_like_0_24)
                1 -> ivCommonLike.setImageResource(R.drawable.like_icon1)
                2 -> ivCommonLike.setImageResource(R.drawable.like_icon2)
                3 -> ivCommonLike.setImageResource(R.drawable.like_icon3)
            }

            val width = item?.postContentWidth?.toIntOrNull() ?: 0
            val height = item?.postContentHeight?.toIntOrNull() ?: 0
            (cardPostVideoPlayer.layoutParams as? ConstraintLayout.LayoutParams)?.apply {
                dimensionRatio = when {
                    width > height -> "16:9"
                    else -> "3:4"
                }
                cardPostVideoPlayer.layoutParams = this
            }

//            if (originalWidth > 0 && originalHeight > 0) {
//                val aspectRatio = originalWidth.toFloat() / originalHeight.toFloat()
//
//                val layoutParams = postVideoPlayer.layoutParams
//                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
//
//                // You can adjust this based on your max allowed height
//                val maxWidth = itemView.resources.displayMetrics.widthPixels
//                val calculatedHeight = (maxWidth / aspectRatio).toInt()
//
//                layoutParams.height = calculatedHeight
//                postVideoPlayer.layoutParams = layoutParams
//
//                // Also apply same for thumbnail so both match
//                videoThumbnail.layoutParams.height = calculatedHeight
//                videoThumbnail.requestLayout()
//            }

            Glide.with(itemView.context).asBitmap().load(videoUrl).frame(1_000_000)
                .into(videoThumbnail)


            val username = item?.publisherData?.username?.takeIf { it.isNotBlank() } ?: "user"
            tvPublisherUsername.text = "@$username"

            val fullName = listOfNotNull(
                item?.publisherData?.firstName?.takeIf { it.isNotBlank() },
                item?.publisherData?.lastName?.takeIf { it.isNotBlank() }).joinToString(" ")
                .ifBlank { "User" }

            tvCommonPostName.text = fullName

            tvVideoPost.text = item?.description ?: ""
            // Pause
            pauseVideo()
            // click handel
            ivCommonComment.setOnClickListener { listener.onItemClick(item, it.id, position) }
            ivCommonLike.setOnClickListener { listener.onItemClick(item, it.id, position) }
            ivCommonShare.setOnClickListener { listener.onItemClick(item, it.id, position) }
            ivCommonMenu.setOnClickListener { listener.onItemClick(item, it.id, position) }
            tvSubscribe.setOnClickListener { listener.onItemClick(item, it.id, position) }
            ivCommonPostProfile.setOnClickListener { listener.onItemClick(item, it.id, position) }
            tvCommonPostName.setOnClickListener { listener.onItemClick(item, it.id, position) }
            clCommon.setOnClickListener { listener.onItemClick(item, it.id, position) }
        }

        // play video function
        fun playVideoSub(url: String, resumeFrom: Long = 0L) {
            // Don't reinitialize if it's already playing this URL
            if (videoPlayer != null && currentVideoUrl == url && videoPlayer?.isPlaying == true) {
                return
            }

            currentVideoUrl = url

            if (videoPlayer == null) {
                videoPlayer = ExoPlayer.Builder(itemView.context).build().also {
                    postVideoPlayer.player = it

                    it.addListener(object : Player.Listener {
                        override fun onPlaybackStateChanged(state: Int) {
                            when (state) {
                                Player.STATE_READY -> {
                                    videoThumbnail.visibility = View.GONE
                                }

                                Player.STATE_BUFFERING -> {
                                    videoThumbnail.visibility = View.VISIBLE
                                }

                                Player.STATE_ENDED -> {
                                    it.seekTo(0)
                                    it.playWhenReady = true
                                }
                            }
                        }
                    })
                }
            } else {
                videoPlayer?.stop()
                videoPlayer?.clearMediaItems()
            }

            val mediaItem = MediaItem.fromUri(url)
            videoPlayer?.setMediaItem(mediaItem)
            videoPlayer?.prepare()

            if (resumeFrom > 0) {
                videoPlayer?.seekTo(resumeFrom)
            }

            videoPlayer?.playWhenReady = true
        }

        fun pauseVideo() {
            videoPlayer?.playWhenReady = false
            videoThumbnail.visibility = View.VISIBLE
        }

        fun getCurrentPosition(): Long {
            return videoPlayer?.currentPosition ?: 0L
        }


        fun isPlaying(): Boolean {
            return videoPlayer?.isPlaying == true
        }

    }
    // text type
    inner class TextPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCommonPostName: AppCompatTextView = itemView.findViewById(R.id.tvCommonPostName)
        private val tvLike: AppCompatTextView = itemView.findViewById(R.id.tvLike)
        private val tvHour: AppCompatTextView = itemView.findViewById(R.id.tvHour)
        private val tvComment: AppCompatTextView = itemView.findViewById(R.id.tvComment)
        private val tvShare: AppCompatTextView = itemView.findViewById(R.id.tvShare)
        private val ivCommonLike: AppCompatImageView = itemView.findViewById(R.id.ivCommonLike)
        private val ivCommonComment: AppCompatImageView = itemView.findViewById(R.id.ivCommonComment)
        private val ivCommonShare: AppCompatImageView = itemView.findViewById(R.id.ivCommonShare)
        private val ivCommonMenu: AppCompatImageView = itemView.findViewById(R.id.ivCommonMenu)
        private val tvTextUsername: AppCompatTextView = itemView.findViewById(R.id.tvTextUsername)
        private val tvTextTypeSector: AppCompatTextView =
            itemView.findViewById(R.id.tvTextTypeSector)
        private val tvTextPost: AppCompatTextView = itemView.findViewById(R.id.tvTextPost)
        private val tvSubscribe: AppCompatTextView = itemView.findViewById(R.id.tvSubscribe)
        private val ivTextVerification: AppCompatImageView =
            itemView.findViewById(R.id.ivTextVerification)
        private val ivCommonPostProfile: ShapeableImageView = itemView.findViewById(R.id.ivCommonPostProfile)
        private val clCommon: ConstraintLayout = itemView.findViewById(R.id.clCommon)
        fun bind(item: GetUserPostData?, listener: OnItemClickListener, position: Int) {

            // subscription button handel
            if (userid.equals(item?.publisherData?._id)){
                tvSubscribe.visibility = View.GONE
            }else{
                tvSubscribe.visibility = View.VISIBLE
            }

            if (item?.isSubscribed==true){
                tvSubscribe.text = tvSubscribe.context.getString(R.string.subscribed)
                tvSubscribe.setTextColor(ContextCompat.getColor(tvSubscribe.context, R.color.black_040404))
            }else{
                tvSubscribe.text = tvSubscribe.context.getString(R.string.subscribe)
                tvSubscribe.setTextColor(ContextCompat.getColor(tvSubscribe.context, R.color.blue_00bef5))
            }

            val imageUrl = Constants.IMAGE_URL + item?.publisherData?.profilePicture
            val safeImageUrl = imageUrl.takeIf { it.isNotBlank() }
            Glide.with(ivCommonPostProfile.context).load(safeImageUrl)
                .placeholder(R.drawable.progress_animation_small)
                .error(R.drawable.ic_round_account_circle_40)
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(ivCommonPostProfile)
            val rawDate = item?.date
            if (rawDate?.isNotEmpty() == true){
                val date = BindingUtils.convertToDate(rawDate)
                val relative = BindingUtils.DateHelper.formatRelativeDate(date)
                tvHour.text = relative
            }
            when (item?.currentUserLikeCount) {
                0 -> ivCommonLike.setImageResource(R.drawable.ic_like_0_24)
                1 -> ivCommonLike.setImageResource(R.drawable.like_icon1)
                2 -> ivCommonLike.setImageResource(R.drawable.like_icon2)
                3 -> ivCommonLike.setImageResource(R.drawable.like_icon3)
            }


            if (item?.publisherData?.verified == true) {
                ivTextVerification.visibility = View.VISIBLE
            } else {
                ivTextVerification.visibility = View.GONE
            }
            val username = item?.publisherData?.username?.takeIf { it.isNotBlank() } ?: "user"
            tvTextUsername.text = "@$username"
            tvTextPost.text = item?.description
            tvTextTypeSector.text = "${item?.publisherData?.city} , ${item?.publisherData?.country}"
            val fullName = listOfNotNull(
                item?.publisherData?.firstName?.takeIf { it.isNotBlank() },
                item?.publisherData?.lastName?.takeIf { it.isNotBlank() }).joinToString(" ")
                .ifBlank { "User" }

            tvCommonPostName.text = fullName

            tvLike.text = item?.likesCount.toString()
            tvComment.text = item?.commentCount.toString()
            //   tvShare.text = item?.likesCount.toString()

            ivCommonComment.setOnClickListener { listener.onItemClick(item, it.id, position) }
            ivCommonLike.setOnClickListener { listener.onItemClick(item, it.id, position) }
            ivCommonShare.setOnClickListener { listener.onItemClick(item, it.id, position) }
            ivCommonMenu.setOnClickListener { listener.onItemClick(item, it.id, position) }
            tvSubscribe.setOnClickListener { listener.onItemClick(item, it.id, position) }
            ivCommonPostProfile.setOnClickListener { listener.onItemClick(item, it.id, position) }
            tvCommonPostName.setOnClickListener { listener.onItemClick(item, it.id, position) }
            clCommon.setOnClickListener { listener.onItemClick(item, it.id, position) }
        }
    }
    // court type
    inner class CourtPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCommonPostName: AppCompatTextView = itemView.findViewById(R.id.tvCommonPostName)
        private val tvTitleCourtName: AppCompatTextView =
            itemView.findViewById(R.id.tvTitleCourtName)
        private val tvHour: AppCompatTextView = itemView.findViewById(R.id.tvHour)
        private val tvCourtRating: AppCompatTextView = itemView.findViewById(R.id.tvCourtRating)
        private val tvCourtHoops: AppCompatTextView = itemView.findViewById(R.id.tvCourtHoops)
        private val tvCourtKing: AppCompatTextView = itemView.findViewById(R.id.tvCourtKing)
        private val tvCourtCity: AppCompatTextView = itemView.findViewById(R.id.tvCourtCity)
        private val tvCourtDistance: AppCompatTextView = itemView.findViewById(R.id.tvCourtDistance)
        private val tvCourtUsername: AppCompatTextView = itemView.findViewById(R.id.tvCourtUsername)
        private val tvCourtSector: AppCompatTextView = itemView.findViewById(R.id.tvCourtSector)
        private val tvCourtTextPost: AppCompatTextView = itemView.findViewById(R.id.tvCourtTextPost)
        private val ivCommonLike: AppCompatImageView = itemView.findViewById(R.id.ivCommonLike)
        private val ivCommonShare: AppCompatImageView =
            itemView.findViewById(R.id.ivCommonShare)
        private val clCommon: ConstraintLayout = itemView.findViewById(R.id.clCommon)
        private val ivCommonPostProfile: ShapeableImageView = itemView.findViewById(R.id.ivCommonPostProfile)
        private val ivCourt: ShapeableImageView = itemView.findViewById(R.id.ivCourt)
        private val ivCommonComment: AppCompatImageView =
            itemView.findViewById(R.id.ivCommonComment)
        private val ivCommonMenu: AppCompatImageView = itemView.findViewById(R.id.ivCommonMenu)
        private val tvLike: AppCompatTextView = itemView.findViewById(R.id.tvLike)
        private val tvComment: AppCompatTextView = itemView.findViewById(R.id.tvComment)
        private val tvShare: AppCompatTextView = itemView.findViewById(R.id.tvComment)
        private val tvSubscribe: AppCompatTextView = itemView.findViewById(R.id.tvSubscribe)
        fun bind(item: GetUserPostData?, listener: OnItemClickListener, position: Int) {


            // subscription button handel
            if (userid.equals(item?.publisherData?._id)){
                tvSubscribe.visibility = View.GONE
            }else{
                tvSubscribe.visibility = View.VISIBLE
            }

            if (item?.isSubscribed==true){
                tvSubscribe.text = tvSubscribe.context.getString(R.string.subscribed)
                tvSubscribe.setTextColor(ContextCompat.getColor(tvSubscribe.context, R.color.black_040404))
            }else{
                tvSubscribe.text = tvSubscribe.context.getString(R.string.subscribe)
                tvSubscribe.setTextColor(ContextCompat.getColor(tvSubscribe.context, R.color.blue_00bef5))
            }


            val fullName = listOfNotNull(
                item?.publisherData?.firstName?.takeIf { it.isNotBlank() },
                item?.publisherData?.lastName?.takeIf { it.isNotBlank() }).joinToString(" ")
                .ifBlank { "User" }

            tvCommonPostName.text = fullName
            val username = item?.publisherData?.username?.takeIf { it.isNotBlank() } ?: "user"
            tvCourtUsername.text = "@$username"
            val location = listOfNotNull(
                item?.publisherData?.country?.takeIf { it.isNotBlank() },
                item?.publisherData?.city?.takeIf { it.isNotBlank() }).joinToString(", ")

            tvCourtSector.text = location.ifBlank { "" }
            tvCourtTextPost.text = item?.description
            val imageUrl = Constants.IMAGE_URL + item?.publisherData?.profilePicture
            val safeImageUrl = imageUrl.takeIf { it.isNotBlank() }
            Glide.with(ivCommonPostProfile.context).load(safeImageUrl)
                .placeholder(R.drawable.progress_animation_small)
                .error(R.drawable.ic_round_account_circle_40)
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(ivCommonPostProfile)

            tvLike.text = item?.likesCount.toString()
            tvComment.text = item?.commentCount.toString()
            //   tvShare.text = item?.likesCount.toString()


            val firstPhoto = item?.court?.photos?.firstOrNull()
            if (!firstPhoto.isNullOrEmpty()) {
                val imageUrl = Constants.IMAGE_URL + firstPhoto
                Glide.with(ivCourt.context).load(imageUrl)
                    .placeholder(R.drawable.progress_animation_small)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(ivCourt)
            }
            tvTitleCourtName.text = item?.court?.name
            tvCourtRating.text = "${item?.court?.grade ?: 0.0}"
            tvCourtCity.text = item?.court?.address
            tvCourtHoops.text = "${item?.court?.hoopsCount} Hopos"
            tvCourtKing.text = "Became King of the court"

            if (item?.court?.coordinates?.size == 2) {
                val lat1 = item.court.coordinates.getOrNull(1)
                val lon1 = item.court.coordinates.getOrNull(0)
                val lat2 = BindingUtils.lat
                val lon2 = BindingUtils.long
                if (lat1 != null && lon1 != null) {
                    val distance = BindingUtils.formattedDistance(lat1, lon1, lat2, lon2)
                    tvCourtDistance.text = distance
                }
            }

            val rawDate = item?.date
            if (rawDate?.isNotEmpty() == true){
                val date = BindingUtils.convertToDate(rawDate)
                val relative = BindingUtils.DateHelper.formatRelativeDate(date)
                tvHour.text = relative
            }

            when (item?.currentUserLikeCount) {
                0 -> ivCommonLike.setImageResource(R.drawable.ic_like_0_24)
                1 -> ivCommonLike.setImageResource(R.drawable.like_icon1)
                2 -> ivCommonLike.setImageResource(R.drawable.like_icon2)
                3 -> ivCommonLike.setImageResource(R.drawable.like_icon3)
            }

            ivCommonComment.setOnClickListener { listener.onItemClick(item, it.id, position) }
            ivCommonLike.setOnClickListener { listener.onItemClick(item, it.id, position) }
            ivCommonShare.setOnClickListener { listener.onItemClick(item, it.id, position) }
            ivCommonMenu.setOnClickListener { listener.onItemClick(item, it.id, position) }
            tvSubscribe.setOnClickListener { listener.onItemClick(item, it.id, position) }
            ivCommonPostProfile.setOnClickListener { listener.onItemClick(item, it.id, position) }
            tvCommonPostName.setOnClickListener { listener.onItemClick(item, it.id, position) }
            clCommon.setOnClickListener { listener.onItemClick(item, it.id, position) }
        }
    }
    // game type
    inner class GamePostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCommonPostName: AppCompatTextView = itemView.findViewById(R.id.tvCommonPostName)
        private val tvCommonPostDesc: AppCompatTextView = itemView.findViewById(R.id.tvCommonPostDesc)
        private val tvHour: AppCompatTextView = itemView.findViewById(R.id.tvHour)
        private val tvGameTypeUsername: AppCompatTextView =
            itemView.findViewById(R.id.tvGameTypeUsername)
        private val tvGameDate: AppCompatTextView = itemView.findViewById(R.id.tvGameDate)
        private val tvGameTime: AppCompatTextView = itemView.findViewById(R.id.tvGameTime)
        private val tvGameMode: AppCompatTextView = itemView.findViewById(R.id.tvGameMode)
        private val tvGamePlayers: AppCompatTextView = itemView.findViewById(R.id.tvGamePlayers)
        private val tvGameStatus: AppCompatTextView = itemView.findViewById(R.id.tvGameStatus)
        private val tvGameCity: AppCompatTextView = itemView.findViewById(R.id.tvGameCity)
        private val tvGameCountry: AppCompatTextView = itemView.findViewById(R.id.tvGameCountry)
        private val ivCommonLike: AppCompatImageView =
            itemView.findViewById(R.id.ivCommonLike)
        private val ivCommonShare: AppCompatImageView =
            itemView.findViewById(R.id.ivCommonShare)
        private val clCommon: ConstraintLayout = itemView.findViewById(R.id.clCommon)
        private val ivCommonPostProfile: ShapeableImageView =
            itemView.findViewById(R.id.ivCommonPostProfile)
        private val ivCommonComment: AppCompatImageView =
            itemView.findViewById(R.id.ivCommonComment)
        private val ivCommonMenu: AppCompatImageView = itemView.findViewById(R.id.ivCommonMenu)
        private val ivGame: AppCompatImageView = itemView.findViewById(R.id.ivGame)
        private val tvGameLike: AppCompatTextView = itemView.findViewById(R.id.tvGameLike)
        private val tvGameComment: AppCompatTextView = itemView.findViewById(R.id.tvGameComment)
        private val tvGameShare: AppCompatTextView = itemView.findViewById(R.id.tvGameShare)
        private val tvSubscribe: AppCompatTextView = itemView.findViewById(R.id.tvSubscribe)


        fun bind(item: GetUserPostData?, listener: OnItemClickListener, position: Int) {


            // subscription button handel
            if (userid.equals(item?.publisherData?._id)){
                tvSubscribe.visibility = View.GONE
            }else{
                tvSubscribe.visibility = View.VISIBLE
            }

            if (item?.isSubscribed==true){
                tvSubscribe.text = tvSubscribe.context.getString(R.string.subscribed)
                tvSubscribe.setTextColor(ContextCompat.getColor(tvSubscribe.context, R.color.black_040404))
            }else{
                tvSubscribe.text = tvSubscribe.context.getString(R.string.subscribe)
                tvSubscribe.setTextColor(ContextCompat.getColor(tvSubscribe.context, R.color.blue_00bef5))
            }



            val imageUrl = Constants.IMAGE_URL + item?.publisherData?.profilePicture
            val safeImageUrl = imageUrl.takeIf { it.isNotBlank() }
            Glide.with(ivCommonPostProfile.context).load(safeImageUrl)
                .placeholder(R.drawable.progress_animation_small)
                .error(R.drawable.ic_round_account_circle_40)
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(ivCommonPostProfile)

            val fullName = listOfNotNull(
                item?.publisherData?.firstName?.takeIf { it.isNotBlank() },
                item?.publisherData?.lastName?.takeIf { it.isNotBlank() }).joinToString(" ")
                .ifBlank { "User" }

            tvCommonPostName.text = fullName
            val username = item?.publisherData?.username?.takeIf { it.isNotBlank() } ?: "user"
            tvGameTypeUsername.text = "@$username"

            tvCommonPostDesc.text = item?.description ?: ""
            tvGameLike.text = item?.likesCount.toString()
            tvGameComment.text = item?.commentCount.toString()
            //tvGameShare.text = item?.shareCount.toString()

            val dateTime = item?.game?.date
            if (dateTime?.isNotEmpty() == true) {
                val (date, time) = BindingUtils.formatDateTime(dateTime.toString())
                tvGameDate.text = date
                tvGameTime.text = time
            }

            val rawDate = item?.date
            if (rawDate?.isNotEmpty() == true){
                val date = BindingUtils.convertToDate(rawDate)
                val relative = BindingUtils.DateHelper.formatRelativeDate(date)
                tvHour.text = relative
            }

            when (item?.currentUserLikeCount) {
                0 -> ivCommonLike.setImageResource(R.drawable.ic_like_0_24)
                1 -> ivCommonLike.setImageResource(R.drawable.like_icon1)
                2 -> ivCommonLike.setImageResource(R.drawable.like_icon2)
                3 -> ivCommonLike.setImageResource(R.drawable.like_icon3)
            }

            ivCommonComment.setOnClickListener { listener.onItemClick(item, it.id, position) }
            ivCommonLike.setOnClickListener { listener.onItemClick(item, it.id, position) }
            ivCommonShare.setOnClickListener { listener.onItemClick(item, it.id, position) }
            ivCommonMenu.setOnClickListener { listener.onItemClick(item, it.id, position) }
            tvSubscribe.setOnClickListener { listener.onItemClick(item, it.id, position) }
            ivCommonPostProfile.setOnClickListener { listener.onItemClick(item, it.id, position) }
            tvCommonPostName.setOnClickListener { listener.onItemClick(item, it.id, position) }
            clCommon.setOnClickListener { listener.onItemClick(item, it.id, position) }
        }
    }
    // event type
    class EventPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDescription: AppCompatTextView = itemView.findViewById(R.id.tvPublisherName)
        fun bind(item: GetUserPostData?, listener: OnItemClickListener, position: Int) {
            tvDescription.text = item?.description



        }
    }
    // mvp view holder add 0 index data
    class MvpViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val rvMvp: RecyclerView = itemView.findViewById(R.id.rvMvp)

        fun bind(item: MutableList<MpvModel?>, listener: OnItemClickListener, position: Int) {
            rvMvp.layoutManager =
                LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            val mvpAdapter = SimpleRecyclerViewAdapter<MpvModel, RvMpvItemLayoutBinding>(
                R.layout.rv_mpv_item_layout, BR.bean
            ) { v, m, pos ->
                when (v.id) {

                }
            }
            mvpAdapter.list = item
            rvMvp.adapter = mvpAdapter
        }
    }


    interface OnItemClickListener {
        fun onItemClick(item: GetUserPostData?, clickedViewId: Int, position: Int)
    }


}