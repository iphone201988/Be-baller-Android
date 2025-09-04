package com.beballer.beballer.ui.player.dash_board.find.game.find_game

import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.beballer.beballer.BR
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.base.SimpleRecyclerViewAdapter
import com.beballer.beballer.utils.BindingUtils
import com.beballer.beballer.data.model.FindModel
import com.beballer.beballer.databinding.FindGameRvItemBinding
import com.beballer.beballer.databinding.FragmentFindGameBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FindGameFragment : BaseFragment<FragmentFindGameBinding>() {
    private val viewModel: FindGameFragmentVM by viewModels()
    private lateinit var findGameAdapter: SimpleRecyclerViewAdapter<FindModel, FindGameRvItemBinding>

    override fun getLayoutResource(): Int {
        return R.layout.fragment_find_game
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnClick()
        // adapter
        initFindAdapter()
    }

    /** handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when(it?.id){
                R.id.cancelImage->{
                    findNavController().popBackStack()
                }

                R.id.tvAddCourt->{
                    BindingUtils.navigateWithSlide(
                        findNavController(), R.id.navigateAddCourtFragment, null
                    )
                }
            }
        }


    }


    /** handle adapter **/
    private lateinit var fullList: List<FindModel>
    private fun initFindAdapter() {
        findGameAdapter =
            SimpleRecyclerViewAdapter(R.layout.find_game_rv_item, BR.bean) { v, m, pos ->
                when (v.id) {
                    R.id.clMain -> {

                    }
                }
            }
        fullList = getList()
        findGameAdapter.list = fullList
        binding.rvFindGame.adapter = findGameAdapter
        setupSearch()
    }


    // add List in data
    private fun getList(): ArrayList<FindModel> {
        return arrayListOf(
            FindModel(R.drawable.ic_court_24, "Courts", 1),
            FindModel(R.drawable.ic_workout_24, "Workouts", 2),
            FindModel(R.drawable.ic_game_24, "Games", 3),
            FindModel(R.drawable.ic_pro_game_24, "Ticketing", 4),
            FindModel(R.drawable.ic_tournament_24, "Tournaments", 5),
            FindModel(R.drawable.ic_camp_24, "Camps", 6),


            )
    }

    /*** add search ***/
    private fun setupSearch() {
        val searchView = binding.courtsSearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filtered = if (!newText.isNullOrBlank()) {
                    fullList.filter {
                        it.title.startsWith(newText, ignoreCase = true)
                    }
                } else {
                    fullList
                }

                findGameAdapter.list = filtered
                findGameAdapter.notifyDataSetChanged()
                return true
            }
        })
    }

}