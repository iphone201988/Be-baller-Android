package com.beballer.beballer.ui.player.dash_board.find.player_profile.inventoy

import android.view.View
import androidx.fragment.app.viewModels
import com.beballer.beballer.BR
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.base.SimpleRecyclerViewAdapter
import com.beballer.beballer.data.model.InventModel
import com.beballer.beballer.databinding.FragmentInventoryBinding
import com.beballer.beballer.databinding.InventRvItemBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InventoryFragment : BaseFragment<FragmentInventoryBinding>() {
    private val viewModel: InventoryFragmentVM by viewModels()
    private lateinit var inventAdapter: SimpleRecyclerViewAdapter<InventModel, InventRvItemBinding>

    override fun getLayoutResource(): Int {
        return R.layout.fragment_inventory
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // adapter
        initInventAdapter()
    }

    /** handle adapter **/
    private lateinit var fullList: List<InventModel>
    private fun initInventAdapter() {
        inventAdapter = SimpleRecyclerViewAdapter(R.layout.invent_rv_item, BR.bean) { v, m, pos ->
            when (v.id) {

            }
        }

        fullList = getList()
        inventAdapter.list = fullList
        binding.rvRanking.adapter = inventAdapter

    }

    // add List in data
    private fun getList(): ArrayList<InventModel> {
        return arrayListOf(
            InventModel("21 match", 20),
            InventModel("20 match", 40),
            InventModel("30 match", 80),
            InventModel("10 match", 50),
            InventModel("40 match", 70),
        )
    }


}