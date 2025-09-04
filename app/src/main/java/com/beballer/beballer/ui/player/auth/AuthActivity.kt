package com.beballer.beballer.ui.player.auth

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseActivity
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.base.location.LocationHandler
import com.beballer.beballer.base.location.LocationResultListener
import com.beballer.beballer.base.permission.PermissionHandler
import com.beballer.beballer.base.permission.Permissions
import com.beballer.beballer.utils.BindingUtils
import com.beballer.beballer.databinding.ActivityAuthBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthActivity : BaseActivity<ActivityAuthBinding>(), LocationResultListener {
    private val viewModel: AuthCommonVM by viewModels()
    private var locationHandler: LocationHandler? = null
    private var PERMISSION_REQUEST_CODE = 16
    private val navController: NavController by lazy {
        (supportFragmentManager.findFragmentById(R.id.authNavigationHost) as NavHostFragment).navController
    }

    override fun getLayoutResource(): Int {
        return R.layout.activity_auth
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        // check permission
        checkLocation()

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                navController.graph =
                    navController.navInflater.inflate(R.navigation.auth_navigation).apply {
                        setStartDestination(R.id.fragmentOnBoarding)
                    }
            }
        }
    }

    /***** check location Function ***/
    private fun checkLocation() {
        Permissions.check(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION,
            0,
            object : PermissionHandler() {
                override fun onGranted() {
                    createLocationHandler()
                }

                override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>?) {
                    super.onDenied(context, deniedPermissions)
                    if (Build.VERSION.SDK_INT > 32) {
                        if (!shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                            getNotificationPermission()
                        }
                    }
                }
            })
    }

    /**** location handler ***/
    private fun createLocationHandler() {
        locationHandler = LocationHandler(this, this)
        locationHandler?.getUserLocation()
        locationHandler?.removeLocationUpdates()
    }

    /**** get location ****/
    override fun getLocation(location: Location) {
        BindingUtils.lat = location.latitude
        BindingUtils.long = location.longitude
        if (Build.VERSION.SDK_INT > 32) {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                getNotificationPermission()
            }
        }
    }

    private fun getNotificationPermission() {
        try {
            if (Build.VERSION.SDK_INT > 32) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), PERMISSION_REQUEST_CODE
                )
            }
        } catch (e: Exception) {
            Log.e("fsds", "setUpObserver: $e")
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                } else {
                    // Permission denied
                    Toast.makeText(this, "Notification Permission Denied", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

}