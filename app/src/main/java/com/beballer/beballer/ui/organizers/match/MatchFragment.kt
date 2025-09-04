package com.beballer.beballer.ui.organizers.match

import android.view.View
import androidx.fragment.app.viewModels
import com.beballer.beballer.BR
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.base.SimpleRecyclerViewAdapter
import com.beballer.beballer.data.model.MatchModel
import com.beballer.beballer.databinding.FragmentMatchBinding
import com.beballer.beballer.databinding.RvMatchItemBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MatchFragment : BaseFragment<FragmentMatchBinding>() {
    private val viewModel: MatchFragmentVM by viewModels()
    private lateinit var matchAdapter: SimpleRecyclerViewAdapter<MatchModel, RvMatchItemBinding>

    override fun getLayoutResource(): Int {
        return R.layout.fragment_match
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // adapter
        initMatchAdapter()
    }


    /** handle adapter **/
    private fun initMatchAdapter() {
        matchAdapter = SimpleRecyclerViewAdapter(R.layout.rv_match_item, BR.bean) { v, m, pos ->
            when (v.id) {

            }
        }
        matchAdapter.list = getList()
        binding.rvMatch.adapter = matchAdapter
    }

    // add List in data
    private fun getList(): ArrayList<MatchModel> {
        return arrayListOf(
            MatchModel("POULE A", "Match 1", "Field 1", "New team 1", "Team 1"),
            MatchModel("POULE A", "Match 2", "Field 2", "New team 2", "Team 2"),
            MatchModel("POULE A", "Match 3", "Field 3", "New team 3", "Team 3"),
            MatchModel("POULE A", "Match 4", "Field 4", "New team 4", "Team 4"),
            MatchModel("POULE A", "Match 5", "Field 5", "New team 5", "Team 5"),
            MatchModel("POULE A", "Match 6", "Field 6", "New team 6", "Team 6"),


            )
    }

}