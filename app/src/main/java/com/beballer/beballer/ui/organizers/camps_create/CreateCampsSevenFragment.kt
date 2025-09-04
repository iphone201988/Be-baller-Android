package com.beballer.beballer.ui.organizers.camps_create

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.utils.BindingUtils
import com.beballer.beballer.data.model.CreateTournamentModel
import com.beballer.beballer.databinding.FragmentCreateCampsSevenBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateCampsSevenFragment : BaseFragment<FragmentCreateCampsSevenBinding>() {
    private val viewModel: CommonCreateCampsFragmentVM by viewModels()
    private var editedCourtsList = ArrayList<CreateTournamentModel>()
    private val adapter by lazy { CouNameAdapter(requireContext(), editedCourtsList) }
    private var type = 0
    override fun getLayoutResource(): Int {
        return R.layout.fragment_create_camps_seven
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // onclick
        initOnCLick()
        val eventType = arguments?.getString("tournamentCount")
        val campsType = arguments?.getString("campsType")
        if (campsType != null) {
            type = if (campsType.contains("camps")) {
                1
            } else {
                2
            }
        }
        if (eventType != null) {
            editedCourtsList = ArrayList(List(eventType.toInt()) { index ->
                CreateTournamentModel("Court ${index + 1}")
            })
            binding.rvCampsSeven.adapter = adapter
        }


    }

    /*** click handel ***/
    private fun initOnCLick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.cancelImage -> {
                    findNavController().popBackStack()
                }

                R.id.btnNext -> {
                    if (editedCourtsList.any { ec -> ec.name.isEmpty() }) {
                        showInfoToast("Please fill all court names")
                    } else {
                        if (type == 2) {
                            BindingUtils.navigateWithSlide(
                                findNavController(), R.id.OrgTournamentDetailsFragment, null
                            )
                        } else {
//                            BindingUtils.navigateWithSlide(
//                                findNavController(), R.id.OrgTournamentDetailsFragment, null
//                            )
                        }

                    }

                }


            }
        }
    }


}