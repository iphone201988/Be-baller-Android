package com.beballer.beballer.ui.player.dash_board.social.sub

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.beballer.beballer.ui.interfacess.OnNextClickListener


class SubAdapter(
    activity: FragmentActivity,
    private val listener: OnNextClickListener
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SubSecondFragment.newInstance(listener)
            1 -> SubFragment.newInstance(listener)
            2 -> SubSecondFragment.newInstance( listener)
            3 -> SubFragment.newInstance(listener)
            else -> SubFragment.newInstance(listener)
        }
    }
}

