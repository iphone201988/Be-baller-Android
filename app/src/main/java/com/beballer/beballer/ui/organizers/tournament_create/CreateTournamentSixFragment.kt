package com.beballer.beballer.ui.organizers.tournament_create

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.utils.BindingUtils
import com.beballer.beballer.databinding.FragmentCreateTournamentSixBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateTournamentSixFragment : BaseFragment<FragmentCreateTournamentSixBinding>() {
    private val viewModel: CommonTournamentVM by viewModels()


    override fun getLayoutResource(): Int {
        return R.layout.fragment_create_tournament_six
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // onclick
        initOnCLick()
    }

    /*** click handel ***/
    private fun initOnCLick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.cancelImage -> {
                    findNavController().popBackStack()
                }


                R.id.btnNext -> {
                    if (validate()) {
                        val bundle = Bundle().apply {
                            putString("tournamentCount", binding.etNumberOfCourts.text.toString().trim())
                            putString("campsType","tournaments")
                        }
                        BindingUtils.navigateWithSlide(
                            findNavController(), R.id.createCampsSevenFragment, bundle
                        )
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

        binding.etNumberOfItem.addTextChangedListener(textWatcher)
        binding.etNumberOfGroup.addTextChangedListener(textWatcher)
        binding.etNumberOfCourts.addTextChangedListener(textWatcher)

    }

    // Function to check all fields
    private fun checkAllFieldsNotEmpty() {
        val isAllNotEmpty = binding.etNumberOfItem.text?.isNotEmpty() == true && binding.etNumberOfGroup.text?.isNotEmpty() == true && binding.etNumberOfCourts.text?.isNotEmpty() == true

        binding.buttonCheck = isAllNotEmpty
    }

    /*** add validation ***/
    private fun validate(): Boolean {
        val numberOfItem = binding.etNumberOfItem.text.toString().trim()
        val numberOfGroup = binding.etNumberOfGroup.text.toString().trim()
        val numberOfCourt = binding.etNumberOfCourts.text.toString().trim()
        if (numberOfItem.isEmpty()) {
            showInfoToast("Please enter number of item")
            return false
        } else if (numberOfGroup.isEmpty()) {
            showInfoToast("Please enter number og group")
            return false
        } else if (numberOfCourt.isEmpty()) {
            showInfoToast("Please enter number of court")
            return false
        }
        return true
    }
}