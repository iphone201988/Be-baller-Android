package com.beballer.beballer.ui.organizers.camps_view

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.beballer.beballer.ui.organizers.finals.FinalsFragment
import com.beballer.beballer.ui.organizers.match.MatchFragment
import com.beballer.beballer.ui.organizers.pools.PoolsFragment


class CampsPagerAdapter(
    fragmentManager: FragmentManager, lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PoolsFragment()
            1 -> MatchFragment()
            2 -> FinalsFragment()
            else -> PoolsFragment()
        }
    }
}
