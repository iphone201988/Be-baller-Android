package com.beballer.beballer.ui.player.dash_board.profile.user

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseActivity
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.databinding.ActivityUserProfileBinding
import com.beballer.beballer.ui.player.dash_board.profile.ProfileFragmentVM
import com.beballer.beballer.utils.BindingUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserProfileActivity : BaseActivity<ActivityUserProfileBinding>() {
    private val viewModel: ProfileFragmentVM by viewModels()
    private val navController: NavController by lazy {
        (supportFragmentManager.findFragmentById(R.id.userProfileNavigationHost) as NavHostFragment).navController
    }

    override fun getLayoutResource(): Int {
        return R.layout.activity_user_profile
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }


    override fun onCreateView() {
        // Status bar setup
        BindingUtils.statusBarStyle(this)
        BindingUtils.statusBarTextColor(this, false)
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                navController.graph =
                    navController.navInflater.inflate(R.navigation.user_profile).apply {
                        val intentFragment = intent.getStringExtra("userType")
                        when (intentFragment) {
                            "EditProfile" -> {
                                setStartDestination(R.id.editFragment)
                            }

                            "ShareProfile" -> {
                                setStartDestination(R.id.shareProfileFragment)
                            }

                            "Position" -> {
                                setStartDestination(R.id.positionFragment)
                            }

                            "Friend" -> {
                                setStartDestination(R.id.friendFragment)
                            }

                            "team" -> {
                                setStartDestination(R.id.teamFragment)
                            }

                            "share" -> {
                                setStartDestination(R.id.shareProfileFragment)
                            }

                            "settings" -> {
                                setStartDestination(R.id.settingsFragment)
                            }

                            "suggestion" -> {
                                setStartDestination(R.id.suggestionFragment)
                            }

                            "policy" -> {
                                setStartDestination(R.id.policyFragment)
                            }
                            "editImage" -> {
                                setStartDestination(R.id.editImageFragment)
                            }
                            "courtFragment"->{
                                setStartDestination(R.id.courtFragment)
                            }
                            "courtDetailsFragment"->{
                                setStartDestination(R.id.courtsDetailsFragment)
                            }

                            "notification"->{
                                setStartDestination(R.id.notificationFragment)
                            }
                            "gameFragment"->{
                                setStartDestination(R.id.myGameFragment)
                            }
                            "ticketFragment"->{
                                setStartDestination(R.id.ticketFragment)
                            }
                            "tournamentFragment"->{
                                setStartDestination(R.id.tournamentsFragment)
                            }
                            "campsFragment"->{
                                setStartDestination(R.id.campsFragment)
                            }


                        }
                    }
            }
        }
    }
}