package com.beballer.beballer.ui.player.dash_board.social.suggested_user

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beballer.beballer.BR
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseActivity
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.base.SimpleRecyclerViewAdapter
import com.beballer.beballer.data.api.Constants
import com.beballer.beballer.data.model.CommonResponse
import com.beballer.beballer.data.model.GetSuggestedResponse
import com.beballer.beballer.data.model.SuggestedUser
import com.beballer.beballer.databinding.ActivitySuggestedUserBinding
import com.beballer.beballer.databinding.RvSuggestedItemBinding
import com.beballer.beballer.databinding.SubscribeBotomItemBinding
import com.beballer.beballer.ui.player.add_post.AddPostActivity.Companion.addPostInterface
import com.beballer.beballer.ui.player.dash_board.find.player_profile.PlayerProfileActivity
import com.beballer.beballer.utils.BaseCustomBottomSheet
import com.beballer.beballer.utils.BindingUtils
import com.beballer.beballer.utils.Status
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SuggestedUserActivity : BaseActivity<ActivitySuggestedUserBinding>() {
    private val viewModel: SuggestedUserActivityVM by viewModels()
    private lateinit var subscribeBottomItem: BaseCustomBottomSheet<SubscribeBotomItemBinding>
    private lateinit var suggestedAdapter: SimpleRecyclerViewAdapter<SuggestedUser, RvSuggestedItemBinding>
    private lateinit var suggestedList: List<SuggestedUser>
    private var currentPage = 1
    private var playerPosition = -1
    private var postSubscribe = false
    private var isLoading = false
    private var isLastPage = false
    override fun getLayoutResource(): Int {
        return R.layout.activity_suggested_user
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        // api call
        val put = HashMap<String, Any>()
        put["page"] = currentPage
        put["limit"] = 20
        viewModel.getSuggestedPlayers(Constants.GET_SUGGESTED_PLAYERS, put)

        // observer
        initObserver()
        // adapter
        initSuggestedAdapter()
        // bottom sheet behaviour
        initBottomSheet()
        // add pagination
        paginationSubAdapter()
    }


    /**
     * sub  adapter handel pagination
     */
    private fun paginationSubAdapter() {
        binding.rvSuggested.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
        val put = HashMap<String, Any>()
        put["page"] = currentPage
        put["limit"] = 20
        viewModel.getSuggestedPlayers(Constants.GET_SUGGESTED_PLAYERS, put)
    }

    /** handle adapter **/
    private fun initSuggestedAdapter() {
        suggestedAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_suggested_item, BR.bean) { v, m, pos ->
                when (v?.id) {
                    R.id.tvSubscription -> {
                        playerPosition = pos
                        postSubscribe = m?.isSubscribed == true
                        subscribeBottomItem(m.isSubscribed, m._id)
                    }
                    R.id.ivCommonPostProfile,R.id.tvCommonPostName->{
                        val intent = Intent(this@SuggestedUserActivity, PlayerProfileActivity::class.java)
                        intent.putExtra("playerProfile", m?._id)
                        startActivity(intent)
                        overridePendingTransition(
                            R.anim.slide_in_right, R.anim.slide_out_left
                        )
                    }
                }
            }

        binding.rvSuggested.adapter = suggestedAdapter
        setupFollowingSearch()
    }

    /**
     * search handel
     */
    private fun setupFollowingSearch() {
        val searchView = binding.svSuggestedSearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filtered = if (!newText.isNullOrBlank()) {
                    suggestedList.filter {
                        it.firstName?.startsWith(
                            newText, ignoreCase = true
                        ) == true || it.lastName?.startsWith(newText, ignoreCase = true) == true
                    }
                } else {
                    suggestedList
                }
                suggestedAdapter.list = filtered
                suggestedAdapter.notifyDataSetChanged()
                return true
            }
        })
    }


    /**  api response observer  **/
    private fun initObserver() {
        viewModel.commonObserver.observe(this@SuggestedUserActivity) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "getSuggestedPlayers" -> {
                            try {
                                val myDataModel: GetSuggestedResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    if (myDataModel.data?.users != null) {
                                        isLoading = false
                                        isLastPage = false
                                        if (currentPage == 1) {
                                            myDataModel.data.let {
                                                suggestedList =
                                                    myDataModel.data.users as List<SuggestedUser>
                                                suggestedAdapter.list = suggestedList
                                            }
                                        } else {
                                            suggestedAdapter.addToList(myDataModel.data.users)
                                        }

                                        isLastPage = currentPage == myDataModel.data.totalPages

                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("error", "callSignUpApi: $e")
                            } finally {
                                hideLoading()
                            }
                        }

                        "playerSubscribeApi" -> {
                            try {
                                val myDataModel: CommonResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel?.success == true) {
                                    showSuccessToast(myDataModel.message.toString())
                                    val item = suggestedAdapter.getList()[playerPosition]
                                    item.isSubscribed = !postSubscribe
                                    suggestedAdapter.notifyDataSetChanged()
                                }
                            } catch (e: Exception) {
                                Log.e("error", "callSignUpApi: $e")
                            } finally {
                                hideLoading()
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

    /** subscribe bottom sheet **/
    private fun subscribeBottomItem(subscribe: Boolean?, userId: String?) {
        subscribeBottomItem = BaseCustomBottomSheet(
            this@SuggestedUserActivity,
            R.layout.subscribe_botom_item
        ) { view ->
            when (view?.id) {
                R.id.tvCancel -> {
                    subscribeBottomItem.dismiss()
                }

                R.id.tvSubscribe -> {
                    subscribeBottomItem.dismiss()
                    val subscribeUser = subscribe == true
                    val data = HashMap<String, Any>()
                    userId?.let {
                        data["subscribed"] = !subscribeUser
                        viewModel.playerSubscribeApi(Constants.USER_SUBSCRIBE + "?id=$it", data)
                    }
                }

            }
        }


        subscribeBottomItem.create()
        subscribeBottomItem.show()

        if (subscribe == true) {
            subscribeBottomItem.binding.tvSubscribe.text = "Unsubscribe"
            subscribeBottomItem.binding.tvSubscribe.setTextColor(
                ContextCompat.getColor(
                    this@SuggestedUserActivity, R.color.red_F27070
                )
            )
        } else {
            subscribeBottomItem.binding.tvSubscribe.setTextColor(
                ContextCompat.getColor(
                    this@SuggestedUserActivity, R.color.blue
                )
            )
        }
    }


    /**
     * handel activity like as bottom sheet
     */
    private fun initBottomSheet() {
        val bottomSheet = findViewById<ConstraintLayout?>(R.id.clBottomSheet)
        val bottomSheetBehavior = BottomSheetBehavior.from<ConstraintLayout?>(bottomSheet)
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
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

}