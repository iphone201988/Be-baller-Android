package com.beballer.beballer.ui.player.dash_board.find.courts

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import com.beballer.beballer.BR
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.base.SimpleRecyclerViewAdapter
import com.beballer.beballer.data.api.Constants
import com.beballer.beballer.data.model.GetCourtApiResponse
import com.beballer.beballer.data.model.GetCourtData
import com.beballer.beballer.databinding.FragmentCourtsBinding
import com.beballer.beballer.databinding.RecyclerCourtItemBinding
import com.beballer.beballer.ui.player.dash_board.find.game.GameActivity
import com.beballer.beballer.ui.player.dash_board.profile.user.UserProfileActivity
import com.beballer.beballer.utils.BindingUtils
import com.beballer.beballer.utils.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CourtsFragment : BaseFragment<FragmentCourtsBinding>(), OnMapReadyCallback {
    private val viewModel: CourtsFragmentVM by viewModels()
    private lateinit var courtsAdapter: SimpleRecyclerViewAdapter<GetCourtData, RecyclerCourtItemBinding>
    private var fullList = ArrayList<GetCourtData>()
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentPage = 1
    private var isLastPage = false
    override fun getLayoutResource(): Int {
        return R.layout.fragment_courts
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // status color change
        BindingUtils.statusBarTextColor(requireActivity(), true)
        // click
        initOnClick()
        // adapter
        initFindAdapter()
        // api call
        val put = HashMap<String, Any>()
        put["page"] = currentPage
        put["limit"] = 20
        viewModel.getCourt(Constants.GET_COURTS, put)
        // observer
        initObserver()

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Get map fragment
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

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
                        "getCourt" -> {
                            try {
                                val myDataModel: GetCourtApiResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    if (myDataModel.courts?.isNotEmpty() == true) {
                                        if (currentPage == 1) {
                                            myDataModel.courts.let {
                                                fullList = it as ArrayList<GetCourtData>
                                                courtsAdapter.setList(fullList)
                                            }
                                        } else {
                                            courtsAdapter.addToList(myDataModel.courts)
                                        }
                                        isLastPage =
                                            currentPage == myDataModel.pagination?.totalPages
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

    /**
     *  handle click
     */
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                // iv notifications
                R.id.ivNotification -> {
                    val intent = Intent(requireContext(), UserProfileActivity::class.java)
                    intent.putExtra("userType", "notification")
                    startActivity(intent)
                    requireActivity().overridePendingTransition(
                        R.anim.slide_in_right, R.anim.slide_out_left
                    )
                }
                // back button click
                R.id.cancelImage -> {
                    requireActivity().finish()
                }
                // add court button click
                R.id.cardView -> {
                    val intent = Intent(requireContext(), GameActivity::class.java)
                    intent.putExtra("pathType", "addCourt")
                    startActivity(intent)
                    requireActivity().overridePendingTransition(
                        R.anim.slide_in_right, R.anim.slide_out_left
                    )
                }
            }
        }


    }


    /**
     * handle adapter
     */
    private fun initFindAdapter() {
        courtsAdapter =
            SimpleRecyclerViewAdapter(R.layout.recycler_court_item, BR.bean) { v, m, pos ->
                when (v.id) {
                    R.id.clMain -> {
                        val intent = Intent(requireContext(), UserProfileActivity::class.java)
                        intent.putExtra("userType", "courtDetailsFragment")
                        sharedPrefManager.saveCourtDetailsId(m.id.toString())
                        startActivity(intent)
                        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    }
                }
            }
        binding.rvCourts.adapter = courtsAdapter
        setupSearch()
    }


    /*** add search ***/
    private fun setupSearch() {
        val searchView = binding.courtsSearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filtered = if (!newText.isNullOrBlank()) {
                    fullList.filter {
                        it.name?.startsWith(newText, ignoreCase = true) == true
                    }
                } else {
                    fullList
                }

                courtsAdapter.list = filtered
                courtsAdapter.notifyDataSetChanged()
                return true
            }
        })
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        enableMyLocation()
    }

    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
            return
        }

        mGoogleMap.isMyLocationEnabled = true

        // Get last known location
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
            }
        }
    }

    // Handle permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation()
        }
    }
}