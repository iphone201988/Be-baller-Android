package com.beballer.beballer.ui.player.dash_board.find.camps

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
import com.beballer.beballer.databinding.FragmentCampsBinding
import com.beballer.beballer.databinding.RvCampsItemBinding
import com.beballer.beballer.ui.player.dash_board.find.game.GameActivity
import com.beballer.beballer.ui.player.dash_board.profile.user.UserProfileActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CampsFragment : BaseFragment<FragmentCampsBinding>() {
    private val viewModel: CampsFragmentVM by viewModels()
    private lateinit var campsAdapter: SimpleRecyclerViewAdapter<FindModel, RvCampsItemBinding>
    private lateinit var createCampsDialogItem: BaseCustomDialog<CreateTournamentDialogItemBinding>
    override fun getLayoutResource(): Int {
        return R.layout.fragment_camps
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
        initCampsAdapter()
    }

    /** handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.cancelImage -> {
                    requireActivity().finish()
                }
                // iv notifications
                R.id.ivNotification->{
                    val intent = Intent(requireContext(), UserProfileActivity::class.java)
                    intent.putExtra("userType", "notification")
                    startActivity(intent)
                    requireActivity().overridePendingTransition(
                        R.anim.slide_in_right, R.anim.slide_out_left
                    )
                }

                R.id.cardView->{
                    createTournamentDialogItem()
                }
            }
        }
    }

    /**** create profile dialog item ****/
    private fun createTournamentDialogItem() {
        createCampsDialogItem = BaseCustomDialog<CreateTournamentDialogItemBinding>(
            requireContext(), R.layout.create_tournament_dialog_item
        ) {
            when (it?.id) {
                // let,s go button click
                R.id.btnNext -> {

                }
            }

        }
        createCampsDialogItem.create()
        createCampsDialogItem.show()
    }



    /** handle adapter **/
    private lateinit var fullList: List<FindModel>
    private fun initCampsAdapter() {
        campsAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_tournament_item, BR.bean) { v, m, pos ->
                when (v.id) {
                    R.id.clCardView -> {
                        val intent = Intent(requireContext(), GameActivity::class.java)
                        intent.putExtra("pathType","four")
                        startActivity(intent)
                        requireActivity().overridePendingTransition(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                    }
                }
            }
        fullList = getList()
        campsAdapter.list = fullList
        binding.rvCamps.adapter = campsAdapter
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
