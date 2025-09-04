package com.beballer.beballer.ui.player.dash_board.find.player_profile.class_mete

import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import com.beballer.beballer.BR
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.base.SimpleRecyclerViewAdapter
import com.beballer.beballer.data.model.FindModel
import com.beballer.beballer.data.model.MpvModel
import com.beballer.beballer.data.model.RankingModel
import com.beballer.beballer.databinding.FragmentProfileClassemeteBinding
import com.beballer.beballer.databinding.RvMpvItemLayoutBinding
import com.beballer.beballer.databinding.RvRankingItemBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileClassmateFragment : BaseFragment<FragmentProfileClassemeteBinding>() {
    private val viewModel : ProfileClassMateFragmentVM by viewModels()
    private lateinit var rankingAdapter: SimpleRecyclerViewAdapter<RankingModel, RvRankingItemBinding>

    override fun getLayoutResource(): Int {
       return R.layout.fragment_profile_classemete
    }

    override fun getViewModel(): BaseViewModel {
       return viewModel
    }

    override fun onCreateView(view: View) {
        binding.type = 1
        binding.tvCity.typeface = ResourcesCompat.getFont(requireContext(), R.font.inter_bold)
        binding.tvRegion.typeface =
            ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
        binding.tvCountry.typeface =
            ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
      // click
        initCLick()
        // adapter
        initRankingAdapter()
    }

    /**** click handel event ***/
    private fun initCLick() {
        viewModel.onClick.observe(viewLifecycleOwner){
            when(it?.id){

                R.id.tvCity->{
                    binding.type = 1
                    binding.tvCity.typeface = ResourcesCompat.getFont(requireContext(), R.font.inter_bold)
                    binding.tvRegion.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
                    binding.tvCountry.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
                }
                R.id.tvRegion->{
                    binding.type = 2
                    binding.tvCity.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
                    binding.tvRegion.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_bold)
                    binding.tvCountry.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
                }
                R.id.tvCountry->{
                    binding.type = 3
                    binding.tvCity.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
                    binding.tvRegion.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
                    binding.tvCountry.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_bold)
                }
            }
        }
    }

    /** handle adapter **/
    private lateinit var fullList: List<RankingModel>
    private fun initRankingAdapter() {
        rankingAdapter = SimpleRecyclerViewAdapter(R.layout.rv_ranking_item, BR.bean) { v, m, pos ->
            when (v.id) {

            }
        }

        fullList = getList()
        rankingAdapter.list = fullList
        binding.rvRanking.adapter = rankingAdapter
        setupSearch()
    }

    // add List in data
    private fun getList(): ArrayList<RankingModel> {
        return arrayListOf(
            RankingModel("101", R.drawable.ic_round_account_circle_40, "Ravi","@ravi123"),
            RankingModel("102", R.drawable.ic_round_account_circle_40, "Ashish","@Ashish123"),
            RankingModel("103", R.drawable.ic_round_account_circle_40, "Gaurav","@Gaurav123"),
            RankingModel("104", R.drawable.ic_round_account_circle_40, "Asit","@Asit123"),
            RankingModel("105", R.drawable.ic_round_account_circle_40, "Karan","@Karan123"),


            )
    }

   /*** add search ***/
    private fun setupSearch() {
        val searchView = binding.rankingSearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filtered = if (!newText.isNullOrBlank()) {
                    fullList.filter {
                        it.name.startsWith(newText, ignoreCase = true)
                    }
                } else {
                    fullList
                }

                rankingAdapter.list = filtered
                rankingAdapter.notifyDataSetChanged()
                return true
            }
        })
    }

}