package com.beballer.beballer.ui.player.dash_board

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.viewpager2.widget.ViewPager2
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseActivity
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.base.location.LocationHandler
import com.beballer.beballer.base.location.LocationResultListener
import com.beballer.beballer.base.permission.PermissionHandler
import com.beballer.beballer.base.permission.Permissions
import com.beballer.beballer.utils.BaseCustomDialog
import com.beballer.beballer.utils.BindingUtils
import com.beballer.beballer.utils.CommonBottomSheet
import com.beballer.beballer.utils.Status
import com.beballer.beballer.utils.event.SingleRequestEvent
import com.beballer.beballer.data.api.Constants
import com.beballer.beballer.databinding.ActivityDashboardBinding
import com.beballer.beballer.databinding.AlertDialodItemBinding
import com.beballer.beballer.databinding.GameBottomSheetLayoutBinding
import com.beballer.beballer.ui.interfacess.VideoHandler
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DashboardActivity : BaseActivity<ActivityDashboardBinding>(), LocationResultListener {
    private val viewModel: DashboardActivityVM by viewModels()
    private lateinit var gameBottomSheet: CommonBottomSheet<GameBottomSheetLayoutBinding>
    private lateinit var alertDialogItem: BaseCustomDialog<AlertDialodItemBinding>
    private lateinit var adapter: MyFragmentPagerAdapter
    private var PERMISSION_REQUEST_CODE = 16
    private var locationHandler: LocationHandler? = null

    companion object {
        var userImageFragment = SingleRequestEvent<String>()
    }

    override fun getLayoutResource(): Int = R.layout.activity_dashboard
    override fun getViewModel(): BaseViewModel = viewModel


    private var lastClickTime = 0L
    private val DEBOUNCE_TIME = 300L

    override fun onCreateView() {
        // Status bar setup
        BindingUtils.statusBarStyle(this)
        BindingUtils.statusBarTextColor(this, true)

        // Setup ViewPager2 with adapter
        adapter = MyFragmentPagerAdapter(this)
        binding.viewPager.apply {
            adapter = this@DashboardActivity.adapter
            isUserInputEnabled = false
            offscreenPageLimit = 1
        }

        // Safe initial tab set
        binding.viewPager.post {
            binding.viewPager.setCurrentItem(0, false)
        }
        // bottom navigation bar
        setupBottomNavigation()
        // observer
        initObserver()
        if (sharedPrefManager.getLoginData()?.data?.user?.profilePicture?.isNotEmpty() == true) {
            setProfileImage(
                binding.bottomNavigation,
                Constants.IMAGE_URL + sharedPrefManager.getLoginData()?.data?.user?.profilePicture
            )
        } else if (sharedPrefManager.getProfileData()?.data?.user?.profilePicture?.isNotEmpty() == true) {
            setProfileImage(
                binding.bottomNavigation,
                Constants.IMAGE_URL + sharedPrefManager.getProfileData()?.data?.user?.profilePicture
            )
        }

        // checkLocation
        checkLocation()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            private var previousPosition = -1

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                if (previousPosition != -1) {
                    val previousFragment = adapter.getFragment(previousPosition)
                    (previousFragment as? VideoHandler)?.pauseVideoIfPlaying()
                }

                previousPosition = position
            }
        })



    }

    /***** check location Function ***/
    private fun checkLocation() {
        Permissions.check(
            this, Manifest.permission.ACCESS_FINE_LOCATION, 0, object : PermissionHandler() {
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

    /*** change image profile icon   ***/
    private fun setProfileImage(bottomNav: BottomNavigationView, imageUrl: String) {
        if (imageUrl.isEmpty()) {
            bottomNav.menu.findItem(R.id.profileFragment).setIcon(R.drawable.profile)
            return
        }
        Glide.with(this).asBitmap().load(imageUrl).circleCrop().error(R.drawable.profile)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val drawable = resource.toDrawable(resources)
                    bottomNav.menu.findItem(R.id.profileFragment).icon = drawable
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    bottomNav.menu.findItem(R.id.profileFragment).setIcon(R.drawable.profile)
                }
            })
    }


    /** Bottom Navigation Setup */
    private fun setupBottomNavigation() {
        binding.bottomNavigation.itemIconTintList = null
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime < DEBOUNCE_TIME) {
                return@setOnItemSelectedListener true // Block rapid clicks
            }
            lastClickTime = currentTime

            binding.viewPager.post {
                when (item.itemId) {
                    R.id.socialFragment -> switchTabSafely(0)
                    R.id.findFragment -> switchTabSafely(1)
                    R.id.gameFragment -> switchTabSafely(2)
                    R.id.progressionFragment -> switchTabSafely(3)
                    R.id.profileFragment -> switchTabSafely(4)
                }
            }
            true
        }
    }

    /*** Safely switches tabs with error handling **/
    private fun switchTabSafely(position: Int) {
        try {
            if (position == 2) {
                gameBottomSheet()
            } else {
                binding.viewPager.setCurrentItem(position, false)
            }

        } catch (e: IllegalStateException) {
            binding.viewPager.postDelayed({
                switchTabSafely(position)
            }, 100)
        }
    }


    private fun gameBottomSheet() {
        gameBottomSheet =
            CommonBottomSheet(this@DashboardActivity, R.layout.game_bottom_sheet_layout) {

                when (it?.id) {
                    R.id.createTournamentCard -> {
                        gameBottomSheet.dismiss()

                    }

                    R.id.requestWorkoutCard -> {
                        gameBottomSheet.dismiss()
                        alertDialogItem()
                    }

                    R.id.addCourtCard -> {

                    }

                    R.id.createGameCard -> {
                        gameBottomSheet.dismiss()

                    }
                }
            }

        gameBottomSheet.apply {
            behavior.isDraggable = true
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            create()
            window?.attributes?.windowAnimations = R.style.BottomSheetAnimation
            show()
        }

    }


    /**** alert dialog item ****/
    private fun alertDialogItem() {
        alertDialogItem = BaseCustomDialog<AlertDialodItemBinding>(
            this@DashboardActivity, R.layout.alert_dialod_item
        ) {
            when (it?.id) {
                R.id.tvBtn -> {
                    alertDialogItem.dismiss()
                }
            }

        }
        alertDialogItem.create()
        alertDialogItem.show()
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

    /** handle api response **/
    private fun initObserver() {
        userImageFragment.observe(this) {
            when (it?.status) {
                Status.SUCCESS -> {
                    it.data?.let { it1 ->
                        setProfileImage(
                            binding.bottomNavigation, Constants.IMAGE_URL + it1
                        )
                    }
                }

                else -> {

                }
            }
        }

    }
}