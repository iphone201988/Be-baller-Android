package com.beballer.beballer.ui.organizers.profile

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.databinding.FragmentOrganizersProfileBinding
import com.beballer.beballer.ui.organizers.dash_board.OrganizersDashBoardActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrganizersProfileFragment : BaseFragment<FragmentOrganizersProfileBinding>() {
    private val viewModel: OrganizersProfileFragmentVM by viewModels()


    override fun getLayoutResource(): Int {
        return R.layout.fragment_organizers_profile
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnClick()
    }


    /** handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.ivBack -> {
                    findNavController().popBackStack()
                }

                R.id.btnNext -> {
                    if (validate()) {
                        val intent =
                            Intent(requireActivity(), OrganizersDashBoardActivity::class.java)
                        startActivity(intent)
                        requireActivity().finishAffinity()
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

        binding.etCompanyName.addTextChangedListener(textWatcher)
        binding.etEmailAddress.addTextChangedListener(textWatcher)

    }

    // Function to check all fields
    private fun checkAllFieldsNotEmpty() {
        val isAllNotEmpty =
            binding.etCompanyName.text?.isNotEmpty() == true && binding.etEmailAddress.text?.isNotEmpty() == true
        binding.buttonCheck = isAllNotEmpty
    }

    /*** add validation ***/
    private fun validate(): Boolean {
        val companyName = binding.etCompanyName.text.toString().trim()
        val emailAddress = binding.etEmailAddress.text.toString().trim()
        val emailRegex = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()

        if (companyName.isEmpty()) {
            showInfoToast("Please enter company name")
            return false
        } else if (emailAddress.isEmpty()) {
            showInfoToast("Please enter email address")
            return false
        } else if (!emailAddress.matches(emailRegex)) {
            showInfoToast("Please enter a valid email address")
            return false
        }
        return true
    }


}