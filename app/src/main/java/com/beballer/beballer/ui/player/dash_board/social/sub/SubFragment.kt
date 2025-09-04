package com.beballer.beballer.ui.player.dash_board.social.sub

import android.view.View
import androidx.fragment.app.viewModels
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.databinding.FragmentSubBinding
import com.beballer.beballer.ui.interfacess.OnNextClickListener
import com.beballer.beballer.ui.player.dash_board.social.SocialsFragmentVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubFragment : BaseFragment<FragmentSubBinding>() {
    private val viewModel: SocialsFragmentVM by viewModels()
    private var listener: OnNextClickListener? = null

    companion object {
        fun newInstance(listener: OnNextClickListener): SubFragment {
            val fragment = SubFragment()
            fragment.listener = listener
            return fragment
        }
    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_sub
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnClick()


    }

    /** click handel ***/
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.btnNext -> {
                    listener?.onNextClicked()
                }
            }
        }
    }


}

