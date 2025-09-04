package com.beballer.beballer.ui.organizers.camps_create

import android.app.DatePickerDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.View
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
import com.beballer.beballer.databinding.FragmentCreateCampsSecondsBinding
import com.beballer.beballer.databinding.PlayFormateBottomLayoutBinding
import com.beballer.beballer.databinding.RvGameModeItemBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class CreateCampsSecondFragment : BaseFragment<FragmentCreateCampsSecondsBinding>() {
    private val viewModel: CommonCreateCampsFragmentVM by viewModels()
    private lateinit var playFormatSheet: BaseCustomBottomSheet<PlayFormateBottomLayoutBinding>
    private lateinit var gameModeAdapter: SimpleRecyclerViewAdapter<GameModeModel, RvGameModeItemBinding>
    override fun getLayoutResource(): Int {
        return R.layout.fragment_create_camps_seconds
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
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

                R.id.etStartDate -> {
                    openCalender(1)
                }

                R.id.etEndDate -> {
                    openCalender(2)
                }

                R.id.etSkip -> {
                    playFormatBottomSheet()
                }

                R.id.btnNext -> {
                    if (validate()) {
                        BindingUtils.navigateWithSlide(
                            findNavController(), R.id.createCampsThirdFragment, null
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


        binding.etEndDate.addTextChangedListener(textWatcher)
        binding.etStartDate.addTextChangedListener(textWatcher)
        binding.etPrice.addTextChangedListener(textWatcher)
        binding.etSkip.addTextChangedListener(textWatcher)
    }


    private fun openCalender(type: Int) {
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

                if (type == 1) {
                    binding.etStartDate.setText(formattedDate)
                } else {
                    binding.etEndDate.setText(formattedDate)
                }

            }, year, month, day
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }


    // Function to check all fields
    private fun checkAllFieldsNotEmpty() {
        val isAllNotEmpty =
            binding.etEndDate.text?.isNotEmpty() == true && binding.etStartDate.text?.isNotEmpty() == true && binding.etPrice.text?.isNotEmpty() == true && binding.etSkip.text?.isNotEmpty() == true
        binding.buttonCheck = isAllNotEmpty
    }


    /** play format bottom sheet **/
    private fun playFormatBottomSheet() {
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
        playFormatSheet.binding.tvTitle.text = "Choose the currency"
        playFormatSheet.create()
        playFormatSheet.show()

        initPlayFormatAdapter()
    }


    /** handle play format adapter **/
    private fun initPlayFormatAdapter() {
        gameModeAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_game_mode_item, BR.bean) { v, m, pos ->
                when (v.id) {
                    R.id.clGame -> {
                        playFormatSheet.dismiss()
                        binding.etSkip.setText(m.title)
                    }
                }
            }
        gameModeAdapter.list = getListPrice()
        playFormatSheet.binding.rvGameModel.adapter = gameModeAdapter
    }

    // add list game mode
    private fun getListPrice(): ArrayList<GameModeModel> {
        return arrayListOf(
            GameModeModel("€ EUR"),
            GameModeModel("$ USD"),
            GameModeModel("£ GBP"),
            GameModeModel("Fr. CHF"),
            GameModeModel("$ AUD"),
            GameModeModel("$ CAD"),
            GameModeModel("kr DKK"),
            GameModeModel("kr SEK"),
            GameModeModel("¥ JPY"),

            )
    }

    /*** add validation ***/
    private fun validate(): Boolean {
        val startDate = binding.etStartDate.text.toString().trim()
        val endDate = binding.etEndDate.text.toString().trim()
        val range = binding.etPrice.text.toString().trim()
        val priceType = binding.etSkip.text.toString().trim()
        if (startDate.isEmpty()) {
            showInfoToast("Please pick start date")
            return false
        } else if (endDate.isEmpty()) {
            showInfoToast("Please pick end date")
            return false
        } else if (range.isEmpty()) {
            showInfoToast("Please enter age range name")
            return false
        } else if (priceType.isEmpty()) {
            showInfoToast("Please pick currency type")
            return false
        }
        binding.buttonCheck = true
        return true
    }

}