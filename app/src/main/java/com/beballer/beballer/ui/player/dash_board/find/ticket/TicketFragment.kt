package com.beballer.beballer.ui.player.dash_board.find.ticket

import android.content.Intent
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
import com.beballer.beballer.databinding.FragmentTicketBinding
import com.beballer.beballer.databinding.RvTickrtingItemBinding
import com.beballer.beballer.ui.player.dash_board.profile.user.UserProfileActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TicketFragment : BaseFragment<FragmentTicketBinding>() {
    private val viewModel: TicketFragmentVM by viewModels()
    private lateinit var ticketAdapter: SimpleRecyclerViewAdapter<FindModel, RvTickrtingItemBinding>
    override fun getLayoutResource(): Int {
        return R.layout.fragment_ticket
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        binding.pos = 1
        binding.subPos = 1
        binding.tvFeed.typeface = ResourcesCompat.getFont(requireContext(), R.font.inter_bold)
        binding.tvSubscriptions.typeface =
            ResourcesCompat.getFont(requireContext(), R.font.inter_medium)

        binding.tvSubFeed.typeface =
            ResourcesCompat.getFont(requireContext(), R.font.inter_bold)
        binding.tvSubSubscriptions.typeface =
            ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
        binding.tvSubFeed2.typeface =
            ResourcesCompat.getFont(requireContext(), R.font.inter_bold)
        // observer
        initObserver()
        // click
        initOnClick()
        // adapter
        initTicketAdapter()
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
                // tvFeed button click
                R.id.tvFeed -> {
                    binding.pos = 1
                    binding.tvFeed.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_bold)
                    binding.tvSubscriptions.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
                }
                // tvSubscriptions  button click
                R.id.tvSubscriptions -> {
                    binding.pos = 2
                    binding.tvFeed.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
                    binding.tvSubscriptions.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_bold)
                }

                // tvFeed button click
                R.id.tvSubFeed -> {
                    binding.subPos = 1
                    binding.tvSubFeed.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_bold)
                    binding.tvSubSubscriptions.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
                }
                // tvSubscriptions  button click
                R.id.tvSubSubscriptions -> {
                    binding.subPos = 2
                    binding.tvSubFeed.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
                    binding.tvSubSubscriptions.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_bold)
                }
            }
        }
    }


    /** handle adapter **/
    private lateinit var fullList: List<FindModel>
    private fun initTicketAdapter() {
        ticketAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_tickrting_item, BR.bean) { v, m, pos ->
                when (v.id) {
                    R.id.clMain -> {

                    }
                }
            }
        fullList = getList()
        ticketAdapter.list = fullList
        binding.rvTicketing.adapter = ticketAdapter
        if (ticketAdapter.list.size > 0) {
            binding.tvEmpty.visibility = View.GONE
        } else {
            binding.tvEmpty.visibility = View.VISIBLE
        }
        setupSearch()
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

                ticketAdapter.list = filtered
                ticketAdapter.notifyDataSetChanged()
                if (ticketAdapter.list.size > 0) {
                    binding.tvEmpty.visibility = View.GONE
                } else {
                    binding.tvEmpty.visibility = View.VISIBLE
                }
                return true
            }
        })
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
