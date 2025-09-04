package com.beballer.beballer.ui.player.dash_board.find

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
import com.beballer.beballer.databinding.AlertDialodItemBinding
import com.beballer.beballer.databinding.FindRvItemBinding
import com.beballer.beballer.databinding.FragmentFindBinding
import com.beballer.beballer.ui.player.dash_board.profile.user.UserProfileActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FindFragment : BaseFragment<FragmentFindBinding>() {
    private val viewModel: FindFragmentVM by viewModels()
    private lateinit var findAdapter: SimpleRecyclerViewAdapter<FindModel, FindRvItemBinding>
    private lateinit var alertDialogItem: BaseCustomDialog<AlertDialodItemBinding>

    override fun getLayoutResource(): Int {
        return R.layout.fragment_find
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
        initFindAdapter()

    }

    /** handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                // iv notifications
                R.id.ivNotification -> {
                    val intent = Intent(requireContext(), UserProfileActivity::class.java)
                    intent.putExtra("userType", "notification")
                    startActivity(intent)
                    requireActivity().overridePendingTransition(
                        R.anim.slide_in_right, R.anim.slide_out_left
                    )
                }
            }
        }
    }


    /**** alert dialog item ****/
    private fun alertDialogItem() {
        alertDialogItem = BaseCustomDialog<AlertDialodItemBinding>(
            requireContext(), R.layout.alert_dialod_item
        ) {
            when (it?.id) {
                R.id.tvBtn -> {
                    alertDialogItem.dismiss()
                }
            }

        }
        alertDialogItem.create()
        alertDialogItem.show()
    }


    /** handle adapter **/
    private fun initFindAdapter() {
        findAdapter = SimpleRecyclerViewAdapter(R.layout.find_rv_item, BR.bean) { v, m, pos ->
            when (pos) {
                0 -> {
                    val intent = Intent(requireContext(), UserProfileActivity::class.java)
                    intent.putExtra("userType", "courtFragment")
                    startActivity(intent)
                    requireActivity().overridePendingTransition(
                        R.anim.slide_in_right, R.anim.slide_out_left
                    )

                }

                1 -> {
                    alertDialogItem()
                }

                2 -> {
                    val intent = Intent(requireContext(), UserProfileActivity::class.java)
                    intent.putExtra("userType", "gameFragment")
                    startActivity(intent)
                    requireActivity().overridePendingTransition(
                        R.anim.slide_in_right, R.anim.slide_out_left
                    )

                }

                3 -> {

                    val intent = Intent(requireContext(), UserProfileActivity::class.java)
                    intent.putExtra("userType", "ticketFragment")
                    startActivity(intent)
                    requireActivity().overridePendingTransition(
                        R.anim.slide_in_right, R.anim.slide_out_left
                    )

                }

                4 -> {

                    val intent = Intent(requireContext(), UserProfileActivity::class.java)
                    intent.putExtra("userType", "tournamentFragment")
                    startActivity(intent)
                    requireActivity().overridePendingTransition(
                        R.anim.slide_in_right, R.anim.slide_out_left
                    )

                }

                5 -> {

                    val intent = Intent(requireContext(), UserProfileActivity::class.java)
                    intent.putExtra("userType", "campsFragment")
                    startActivity(intent)
                    requireActivity().overridePendingTransition(
                        R.anim.slide_in_right, R.anim.slide_out_left
                    )
                }
            }
        }
        findAdapter.list = getList()
        binding.rvFind.adapter = findAdapter
    }

    // add List in data
    private fun getList(): ArrayList<FindModel> {
        return arrayListOf(
            FindModel(R.drawable.ic_court_24, "Courts", 1),
            FindModel(R.drawable.ic_workout_24, "Workouts", 2),
            FindModel(R.drawable.ic_game_24, "Games", 3),
            FindModel(R.drawable.ic_pro_game_24, "Ticketing", 4),
            FindModel(R.drawable.ic_tournament_24, "Tournaments", 5),
            FindModel(R.drawable.ic_camp_24, "Camps", 6),)
    }

    /** handle api response **/
    private fun initObserver() {


    }
}