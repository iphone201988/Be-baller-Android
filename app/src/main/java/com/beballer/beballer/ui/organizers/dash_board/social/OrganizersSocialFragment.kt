package com.beballer.beballer.ui.organizers.dash_board.social

import android.view.View
import androidx.fragment.app.viewModels
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.databinding.FragmentOrganizersSocialBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrganizersSocialFragment : BaseFragment<FragmentOrganizersSocialBinding>() {
    private val viewModel: OrganizersSocialFragmentVM by viewModels()


    override fun getLayoutResource(): Int {
        return R.layout.fragment_organizers_social
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {

    }
}