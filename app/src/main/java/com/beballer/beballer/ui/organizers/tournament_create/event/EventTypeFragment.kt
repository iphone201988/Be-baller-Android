package com.beballer.beballer.ui.organizers.tournament_create.event

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.utils.BindingUtils
import com.beballer.beballer.databinding.FragmentEventTypeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventTypeFragment : BaseFragment<FragmentEventTypeBinding>() {
    private val viewModel: EventTypeFragmentVM by viewModels()

    private var selectedType = 1

    override fun getLayoutResource(): Int {
        return R.layout.fragment_event_type
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // check
        binding.tvCreate.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.tvThanks.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_000000))
        binding.clNoThanks.setBackgroundResource(R.drawable.player_bg)
        binding.clCreate.setBackgroundResource(R.drawable.create_tournament_bg)
        binding.ivSelected.setImageResource(R.drawable.unique_selected_element)
        binding.ivUnSelected.setImageResource(R.drawable.unique_unselected_element)

        // click
        initOnCLick()
    }

    /*** click handel ***/
    private fun initOnCLick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.cancelImage -> {
                    findNavController().popBackStack()
                }

                R.id.clCreate -> {
                    selectedType = 1
                    binding.tvCreate.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    binding.tvThanks.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_000000))
                    binding.clNoThanks.setBackgroundResource(R.drawable.player_bg)
                    binding.clCreate.setBackgroundResource(R.drawable.create_tournament_bg)
                    binding.ivSelected.setImageResource(R.drawable.unique_selected_element)
                    binding.ivUnSelected.setImageResource(R.drawable.unique_unselected_element)
                }
                R.id.clNoThanks -> {
                    selectedType = 2
                    binding.tvCreate.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_000000))
                    binding.tvThanks.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    binding.clNoThanks.setBackgroundResource(R.drawable.create_tournament_bg)
                    binding.clCreate.setBackgroundResource(R.drawable.player_bg)
                    binding.ivSelected.setImageResource(R.drawable.unique_unselected_element)
                    binding.ivUnSelected.setImageResource(R.drawable.unique_selected_element)
                }

                R.id.btnNext -> {
                    if (selectedType==1){
                        BindingUtils.navigateWithSlide(findNavController(), R.id.tournamentFirst, null)
                    }
                    else {
                        val bundle = Bundle().apply {
                            putString("eventType", "2_tournament")
                        }
                        BindingUtils.navigateWithSlide(findNavController(), R.id.tournamentEight, null)
                    }

                }

            }
        }

    }
}