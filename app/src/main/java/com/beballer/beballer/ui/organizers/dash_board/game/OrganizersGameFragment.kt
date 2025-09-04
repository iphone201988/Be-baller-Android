package com.beballer.beballer.ui.organizers.dash_board.game

import android.content.Intent
import android.view.View
import androidx.fragment.app.viewModels
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.databinding.FragmentOrganizersGameBinding
import com.beballer.beballer.ui.organizers.dash_board.tournament.TournamentsActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class OrganizersGameFragment : BaseFragment<FragmentOrganizersGameBinding>() {
    private val viewModel: OrganizersGameFragmentVM by viewModels()

    override fun getLayoutResource(): Int {
        return R.layout.fragment_organizers_game
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // observer
        initObserver()
        // click
        initOnClick()
    }

    /** handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when(it?.id){
                R.id.clCamps->{
                    val intent = Intent(requireContext(),TournamentsActivity::class.java)
                    intent.putExtra("organizersPathType","CampsCreate")
                    startActivity(intent)
                }
                R.id.clTournament->{
                    val intent = Intent(requireContext(),TournamentsActivity::class.java)
                    intent.putExtra("organizersPathType","tournamentCreate")
                    startActivity(intent)
                }
            }
        }
    }

    /** handle api response **/
    private fun initObserver() {


    }
}