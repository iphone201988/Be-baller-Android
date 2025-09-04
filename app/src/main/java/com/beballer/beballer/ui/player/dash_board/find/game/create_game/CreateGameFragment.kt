package com.beballer.beballer.ui.player.dash_board.find.game.create_game

import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.beballer.beballer.BR
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.base.SimpleRecyclerViewAdapter
import com.beballer.beballer.utils.BaseCustomBottomSheet
import com.beballer.beballer.utils.BindingUtils
import com.beballer.beballer.data.model.GameModeModel
import com.beballer.beballer.data.model.MpvModel
import com.beballer.beballer.databinding.AutoRefereeingBottomSheetItemBinding
import com.beballer.beballer.databinding.FragmentCreateGameBinding
import com.beballer.beballer.databinding.GameModeBottomSheetItemBinding
import com.beballer.beballer.databinding.RvGameModeItemBinding
import com.beballer.beballer.databinding.RvOutSideItemBinding
import com.beballer.beballer.databinding.RvTeamItemBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateGameFragment : BaseFragment<FragmentCreateGameBinding>() {
    private val viewModel: CreateGameFragmentVM by viewModels()
    private lateinit var homeTeamAdapter: SimpleRecyclerViewAdapter<MpvModel, RvTeamItemBinding>
    private lateinit var outSideTeamAdapter: SimpleRecyclerViewAdapter<MpvModel, RvOutSideItemBinding>
    private lateinit var gameModeAdapter: SimpleRecyclerViewAdapter<GameModeModel, RvGameModeItemBinding>
    private lateinit var gameModeSheet: BaseCustomBottomSheet<GameModeBottomSheetItemBinding>
    private lateinit var autoRefereeingBottomSheet: BaseCustomBottomSheet<AutoRefereeingBottomSheetItemBinding>


    override fun getLayoutResource(): Int {
        return R.layout.fragment_create_game
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnClick()
        // adapter
        initHomeTeamAdapter()
        initOutSideAdapter()
    }

    /*** click event handel **/
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.cancelImage -> {
                    requireActivity().finish()
                }

                R.id.tvGameMode -> {
                    gameModeBottomSheet()
                }

                R.id.game_referee_tv -> {
                    autoRefereeBottomSheet()
                }

                R.id.tvGameCourt -> {
                    BindingUtils.navigateWithSlide(
                        findNavController(), R.id.navigateFindFragmentFragment, null
                    )
                }


            }
        }
    }

    /** game mode bottom sheet **/
    private fun gameModeBottomSheet() {
        gameModeSheet =
            BaseCustomBottomSheet(requireContext(), R.layout.game_mode_bottom_sheet_item) {
                when (it?.id) {

                }
            }
        gameModeSheet.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        gameModeSheet.behavior.isDraggable = true
        gameModeSheet.create()
        gameModeSheet.show()

        initGameModeAdapter()
    }

    /** auto refereeing bottom sheet **/
    private fun autoRefereeBottomSheet() {
        autoRefereeingBottomSheet =
            BaseCustomBottomSheet(requireContext(), R.layout.auto_refereeing_bottom_sheet_item) {
                when (it?.id) {
                    R.id.tvName1 -> {
                        autoRefereeingBottomSheet.dismiss()
                        BindingUtils.navigateWithSlide(
                            findNavController(), R.id.navigateShareCreateGameFragment, null
                        )
                    }
                }
            }
        autoRefereeingBottomSheet.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        autoRefereeingBottomSheet.behavior.isDraggable = true
        autoRefereeingBottomSheet.create()
        autoRefereeingBottomSheet.show()

    }

    /** handle game mode adapter **/
    private fun initGameModeAdapter() {
        gameModeAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_game_mode_item, BR.bean) { v, m, pos ->
                when (v.id) {
                    R.id.clGame -> {
                        Log.d("fgfdgfdgfdg", "initGameModeAdapter: ")
                    }
                }
            }
        gameModeAdapter.list = getListGame()
        gameModeSheet.binding.rvGameModel.adapter = gameModeAdapter
    }

    /** handle home team adapter **/
    private fun initHomeTeamAdapter() {
        homeTeamAdapter = SimpleRecyclerViewAdapter(R.layout.rv_team_item, BR.bean) { v, m, pos ->
            when (v.id) {

            }
        }
        homeTeamAdapter.list = getList()
        binding.rvHomeTeam.adapter = homeTeamAdapter
        Log.d("fsddfsdf", "initHomeTeamAdapter: ${homeTeamAdapter.list.size}")
    }

    /** handle out side adapter **/
    private fun initOutSideAdapter() {
        outSideTeamAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_out_side_item, BR.bean) { v, m, pos ->
                when (v.id) {

                }
            }
        outSideTeamAdapter.list = getList()
        binding.rvOutsideTeam.adapter = outSideTeamAdapter
        Log.d("fsddfsdf", "initOutSideAdapter: ${outSideTeamAdapter.list.size}")
    }


    // add List in data
    private fun getList(): ArrayList<MpvModel> {
        return arrayListOf(
            MpvModel("Elliot Le Gall", "Camaret-sur-Mer", "131pts"),
            MpvModel("Leo Florentin", "Forcalquier", "175pts"),
            MpvModel("Elliot Le Gall", "Camaret-sur-Mer", "11pts"),
            MpvModel("Leo Florentin", "Forcalquier", "75pts"),
            MpvModel("Elliot Le Gall", "Camaret-sur-Mer", "131pts"),
            MpvModel("Leo Florentin", "Forcalquier", "120pts"),
            MpvModel("Elliot Le Gall", "Camaret-sur-Mer", "131pts"),
            MpvModel("Leo Florentin", "Forcalquier", "100pts"),

            )
    }

    // add list game mode
    private fun getListGame(): ArrayList<GameModeModel> {
        return arrayListOf(
            GameModeModel("1 VS 1"),
            GameModeModel("2 VS 2"),
            GameModeModel("3 VS 3"),
            GameModeModel("4 VS 4"),
            GameModeModel("5 VS 5"),
            GameModeModel("H.O.R.S.E"),
            GameModeModel("Lucky Luke"),
            GameModeModel("Concours 3 pts"),
            GameModeModel("Concours Dunks"),


            )
    }

}