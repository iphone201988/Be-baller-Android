package com.beballer.beballer.ui.organizers.dash_board.tournament

import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseActivity
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.utils.BindingUtils
import com.beballer.beballer.databinding.ActivityTournamentsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TournamentsActivity : BaseActivity<ActivityTournamentsBinding>() {

    private val viewModel: TournamentsActivityVM by viewModels()

    private var organizersPathType = ""
    private val navController: NavController by lazy {
        (supportFragmentManager.findFragmentById(R.id.organizersTournamentNavigationHost) as NavHostFragment).navController
    }

    override fun getLayoutResource(): Int {
        return R.layout.activity_tournaments
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        val intent = intent.getStringExtra("organizersPathType")
        if (intent?.isNotEmpty() == true) {
            organizersPathType = intent
        }
        // set status bar color
        BindingUtils.statusBarStyle(this@TournamentsActivity)
        BindingUtils.statusBarTextColor(this@TournamentsActivity, true)
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                navController.graph =
                    navController.navInflater.inflate(R.navigation.organizers_tournaments).apply {
                        if (organizersPathType.contains("organizersDetails")) {
                            setStartDestination(R.id.organizersFindDetailsFragment)
                        } else if (organizersPathType.contains("tournamentCreate")) {
                            setStartDestination(R.id.organizersEventTypeFragment)
                        } else if (organizersPathType.contains("CampsCreate")) {
                            setStartDestination(R.id.createCampsFirstFragment)
                        }

                    }
            }
        }
    }
}