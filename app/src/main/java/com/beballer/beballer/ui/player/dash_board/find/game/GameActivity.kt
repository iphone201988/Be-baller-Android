package com.beballer.beballer.ui.player.dash_board.find.game

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
import com.beballer.beballer.databinding.ActivityGameBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GameActivity : BaseActivity<ActivityGameBinding>() {

    private val viewModel: GameActivityVM by viewModels()
    private var pathType = ""


    private val navController: NavController by lazy {
        (supportFragmentManager.findFragmentById(R.id.gameNavigationHost) as NavHostFragment).navController
    }


    override fun getLayoutResource(): Int {
        return R.layout.activity_game
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        val intent = intent.getStringExtra("pathType")
        if (intent?.isNotEmpty() == true){
            pathType = intent
        }
        // set status bar color
        BindingUtils.statusBarStyle(this@GameActivity)
        BindingUtils.statusBarTextColor(this@GameActivity,true)
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                navController.graph =
                    navController.navInflater.inflate(R.navigation.game_navigation).apply {
                        if (pathType.contains("first")){
                            setStartDestination(R.id.createGameFragment)
                        }else if (pathType.contains("second")){
                            setStartDestination(R.id.gameDetailsFragment)
                        }else if (pathType.contains("third")){
                            setStartDestination(R.id.tournamentDetailsFragment)
                        }
                        else if (pathType.contains("four")){
                            setStartDestination(R.id.campsDetailsFragment)
                        }else if (pathType.contains("addCourt")){
                            setStartDestination(R.id.addPhotoFragment)
                        }

                    }
            }
        }
    }


}