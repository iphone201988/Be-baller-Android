package com.beballer.beballer.ui.player.dash_board

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.beballer.beballer.ui.player.dash_board.find.FindFragment
import com.beballer.beballer.ui.player.dash_board.profile.ProfileFragment
import com.beballer.beballer.ui.player.dash_board.progression.ProgressionsFragment
import com.beballer.beballer.ui.player.dash_board.social.SocialsFragment


class MyFragmentPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    private val fragments = mutableMapOf<Int, Fragment>()

    override fun getItemCount(): Int = 5

    override fun createFragment(position: Int): Fragment {
        return fragments.getOrPut(position) {
            when (position) {
                0 -> SocialsFragment()
                1 -> FindFragment()
                2 -> FindFragment()
                3 -> ProgressionsFragment()
                4 -> ProfileFragment()
                else -> throw IllegalStateException("Invalid position $position")
            }
        }
    }


    /** ✅ Expose fragment by position */
    fun getFragment(position: Int): Fragment? = fragments[position]
}