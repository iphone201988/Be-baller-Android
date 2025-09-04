package com.beballer.beballer.ui.player.create_profile.profile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.utils.BindingUtils
import com.beballer.beballer.utils.Status
import com.beballer.beballer.data.api.Constants
import com.beballer.beballer.data.model.CommonResponse
import com.beballer.beballer.databinding.FragmentCreateProfileBinding
import com.beballer.beballer.ui.player.create_profile.CreateProfileActivityVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateProfileFragment : BaseFragment<FragmentCreateProfileBinding>() {

    private val viewModel: CreateProfileActivityVM by viewModels()


    override fun getLayoutResource(): Int {
        return R.layout.fragment_create_profile
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnCLick()
        // observer
        initObserver()
    }

    /** handle click **/
    private fun initOnCLick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.cancelImage -> {
                    requireActivity().finish()
                }

                R.id.btnNext -> {
                    if (validate()) {
                        val userName = binding.etUserName.text.toString().trim()
                        val data = HashMap<String, Any>()
                        data["username"] = userName
                        viewModel.userNameCheck(data, Constants.USER_NAME_UNIQUE)
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

        binding.etFirst.addTextChangedListener(textWatcher)
        binding.etUserName.addTextChangedListener(textWatcher)
        binding.etLastName.addTextChangedListener(textWatcher)

    }


    /**  api response observer  **/
    private fun initObserver() {
        viewModel.commonObserver.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.LOADING -> {
                 showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "userNameCheck" -> {
                            try {
                                val myDataModel: CommonResponse? = BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    if (myDataModel.message?.isNotEmpty() == true) {
                                        val firstName = binding.etFirst.text.toString().trim()
                                        val lastName = binding.etLastName.text.toString().trim()
                                        val userName = binding.etUserName.text.toString().trim()
                                        val countryPhoneCode =
                                            binding.countryCodePicker.selectedCountryCode
                                        val bundle = Bundle().apply {
                                            putString("firstName", firstName)
                                            putString("lastName", lastName)
                                            putString("userName", userName)
                                            putString("countryCode", countryPhoneCode)
                                        }
                                        BindingUtils.navigateWithSlide(
                                            findNavController(),
                                            R.id.navigateInformationFragment,
                                            bundle
                                        )
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("error", "userNameCheck: $e")
                            }finally {
                                hideLoading()
                            }
                        }

                    }
                }

                Status.ERROR -> {
                    hideLoading()
                    showErrorToast(it.message.toString())
                }

                else -> {
                }
            }
        }
    }


    // Function to check all fields
    private fun checkAllFieldsNotEmpty() {
        val isAllNotEmpty =
            binding.etFirst.text?.isNotEmpty() == true && binding.etUserName.text?.isNotEmpty() == true && binding.etLastName.text?.isNotEmpty() == true

        binding.buttonCheck = isAllNotEmpty
    }


    /*** add validation ***/
    private fun validate(): Boolean {
        val firstName = binding.etFirst.text.toString().trim()
        val lastName = binding.etLastName.text.toString().trim()
        val userName = binding.etUserName.text.toString().trim()
        if (firstName.isEmpty()) {
            showInfoToast("Please enter first name")
            return false
        } else if (lastName.isEmpty()) {
            showInfoToast("Please enter last name")
            return false
        } else if (userName.isEmpty()) {
            showInfoToast("Please enter user name")
            return false
        }
        return true
    }

}