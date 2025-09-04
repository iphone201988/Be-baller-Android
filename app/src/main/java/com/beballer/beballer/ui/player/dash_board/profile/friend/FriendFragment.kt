package com.beballer.beballer.ui.player.dash_board.profile.friend

import android.view.View
import androidx.fragment.app.viewModels
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.databinding.FragmentFriendBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FriendFragment : BaseFragment<FragmentFriendBinding>() {
    private val viewModel: FriendFragmentVM by viewModels()


    override fun getLayoutResource(): Int {
        return R.layout.fragment_friend
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

                R.id.tvCodeCopy -> {

                }
            }
        }
    }
}