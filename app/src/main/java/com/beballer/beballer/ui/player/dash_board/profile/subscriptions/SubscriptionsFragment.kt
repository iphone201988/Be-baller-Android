package com.beballer.beballer.ui.player.dash_board.profile.subscriptions

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import com.beballer.beballer.BR
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.base.SimpleRecyclerViewAdapter
import com.beballer.beballer.data.model.GameModeModel
import com.beballer.beballer.data.model.SubscriptionModel
import com.beballer.beballer.databinding.FragmentSubscriptionsBinding
import com.beballer.beballer.databinding.RvGameModeItemBinding
import com.beballer.beballer.databinding.RvSubscriptionItemBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SubscriptionsFragment : BaseFragment<FragmentSubscriptionsBinding>() {
    private val viewModel: SubscriptionsFragmentVM by viewModels()
    private lateinit var subscriptionAdapter: SimpleRecyclerViewAdapter<SubscriptionModel, RvSubscriptionItemBinding>

    override fun getLayoutResource(): Int {
        return R.layout.fragment_subscriptions
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnClick()
        // adapter
        initSubscriptionAdapter()
    }

    /** handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                // back button click
                R.id.ivBack -> {
                    requireActivity().onBackPressed()
                }

            }
        }
    }


    /** handle subscription  adapter **/
    private fun initSubscriptionAdapter() {
        subscriptionAdapter = SimpleRecyclerViewAdapter(R.layout.rv_subscription_item, BR.bean) { v, m, pos ->
            when (v.id) {
                R.id.clGame->{

                }
            }
        }
        subscriptionAdapter.list = getListSuggestions()
        binding.rvSubs.adapter = subscriptionAdapter
    }

    // add list game mode
    private fun getListSuggestions(): ArrayList<SubscriptionModel> {
        return arrayListOf(
            SubscriptionModel("Unable to use the application"),
            SubscriptionModel("Unusable feature"),
            SubscriptionModel("Display problem"),
            SubscriptionModel("Problème mineur n'empêchant pas le bon fonctionnement"),
            SubscriptionModel("Other"),)
    }
}