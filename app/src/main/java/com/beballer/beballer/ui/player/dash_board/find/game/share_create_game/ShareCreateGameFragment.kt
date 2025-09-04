package com.beballer.beballer.ui.player.dash_board.find.game.share_create_game

import android.view.View
import androidx.fragment.app.viewModels
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.databinding.FragmentShareCreateGameBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShareCreateGameFragment : BaseFragment<FragmentShareCreateGameBinding>() {
    private val viewModel: ShareCreateGameVM by viewModels()


    override fun getLayoutResource(): Int {
        return R.layout.fragment_share_create_game
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
                R.id.ivCopy -> {

                }
                R.id.btnNext->{

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