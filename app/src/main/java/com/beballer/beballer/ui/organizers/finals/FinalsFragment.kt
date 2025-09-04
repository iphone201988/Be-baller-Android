package com.beballer.beballer.ui.organizers.finals

import android.view.View
import androidx.fragment.app.viewModels
import com.beballer.beballer.BR
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.base.SimpleRecyclerViewAdapter
import com.beballer.beballer.data.model.FinalModel
import com.beballer.beballer.data.model.MatchModel
import com.beballer.beballer.databinding.FragmentFinalsBinding
import com.beballer.beballer.databinding.RvFinalsItemBinding
import com.beballer.beballer.databinding.RvMatchItemBinding
import com.beballer.beballer.ui.organizers.match.MatchFragmentVM
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FinalsFragment : BaseFragment<FragmentFinalsBinding>() {
    private val viewModel: FinalsFragmentVM by viewModels()
    private lateinit var finalAdapter: SimpleRecyclerViewAdapter<FinalModel, RvFinalsItemBinding>

    override fun getLayoutResource(): Int {
        return R.layout.fragment_finals
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // adapter
        initFinalsAdapter()
    }
    /** handle adapter **/
    private fun initFinalsAdapter() {
        finalAdapter = SimpleRecyclerViewAdapter(R.layout.rv_finals_item, BR.bean) { v, m, pos ->
            when (v.id) {
                 R.id.clFinals->{
                     m.check = !m.check
                     finalAdapter.notifyDataSetChanged()
                 }

            }
        }
        finalAdapter.list = getList()
        binding.rvFinal.adapter = finalAdapter
    }

    // add List in data
    private fun getList(): ArrayList<FinalModel> {
        return arrayListOf(
            FinalModel("Finals"),
            FinalModel("Finals"),
            FinalModel("Finals"),
            FinalModel("Finals"),
            FinalModel("Finals"),


            )
    }
}