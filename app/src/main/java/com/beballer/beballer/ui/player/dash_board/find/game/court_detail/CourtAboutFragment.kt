package com.beballer.beballer.ui.player.dash_board.find.game.court_detail

import android.os.Bundle
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
import com.beballer.beballer.databinding.AccessibilityDialogItemBinding
import com.beballer.beballer.databinding.FragmentCourtAboutBinding
import com.beballer.beballer.databinding.RvGameModeItemBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CourtAboutFragment : BaseFragment<FragmentCourtAboutBinding>() {
    private val viewModel: CourtAboutFragmentVM by viewModels()
    private lateinit var accessibilityDialog: BaseCustomBottomSheet<AccessibilityDialogItemBinding>
    private lateinit var accessibilityAdapter: SimpleRecyclerViewAdapter<GameModeModel, RvGameModeItemBinding>

    override fun getLayoutResource(): Int {
        return R.layout.fragment_court_about
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
      // click
        initOnclick()
    }

    /*** click event handel **/
    private fun initOnclick() {
        viewModel.onClick.observe(viewLifecycleOwner){
            when(it?.id){
                R.id.cancelImage->{
                    findNavController().popBackStack()
                }
                R.id.btnNext->{
                    if (validate()){
                        val courtName = arguments?.getString("courtName")
                        val courtAddress = arguments?.getString("courtAddress")
                        val accessibility = arguments?.getString("accessibility")
                        val hoopsCount = arguments?.getString("hoopsCount")
                        val bundle = Bundle().apply {
                            putString("boardType", binding.etBoardType.text.toString().trim())
                            putString("netType", binding.etNetType.text.toString().trim())
                            putString("floorType", binding.etFloorType.text.toString().trim())
                            putString("linesAnd", binding.etLines.text.toString().trim())
                            putString("waterPoint", binding.etWaterPoint.text.toString().trim())
                            putString("courtName", courtName)
                            putString("courtAddress", courtAddress)
                            putString("accessibility", accessibility)
                            putString("hoopsCount",hoopsCount)
                        }

                        BindingUtils.navigateWithSlide(
                            findNavController(), R.id.navigateAddPhotoFragment, bundle
                        )
                    }
                }
                R.id.etBoardType->{
                    accessibilityBottomSheet(1)
                }
                R.id.etNetType->{
                    accessibilityBottomSheet(2)
                }
                R.id.etFloorType->{
                    accessibilityBottomSheet(3)
                }
                R.id.etLines->{
                    accessibilityBottomSheet(4)
                }
                R.id.etWaterPoint->{
                    accessibilityBottomSheet(5)
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

        binding.etBoardType.addTextChangedListener(textWatcher)
        binding.etNetType.addTextChangedListener(textWatcher)
        binding.etFloorType.addTextChangedListener(textWatcher)
        binding.etLines.addTextChangedListener(textWatcher)
        binding.etWaterPoint.addTextChangedListener(textWatcher)
    }



    /*** Function to check all fields  ***/
    private fun checkAllFieldsNotEmpty() {
        val isAllNotEmpty =
            binding.etBoardType.text?.isNotEmpty() == true &&
                    binding.etNetType.text?.isNotEmpty() == true &&
                    binding.etFloorType.text?.isNotEmpty() == true &&
                    binding.etLines.text?.isNotEmpty() == true &&
                    binding.etWaterPoint.text?.isNotEmpty() == true

        binding.buttonCheck = isAllNotEmpty
    }

    /** game mode bottom sheet **/
    private fun accessibilityBottomSheet(type :Int) {
        accessibilityDialog =
            BaseCustomBottomSheet(requireContext(), R.layout.accessibility_dialog_item) {
                when (it?.id) {
                    R.id.tvCancel->{
                        accessibilityDialog.dismiss()
                    }
                }
            }
        accessibilityDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        accessibilityDialog.behavior.isDraggable = true
        accessibilityDialog.create()
        accessibilityDialog.show()
        when(type){
            1->{
                accessibilityDialog.binding.tvTitle.text="Board type"
            }
            2->{
                accessibilityDialog.binding.tvTitle.text="Net type"
            }
            3->{
            accessibilityDialog.binding.tvTitle.text="Floor type"
        }
            4->{
            accessibilityDialog.binding.tvTitle.text="Lines and dimensions"
        }
            5->{
            accessibilityDialog.binding.tvTitle.text="Water point"
        }



        }
        initAccessibilityAdapter(type)
    }


    /** handle game mode adapter **/
    private fun initAccessibilityAdapter(type: Int) {
        accessibilityAdapter = SimpleRecyclerViewAdapter(R.layout.rv_game_mode_item, BR.bean) { v, m, pos ->
            when (v.id) {
                R.id.clGame->{
                    accessibilityDialog.dismiss()
                    when (type) {
                        1 -> {
                            binding.etBoardType.setText(m.title)
                        }
                        2 -> {
                            binding.etNetType.setText(m.title)
                        }
                        3 -> {
                            binding.etFloorType.setText(m.title)
                        }
                        4 -> {
                            binding.etLines.setText(m.title)
                        }
                        5 -> {
                            binding.etWaterPoint.setText(m.title)
                        }
                    }

                }
            }
        }
        when (type) {
            1 -> {
                accessibilityAdapter.list = getListBoardType()
            }
            2 -> {
                accessibilityAdapter.list = getListNetType()
            }
            3 -> {
                accessibilityAdapter.list = getListFloorType()
            }
            4 -> {
                accessibilityAdapter.list = getListLineType()
            }
            5 -> {
                accessibilityAdapter.list = getListWaterPointType()
            }
        }

        accessibilityDialog.binding.rvGameModel.adapter = accessibilityAdapter
    }

    // add list game mode
    private fun getListBoardType(): ArrayList<GameModeModel> {
        return arrayListOf(
            GameModeModel("Steel"),
            GameModeModel("Wood"),
            GameModeModel("Plastic"),
            GameModeModel("Plexiglas"),)
    }

    private fun getListNetType(): ArrayList<GameModeModel> {
        return arrayListOf(
            GameModeModel("String"),
            GameModeModel("Chain"),
            GameModeModel("Plastic"),
            GameModeModel("No nets"),

            )
    }

    private fun getListFloorType(): ArrayList<GameModeModel> {
        return arrayListOf(
            GameModeModel("Asphalt with gravel"),
            GameModeModel("Bitumen without gravel"),
            GameModeModel("synth"),
            GameModeModel("parquet"),

            )
    }

    private fun getListLineType(): ArrayList<GameModeModel> {
        return arrayListOf(
            GameModeModel("Up to standards"),
            GameModeModel("Not up to standard"),

            )
    }


    private fun getListWaterPointType(): ArrayList<GameModeModel> {
        return arrayListOf(
            GameModeModel("With"),
            GameModeModel("Without"),

            )
    }



    /*** add validation ***/
    private fun validate(): Boolean {
        val boardType = binding.etBoardType.text.toString().trim()
        val netType = binding.etNetType.text.toString().trim()
        val floorType = binding.etFloorType.text.toString().trim()
        val lineType = binding.etLines.text.toString().trim()
        val waterPoint = binding.etWaterPoint.text.toString().trim()
        if (boardType.isEmpty()) {
            showInfoToast("Please pick board type")
            return false
        } else if (netType.isEmpty()) {
            showInfoToast("Please pick net type")
            return false
        } else if (floorType.isEmpty()) {
            showInfoToast("Please pick floor type")
            return false
        }
        else if (lineType.isEmpty()) {
            showInfoToast("Please pick lines and dimensions")
            return false
        }else if (waterPoint.isEmpty()) {
            showInfoToast("Please pick water point")
            return false
        }
        return true
    }

}