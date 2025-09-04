package com.beballer.beballer.ui.player.dash_board.find.tournament.details.spectators

import android.view.View
import androidx.fragment.app.viewModels
import com.beballer.beballer.BR
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.base.SimpleRecyclerViewAdapter
import com.beballer.beballer.data.model.PlayerPostModel
import com.beballer.beballer.databinding.FragmentSpectatorsBinding
import com.beballer.beballer.databinding.RvSpectatorsItemBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SpectatorsFragment : BaseFragment<FragmentSpectatorsBinding>() {
    private val viewModel: SpectatorsFragmentVM by viewModels()
    private lateinit var spectatorsAdapter: SimpleRecyclerViewAdapter<PlayerPostModel, RvSpectatorsItemBinding>
    override fun getLayoutResource(): Int {
        return R.layout.fragment_spectators
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
     // initAdapter
        initSpectatorsAdapter()
    }


    /** handle adapter **/
    private fun initSpectatorsAdapter() {
        spectatorsAdapter = SimpleRecyclerViewAdapter(R.layout.rv_spectators_item, BR.bean) { v, m, pos ->
            when (v.id) {
                R.id.clAvatar -> {

                }
            }
        }
        spectatorsAdapter.list = getList()
        binding.rvSpectators.adapter = spectatorsAdapter
    }


    // add List in data
    private fun getList(): ArrayList<PlayerPostModel> {
        return arrayListOf(
            PlayerPostModel(R.drawable.ic_beballer_grey_800, "Elisvieira",1),
            PlayerPostModel(R.drawable.ic_round_account_circle_40, "Elisvieira",1),
            PlayerPostModel(R.drawable.circle_basketball_24, "Elisvieira",2),
            PlayerPostModel(R.drawable.ic_beballer_grey_800, "Elisvieira",1),
            PlayerPostModel(R.drawable.ic_round_account_circle_40, "Elisvieira",1),
            PlayerPostModel(R.drawable.circle_basketball_24, "Elisvieira",2),
            PlayerPostModel(R.drawable.circle_basketball_24, "Elisvieira",2),


            )
    }

}