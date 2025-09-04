package com.beballer.beballer.ui.player.auth.onboarding

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.utils.BindingUtils
import com.beballer.beballer.databinding.FragmentOnBoardingBinding
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class OnBoardingFragment : BaseFragment<FragmentOnBoardingBinding>() {
    private val viewModel: OnBoardingFragmentVM by viewModels()
    private var userRole = 0
    override fun getLayoutResource(): Int {
        return R.layout.fragment_on_boarding
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // observer
        initObserver()
        // click
        initOnClick()
        userRole = 0
        // set  image
        Glide.with(this)
            .load(R.drawable.playeraccountillustration)
            .override(1080, 1080)
            .into(binding.ivTop)

        Glide.with(this)
            .load(R.drawable.player_colored_icon)
            .override(1080, 1080)
            .into(binding.ivStart)
        Glide.with(this)
            .load(R.drawable.organizer_icon)
            .override(1080, 1080)
            .into(binding.ivEnd)
    }

    /** handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.clStart -> {
                    Glide.with(this)
                        .load(R.drawable.playeraccountillustration)
                        .override(1080, 1080)
                        .into(binding.ivTop)

                    Glide.with(this)
                        .load(R.drawable.player_colored_icon)
                        .override(1080, 1080)
                        .into(binding.ivStart)

                    Glide.with(this)
                        .load(R.drawable.organizer_icon)
                        .override(1080, 1080)
                        .into(binding.ivEnd)
                    userRole = 1
                    binding.buttonCheck = true
                }

                R.id.clEnd -> {
                    Glide.with(this)
                        .load(R.drawable.organizeraccountillustration)
                        .override(1080, 1080)
                        .into(binding.ivTop)
                    userRole = 2
                    binding.buttonCheck = true

                    Glide.with(this)
                        .load(R.drawable.player_icon)
                        .override(1080, 1080)
                        .into(binding.ivStart)
                    Glide.with(this)

                        .load(R.drawable.organizer_colored_icon)
                        .override(1080, 1080)
                        .into(binding.ivEnd)
                }

                R.id.btnNext -> {
                    if (userRole != 0) {
                        if (userRole == 1) {
                            BindingUtils.navigateWithSlide(
                                findNavController(), R.id.navigateSignupFragment, null
                            )
                        } else {
                            BindingUtils.navigateWithSlide(
                                findNavController(), R.id.navigateOrganisateurLoginFragment, null
                            )
                        }
                    } else {
                        showInfoToast("Please select user role")
                    }

                }
            }
        }
    }

    /** handle api response **/
    private fun initObserver() {


    }
}