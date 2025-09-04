package com.beballer.beballer.ui.player.dash_board.profile.edit_profile

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.data.api.Constants
import com.beballer.beballer.data.model.UserProfile
import com.beballer.beballer.databinding.DeleteAccountDialogItemBinding
import com.beballer.beballer.databinding.FragmentEditProfileBinding
import com.beballer.beballer.databinding.GenderBottomSheetItemBinding
import com.beballer.beballer.ui.player.dash_board.profile.position.PositionFragment
import com.beballer.beballer.ui.player.dash_board.profile.team.TeamFragment
import com.beballer.beballer.ui.player.dash_board.profile.user.UserProfileActivity
import com.beballer.beballer.utils.BaseCustomDialog
import com.beballer.beballer.utils.BindingUtils
import com.beballer.beballer.utils.CommonBottomSheet
import com.beballer.beballer.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone


@AndroidEntryPoint
class EditProfileFragment : BaseFragment<FragmentEditProfileBinding>() {
    private val viewModel: EditProfileFragmentVM by viewModels()
    private lateinit var genderBottomSheet: CommonBottomSheet<GenderBottomSheetItemBinding>
    private lateinit var deleteDialogItem: BaseCustomDialog<DeleteAccountDialogItemBinding>
    private var birthDate = ""

    companion object {
        var positionName = ""
        var positionId = ""
        var favoriteProTeam = ""
        var favoriteProTeamName = ""
    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_edit_profile
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(view: View) {
        // set country code
        binding.etCountryName.setText(binding.countryCode.selectedCountryName)
        binding.countryCode.setOnCountryChangeListener {
            val selectedName = binding.countryCode.selectedCountryName
            binding.etCountryName.setText(selectedName)
        }
        // click
        initOnClick()
        // observer
        initObserver()
        // api call
        val data = HashMap<String, Any>()
        viewModel.getProfileApi(Constants.USER_PROFILE, data)
    }

    override fun onResume() {
        super.onResume()

        binding.etClub.setText(favoriteProTeamName)
        binding.etPosition.setText(positionName)
    }


    /** delete dialog item **/
    private fun deleteDialogItem() {
        deleteDialogItem = BaseCustomDialog<DeleteAccountDialogItemBinding>(
            requireContext(), R.layout.delete_account_dialog_item
        ) {
            when (it?.id) {
                R.id.btnCancel -> {
                    deleteDialogItem.dismiss()
                }

                R.id.btnConfirm -> {
                    deleteDialogItem.dismiss()
                }
            }

        }
        deleteDialogItem.create()
        deleteDialogItem.show()
    }


    /** handle click **/
    var type = false

    @SuppressLint("DefaultLocale")
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                // tvBack
                R.id.tvBack -> {
                    requireActivity().finish()
                }

                R.id.tvChangePhoto -> {
                    val intent = Intent(requireContext(), UserProfileActivity::class.java)
                    intent.putExtra("userType", "editImage")
                    startActivity(intent)
                    requireActivity().overridePendingTransition(
                        R.anim.slide_in_right, R.anim.slide_out_left
                    )
                }

                R.id.tvSave -> {
                    val firstName = binding.etName.text.toString().trim()
                    val lastName = binding.etLastName.text.toString().trim()
                    val userName = binding.etUserName.text.toString().trim()
                    val cityName = binding.etCity.text.toString().trim()
                    val userHeight = binding.etHeight.text.toString().trim()
                    val userGender = binding.etGender.text.toString().trim().lowercase()
                    val userRecruiters = binding.etRecruiters.text.toString().trim()
                    val userHeightCount = userHeight.replace("cm", "").trim()
                    val data = HashMap<String, RequestBody>()
                    data["lastName"] = lastName.toRequestBody("text/plain".toMediaTypeOrNull())
                    data["firstName"] = firstName.toRequestBody("text/plain".toMediaTypeOrNull())
                    data["countryCode"] =
                        binding.countryCode.selectedCountryCode.toRequestBody("text/plain".toMediaTypeOrNull())
                    data["country"] =
                        binding.countryCode.selectedCountryName.toRequestBody("text/plain".toMediaTypeOrNull())
                    data["username"] = userName.toRequestBody("text/plain".toMediaTypeOrNull())
                    data["height"] = userHeightCount.toRequestBody("text/plain".toMediaTypeOrNull())
                    data["birthDate"] = birthDate.toRequestBody("text/plain".toMediaTypeOrNull())
                    data["gender"] = userGender.toRequestBody("text/plain".toMediaTypeOrNull())
                    data["city"] = cityName.toRequestBody("text/plain".toMediaTypeOrNull())
                    data["position"] = positionName.toRequestBody("text/plain".toMediaTypeOrNull())
                    data["playPositionId"] =
                        positionId.toRequestBody("text/plain".toMediaTypeOrNull())
                    data["favoriteProTeam"] =
                        favoriteProTeam.toRequestBody("text/plain".toMediaTypeOrNull())
                    data["recutersViewed"] =
                        userRecruiters.toRequestBody("text/plain".toMediaTypeOrNull())

                    viewModel.updateProfileApi(Constants.CREATE_PROFILE, data, null)
                }
                // pick height
                R.id.etHeight -> {
                    showGenderPicker(2)
                }
                // pick gender
                R.id.etGender -> {
                    showGenderPicker(1)
                }

                R.id.btnDeleteAccount -> {
                    deleteDialogItem()
                }
                // pick position
                R.id.etPosition -> {
                    PositionFragment.positionType = 2
                    val intent = Intent(requireContext(), UserProfileActivity::class.java)
                    intent.putExtra("userType", "Position")
                    startActivity(intent)
                    requireActivity().overridePendingTransition(
                        R.anim.slide_in_right, R.anim.slide_out_left
                    )
                }
                // pick club
                R.id.etClub -> {
                    TeamFragment.teamType = 3
                    val intent = Intent(requireContext(), UserProfileActivity::class.java)
                    intent.putExtra("userType", "team")
                    startActivity(intent)
                    requireActivity().overridePendingTransition(
                        R.anim.slide_in_right, R.anim.slide_out_left
                    )
                }


                // pick birth
                R.id.etBirth -> {
                    val calendar = Calendar.getInstance()
                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH)
                    val day = calendar.get(Calendar.DAY_OF_MONTH)

                    val maxDateCalendar = Calendar.getInstance()
                    maxDateCalendar.add(Calendar.YEAR, -12)

                    val datePickerDialog = DatePickerDialog(
                        requireActivity(), { _, selectedYear, selectedMonth, selectedDay ->
                            val selectedCalendar = Calendar.getInstance()
                            selectedCalendar.set(selectedYear, selectedMonth, selectedDay)

                            val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                            val formattedDate = sdf.format(selectedCalendar.time)
                            binding.etBirth.setText(formattedDate)


                            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                            formatter.timeZone = TimeZone.getTimeZone("UTC")

                            birthDate = formatter.format(selectedCalendar.time)


                        }, year, month, day
                    )

                    datePickerDialog.datePicker.maxDate = maxDateCalendar.timeInMillis

                    datePickerDialog.show()
                }
            }
        }
    }


    private val genderOptions = listOf("Male", "Female", "Other")
    private val heightOptions = (100..300).map { "$it cm" }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showGenderPicker(type: Int) {
        genderBottomSheet = CommonBottomSheet(requireContext(), R.layout.gender_bottom_sheet_item) {
            when (it?.id) {
                R.id.btnOk -> {
                    val selectedValue = if (type == 1) {
                        genderOptions[genderBottomSheet.binding?.genderPicker?.value ?: 0]
                    } else {
                        heightOptions[genderBottomSheet.binding?.genderPicker?.value ?: 0]
                    }
                    if (type == 1) {
                        binding.etGender.setText(selectedValue)
                    } else {
                        binding.etHeight.setText(selectedValue)
                    }

                    genderBottomSheet.dismiss()
                }
            }
        }

        val picker = genderBottomSheet.binding?.genderPicker
        picker?.minValue = 0
        picker?.wrapSelectorWheel = false
        picker?.textSize = 30f
        picker?.textColor = ContextCompat.getColor(requireContext(), R.color.black_000000)
        if (type == 1) {
            picker?.maxValue = genderOptions.size - 1
            picker?.displayedValues = genderOptions.toTypedArray()
        } else {
            picker?.maxValue = heightOptions.size - 1
            picker?.displayedValues = heightOptions.toTypedArray()
        }

        genderBottomSheet.behavior.isDraggable = true
        genderBottomSheet.setCancelable(true)
        genderBottomSheet.show()
    }


    /**  api response observer  **/
    private fun initObserver() {
        viewModel.commonObserver.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "getProfileApi" -> {
                            try {
                                val myDataModel: UserProfile? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    if (myDataModel.data != null) {
                                        sharedPrefManager.setProfileData(myDataModel)
                                        binding.bean = myDataModel.data.user
                                        val heightCm = myDataModel.data.user?.height ?: 0
                                        val date = myDataModel.data.user?.birthDate ?: ""
                                        binding.etHeight.setText("$heightCm cm")
                                        val formattedDate = BindingUtils.formatBirthDate(date)
                                        binding.etBirth.setText(formattedDate)
                                    }
                                }

                            } catch (e: Exception) {
                                Log.e("error", "getProfileApi: $e")
                            } finally {
                                binding.clEdit.visibility = View.VISIBLE
                                binding.btnDeleteAccount.visibility = View.VISIBLE
                                hideLoading()
                            }
                        }

                        "updateProfileApi" -> {
                            try {
                                val myDataModel: UserProfile? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    showSuccessToast(myDataModel.message.toString())
                                }

                            } catch (e: Exception) {
                                Log.e("error", "updateProfileApi: $e")
                            } finally {
                                hideLoading()
                            }
                        }
                    }
                }

                Status.ERROR -> {
                    hideLoading()
                    binding.clEdit.visibility = View.GONE
                    binding.btnDeleteAccount.visibility = View.GONE
                    showErrorToast(it.message.toString())
                }

                else -> {
                }
            }
        }
    }

}