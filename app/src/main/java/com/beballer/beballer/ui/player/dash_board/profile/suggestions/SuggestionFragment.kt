package com.beballer.beballer.ui.player.dash_board.profile.suggestions

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import com.beballer.beballer.BR
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.base.SimpleRecyclerViewAdapter
import com.beballer.beballer.utils.BaseCustomBottomSheet
import com.beballer.beballer.data.model.GameModeModel
import com.beballer.beballer.databinding.FragmentSuggestionBinding
import com.beballer.beballer.databinding.RvGameModeItemBinding
import com.beballer.beballer.databinding.SuggestionBottomSheetItemBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint


    @AndroidEntryPoint
    class SuggestionFragment : BaseFragment<FragmentSuggestionBinding>() {
        private val viewModel: SuggestionFragmentVM by viewModels()
        private lateinit var suggestionsAdapter: SimpleRecyclerViewAdapter<GameModeModel, RvGameModeItemBinding>
        private lateinit var suggestionsSheet: BaseCustomBottomSheet<SuggestionBottomSheetItemBinding>

        override fun getLayoutResource(): Int {
            return R.layout.fragment_suggestion
        }

        override fun getViewModel(): BaseViewModel {
            return viewModel
        }

        override fun onCreateView(view: View) {
            // set block pos
            binding.pos = 1
            // click
            initOnClick()
        }


        /** handle click **/
        private fun initOnClick() {
            viewModel.onClick.observe(viewLifecycleOwner) {
                when (it?.id) {
                    R.id.etImpact->{
                        suggestionBottomSheet()
                    }
                    // back button click
                    R.id.ivBack -> {
                        requireActivity().onBackPressed()
                    }
                    // tvFeed button click
                    R.id.tvFeed -> {
                        binding.pos = 1
                        binding.tvFeed.typeface = ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
                        binding.tvSubscriptions.typeface = ResourcesCompat.getFont(requireContext(), R.font.inter_regular)
                    }
                    // tvSubscriptions  button click
                    R.id.tvSubscriptions -> {
                        binding.pos = 2
                        binding.tvFeed.typeface = ResourcesCompat.getFont(requireContext(), R.font.inter_regular)
                        binding.tvSubscriptions.typeface = ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
                    }
                }
            }

            binding.etDetails.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    binding.buttonCheck = s?.isNotEmpty() == true
                }

            })
            binding.etSuggestion.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    binding.buttonCheck1 = s?.isNotEmpty() == true
                }

            })
        }


        /** game mode bottom sheet **/
        private fun suggestionBottomSheet() {
            suggestionsSheet =
                BaseCustomBottomSheet(requireContext(), R.layout.suggestion_bottom_sheet_item) {
                    when (it?.id) {
                        R.id.tvAnnuler->{
                            suggestionsSheet.dismiss()
                        }
                    }
                }
            suggestionsSheet.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            suggestionsSheet.behavior.isDraggable = true
            suggestionsSheet.create()
            suggestionsSheet.show()

            initSuggestionsAdapter()
        }

        /** handle game mode adapter **/
        private fun initSuggestionsAdapter() {
            suggestionsAdapter = SimpleRecyclerViewAdapter(R.layout.rv_game_mode_item, BR.bean) { v, m, pos ->
                when (v.id) {
                    R.id.clGame->{
                        suggestionsSheet.dismiss()
                       binding.etImpact.setText(m.title)
                    }
                }
            }
            suggestionsAdapter.list = getListSuggestions()
            suggestionsSheet.binding.rvGameModel.adapter = suggestionsAdapter
        }

        // add list game mode
        private fun getListSuggestions(): ArrayList<GameModeModel> {
            return arrayListOf(
                GameModeModel("Unable to use the application"),
                GameModeModel("Unusable feature"),
                GameModeModel("Display problem"),
                GameModeModel("Problème mineur n'empêchant pas le bon fonctionnement"),
                GameModeModel("Other"),)
        }


    }