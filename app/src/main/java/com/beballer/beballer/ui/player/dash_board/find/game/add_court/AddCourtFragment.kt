package com.beballer.beballer.ui.player.dash_board.find.game.add_court

import android.Manifest
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.beballer.beballer.BR
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.base.SimpleRecyclerViewAdapter
import com.beballer.beballer.data.model.GameModeModel
import com.beballer.beballer.databinding.AccessibilityDialogItemBinding
import com.beballer.beballer.databinding.FragmentAddCourtBinding
import com.beballer.beballer.databinding.RvGameModeItemBinding
import com.beballer.beballer.utils.BaseCustomBottomSheet
import com.beballer.beballer.utils.BindingUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class AddCourtFragment : BaseFragment<FragmentAddCourtBinding>() {
    private val viewModel: AddCourtFragmentVM by viewModels()
    private lateinit var accessibilityDialog: BaseCustomBottomSheet<AccessibilityDialogItemBinding>
    private lateinit var accessibilityAdapter: SimpleRecyclerViewAdapter<GameModeModel, RvGameModeItemBinding>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var autocompleteFragment: AutocompleteSupportFragment
    private var lat: Double? = null
    private var long: Double? = null
    override fun getLayoutResource(): Int {
        return R.layout.fragment_add_court
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnCLick()
        // location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // location search
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), getString(R.string.maps_api_key))
        }

        val placesClient = Places.createClient(requireContext())

        val locationSearchManager = GoogleLocationSearchManager(requireContext(), placesClient)

        locationSearchManager.setupSearch(
            autoCompleteTextView = binding.etCourtAddress,
            onResultSelected = { label, coordinates ->
                binding.etCourtAddress.clearFocus()
                val imm = requireContext().getSystemService(InputMethodManager::class.java)
                imm?.hideSoftInputFromWindow(binding.etCourtAddress.windowToken, 0)
                lat = coordinates.latitude
                long = coordinates.longitude
            }
        )


    }

    /*** click event handel **/
    private fun initOnCLick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.cancelImage -> {
                    requireActivity().finish()
                }

                R.id.btnNext -> {
                    if (validate()) {
                        val bundle = Bundle().apply {
                            putString("courtName", binding.etCourtName.text.toString().trim())
                            putString("courtAddress", binding.etCourtAddress.text.toString().trim())
                            putString("accessibility", binding.etAccessibility.text.toString().trim())
                            putString("hoopsCount", binding.etHoopsCount.text.toString().trim())
                        }
                        BindingUtils.navigateWithSlide(
                            findNavController(),
                            R.id.navigateCourtsAboutFragment,
                            bundle
                        )
                    }

                }

                R.id.etHoopsCount -> {
                    // ope bottom sheet
                    accessibilityBottomSheet(2)
                }

                R.id.etAccessibility -> {
                    // ope bottom sheet
                    accessibilityBottomSheet(1)
                }
                // current location text click
                R.id.tvCurrentLocation -> {
                    if (ContextCompat.checkSelfPermission(
                            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        getCurrentLocation()
                    } else {
                        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
                    }
                }
            }
        }


        // Add TextWatchers
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                checkAllFieldsNotEmpty()
            }
        }

        binding.etCourtName.addTextChangedListener(textWatcher)
        binding.etCourtAddress.addTextChangedListener(textWatcher)
        binding.etAccessibility.addTextChangedListener(textWatcher)
        binding.etHoopsCount.addTextChangedListener(textWatcher)
    }

    /**
     * get current location text click
     */
    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val fullAddress = addresses[0].getAddressLine(0)
                    binding.etCourtAddress.setText(fullAddress)
                }
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation()
        } else {
            showErrorToast("Location permission denied")
        }
    }


    /*** Function to check all fields  ***/
    private fun checkAllFieldsNotEmpty() {
        val isAllNotEmpty =
            binding.etCourtName.text?.isNotEmpty() == true && binding.etCourtAddress.text?.isNotEmpty() == true && binding.etAccessibility.text?.isNotEmpty() == true && binding.etHoopsCount.text?.isNotEmpty() == true

        binding.buttonCheck = isAllNotEmpty
    }

    /** game mode bottom sheet **/
    private fun accessibilityBottomSheet(type: Int) {
        accessibilityDialog =
            BaseCustomBottomSheet(requireContext(), R.layout.accessibility_dialog_item) {
                when (it?.id) {
                    R.id.tvCancel -> {
                        accessibilityDialog.dismiss()
                    }
                }
            }
        accessibilityDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        accessibilityDialog.behavior.isDraggable = true
        accessibilityDialog.create()
        accessibilityDialog.show()
        if (type == 1) {
            accessibilityDialog.binding.tvTitle.text = "Accessbility"
        } else {
            accessibilityDialog.binding.tvTitle.text = "Number of hoops"
        }
        initAccessibilityAdapter(type)
    }


    /** handle game mode adapter **/
    private fun initAccessibilityAdapter(type: Int) {
        accessibilityAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_game_mode_item, BR.bean) { v, m, pos ->
                when (v.id) {
                    R.id.clGame -> {
                        accessibilityDialog.dismiss()
                        if (type == 1) {
                            binding.etAccessibility.setText(m.title)
                        } else {
                            binding.etHoopsCount.setText(m.title)
                        }

                    }
                }
            }
        if (type == 1) {
            accessibilityAdapter.list = getListAccessibility()
        } else {
            accessibilityAdapter.list = getListHoopsCount()
        }

        accessibilityDialog.binding.rvGameModel.adapter = accessibilityAdapter
    }

    // add list game mode
    private fun getListAccessibility(): ArrayList<GameModeModel> {
        return arrayListOf(
            GameModeModel("Available to everyone"),
            GameModeModel("Available to licensees"),
            GameModeModel("Special opening hours"),
        )
    }

    private fun getListHoopsCount(): ArrayList<GameModeModel> {
        return arrayListOf(
            GameModeModel("1"),
            GameModeModel("2"),
            GameModeModel("3"),
            GameModeModel("4"),
            GameModeModel("5+"),

            )
    }

    /*** add validation ***/
    private fun validate(): Boolean {
        val courtName = binding.etCourtName.text.toString().trim()
        val courtAddress = binding.etCourtAddress.text.toString().trim()
        val accessibility = binding.etAccessibility.text.toString().trim()
        val hoopsCount = binding.etHoopsCount.text.toString().trim()
        if (courtName.isEmpty()) {
            showInfoToast("Please enter court name")
            return false
        } else if (courtAddress.isEmpty()) {
            showInfoToast("Please enter current address")
            return false
        } else if (accessibility.isEmpty()) {
            showInfoToast("Please pick accessibility")
            return false
        } else if (hoopsCount.isEmpty()) {
            showInfoToast("Please pick hoops count")
            return false
        }
        return true
    }

}