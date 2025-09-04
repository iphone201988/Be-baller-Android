package com.beballer.beballer.ui.organizers.camps_create

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.utils.BindingUtils
import com.beballer.beballer.databinding.FragmentCreateCampsThirdBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateCampsThirdFragment : BaseFragment<FragmentCreateCampsThirdBinding>() {
    private val viewModel: CommonCreateCampsFragmentVM by viewModels()


    override fun getLayoutResource(): Int {
        return R.layout.fragment_create_camps_third
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

                R.id.btnNext -> {
                    BindingUtils.navigateWithSlide(
                        findNavController(), R.id.createCampsFourFragment, null
                    )
                }
            }
        }


    }
}