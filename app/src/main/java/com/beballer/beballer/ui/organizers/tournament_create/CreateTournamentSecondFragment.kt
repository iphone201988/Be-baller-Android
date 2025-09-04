package com.beballer.beballer.ui.organizers.tournament_create

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
import com.beballer.beballer.databinding.FragmentCreateTournamentSecondBinding
import com.beballer.beballer.databinding.PlayFormateBottomLayoutBinding
import com.beballer.beballer.databinding.RvGameModeItemBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class CreateTournamentSecondFragment : BaseFragment<FragmentCreateTournamentSecondBinding>() {
    private val viewModel: CommonTournamentVM by viewModels()
    private lateinit var playFormatSheet: BaseCustomBottomSheet<PlayFormateBottomLayoutBinding>
    private lateinit var gameModeAdapter: SimpleRecyclerViewAdapter<GameModeModel, RvGameModeItemBinding>

    override fun getLayoutResource(): Int {
        return R.layout.fragment_create_tournament_second
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

                R.id.etPlayFormat -> {
                    playFormatBottomSheet(1)
                }

                R.id.etStartDate -> {
                    openCalender()
                }

                R.id.clSkip -> {
                    playFormatBottomSheet(2)
                }

                R.id.btnNext -> {
                    if (validate()) {
                        BindingUtils.navigateWithSlide(
                            findNavController(), R.id.tournamentThird, null
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


        binding.etPlayFormat.addTextChangedListener(textWatcher)
        binding.etStartDate.addTextChangedListener(textWatcher)
        binding.etPrice.addTextChangedListener(textWatcher)
        binding.etSkip.addTextChangedListener(textWatcher)
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

                    binding.etStartDate.setText(formattedDate)

            }, year, month, day
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }

    // Function to check all fields
    private fun checkAllFieldsNotEmpty() {
        val isAllNotEmpty =
            binding.etPlayFormat.text?.isNotEmpty() == true && binding.etStartDate.text?.isNotEmpty() == true && binding.etPrice.text?.isNotEmpty() == true && binding.etSkip.text?.isNotEmpty() == true
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
                            binding.etPlayFormat.setText(m.title)
                        } else {
                            playFormatSheet.dismiss()
                            binding.etSkip.setText(m.title)
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
            GameModeModel("1x1"),
            GameModeModel("2x2"),
            GameModeModel("3x3"),
            GameModeModel("4x4"),
            GameModeModel("5x5"),

            )
    }


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
        val name = binding.etPlayFormat.text.toString().trim()
        val address = binding.etStartDate.text.toString().trim()
        val range = binding.etPrice.text.toString().trim()
        val priceType = binding.etSkip.text.toString().trim()
        if (name.isEmpty()) {
            showInfoToast("Please pick play format")
            return false
        } else if (address.isEmpty()) {
            showInfoToast("Please enter address name")
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