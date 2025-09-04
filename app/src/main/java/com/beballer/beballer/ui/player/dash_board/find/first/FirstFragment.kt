package com.beballer.beballer.ui.player.dash_board.find.first

import android.view.View
import androidx.fragment.app.viewModels
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.databinding.FragmentFirstBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FirstFragment : BaseFragment<FragmentFirstBinding>() {
    private val viewModel: FirstFragmentVM by viewModels()


    override fun getLayoutResource(): Int {
        return R.layout.fragment_first
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        val imageList : ArrayList<Int> = arguments?.getIntegerArrayList("imageList") as ArrayList<Int>
        val position = arguments?.getInt("position", -1)


        if(position != null && position >= 0 && position < imageList.size){
            binding.img.setImageResource(imageList[position])
        }
    }

}