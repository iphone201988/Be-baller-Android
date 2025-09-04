package com.beballer.beballer.ui.organizers.tournament_create

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
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
import com.beballer.beballer.databinding.FragmentCreateTournamentFourBinding
import com.beballer.beballer.databinding.PlayFormateBottomLayoutBinding
import com.beballer.beballer.databinding.RvGameModeItemBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateTournamentFourFragment : BaseFragment<FragmentCreateTournamentFourBinding>() {
    private val viewModel: CommonTournamentVM by viewModels()
    private lateinit var playFormatSheet: BaseCustomBottomSheet<PlayFormateBottomLayoutBinding>
    private lateinit var gameModeAdapter: SimpleRecyclerViewAdapter<GameModeModel, RvGameModeItemBinding>

    override fun getLayoutResource(): Int {
        return R.layout.fragment_create_tournament_four
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        val layoutParams = binding.etDescription.layoutParams
        resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._82sdp)
        binding.etDescription.layoutParams = layoutParams
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

                R.id.etTournamentName -> {
                    playFormatBottomSheet(1)
                }

                R.id.etTournamentAddress -> {
                    playFormatBottomSheet(2)
                }

                R.id.btnNext -> {
                    if (validate()) {
                        if (binding.etTournamentAddress.text?.contains("I already have a link") == true) {
                            if (binding.etAgeRange.text?.isEmpty() == true) {
                                showInfoToast("Please enter link")
                            } else {
                                BindingUtils.navigateWithSlide(
                                    findNavController(), R.id.tournamentFive, null
                                )
                            }
                        } else {
                            BindingUtils.navigateWithSlide(
                                findNavController(), R.id.tournamentFive, null
                            )
                        }

                    }
                }
            }
        }


        // Add TextWatchers
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                checkAllFieldsNotEmpty()
            }
        }




        binding.etTournamentName.addTextChangedListener(textWatcher)
        binding.etTournamentAddress.addTextChangedListener(textWatcher)
        binding.etDescription.addTextChangedListener(textWatcher)
    }


    // Function to check all fields
    private fun checkAllFieldsNotEmpty() {
        val isAllNotEmpty =
            binding.etTournamentName.text?.isNotEmpty() == true && binding.etTournamentAddress.text?.isNotEmpty() == true && binding.etDescription.text?.isNotEmpty() == true
        if (binding.etTournamentAddress.text?.contains("I already have a link") == true) {
            binding.clThird.visibility = View.VISIBLE
            binding.tvAgeRange.visibility = View.VISIBLE
        } else {
            binding.clThird.visibility = View.GONE
            binding.tvAgeRange.visibility = View.INVISIBLE

        }
        val descriptionText = binding.etDescription.text?.toString() ?: ""
        val layoutParams = binding.etDescription.layoutParams

        layoutParams.height = if (descriptionText.isNotEmpty()) {
            ViewGroup.LayoutParams.WRAP_CONTENT
        } else {
            resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._82sdp)
        }
        binding.etDescription.layoutParams = layoutParams

        binding.buttonCheck = isAllNotEmpty
    }


    /** play format bottom sheet **/
    private fun playFormatBottomSheet(type: Int) {
        playFormatSheet =
            BaseCustomBottomSheet(requireContext(), R.layout.play_formate_bottom_layout) {
                when (it?.id) {
                    R.id.tvCancel -> {
                        playFormatSheet.dismiss()
                    }
                }
            }
        playFormatSheet.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        playFormatSheet.behavior.isDraggable = true
        if (type == 1) {
            playFormatSheet.binding.tvTitle.text = "Choose an option"
        } else {
            playFormatSheet.binding.tvTitle.text = "Choose the currency"
        }


        playFormatSheet.create()
        playFormatSheet.show()

        initPlayFormatAdapter(type)
    }


    /** handle play format adapter **/
    private fun initPlayFormatAdapter(type: Int) {
        gameModeAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_game_mode_item, BR.bean) { v, m, pos ->
                when (v.id) {
                    R.id.clGame -> {
                        if (type == 1) {
                            playFormatSheet.dismiss()
                            binding.etTournamentName.setText(m.title)
                        } else {
                            playFormatSheet.dismiss()
                            binding.etTournamentAddress.setText(m.title)
                        }

                    }
                }
            }
        if (type == 1) {
            gameModeAdapter.list = getListPlayFormat()
        } else {
            gameModeAdapter.list = getListPrice()
        }

        playFormatSheet.binding.rvGameModel.adapter = gameModeAdapter
    }

    // add list game mode
    private fun getListPlayFormat(): ArrayList<GameModeModel> {
        return arrayListOf(
            GameModeModel("Beginner"),
            GameModeModel("Intermediate"),
            GameModeModel("Experienced"),
            GameModeModel("Professional"),
        )
    }


    private fun getListPrice(): ArrayList<GameModeModel> {
        return arrayListOf(
            GameModeModel("Generated on BEBALLER"),
            GameModeModel("I already have a link"),
        )
    }

    /*** add validation ***/
    private fun validate(): Boolean {
        val name = binding.etTournamentName.text.toString().trim()
        val address = binding.etTournamentAddress.text.toString().trim()
        val priceType = binding.etDescription.text.toString().trim()
        if (name.isEmpty()) {
            showInfoToast("Please pick expected level")
            return false
        } else if (address.isEmpty()) {
            showInfoToast("Please pick registration player")
            return false
        } else if (priceType.isEmpty()) {
            showInfoToast("Please enter short description")
            return false
        }
        return true
    }
}