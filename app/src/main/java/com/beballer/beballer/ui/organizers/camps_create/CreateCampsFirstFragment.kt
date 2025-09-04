package com.beballer.beballer.ui.organizers.camps_create

import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.utils.BindingUtils
import com.beballer.beballer.databinding.FragmentCreateCampsFirstBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateCampsFirstFragment : BaseFragment<FragmentCreateCampsFirstBinding>() {
    private val viewModel: CommonCreateCampsFragmentVM by viewModels()


    override fun getLayoutResource(): Int {
        return R.layout.fragment_create_camps_first
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(view: View) {
        // click
        initOnCLick()
    }


    /** handle click **/
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun initOnCLick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.cancelImage -> {
                    requireActivity().finish()
                }

                R.id.btnNext -> {
                    if (validate()) {
                        BindingUtils.navigateWithSlide(
                            findNavController(), R.id.createCampsSecondFragment, null
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

        binding.etTournamentName.addTextChangedListener(textWatcher)
        binding.etTournamentAddress.addTextChangedListener(textWatcher)
        binding.etAgeRange.addTextChangedListener(textWatcher)

    }


    // Function to check all fields
    private fun checkAllFieldsNotEmpty() {
        val isAllNotEmpty =
            binding.etTournamentName.text?.isNotEmpty() == true && binding.etTournamentAddress.text?.isNotEmpty() == true && binding.etAgeRange.text?.isNotEmpty() == true

        binding.buttonCheck = isAllNotEmpty
    }


    /*** add validation ***/
    private fun validate(): Boolean {
        val name = binding.etTournamentName.text.toString().trim()
        val address = binding.etTournamentAddress.text.toString().trim()
        val range = binding.etAgeRange.text.toString().trim()
        if (name.isEmpty()) {
            showInfoToast("Please enter tournament name")
            return false
        } else if (address.isEmpty()) {
            showInfoToast("Please enter address name")
            return false
        } else if (range.isEmpty()) {
            showInfoToast("Please enter age range name")
            return false
        }
        return true
    }

}