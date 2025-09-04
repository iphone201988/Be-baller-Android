package com.beballer.beballer.ui.player.dash_board.find.tournament

import android.content.Intent
import android.view.View
import androidx.fragment.app.viewModels
import com.beballer.beballer.BR
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.base.SimpleRecyclerViewAdapter
import com.beballer.beballer.utils.BaseCustomDialog
import com.beballer.beballer.data.model.FindModel
import com.beballer.beballer.databinding.CreateTournamentDialogItemBinding
import com.beballer.beballer.databinding.FragmentTournamentBinding
import com.beballer.beballer.databinding.RvTournamentItemBinding
import com.beballer.beballer.ui.player.dash_board.find.game.GameActivity
import com.beballer.beballer.ui.player.dash_board.profile.user.UserProfileActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TournamentFragment : BaseFragment<FragmentTournamentBinding>() {
    private val viewModel: TournamentFragmentVM by viewModels()
    private lateinit var tournamentAdapter: SimpleRecyclerViewAdapter<FindModel, RvTournamentItemBinding>
    private lateinit var createTournamentDialogItem: BaseCustomDialog<CreateTournamentDialogItemBinding>
    override fun getLayoutResource(): Int {
        return R.layout.fragment_tournament
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // observer
        initObserver()
        // click
        initOnClick()
        // adapter
        initTournamentAdapter()
    }

    /** handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.cancelImage -> {
                    requireActivity().finish()
                }
                // iv notifications
                R.id.ivNotification -> {
                    val intent = Intent(requireContext(), UserProfileActivity::class.java)
                    intent.putExtra("userType", "notification")
                    startActivity(intent)
                    requireActivity().overridePendingTransition(
                        R.anim.slide_in_right, R.anim.slide_out_left
                    )
                }

                R.id.cardView -> {
                    createTournamentDialogItem()
                }
            }
        }
    }

    /**** create profile dialog item ****/
    private fun createTournamentDialogItem() {
        createTournamentDialogItem = BaseCustomDialog<CreateTournamentDialogItemBinding>(
            requireContext(), R.layout.create_tournament_dialog_item
        ) {
            when (it?.id) {
                // let,s go button click
                R.id.btnNext -> {

                }
            }

        }
        createTournamentDialogItem.create()
        createTournamentDialogItem.show()
    }


    /** handle adapter **/
    private lateinit var fullList: List<FindModel>
    private fun initTournamentAdapter() {
        tournamentAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_tournament_item, BR.bean) { v, m, pos ->
                when (v.id) {
                    R.id.clCardView -> {
                        val intent = Intent(requireContext(), GameActivity::class.java)
                        intent.putExtra("pathType", "third")
                        startActivity(intent)
                        requireActivity().overridePendingTransition(
                            R.anim.slide_in_right, R.anim.slide_out_left
                        )
                    }
                }
            }
        fullList = getList()
        tournamentAdapter.list = fullList
        binding.rvTournament.adapter = tournamentAdapter
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


    /** handle api response **/
    private fun initObserver() {


    }
}
