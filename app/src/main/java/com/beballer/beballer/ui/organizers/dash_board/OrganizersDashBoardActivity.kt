package com.beballer.beballer.ui.organizers.dash_board

import androidx.activity.viewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseActivity
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.utils.BindingUtils
import com.beballer.beballer.databinding.ActivityOrganizersDashBoardBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrganizersDashBoardActivity : BaseActivity<ActivityOrganizersDashBoardBinding>() {
    private val viewModel: OrganizersDashBoardActivityVM by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_organizers_dash_board
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        // set status bar color
        BindingUtils.statusBarStyle(this@OrganizersDashBoardActivity)
        BindingUtils.statusBarTextColor(this@OrganizersDashBoardActivity, true)
        // view
        initView()
        // click
        initOnClick()
    }

    /*** all click handel function ***/
    private fun initOnClick() {
        viewModel.onClick.observe(this@OrganizersDashBoardActivity) {
            when (it?.id) {

            }
        }
    }


    /** handle view **/
    private fun initView() {
        // set bottom nav bar
        binding.organizersBottomNavigation.itemIconTintList = null
        val navController = findNavController(R.id.navHostOrganizersFragment)
        binding.organizersBottomNavigation.setupWithNavController(navController)
        binding.organizersBottomNavigation.setOnNavigationItemSelectedListener { item ->
            val currentDestinationId = navController.currentDestination?.id
            when (item.itemId) {
                R.id.organizersFindFragment -> {
                    if (currentDestinationId != R.id.organizersFindFragment) {
                        navController.popBackStack(R.id.organizersFindFragment, true)
                        navController.navigate(R.id.organizersFindFragment)
                    }
                    true
                }

                R.id.socialFragment -> {
                    if (currentDestinationId != R.id.socialFragment) {
                        navController.popBackStack(R.id.socialFragment, true)
                        navController.navigate(R.id.socialFragment)
                    }
                    true
                }

                R.id.organizersGameFragment -> {
                    if (currentDestinationId != R.id.organizersGameFragment) {
                        navController.popBackStack(R.id.organizersGameFragment, true)
                        navController.navigate(R.id.organizersGameFragment)
                    }
                    true
                }

                R.id.profileFragment -> {
                    if (currentDestinationId != R.id.profileFragment) {
                        navController.popBackStack(R.id.profileFragment, true)
                        navController.navigate(R.id.profileFragment)
                    }
                    true
                }


                else -> false
            }
        }
    }

}