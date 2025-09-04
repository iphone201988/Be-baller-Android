package com.beballer.beballer.ui.player.dash_board.social

import android.content.Intent
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.data.api.Constants
import com.beballer.beballer.data.api.Constants.userType
import com.beballer.beballer.data.model.CommonResponse
import com.beballer.beballer.data.model.GetUserPostData
import com.beballer.beballer.data.model.GetUserPostResponse
import com.beballer.beballer.data.model.MpvModel
import com.beballer.beballer.databinding.CreateProfileDialogItemDesignBinding
import com.beballer.beballer.databinding.FragmentSocialsBinding
import com.beballer.beballer.databinding.ReportOrDeletePostBottomItemBinding
import com.beballer.beballer.databinding.SubscribeBotomItemBinding
import com.beballer.beballer.databinding.WelcomeDialogItemBinding
import com.beballer.beballer.ui.FeedItem
import com.beballer.beballer.ui.interfacess.AddPostInterface
import com.beballer.beballer.ui.interfacess.CommonPostInterface
import com.beballer.beballer.ui.interfacess.OnNextClickListener
import com.beballer.beballer.ui.interfacess.VideoHandler
import com.beballer.beballer.ui.player.add_post.AddPostActivity
import com.beballer.beballer.ui.player.create_profile.CreateProfileActivity
import com.beballer.beballer.ui.player.dash_board.find.player_profile.PlayerProfileActivity
import com.beballer.beballer.ui.player.dash_board.profile.user.UserProfileActivity
import com.beballer.beballer.ui.player.dash_board.social.adapter.MultiViewAdapter
import com.beballer.beballer.ui.player.dash_board.social.adapter.MultiViewAdapter.VideoPostViewHolder
import com.beballer.beballer.ui.player.dash_board.social.adapter.MultiViewAdapterSub
import com.beballer.beballer.ui.player.dash_board.social.adapter.MultiViewAdapterSub.VideoPostViewHolderSub
import com.beballer.beballer.ui.player.dash_board.social.details.SocialDetailsActivity
import com.beballer.beballer.ui.player.dash_board.social.sub.SubAdapter
import com.beballer.beballer.ui.player.dash_board.social.suggested_user.SuggestedUserActivity
import com.beballer.beballer.utils.BaseCustomBottomSheet
import com.beballer.beballer.utils.BaseCustomDialog
import com.beballer.beballer.utils.BindingUtils
import com.beballer.beballer.utils.Status
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs


@AndroidEntryPoint
class SocialsFragment : BaseFragment<FragmentSocialsBinding>(), VideoHandler, CommonPostInterface,
    AddPostInterface {
    private val viewModel: SocialsFragmentVM by viewModels()
    private lateinit var createProfileDialogItem: BaseCustomDialog<CreateProfileDialogItemDesignBinding>
    private lateinit var welcomeDialogItem: BaseCustomDialog<WelcomeDialogItemBinding>
    private lateinit var homePostAdapter: MultiViewAdapter
    private lateinit var homeSubPostAdapter: MultiViewAdapterSub
    private lateinit var subscribeBottomItem: BaseCustomBottomSheet<SubscribeBotomItemBinding>
    private lateinit var reportOrDeleteBottomItem: BaseCustomBottomSheet<ReportOrDeletePostBottomItemBinding>
    private var currentPage = 1
    private var currentSubPage = 1
    private var isLoading = false
    private var isLoadingSub = false
    private var isLastPageSub = false
    private var isLastPage = false
    private var isProgress = false
    private var isProgressSub = false
    private var postPosition = -1
    private var postSubPosition = -1
    private var postSubscribe = false
    override fun getLayoutResource(): Int {
        return R.layout.fragment_socials
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // set block pos
        binding.pos = 1
        binding.tvFeed.typeface = ResourcesCompat.getFont(requireContext(), R.font.inter_bold)
        binding.tvSubscriptions.typeface =
            ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
        // observer
        initObserver()
        // click
        initOnClick()
        // adapter
        val userId: String =
            sharedPrefManager.getLoginData()?.data?.user?._id.takeIf { !it.isNullOrEmpty() } ?: ""
        initHomeAdapter(userId)
        initHomeSubAdapter(userId)
        // open dialog box
//        if (sharedPrefManager.getLoginData()?.data?.user?.isProfileCompleted == true) {
//            welcomeDialogItem()
//        }
        // add pagination
        pagination()
        paginationSubAdapter()
        // api call
        val put = HashMap<String, Any>()
        put["page"] = currentPage
        put["limit"] = 20
        viewModel.getPostApi(Constants.USER_GET_POST, put)
        // interFace
        SocialDetailsActivity.commonPostInterface = this
        AddPostActivity.addPostInterface = this

        /** Refresh **/
        binding.ssPullRefresh.setColorSchemeResources(
            ContextCompat.getColor(
                requireContext(), R.color.organize_color
            )
        )
        binding.ssPullRefreshSub.setColorSchemeResources(
            ContextCompat.getColor(
                requireContext(), R.color.organize_color
            )
        )
        binding.ssPullRefresh.setOnRefreshListener {
            Handler().postDelayed({
                binding.ssPullRefresh.isRefreshing = false
                currentPage = 1
                userType = 1
                // api call
                val data = hashMapOf<String, Any>()
                data["page"] = currentPage
                data["limit"] = 20
                viewModel.getPostApi(Constants.USER_GET_POST, data)

            }, 2000)
        }
        binding.ssPullRefreshSub.setOnRefreshListener {
            Handler().postDelayed({
                binding.ssPullRefreshSub.isRefreshing = false
                currentSubPage = 1
                userType = 2
                // api call
                val data = hashMapOf<String, Any>()
                data["page"] = currentSubPage
                data["limit"] = 20
                data["isOnlySubscribed"] = true
                viewModel.getPostSubApi(Constants.USER_GET_POST, data)

            }, 2000)
        }
    }


    /**
     * home adapter handel pagination
     */
    private fun pagination() {
        binding.rvHome.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                        loadMoreItems()
                    }
                }

            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    playCenterVideoRvAdapter(recyclerView)
                }
            }
        })

    }

    /**
     * sub  adapter handel pagination
     */
    private fun paginationSubAdapter() {
        binding.rvHomeSub.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                if (!isLoadingSub && !isLastPageSub) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                        loadMoreSubItems()
                    }
                }

            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    playCenterVideoSubAdapter(recyclerView)
                }
            }
        })

    }

    /**
     * play video handel rv adapter
     */
    private fun playCenterVideoRvAdapter(recyclerView: RecyclerView) {
        recyclerView.layoutManager as? LinearLayoutManager ?: return
        val center = recyclerView.height / 2

        var closestDistance = Int.MAX_VALUE
        var centerView: View? = null
        var centerPosition = -1

        for (i in 0 until recyclerView.childCount) {
            val child = recyclerView.getChildAt(i)
            val childCenter = (child.top + child.bottom) / 2
            val distance = abs(center - childCenter)

            if (distance < closestDistance) {
                closestDistance = distance
                centerView = child
                centerPosition = recyclerView.getChildAdapterPosition(child)
            }
        }

        if (centerPosition != -1) {
            val adapter = recyclerView.adapter as? MultiViewAdapter ?: return
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(centerPosition)

            if (viewHolder is VideoPostViewHolder) {
                val currentPlaying = adapter.currentlyPlayingHolder
                if (currentPlaying != null && currentPlaying == viewHolder && currentPlaying.isPlaying()) {
                    return
                }
                adapter.pauseCurrentlyPlaying()
                val resumePosition = adapter.playbackPositions[centerPosition] ?: 0L
                adapter.getList()[centerPosition]

                val post = adapter.getPostAt(centerPosition)
                val url = Constants.IMAGE_URL + (post?.video ?: "")

                viewHolder.playVideo(url, resumePosition)
                adapter.setCurrentlyPlayingHolder(viewHolder)
            } else {
                adapter.pauseCurrentlyPlaying()
            }
        }
    }

    /**
     * play video handel sub adapter
     */
    private fun playCenterVideoSubAdapter(recyclerView: RecyclerView) {
        recyclerView.layoutManager as? LinearLayoutManager ?: return
        val center = recyclerView.height / 2

        var closestDistance = Int.MAX_VALUE
        var centerView: View? = null
        var centerPosition = -1

        for (i in 0 until recyclerView.childCount) {
            val child = recyclerView.getChildAt(i)
            val childCenter = (child.top + child.bottom) / 2
            val distance = abs(center - childCenter)

            if (distance < closestDistance) {
                closestDistance = distance
                centerView = child
                centerPosition = recyclerView.getChildAdapterPosition(child)
            }
        }

        if (centerPosition != -1) {
            val adapter = recyclerView.adapter as? MultiViewAdapterSub ?: return
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(centerPosition)

            if (viewHolder is VideoPostViewHolderSub) {
                val currentPlaying = adapter.currentlyPlayingHolder
                if (currentPlaying != null && currentPlaying == viewHolder && currentPlaying.isPlaying()) {
                    return
                }
                adapter.pauseCurrentlyPlaying()
                val resumePosition = adapter.playbackPositions[centerPosition] ?: 0L
                adapter.getList()[centerPosition]

                val post = adapter.getPostAt(centerPosition)
                val url = Constants.IMAGE_URL + (post?.video ?: "")

                viewHolder.playVideoSub(url, resumePosition)
                adapter.setCurrentlyPlayingHolder(viewHolder)
            } else {
                adapter.pauseCurrentlyPlaying()
            }
        }
    }

    /**
     * pause video handel if playing change fragment or tab
     */
    override fun pauseVideoIfPlaying() {
        binding.rvHomeSub.adapter?.let { (it as? MultiViewAdapterSub)?.pauseCurrentlyPlaying() }
        binding.rvHome.adapter?.let { (it as? MultiViewAdapter)?.pauseCurrentlyPlaying() }
    }


    /**
     * on resume call
     */
    override fun onResume() {
        super.onResume()
        if (userType == 2) {
            playCenterVideoSubAdapter(binding.rvHomeSub)
        } else {
            playCenterVideoRvAdapter(binding.rvHome)
        }
    }

    /**
     *  load more function call
     **/
    private fun loadMoreItems() {
        isLoading = true
        homePostAdapter.showLoader()
        currentPage++
        val data = hashMapOf<String, Any>(
            "page" to currentPage, "limit" to 20
        )

        viewModel.getPostApi(Constants.USER_GET_POST, data)
    }

    /**
     *  load more sub function call
     **/
    private fun loadMoreSubItems() {
        isLoadingSub = true
        homeSubPostAdapter.showLoader()
        currentSubPage++
        val data = hashMapOf<String, Any>("page" to currentSubPage, "limit" to 20)
        data["isOnlySubscribed"] = true

        viewModel.getPostSubApi(Constants.USER_GET_POST, data)
    }


    /**
     * handle click
     */
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                // iv notifications
                R.id.ivNotification -> {
                    val intent = Intent(requireContext(), UserProfileActivity::class.java)
                    intent.putExtra("userType", "notification")
                    startActivity(intent)
                    requireActivity().overridePendingTransition(
                        R.anim.slide_in_right, R.anim.slide_out_left
                    )
                }
                // tvFeed button click
                R.id.tvFeed -> {
                    binding.pos = 1
                    userType = 1
                    binding.tvFeed.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_bold)
                    binding.tvSubscriptions.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
                    pauseVideoIfPlaying()
                }
                // tvSubscriptions  button click
                R.id.tvSubscriptions -> {
                    userType = 2
                    binding.pos = 2
                    binding.tvFeed.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
                    binding.tvSubscriptions.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_bold)
                    pauseVideoIfPlaying()
                    if (homeSubPostAdapter.getList().isEmpty()) {
                        currentSubPage = 1
                        // api call
                        val data = hashMapOf<String, Any>()
                        data["page"] = currentSubPage
                        data["limit"] = 20
                        data["isOnlySubscribed"] = true
                        viewModel.getPostSubApi(Constants.USER_GET_POST, data)
                    }
                }
                // floating button click
                R.id.buttonSettings -> {
                    pauseVideoIfPlaying()
                    val intent = Intent(requireContext(), AddPostActivity::class.java)
                    startActivity(intent)
                    requireActivity().overridePendingTransition(R.anim.slide_in_bottom, R.anim.no_animation)
                }
                // sub text click
                R.id.tvSubscription -> {
                    val intent = Intent(requireContext(), SuggestedUserActivity::class.java)
                    startActivity(intent)
                    requireActivity().overridePendingTransition(R.anim.slide_in_bottom, R.anim.no_animation)
                }
            }
        }
    }


    /****
     * create profile dialog item
     */
    private fun createProfileDialogItem() {
        createProfileDialogItem = BaseCustomDialog<CreateProfileDialogItemDesignBinding>(
            requireContext(), R.layout.create_profile_dialog_item_design
        ) {
            when (it?.id) {
                // let,s go button click
                R.id.btnNext -> {
                    val intent = Intent(requireContext(), CreateProfileActivity::class.java)
                    startActivity(intent)
                    createProfileDialogItem.dismiss()
                    requireActivity().overridePendingTransition(
                        R.anim.slide_in_right, R.anim.slide_out_left
                    )
                }
            }

        }
        createProfileDialogItem.create()
        createProfileDialogItem.show()

    }

    /****
     * welcome dialog item
     */
    private fun welcomeDialogItem() {
        welcomeDialogItem = BaseCustomDialog<WelcomeDialogItemBinding>(
            requireContext(), R.layout.welcome_dialog_item
        ) {}
        welcomeDialogItem.create()
        welcomeDialogItem.show()

        val adapter = SubAdapter(requireActivity(), object : OnNextClickListener {
            override fun onNextClicked() {
                val viewPager = welcomeDialogItem.binding.viewPager
                val currentItem = viewPager.currentItem
                if (currentItem == 1) {
                    viewPager.setCurrentItem(2, true)
                } else if (currentItem == 2) {
                    welcomeDialogItem.dismiss()
                    createProfileDialogItem()
                }
            }
        })

        val viewPager = welcomeDialogItem.binding.viewPager
        viewPager.adapter = adapter
        viewPager.isUserInputEnabled = true
        viewPager.setCurrentItem(1, false)
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    when (viewPager.currentItem) {
                        0 -> viewPager.setCurrentItem(2, false)
                        3 -> viewPager.setCurrentItem(1, false)
                    }
                }
            }
        })
    }


    /**
     * handle api response
     */
    private fun initObserver() {
        viewModel.commonObserver.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.LOADING -> {
                    if (!isProgress) {
                        showLoading()
                    }
                }

                Status.SUCCESS -> {
                    hideLoading()
                    homePostAdapter.hideLoader()
                    when (it.message) {
                        "getPostApi" -> {
                            val myDataModel: GetUserPostResponse? =
                                BindingUtils.parseJson(it.data.toString())
                            if (myDataModel?.data != null) {
                                isProgress = true
                                isLoading = false
                                isLastPage = false
                                if (currentPage == 1) {
                                    myDataModel.data.let {
                                        homePostAdapter.setList(it, getList())
                                    }
                                } else {
                                    homePostAdapter.addToList(myDataModel.data)
                                }

                                isLastPage = currentPage == myDataModel.pagination?.totalPages


                            }
                        }


                        "postLikeApi" -> {
                            val myDataModel: CommonResponse? =
                                BindingUtils.parseJson(it.data.toString())
                            if (myDataModel?.success == true) {
                                showSuccessToast("Liked successfully")
                                val item = homePostAdapter.getList()[postPosition]
                                if (item is FeedItem.Post) {
                                    val post = item.post
                                    post.likesCount = (post.likesCount ?: 0) + 1
                                    post.currentUserLikeCount = (post.currentUserLikeCount ?: 0) + 1
                                    homePostAdapter.notifyItemChanged(postPosition)
                                }
                            }
                        }


                        "postSubscribeApi" -> {
                            val myDataModel: CommonResponse? =
                                BindingUtils.parseJson(it.data.toString())
                            if (myDataModel?.success == true) {
                                showSuccessToast(myDataModel.message.toString())
                                val item = homePostAdapter.getList()[postPosition]
                                if (item is FeedItem.Post) {
                                    val post = item.post
                                    post.isSubscribed = !postSubscribe
                                    homePostAdapter.notifyItemChanged(postPosition)
                                }
                            }
                        }

                        "reportOrDeletePostApi" -> {
                            val myDataModel: CommonResponse? =
                                BindingUtils.parseJson(it.data.toString())
                            if (myDataModel?.success == true) {
                                showSuccessToast(myDataModel.message.toString())
                                homePostAdapter.removeAt(postPosition)
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
        // sub observer
        viewModel.commonObserverSub.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.LOADING -> {
                    if (!isProgressSub) {
                        showLoading()
                    }
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "getPostSubApi" -> {
                            try {
                                val myDataModel: GetUserPostResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    isProgressSub = true
                                    isLoadingSub = false
                                    isLastPageSub = false
                                    if (!myDataModel.data.isNullOrEmpty()) {
                                        if (currentSubPage == 1) {
                                            myDataModel.data.let { homeSubPostAdapter.setList(it, getList()) }
                                        } else {
                                            homeSubPostAdapter.addToList(myDataModel.data)
                                        }
                                        isLastPageSub = currentSubPage == myDataModel.pagination?.totalPages
                                        binding.tvSubscription.visibility = View.GONE
                                    } else {
                                        binding.tvSubscription.visibility = View.VISIBLE
                                    }

                                } else {
                                    binding.tvSubscription.visibility = View.GONE
                                }
                            } catch (e: Exception) {
                                Log.e("error", "getPostSubApi: $e")
                            } finally {
                                homeSubPostAdapter.hideLoader()
                                hideLoading()
                            }
                        }

                        "postLikeSubApi" -> {
                            try {
                                val myDataModel: CommonResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel?.success == true) {
                                    showSuccessToast("Liked successfully")
                                    val item = homeSubPostAdapter.getList()[postSubPosition]
                                    if (item is FeedItem.Post) {
                                        val post = item.post
                                        post.likesCount = (post.likesCount ?: 0) + 1
                                        post.currentUserLikeCount =
                                            (post.currentUserLikeCount ?: 0) + 1
                                        homeSubPostAdapter.notifyItemChanged(postSubPosition)
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("error", "postLikeSubApi: $e")
                            } finally {
                                hideLoading()
                            }
                        }

                        "postSubscribeSubApi" -> {
                            try {
                                val myDataModel: CommonResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel?.success == true) {
                                    showSuccessToast(myDataModel.message.toString())
                                    val item = homeSubPostAdapter.getList()[postSubPosition]
                                    if (item is FeedItem.Post) {
                                        val post = item.post
                                        post.isSubscribed = !postSubscribe
                                        homeSubPostAdapter.notifyItemChanged(postSubPosition)
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("error", "postSubscribeSubApi: $e")
                            } finally {
                                hideLoading()
                            }
                        }

                        "reportOrDeletePostSubApi" -> {
                            try {
                                val myDataModel: CommonResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel?.success == true) {
                                    showSuccessToast(myDataModel.message.toString())
                                    homeSubPostAdapter.removeAt(postSubPosition)
                                }
                            } catch (e: Exception) {
                                Log.e("error", "reportOrDeletePostSubApi: $e")
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

    /***
     * adapter home adapter inlined
     */
    private fun initHomeAdapter(userId: String) {
        homePostAdapter = MultiViewAdapter(object : MultiViewAdapter.OnItemClickListener {
            override fun onItemClick(item: GetUserPostData?, clickedViewId: Int, position: Int) {
                binding.rvHome.adapter?.let { (it as? MultiViewAdapter)?.pauseCurrentlyPlaying() }
                postPosition = position
                when (clickedViewId) {
                    R.id.clCommon, R.id.ivCommonComment -> {
                        val intent = Intent(requireActivity(), SocialDetailsActivity::class.java)
                        intent.putExtra("socialDetails", item?.contentType)
                        intent.putExtra("socialPos", position)
                        intent.putExtra("socialData", item)
                        startActivity(intent)
                    }
                    // menu
                    R.id.ivCommonMenu -> {
                        reportOrDeleteBottomItem(item?.publisherData?._id, item?._id, 1)
                    }
                    //share
                    R.id.ivCommonShare -> {
                        showInfoToast("ivCommonShare")
                    }
                    // subscribe
                    R.id.tvSubscribe -> {
                        postSubscribe = item?.isSubscribed == true
                        subscribeBottomSheet(item?.isSubscribed, item?.publisherData?._id, 1)
                    }
                    // post name and profile image
                    R.id.ivCommonPostProfile, R.id.tvCommonPostName -> {
                        val intent = Intent(requireContext(), PlayerProfileActivity::class.java)
                        intent.putExtra("playerProfile", item?.publisherData?._id)
                        startActivity(intent)
                        requireActivity().overridePendingTransition(
                            R.anim.slide_in_right, R.anim.slide_out_left
                        )
                    }
                    // like
                    R.id.ivCommonLike -> {
                        item?.let { safeItem ->
                            if (!safeItem._id.isNullOrEmpty()) {
                                if ((safeItem.currentUserLikeCount ?: 0) < 3) {
                                    isProgress = false
                                    viewModel.postLikeApi("${Constants.USER_POST_LIKE}?postId=${safeItem._id}")
                                } else {
                                    showInfoToast("You can like only up to 3 times.")
                                }
                            }
                        }
                    }
                }
            }
        }, userId)
        binding.rvHome.adapter = homePostAdapter
    }

    /***
     * adapter sub adapter inlined
     */
    private fun initHomeSubAdapter(userId: String) {
        homeSubPostAdapter = MultiViewAdapterSub(object : MultiViewAdapterSub.OnItemClickListener {
            override fun onItemClick(item: GetUserPostData?, clickedViewId: Int, position: Int) {
                binding.rvHomeSub.adapter?.let { (it as? MultiViewAdapterSub)?.pauseCurrentlyPlaying() }
                postSubPosition = position
                when (clickedViewId) {
                    R.id.clCommon, R.id.ivCommonComment -> {
                        val intent = Intent(requireActivity(), SocialDetailsActivity::class.java)
                        intent.putExtra("socialDetails", item?.contentType)
                        intent.putExtra("socialPos", position)
                        intent.putExtra("socialData", item)
                        startActivity(intent)
                        showInfoToast("clCommon")
                    }
                    // menu
                    R.id.ivCommonMenu -> {
                        reportOrDeleteBottomItem(item?.publisherData?._id, item?._id, 2)
                    }
                    //share
                    R.id.ivCommonShare -> {
                        showInfoToast("ivCommonShare")
                    }
                    // subscribe
                    R.id.tvSubscribe -> {
                        postSubscribe = item?.isSubscribed == true
                        subscribeBottomSheet(item?.isSubscribed, item?.publisherData?._id, 2)
                    }
                    // post name and profile image
                    R.id.ivCommonPostProfile, R.id.tvCommonPostName -> {
                        val intent = Intent(requireContext(), PlayerProfileActivity::class.java)
                        intent.putExtra("playerProfile", item?.publisherData?._id)
                        startActivity(intent)
                        requireActivity().overridePendingTransition(
                            R.anim.slide_in_right, R.anim.slide_out_left
                        )
                    }

                    // like
                    R.id.ivCommonLike -> {
                        item?.let { safeItem ->
                            if (!safeItem._id.isNullOrEmpty()) {
                                if ((safeItem.currentUserLikeCount ?: 0) < 3) {
                                    isProgress = false
                                    viewModel.postLikeSubApi("${Constants.USER_POST_LIKE}?postId=${safeItem._id}")
                                } else {
                                    showInfoToast("You can like only up to 3 times.")
                                }
                            }
                        }
                    }
                }
            }
        }, userId)
        binding.rvHomeSub.adapter = homeSubPostAdapter
    }

    // add List in data
    private fun getList(): ArrayList<MpvModel> {
        return arrayListOf(
            MpvModel("Elliot Le Gall", "Camaret-sur-Mer", "131pts"),
            MpvModel("Leo Florentin", "Forcalquier", "175pts"),
            MpvModel("Elliot Le Gall", "Camaret-sur-Mer", "11pts"),
            MpvModel("Leo Florentin", "Forcalquier", "75pts"),
            MpvModel("Elliot Le Gall", "Camaret-sur-Mer", "131pts"),
            MpvModel("Leo Florentin", "Forcalquier", "120pts"),
            MpvModel("Elliot Le Gall", "Camaret-sur-Mer", "131pts"),
            MpvModel("Leo Florentin", "Forcalquier", "100pts"),

            )
    }

    /***
     * add like interface
     */
    override fun likeCount(
        likeCount: Int, currentLikeCount: Int, position: Int
    ) {
        if (position != -1) {
            if (userType == 2) {
                homeSubPostAdapter.updateLikesAt(position, likeCount, currentLikeCount)
            } else {
                homePostAdapter.updateLikesAt(position, likeCount, currentLikeCount)
            }

        }
    }

    /**
     * add comment interface
     */
    override fun commentCount(commentCount: Int, position: Int) {
        if (position != -1) {
            if (userType == 2) {
                homeSubPostAdapter.updateCommentCount(position, commentCount)
            } else {
                homePostAdapter.updateCommentCount(position, commentCount)
            }
        }
    }

    /**
     *  add post interface
     */
    override fun addPost(isChecked: Boolean) {
        pauseVideoIfPlaying()
        if (userType == 2) {
            isProgressSub = false
            currentSubPage = 1
            // api call
            val data = hashMapOf<String, Any>("page" to currentSubPage, "limit" to 20)
            data["isOnlySubscribed"] = true
            viewModel.getPostSubApi(Constants.USER_GET_POST, data)
        } else {
            isProgress = false
            currentPage = 1
            // api call
            val data = hashMapOf<String, Any>("page" to currentPage, "limit" to 20)
            data["isOnlySubscribed"] = true
            viewModel.getPostApi(Constants.USER_GET_POST, data)
        }

    }

    /**
     * subscribe bottom sheet
     */
    private fun subscribeBottomSheet(subscribe: Boolean?, userId: String?, type: Int) {
        subscribeBottomItem =
            BaseCustomBottomSheet(requireContext(), R.layout.subscribe_botom_item) {
                when (it?.id) {
                    R.id.tvCancel -> {
                        subscribeBottomItem.dismiss()
                    }

                    R.id.tvSubscribe -> {
                        subscribeBottomItem.dismiss()
                        val subscribeUser = subscribe == true
                        val data = HashMap<String, Any>()
                        userId?.let {
                            if (type == 1) {
                                isProgress = false
                                data["subscribed"] = !subscribeUser
                                viewModel.postSubscribeApi(
                                    Constants.USER_SUBSCRIBE + "?id=$it", data
                                )
                            } else {
                                isProgressSub = false
                                data["subscribed"] = !subscribeUser
                                viewModel.postSubscribeSubApi(
                                    Constants.USER_SUBSCRIBE + "?id=$it", data
                                )
                            }
                        }

                    }

                }
            }
        subscribeBottomItem.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        subscribeBottomItem.behavior.isDraggable = true
        subscribeBottomItem.create()
        subscribeBottomItem.show()

        if (subscribe == true) {
            subscribeBottomItem.binding.tvSubscribe.text = "Unsubscribe"
            subscribeBottomItem.binding.tvSubscribe.setTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.red_F27070
                )
            )
        } else {
            subscribeBottomItem.binding.tvSubscribe.setTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.blue
                )
            )
        }

    }

    /***
     * report or delete bottom sheet
     */
    private fun reportOrDeleteBottomItem(userId: String?, postId: String?, type: Int) {
        reportOrDeleteBottomItem =
            BaseCustomBottomSheet(requireContext(), R.layout.report_or_delete_post_bottom_item) {
                when (it?.id) {
                    R.id.tvCancel -> {
                        reportOrDeleteBottomItem.dismiss()
                    }

                    R.id.tvReport -> {
                        reportOrDeleteBottomItem.dismiss()
                        val data = HashMap<String, Any>()
                        postId?.let {
                            if (type == 1) {
                                isProgress = false
                                data["postId"] = it
                                data["type"] = "report"
                                viewModel.reportOrDeletePostApi(
                                    Constants.REPORT_OR_DELETE_POST, data
                                )
                            } else {
                                isProgressSub = false
                                data["postId"] = it
                                data["type"] = "report"
                                viewModel.reportOrDeletePostSubApi(
                                    Constants.REPORT_OR_DELETE_POST, data
                                )
                            }
                        }

                    }

                    R.id.tvDelete -> {
                        reportOrDeleteBottomItem.dismiss()
                        val data = HashMap<String, Any>()
                        postId?.let {
                            if (type == 1) {
                                isProgress = false
                                data["postId"] = it
                                data["type"] = "delete"
                                viewModel.reportOrDeletePostApi(
                                    Constants.REPORT_OR_DELETE_POST, data
                                )
                            } else {
                                isProgressSub = false
                                data["postId"] = it
                                data["type"] = "delete"
                                viewModel.reportOrDeletePostSubApi(
                                    Constants.REPORT_OR_DELETE_POST, data
                                )
                            }

                        }

                    }

                }
            }
        reportOrDeleteBottomItem.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        reportOrDeleteBottomItem.behavior.isDraggable = true
        reportOrDeleteBottomItem.create()
        reportOrDeleteBottomItem.show()

        if (userId.equals(sharedPrefManager.getLoginData()?.data?.user?._id)) {
            reportOrDeleteBottomItem.binding.tvReport.visibility = View.VISIBLE
            reportOrDeleteBottomItem.binding.tvDelete.visibility = View.VISIBLE
            reportOrDeleteBottomItem.binding.vertical.visibility = View.VISIBLE
        } else {
            reportOrDeleteBottomItem.binding.tvReport.visibility = View.VISIBLE
            reportOrDeleteBottomItem.binding.tvDelete.visibility = View.GONE
            reportOrDeleteBottomItem.binding.vertical.visibility = View.GONE
        }

    }


}
