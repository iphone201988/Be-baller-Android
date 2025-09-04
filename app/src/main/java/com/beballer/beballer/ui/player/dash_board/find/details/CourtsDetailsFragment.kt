package com.beballer.beballer.ui.player.dash_board.find.details

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.location.Geocoder
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.data.api.Constants
import com.beballer.beballer.data.model.GetCourtByIdResponse
import com.beballer.beballer.databinding.CreateGameDialogBinding
import com.beballer.beballer.databinding.FragmentCourtsDetailsBinding
import com.beballer.beballer.ui.player.dash_board.find.game.GameActivity
import com.beballer.beballer.ui.player.dash_board.find.player_profile.PlayerProfileActivity
import com.beballer.beballer.utils.BaseCustomDialog
import com.beballer.beballer.utils.BindingUtils
import com.beballer.beballer.utils.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.zhpan.indicator.enums.IndicatorSlideMode
import com.zhpan.indicator.enums.IndicatorStyle
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale


@AndroidEntryPoint
class CourtsDetailsFragment : BaseFragment<FragmentCourtsDetailsBinding>(), OnMapReadyCallback {
    private val viewModel: CourtsDetailsFragmentVM by viewModels()
    private val translationYaxis = -100F
    private var isFabMenuVisible = false
    private val interpolator = OvershootInterpolator()
    private lateinit var createGameDialogItem: BaseCustomDialog<CreateGameDialogBinding>

    private var mMap: GoogleMap? = null
    private var customMarker: Marker? = null

    override fun getLayoutResource(): Int {
        return R.layout.fragment_courts_details
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnClick()
        // fab button click
        initFabMenu()
        val courtDetails = sharedPrefManager.getCourtDetailsId()
        courtDetails.let {
            // api call
            val put = HashMap<String, Any>()
            viewModel.getCourtById(Constants.GET_COURTS_BY_ID + "$it", put)
        }
        // observer
        initObserver()
        // Get map fragment
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

    }


    /** handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.cancelImage -> {
                    requireActivity().finish()
                }

                R.id.tvCourtPicture -> {
                    val intent = Intent(requireContext(), PlayerProfileActivity::class.java)
                    startActivity(intent)
                    requireActivity().overridePendingTransition(
                        com.airbnb.lottie.R.anim.abc_slide_in_bottom,
                        com.airbnb.lottie.R.anim.abc_fade_out
                    )
                }

                R.id.tvCourtKing -> {
                    createGameDialogItem()
                }
            }
        }
    }


    /**** create game dialog item ****/
    private fun createGameDialogItem() {
        createGameDialogItem = BaseCustomDialog<CreateGameDialogBinding>(
            requireContext(), R.layout.create_game_dialog
        ) {
            when (it?.id) {
                R.id.btnCreateGame -> {
                    val intent = Intent(requireContext(), GameActivity::class.java)
                    intent.putExtra("pathType", "first")
                    startActivity(intent)
                    createGameDialogItem.dismiss()
                    requireActivity().overridePendingTransition(
                        R.anim.slide_in_right, R.anim.slide_out_left
                    )
                }
            }

        }
        createGameDialogItem.create()
        createGameDialogItem.show()
    }


    /*** view pager handel **/
    private fun initViewPager(imageList: List<String> = emptyList()) {
        val uriList = ArrayList<String>()
        for (i in imageList) {
            uriList.add(i)
        }

        Log.d("dgfdfd", "initViewPager: $uriList")

        val imagesPagerAdapter = ImagesPagerAdapter(requireContext(), uriList)
        binding.viewPager.adapter = imagesPagerAdapter
        binding.dotsIndicator.apply {
            setSliderColor(
                ContextCompat.getColor(requireContext(), R.color.text_light),
                ContextCompat.getColor(requireContext(), R.color.white)
            )
            setSliderWidth(
                resources.getDimension(com.intuit.sdp.R.dimen._6sdp),
                resources.getDimension(com.intuit.sdp.R.dimen._6sdp)
            )
            setSliderHeight(resources.getDimension(com.intuit.sdp.R.dimen._6sdp))
            setSlideMode(IndicatorSlideMode.NORMAL)
            setIndicatorStyle(IndicatorStyle.CIRCLE)

            binding.dotsIndicator.setupWithViewPager(binding.viewPager)
            setPageSize(imagesPagerAdapter.itemCount)
            notifyDataChanged()

            binding.noCourtPicture.isVisible = imagesPagerAdapter.itemCount == 0
        }
    }


    /*** fab button click handel  **/
    private fun initFabMenu() {
        binding.courtMenusLayout.alpha = 0F
        binding.courtMenusLayout.translationY = translationYaxis
        binding.courtMenusLayout.isVisible = false
        binding.courtMenuFab.setOnClickListener {
            when (isFabMenuVisible) {
                true -> {
                    binding.courtMenuFab.animate().rotation(0F).setInterpolator(interpolator)
                        .setDuration(150).start()
                    binding.courtMenusLayout.animate().translationY(translationYaxis).alpha(0F)
                        .setInterpolator(interpolator).setDuration(300)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                super.onAnimationEnd(animation)
                                binding.courtMenusLayout.isVisible = false

                            }
                        }).start()
                }

                false -> {
                    binding.courtMenusLayout.isVisible = true

                    binding.courtMenuFab.animate().rotation(-90F).setInterpolator(interpolator)
                        .setDuration(150).start()

                    binding.courtMenusLayout.animate().translationY(0F).setListener(null).alpha(1F)
                        .setInterpolator(interpolator).setDuration(300).start()
                }
            }
            isFabMenuVisible = !isFabMenuVisible
        }
    }


    /**
     * handle api response
     */
    private fun initObserver() {
        viewModel.commonObserver.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "getCourtById" -> {
                            try {
                                val myDataModel: GetCourtByIdResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    if (myDataModel.data != null) {
                                        if (myDataModel.data.court != null) {
                                            binding.bean = myDataModel.data.court

                                            if (myDataModel.data.court.lat != null && myDataModel.data.court.long != null) {
                                                val lat1 = myDataModel.data.court.lat
                                                val lon1 = myDataModel.data.court.long
                                                val lat2 = BindingUtils.lat
                                                val lon2 = BindingUtils.long
                                                val distance = BindingUtils.formattedDistance(
                                                    lat1, lon1, lat2, lon2
                                                )
                                                binding.tvCourtDistance.text = distance
                                            }

                                            // view pager
                                            initViewPager(myDataModel.data.court.photos as List<String>)

                                        }
                                    } else {
                                        showErrorToast(myDataModel.message.toString())
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            } finally {
                                hideLoading()
                            }
                        }


                    }
                }

                Status.ERROR -> {
                    hideLoading()
                    showErrorToast(it.message.toString())
                }

                else -> {
                }
            }
        }


    }


    /*** map ready ***/

    private var selectedMarker: Marker? = null

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val location = LatLng(30.7024577, 76.822114)

        // Add default marker
        customMarker = mMap?.addMarker(
            MarkerOptions().position(location).title("Selected Location").icon(
                    bitmapDescriptorFromVector(
                        requireContext(),
                        R.drawable.findcourticon,
                        40,
                        40
                    )
                )
        )

        // Move camera
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))

        // ✅ Marker click listener
        mMap?.setOnMarkerClickListener { marker ->
            val address = getAddressFromLatLng(marker.position)
            marker.title = address ?: "Unknown Location"

            if (selectedMarker == marker) {
                // Same marker tapped again → toggle off
                marker.hideInfoWindow()
                marker.setIcon(
                    bitmapDescriptorFromVector(
                        requireContext(),
                        R.drawable.findcourticon,
                        40,
                        40
                    )
                )
                selectedMarker = null
            } else {
                // Reset old marker
                selectedMarker?.setIcon(
                    bitmapDescriptorFromVector(
                        requireContext(),
                        R.drawable.findcourticon,
                        40,
                        40
                    )
                )
                selectedMarker?.hideInfoWindow()

                // Highlight new marker
                marker.setIcon(
                    bitmapDescriptorFromVector(
                        requireContext(),
                        R.drawable.pinfilledcourt,
                        40,
                        40
                    )
                )
                marker.showInfoWindow()
                selectedMarker = marker
            }
            true
        }

        // ✅ Map click listener
        mMap?.setOnMapClickListener {
            selectedMarker?.let { marker ->
                marker.hideInfoWindow()
                marker.setIcon(
                    bitmapDescriptorFromVector(
                        requireContext(),
                        R.drawable.findcourticon,
                        40,
                        40
                    )
                )
            }
            selectedMarker = null
        }
    }


    /*** bitmap handel **/
    private fun bitmapDescriptorFromVector(
        context: Context, vectorResId: Int, width: Int, height: Int
    ): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)!!
        vectorDrawable.setBounds(0, 0, width, height)

        val bitmap = createBitmap(width, height)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    /** show title **/
    private fun getAddressFromLatLng(latLng: LatLng): String? {
        return try {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                addresses[0].getAddressLine(0)
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}