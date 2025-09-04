package com.beballer.beballer.ui.organizers.tournament_create

import android.app.DatePickerDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.viewModels
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.databinding.FragmentCreateTournamentSevenBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class CreateTournamentSevenFragment : BaseFragment<FragmentCreateTournamentSevenBinding>() {
    private val viewModel: CommonTournamentVM by viewModels()

    override fun getLayoutResource(): Int {
        return R.layout.fragment_create_tournament_seven
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnCLick()
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
                        requireActivity().finish()
                    }
                }

                R.id.etAgeRange -> {
                    openCalender()
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

        binding.etFirstName.addTextChangedListener(textWatcher)
        binding.etLastName.addTextChangedListener(textWatcher)
        binding.etAgeRange.addTextChangedListener(textWatcher)
        binding.etNumberOfPlayer.addTextChangedListener(textWatcher)

    }

    // Function to check all fields
    private fun checkAllFieldsNotEmpty() {
        val isAllNotEmpty =
            binding.etFirstName.text?.isNotEmpty() == true && binding.etLastName.text?.isNotEmpty() == true && binding.etAgeRange.text?.isNotEmpty() == true && binding.etNumberOfPlayer.text?.isNotEmpty() == true

        binding.buttonCheck = isAllNotEmpty
    }


    private fun openCalender() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireActivity(), { _, selectedYear, selectedMonth, selectedDay ->

                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(Calendar.YEAR, selectedYear)
                selectedCalendar.set(Calendar.MONTH, selectedMonth)
                selectedCalendar.set(Calendar.DAY_OF_MONTH, selectedDay)

                // Set current hour & minute to preserve time format
                val hour = selectedCalendar.get(Calendar.HOUR_OF_DAY)
                val minute = selectedCalendar.get(Calendar.MINUTE)

                val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy - HH:mm", Locale.US)
                val formattedDate = sdf.format(selectedCalendar.time)


                binding.etAgeRange.setText(formattedDate)


            }, year, month, day
        )

        datePickerDialog.show()
    }


    /*** add validation ***/
    private fun validate(): Boolean {
        val firstName = binding.etFirstName.text.toString().trim()
        val lastName = binding.etLastName.text.toString().trim()
        val age = binding.etAgeRange.text.toString().trim()
        val playerNumber = binding.etNumberOfPlayer.text.toString().trim()
        if (firstName.isEmpty()) {
            showInfoToast("Please enter first name")
            return false
        } else if (lastName.isEmpty()) {
            showInfoToast("Please enter last name")
            return false
        } else if (age.isEmpty()) {
            showInfoToast("Please pick age")
            return false
        } else if (playerNumber.isEmpty()) {
            showInfoToast("Please enter number of player")
            return false
        }
        return true
    }

}