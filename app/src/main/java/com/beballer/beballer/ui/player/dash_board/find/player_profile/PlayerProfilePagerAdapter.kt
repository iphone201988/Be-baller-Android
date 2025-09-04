package com.beballer.beballer.ui.player.dash_board.find.player_profile

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.beballer.beballer.ui.player.dash_board.find.player_profile.class_mete.ProfileClassmateFragment
import com.beballer.beballer.ui.player.dash_board.find.player_profile.inventoy.InventoryFragment
import com.beballer.beballer.ui.player.dash_board.find.player_profile.posts.ProfilePostsFragment
import com.beballer.beballer.ui.player.dash_board.find.player_profile.statistics.StatisticsFragment


/*
class PlayerProfilePagerAdapter(
    fragmentManager: FragmentManager, lifecycle: Lifecycle, private val playerId: String
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
       */
/*     0 -> ProfilePostsFragment.newInstance(playerId)
            1 -> ProfileClassmateFragment.newInstance(playerId)
            2 -> StatisticsFragment.newInstance(playerId)
            3 -> InventoryFragment.newInstance(playerId)
            else -> ProfilePostsFragment.newInstance(playerId) *//*
        }
    }
}*/

class PlayerProfilePagerAdapter(
    fragmentManager: FragmentManager, lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {

            0 -> ProfilePostsFragment()
            1 -> ProfileClassmateFragment()
            2 -> StatisticsFragment()
            3 -> InventoryFragment()
            else -> ProfilePostsFragment()
        }
    }
}