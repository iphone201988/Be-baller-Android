package com.beballer.beballer.ui.player.dash_board.find.player_profile.statistics

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.beballer.beballer.BR
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.base.SimpleRecyclerViewAdapter
import com.beballer.beballer.data.model.RankingModel
import com.beballer.beballer.data.model.StatistModel
import com.beballer.beballer.databinding.FragmentStatisticsBinding
import com.beballer.beballer.databinding.RvRankingItemBinding
import com.beballer.beballer.databinding.StatistiquesRvItemBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatisticsFragment : BaseFragment<FragmentStatisticsBinding>() {
    private val viewModel: StatisticsFragmentVM by viewModels()
    private lateinit var statistAdapter: SimpleRecyclerViewAdapter<StatistModel, StatistiquesRvItemBinding>
    override fun getLayoutResource(): Int {
      return R.layout.fragment_statistics
    }

    override fun getViewModel(): BaseViewModel {
      return viewModel
    }

    override fun onCreateView(view: View) {
   // adapter
        initStatistAdapter()
    }

    /** handle adapter **/
    private lateinit var fullList: List<StatistModel>
    private fun initStatistAdapter() {
        statistAdapter = SimpleRecyclerViewAdapter(R.layout.statistiques_rv_item, BR.bean) { v, m, pos ->
            when (v.id) {

            }
        }

        fullList = getList()
        statistAdapter.list = fullList
        binding.rvStatist.adapter = statistAdapter

    }

    // add List in data
    private fun getList(): ArrayList<StatistModel> {
        return arrayListOf(
            StatistModel("1VS1",40),
            StatistModel("2VS2",50),
            StatistModel("3VS3",80),
            StatistModel("4VS$",20),)
    }


}