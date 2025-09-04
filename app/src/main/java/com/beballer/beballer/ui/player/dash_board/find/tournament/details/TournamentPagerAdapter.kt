package com.beballer.beballer.ui.player.dash_board.find.tournament.details

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.beballer.beballer.ui.player.dash_board.find.player_profile.posts.ProfilePostsFragment
import com.beballer.beballer.ui.player.dash_board.find.tournament.details.organizers.OrganizersFragment
import com.beballer.beballer.ui.player.dash_board.find.tournament.details.player.PlayerFragment
import com.beballer.beballer.ui.player.dash_board.find.tournament.details.referees.RefereesFragment
import com.beballer.beballer.ui.player.dash_board.find.tournament.details.spectators.SpectatorsFragment

class TournamentPagerAdapter(
    fragmentManager: FragmentManager, lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PlayerFragment()
            1 -> OrganizersFragment()
            2 -> RefereesFragment()
            3 -> SpectatorsFragment()
            else -> ProfilePostsFragment()
        }
    }
}