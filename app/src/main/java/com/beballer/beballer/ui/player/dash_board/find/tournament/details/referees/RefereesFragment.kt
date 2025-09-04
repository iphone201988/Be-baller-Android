package com.beballer.beballer.ui.player.dash_board.find.tournament.details.referees

import android.view.View
import androidx.fragment.app.viewModels
import com.beballer.beballer.BR
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.base.SimpleRecyclerViewAdapter
import com.beballer.beballer.data.model.PlayerPostModel
import com.beballer.beballer.databinding.FragmentRefereesBinding
import com.beballer.beballer.databinding.RvOrganizersItemBinding
import com.beballer.beballer.databinding.RvSpectatorsItemBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RefereesFragment : BaseFragment<FragmentRefereesBinding>() {
    private val viewModel: RefereesFragmentVM by viewModels()

    override fun getLayoutResource(): Int {
        return R.layout.fragment_referees
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {

    }




}