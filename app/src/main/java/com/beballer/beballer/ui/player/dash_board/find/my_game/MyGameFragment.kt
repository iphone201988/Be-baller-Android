package com.beballer.beballer.ui.player.dash_board.find.my_game

import android.content.Intent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import com.beballer.beballer.BR
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.base.SimpleRecyclerViewAdapter
import com.beballer.beballer.data.model.MpvModel
import com.beballer.beballer.databinding.FragmentMyGameBinding
import com.beballer.beballer.databinding.MyGameRvItemBinding
import com.beballer.beballer.ui.player.dash_board.find.game.GameActivity
import com.beballer.beballer.ui.player.dash_board.profile.user.UserProfileActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyGameFragment : BaseFragment<FragmentMyGameBinding>() {
    private val viewModel: MyGameFragmentVm by viewModels()
    private lateinit var myGameAdapter: SimpleRecyclerViewAdapter<MpvModel, MyGameRvItemBinding>

    override fun getLayoutResource(): Int {
        return R.layout.fragment_my_game
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // set block pos
        binding.pos = 1
        binding.tvFeed.typeface = ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
        binding.tvSubscriptions.typeface = ResourcesCompat.getFont(requireContext(), R.font.inter_regular)
        // click
        initOnClick()
        // adapter
        initMyGameAdapter()
    }

    /** handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
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
                    val intent = Intent(requireContext(), GameActivity::class.java)
                    intent.putExtra("pathType","first")
                    startActivity(intent  )
                    requireActivity().overridePendingTransition(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left
                    )
                }
                R.id.cancelImage->{
                    requireActivity().finish()
                }
                // tvFeed button click
                R.id.tvFeed -> {
                    binding.pos = 1
                    binding.tvFeed.typeface = ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
                    binding.tvSubscriptions.typeface = ResourcesCompat.getFont(requireContext(), R.font.inter_regular)
                }
                // tvSubscriptions  button click
                R.id.tvSubscriptions -> {
                    binding.pos = 2
                    binding.tvFeed.typeface = ResourcesCompat.getFont(requireContext(), R.font.inter_regular)
                    binding.tvSubscriptions.typeface = ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
                }
            }
        }
    }


    /** handle adapter **/
    private fun initMyGameAdapter() {
        myGameAdapter = SimpleRecyclerViewAdapter(R.layout.my_game_rv_item, BR.bean) { v, m, pos ->
            when (v.id) {
             R.id.clGame->{
                val intent = Intent(requireContext(),GameActivity::class.java)
                 intent.putExtra("pathType","second")
                 startActivity(intent)
                 requireActivity().overridePendingTransition(
                     R.anim.slide_in_right,
                     R.anim.slide_out_left
                 )
             }
            }
        }
        myGameAdapter.list = getList()
        binding.rvMyGame.adapter = myGameAdapter
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

}