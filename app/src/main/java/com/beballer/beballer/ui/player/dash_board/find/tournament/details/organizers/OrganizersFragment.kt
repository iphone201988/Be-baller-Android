package com.beballer.beballer.ui.player.dash_board.find.tournament.details.organizers

import android.view.View
import androidx.fragment.app.viewModels
import com.beballer.beballer.BR
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.base.SimpleRecyclerViewAdapter
import com.beballer.beballer.data.model.PlayerPostModel
import com.beballer.beballer.databinding.FragmentOrganizersBinding
import com.beballer.beballer.databinding.PlayerPostRvItemBinding
import com.beballer.beballer.databinding.RvOrganizersItemBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class OrganizersFragment : BaseFragment<FragmentOrganizersBinding>() {
    private val viewModel: OrganizersFragmentVM by viewModels()
    private lateinit var organizersAdapter: SimpleRecyclerViewAdapter<PlayerPostModel, RvOrganizersItemBinding>
    override fun getLayoutResource(): Int {
        return R.layout.fragment_organizers
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // initAdapter
        initOrganizersAdapter()
    }

    /** handle adapter **/
    private fun initOrganizersAdapter() {
        organizersAdapter = SimpleRecyclerViewAdapter(R.layout.rv_organizers_item, BR.bean) { v, m, pos ->
            when (v.id) {
                R.id.clAvatar -> {

                }
            }
        }
        organizersAdapter.list = getList()
        binding.rvOrganizers.adapter = organizersAdapter
    }


    // add List in data
    private fun getList(): ArrayList<PlayerPostModel> {
        return arrayListOf(
            PlayerPostModel(R.drawable.ic_beballer_grey_800, "Sonicbasketball",1),
            PlayerPostModel(R.drawable.ic_round_account_circle_40, "Sonicbasketball",1),
            PlayerPostModel(R.drawable.circle_basketball_24, "Sonicbasketball",2),
            PlayerPostModel(R.drawable.ic_beballer_grey_800, "Sonicbasketball",1),
            PlayerPostModel(R.drawable.ic_round_account_circle_40, "Sonicbasketball",1),
            PlayerPostModel(R.drawable.circle_basketball_24, "Sonicbasketball",2),
            PlayerPostModel(R.drawable.circle_basketball_24, "Sonicbasketball",2),


            )
    }



}