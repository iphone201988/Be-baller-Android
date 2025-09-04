package com.beballer.beballer.ui.player.dash_board.profile.followers

import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import com.beballer.beballer.BR
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseActivity
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.base.SimpleRecyclerViewAdapter
import com.beballer.beballer.data.api.Constants
import com.beballer.beballer.data.model.CommonResponse
import com.beballer.beballer.data.model.FollowerUser
import com.beballer.beballer.data.model.FollowersResponse
import com.beballer.beballer.data.model.FollowingResponse
import com.beballer.beballer.data.model.FollowingUser
import com.beballer.beballer.data.model.PlayerTeamData
import com.beballer.beballer.data.model.UserProfile
import com.beballer.beballer.databinding.ActivityFollowersAndFollowingBinding
import com.beballer.beballer.databinding.RvFollowersItemBinding
import com.beballer.beballer.databinding.RvFollowingItemBinding
import com.beballer.beballer.databinding.TeamRvItemBinding
import com.beballer.beballer.ui.player.dash_board.DashboardActivity
import com.beballer.beballer.ui.player.dash_board.profile.edit_profile.EditProfileFragment
import com.beballer.beballer.ui.player.dash_board.profile.team.TeamFragment.Companion.teamType
import com.beballer.beballer.utils.BindingUtils
import com.beballer.beballer.utils.Resource
import com.beballer.beballer.utils.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FollowersAndFollowingActivity : BaseActivity<ActivityFollowersAndFollowingBinding>() {
    private val viewModel: FollowersAndFollowingActivityVM by viewModels()
    private lateinit var followingAdapter: SimpleRecyclerViewAdapter<FollowingUser, RvFollowingItemBinding>
    private lateinit var followersAdapter: SimpleRecyclerViewAdapter<FollowerUser, RvFollowersItemBinding>
    private lateinit var fullListFollowing: List<FollowingUser>
    private lateinit var fullListFollowers: List<FollowerUser>
    private val currentPage = 1
    override fun getLayoutResource(): Int {
        return R.layout.activity_followers_and_following
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        // intent get data
        val type = intent.getStringExtra("FollowersType")
        type?.let {
            val isFollowers = it.equals("Followers", ignoreCase = true)
            // Toggle visibility
            binding.svFollowersSearchView.visibility = if (isFollowers) View.VISIBLE else View.GONE
            binding.svFollowingsSearchView.visibility = if (isFollowers) View.GONE else View.VISIBLE
            binding.rvFollowers.visibility = if (isFollowers) View.VISIBLE else View.GONE
            binding.rvFollowing.visibility = if (isFollowers) View.GONE else View.VISIBLE

            // Update title
            binding.tvFollowers.text = getString(if (isFollowers) R.string.followers else R.string.following)

            // Common data map
            val data = hashMapOf<String, Any>(
                "type" to it.lowercase(),
                "page" to currentPage,
                "limit" to 10,
                "userId" to (sharedPrefManager.getLoginData()?.data?.user?._id.orEmpty())
            )

            // Call correct API
            if (isFollowers) {
                viewModel.getFollowersFollowingApi(Constants.GET_FOLLOWERS_FOLLOWING, data)
            } else {
                viewModel.getFollowingsFollowingApi(Constants.GET_FOLLOWERS_FOLLOWING, data)
            }
        }

        //CLick
        initOnClick()

        // adapter
        initFollowingAdapter()
        initFollowersAdapter()
        // observer
        initObserver()
    }

    /** handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(this@FollowersAndFollowingActivity) {
            when (it?.id) {
                R.id.ivBack -> {
                    finish()
                }

            }
        }
    }

    /**  api response observer  **/
    private fun initObserver() {
        viewModel.commonObserver.observe(this@FollowersAndFollowingActivity) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }
                Status.SUCCESS -> {
                    when (it.message) {
                        "getFollowingsFollowingApi" -> {
                            try {
                                val myDataModel: FollowingResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    if (myDataModel.data != null) {
                                        fullListFollowing = myDataModel.data.followingUser as List<FollowingUser>
                                       followingAdapter.list = fullListFollowing
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("error", "getFollowersFollowingApi: $e")
                            } finally {
                                hideLoading()
                            }
                        }

                        "getFollowersFollowingApi" -> {
                            try {
                                val myDataModel: FollowersResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    if (myDataModel.data != null) {
                                        fullListFollowers = myDataModel.data.followerUser as List<FollowerUser>
                                        followersAdapter.list = fullListFollowers
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("error", "getFollowersFollowingApi: $e")
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
    /** handle adapter **/
    private fun initFollowingAdapter() {
        followingAdapter = SimpleRecyclerViewAdapter(R.layout.rv_following_item, BR.bean) { v, m, pos ->
            when (v.id) {

            }
        }

        binding.rvFollowing.adapter = followingAdapter
        setupFollowingSearch()
    }

    private fun initFollowersAdapter() {
        followersAdapter = SimpleRecyclerViewAdapter(R.layout.rv_followers_item, BR.bean) { v, m, pos ->
            when (v.id) {

            }
        }

        binding.rvFollowers.adapter = followersAdapter
        setupFollowersSearch()
    }


    /*** add search ***/
    private fun setupFollowersSearch() {
        val searchView = binding.svFollowersSearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                val filtered = if (!newText.isNullOrBlank()) {
                    fullListFollowers.filter {
                        it.firstName?.startsWith(newText, ignoreCase = true) == true ||   it.lastName?.startsWith(newText, ignoreCase = true) == true
                    }
                } else {
                    fullListFollowers
                }
                followersAdapter.list = filtered
                followersAdapter.notifyDataSetChanged()
                return true
            }
        })
    }

    private fun setupFollowingSearch() {
        val searchView = binding.svFollowingsSearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                val filtered = if (!newText.isNullOrBlank()) {
                    fullListFollowing.filter {
                        it.firstName?.startsWith(newText, ignoreCase = true) == true ||   it.lastName?.startsWith(newText, ignoreCase = true) == true
                    }
                } else {
                    fullListFollowing
                }
                followingAdapter.list = filtered
                followingAdapter.notifyDataSetChanged()
                return true
            }
        })
    }




}