package com.beballer.beballer.ui.player.create_profile.information

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.utils.BindingUtils
import com.beballer.beballer.utils.CommonBottomSheet
import com.beballer.beballer.databinding.FragmentInformationBinding
import com.beballer.beballer.databinding.GenderBottomSheetItemBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class InformationFragment : BaseFragment<FragmentInformationBinding>() {
    private val viewModel: InformationFragmentVM by viewModels()
    private lateinit var genderBottomSheet: CommonBottomSheet<GenderBottomSheetItemBinding>
    override fun getLayoutResource(): Int {
        return R.layout.fragment_information
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(view: View) {
        // click
        initOnCLick()
        val firstName = arguments?.getString("firstName") ?: ""
        val lastName = arguments?.getString("lastName") ?: ""
        val userName = arguments?.getString("userName") ?: ""
        val countryCode = arguments?.getString("countryCode") ?: ""
    }

    /** handle click **/
    @SuppressLint("DefaultLocale")
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun initOnCLick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.cancelImage -> {
                    findNavController().popBackStack()
                }

                R.id.btnNext -> {
                    if (validate()) {
                        val userDob = binding.etDate.text.toString().trim()
                        val userGender = binding.etGender.text.toString().trim()
                        val userHeight = binding.etHeight.text.toString().trim()

                        // Get previous values from arguments
                        val firstName = arguments?.getString("firstName") ?: ""
                        val lastName = arguments?.getString("lastName") ?: ""
                        val userName = arguments?.getString("userName") ?: ""
                        val countryCode = arguments?.getString("countryCode") ?: ""

                        // Combine all into one Bundle
                        val bundle = Bundle().apply {
                            putString("firstName", firstName)
                            putString("lastName", lastName)
                            putString("userName", userName)
                            putString("countryCode", countryCode)

                            putString("userDob", userDob)
                            putString("userGender", userGender)
                            putString("userHeight", userHeight)
                        }

                        BindingUtils.navigateWithSlide(
                            findNavController(),
                            R.id.navigateAddProfileFragment,
                            bundle
                        )
                    }

                }

                R.id.ivGender -> {
                    showGenderPicker(1)
                }

                R.id.ivHeight -> {
                    showGenderPicker(2)
                }

                R.id.clSkip -> {

                    // Get previous values from arguments
                    val firstName = arguments?.getString("firstName") ?: ""
                    val lastName = arguments?.getString("lastName") ?: ""
                    val userName = arguments?.getString("userName") ?: ""
                    val countryCode = arguments?.getString("countryCode") ?: ""

                    // Combine all into one Bundle
                    val bundle = Bundle().apply {
                        putString("firstName", firstName)
                        putString("lastName", lastName)
                        putString("userName", userName)
                        putString("countryCode", countryCode)

                    }

                    BindingUtils.navigateWithSlide(
                        findNavController(), R.id.navigateAddProfileFragment, bundle
                    )
                }

                R.id.ivDate -> {
                    val calendar = Calendar.getInstance()
                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH)
                    val day = calendar.get(Calendar.DAY_OF_MONTH)

                    val maxDateCalendar = Calendar.getInstance()
                    maxDateCalendar.add(Calendar.YEAR, -12)

                    val datePickerDialog = DatePickerDialog(
                        requireActivity(),
                        { _, selectedYear, selectedMonth, selectedDay ->
                            val selectedCalendar = Calendar.getInstance()
                            selectedCalendar.set(selectedYear, selectedMonth, selectedDay)

                            // Format: 01 June 1988
                            val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                            val formattedDate = sdf.format(selectedCalendar.time)
                            binding.etDate.setText(formattedDate)

                        },
                        year,
                        month,
                        day)

                    datePickerDialog.datePicker.maxDate = maxDateCalendar.timeInMillis
                    datePickerDialog.show()
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

        binding.etDate.addTextChangedListener(textWatcher)
        binding.etGender.addTextChangedListener(textWatcher)
        binding.etHeight.addTextChangedListener(textWatcher)

    }

    private val genderOptions = listOf("Male", "Female", "Other")
    private val heightOptions = (100..300).map { "$it cm" }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showGenderPicker(type: Int) {
        genderBottomSheet =
            CommonBottomSheet(requireContext(), R.layout.gender_bottom_sheet_item) {
                when (it?.id) {
                    R.id.btnOk -> {
                        val selectedValue = if (type == 1) {
                            genderOptions[genderBottomSheet.binding?.genderPicker?.value ?: 0]
                        } else {
                            heightOptions[genderBottomSheet.binding?.genderPicker?.value ?: 0]
                        }
                        if (type == 1) {
                            binding.etGender.setText(selectedValue)
                        } else {
                            binding.etHeight.setText(selectedValue)
                        }

                        genderBottomSheet.dismiss()
                    }
                }
            }

        val picker = genderBottomSheet.binding?.genderPicker
        picker?.minValue = 0
        picker?.wrapSelectorWheel = false
        picker?.textSize = 30f
        picker?.textColor = ContextCompat.getColor(requireContext(), R.color.black_000000)
        if (type == 1) {
            picker?.maxValue = genderOptions.size - 1
            picker?.displayedValues = genderOptions.toTypedArray()
        } else {
            picker?.maxValue = heightOptions.size - 1
            picker?.displayedValues = heightOptions.toTypedArray()
        }

        genderBottomSheet.behavior.isDraggable = true
        genderBottomSheet.setCancelable(true)
        genderBottomSheet.show()
    }


    // Function to check all fields
    private fun checkAllFieldsNotEmpty() {
        val isAllNotEmpty =
            binding.etDate.text?.isNotEmpty() == true && binding.etGender.text?.isNotEmpty() == true && binding.etHeight.text?.isNotEmpty() == true

        binding.buttonCheck = isAllNotEmpty
    }





    /*** add validation ***/
    private fun validate(): Boolean {
        val date = binding.etDate.text.toString().trim()
        val gender = binding.etGender.text.toString().trim()
        val height = binding.etHeight.text.toString().trim()
        if (date.isEmpty()) {
            showInfoToast("Pick your date of birth")
            return false
        } else if (gender.isEmpty()) {
            showInfoToast("Pick your gender")
            return false
        } else if (height.isEmpty()) {
            showInfoToast("Pick your height")
            return false
        }
        return true
    }


}