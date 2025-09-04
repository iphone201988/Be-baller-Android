package com.beballer.beballer.ui.organizers.team_profile

import android.view.View
import androidx.fragment.app.viewModels
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.databinding.FragmentTeamProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeamProfileFragment : BaseFragment<FragmentTeamProfileBinding>() {

    private val viewModel: TeamProfileFragmentVM by viewModels()


    override fun getLayoutResource(): Int {
        return R.layout.fragment_team_profile
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {

    }

}