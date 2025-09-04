package com.beballer.beballer.ui.player.dash_board.find.tournament.details.player

import android.view.View
import androidx.fragment.app.viewModels
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.databinding.FragmentPlayerBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PlayerFragment : BaseFragment<FragmentPlayerBinding>() {
    private val viewModel: PlayerFragmentVM by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.fragment_player
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {

    }



}