package com.beballer.beballer.ui

import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseActivity
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.databinding.ActivityMySplashBinding
import com.beballer.beballer.ui.player.auth.AuthActivity
import com.beballer.beballer.ui.player.auth.AuthCommonVM
import com.beballer.beballer.ui.player.dash_board.DashboardActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MySplashActivity : BaseActivity<ActivityMySplashBinding>() {
    private val viewModel: AuthCommonVM by viewModels()

    override fun getLayoutResource(): Int {
        return R.layout.activity_my_splash
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
      Handler(Looper.getMainLooper()).postDelayed({
          val loginData = sharedPrefManager.getLoginData()
          if (loginData?.data != null) {
              startActivity(Intent(this, DashboardActivity::class.java))
          } else {
              startActivity(Intent(this, AuthActivity::class.java))
             // startActivity(Intent(this, AuthActivity::class.java))
          }
          overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
          finish()

      },2000)
    }




}