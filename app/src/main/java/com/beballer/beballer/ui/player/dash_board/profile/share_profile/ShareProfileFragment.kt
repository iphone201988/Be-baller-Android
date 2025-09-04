package com.beballer.beballer.ui.player.dash_board.profile.share_profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.databinding.FragmentShareProfileBinding
import dagger.hilt.android.AndroidEntryPoint

    @AndroidEntryPoint
    class ShareProfileFragment : BaseFragment<FragmentShareProfileBinding>() {
        private val viewModel: ShareProfileFragmentVM by viewModels()


        override fun getLayoutResource(): Int {
            return R.layout.fragment_share_profile
        }

        override fun getViewModel(): BaseViewModel {
            return viewModel
        }

        override fun onCreateView(view: View) {
            // click
            initClick()
        }

        /** handle click **/
        private fun initClick() {
            viewModel.onClick.observe(viewLifecycleOwner) {
                when (it?.id) {
                    R.id.ivBack -> {
                        requireActivity().finish()
                    }
                    R.id.tvCopy->{

                    }
                    R.id.tvCodeCopy->{

                    }
                    R.id.ivInstagram->{

                    }
                    R.id.ivMessenger->{

                    }
                    R.id.ivTwitter->{

                    }
                    R.id.ivWhatsapp->{

                    }
                }
            }
        }

    }