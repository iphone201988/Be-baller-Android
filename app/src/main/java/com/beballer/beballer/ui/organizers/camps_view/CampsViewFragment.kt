package com.beballer.beballer.ui.organizers.camps_view

import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.databinding.FragmentCampsViewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CampsViewFragment : BaseFragment<FragmentCampsViewBinding>() {
    private val viewModel: CampsViewFragmentVM by viewModels()


    override fun getLayoutResource(): Int {
        return R.layout.fragment_camps_view
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // check
        binding.pos = 1
        binding.tvPools.typeface = ResourcesCompat.getFont(requireContext(), R.font.inter_bold)
        binding.tvMatch.typeface =
            ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
        binding.tvFinals.typeface =
            ResourcesCompat.getFont(requireContext(), R.font.inter_medium)

        // click
        initOnCLick()
        // adapter
        val adapter = CampsPagerAdapter(requireActivity().supportFragmentManager, lifecycle)
        binding.viewPagerProfile.adapter = adapter
        binding.viewPagerProfile.isUserInputEnabled = false
    }

    /*** all click event handel ***/
    private fun initOnCLick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.tvPools -> {
                    binding.pos = 1
                    binding.viewPagerProfile.currentItem = 0
                    binding.tvPools.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_bold)
                    binding.tvMatch.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
                    binding.tvFinals.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
                }

                R.id.tvMatch -> {
                    binding.pos = 2
                    binding.viewPagerProfile.currentItem = 1
                    binding.tvPools.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_bold)
                    binding.tvMatch.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
                    binding.tvFinals.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
                }

                R.id.tvFinals -> {
                    binding.pos = 3
                    binding.viewPagerProfile.currentItem = 2
                    binding.tvPools.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_bold)
                    binding.tvMatch.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
                    binding.tvFinals.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
                }
            }
        }
    }

}