package com.beballer.beballer.ui.player.dash_board.find.player_profile.posts

import android.content.Intent
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beballer.beballer.BR
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.base.SimpleRecyclerViewAdapter
import com.beballer.beballer.data.api.Constants
import com.beballer.beballer.data.model.PlayerData
import com.beballer.beballer.data.model.PlayerProfileResponse
import com.beballer.beballer.databinding.FragmentProfilePostsBinding
import com.beballer.beballer.databinding.PlayerPostRvItemBinding
import com.beballer.beballer.ui.player.post_details.PlayerPostDetailsActivity
import com.beballer.beballer.utils.BindingUtils
import com.beballer.beballer.utils.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfilePostsFragment : BaseFragment<FragmentProfilePostsBinding>() {
    private val viewModel: ProfilePostsFragmentVM by viewModels()
    private var currentPage = 1
    private var isLastPage = false
    private var isLoading = false
    private lateinit var playerPostAdapter: SimpleRecyclerViewAdapter<PlayerData, PlayerPostRvItemBinding>
    override fun getLayoutResource(): Int {
        return R.layout.fragment_profile_posts
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // adapter
        initAvatarAdapter()
        // observer
        initObserver()

        // api call
        val data = HashMap<String, Any>()
        var userId = sharedPrefManager.getLoginData()?.data?.user?.id
        data["page"] = currentPage
        data["limit"] = 20
        data["type"] = "players"
        viewModel.postPublisherId(Constants.POST_PUBLISHER_ID + "$userId", data)
        // pagination
        paginationHandel()
    }


    /**
     * post adapter handel pagination
     */
    private fun paginationHandel() {
        binding.rvPost.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
        })

    }


    /**
     *  load more function call
     **/
    private fun loadMoreItems() {
        isLoading = true
        currentPage++
        val data = HashMap<String, Any>()
        var userId = sharedPrefManager.getLoginData()?.data?.user?.id
        data["page"] = currentPage
        data["limit"] = 20
        data["type"] = "players"
        viewModel.postPublisherId(Constants.POST_PUBLISHER_ID + "$userId", data)
    }


    /** handle api response **/
    private fun initObserver() {
        viewModel.commonObserver.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()

                }

                Status.SUCCESS -> {
                    hideLoading()
                    when (it.message) {
                        "postPublisherId" -> {
                            val myDataModel: PlayerProfileResponse? =
                                BindingUtils.parseJson(it.data.toString())
                            if (myDataModel?.data != null) {
                                if (currentPage == 1) {
                                    myDataModel.data.let {
                                        playerPostAdapter.setList(it)
                                    }
                                } else {
                                    playerPostAdapter.addToList(myDataModel.data)
                                }

                                isLastPage = currentPage == myDataModel.pagination?.totalPages


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
    private fun initAvatarAdapter() {
        playerPostAdapter =
            SimpleRecyclerViewAdapter(R.layout.player_post_rv_item, BR.bean) { v, m, pos ->
                when (v.id) {
                    R.id.clPlayerPost -> {
                        val intent =
                            Intent(requireActivity(), PlayerPostDetailsActivity::class.java)
                        intent.putExtra("playerPost", m)
                        startActivity(intent)
                    }
                }
            }

        binding.rvPost.adapter = playerPostAdapter
    }


}