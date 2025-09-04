package com.beballer.beballer.ui.player.dash_board.profile

import android.app.DatePickerDialog
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.utils.BaseCustomDialog
import com.beballer.beballer.utils.BindingUtils
import com.beballer.beballer.utils.CommonBottomSheet
import com.beballer.beballer.utils.Resource
import com.beballer.beballer.utils.Status
import com.beballer.beballer.data.api.Constants
import com.beballer.beballer.data.model.CommonResponse
import com.beballer.beballer.data.model.UserProfile
import com.beballer.beballer.databinding.FragmentProfileBinding
import com.beballer.beballer.databinding.GenderBottomSheetItemBinding
import com.beballer.beballer.databinding.ProfileWelcomeDialogItemBinding
import com.beballer.beballer.databinding.UnlockBatchDialogItemBinding
import com.beballer.beballer.ui.player.dash_board.DashboardActivity
import com.beballer.beballer.ui.player.dash_board.find.player_profile.PlayerProfilePagerAdapter
import com.beballer.beballer.ui.player.dash_board.profile.followers.FollowersAndFollowingActivity
import com.beballer.beballer.ui.player.dash_board.profile.followers.FollowersAndFollowingActivityVM
import com.beballer.beballer.ui.player.dash_board.profile.position.PositionFragment
import com.beballer.beballer.ui.player.dash_board.profile.team.TeamFragment
import com.beballer.beballer.ui.player.dash_board.profile.user.UserProfileActivity
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {
    private val viewModel: ProfileFragmentVM by viewModels()
    private lateinit var genderBottomSheet: CommonBottomSheet<GenderBottomSheetItemBinding>
    private lateinit var welcomeDialogItem: BaseCustomDialog<ProfileWelcomeDialogItemBinding>
    private lateinit var unLockBatchDialogItem: BaseCustomDialog<UnlockBatchDialogItemBinding>
    private var heightCount = ""
    private var birthDate = ""
    override fun getLayoutResource(): Int {
        return R.layout.fragment_profile
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // check
        binding.pos = 1
        binding.first.visibility = View.INVISIBLE
        binding.second.visibility = View.VISIBLE
        binding.third.visibility = View.VISIBLE
        binding.textChange = 0
        //CLick
        initOnClick()
        // adapter
        val adapter = PlayerProfilePagerAdapter(requireActivity().supportFragmentManager, lifecycle)
        binding.viewPagerProfile.adapter = adapter
        binding.viewPagerProfile.isUserInputEnabled = false
        // observer
        initObserver()

    }

    override fun onResume() {
        super.onResume()
        // api call
        val data  = HashMap<String, Any>()
        viewModel.getProfileApi(Constants.USER_PROFILE,data)
    }


    /** handle click **/
    var type = false
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.tvPost -> {
                    binding.pos = 1
                    binding.viewPagerProfile.currentItem = 0
                    binding.first.visibility = View.INVISIBLE
                    binding.second.visibility = View.VISIBLE
                    binding.third.visibility = View.VISIBLE
                }

                R.id.tvClassmates -> {
                    binding.pos = 2
                    binding.viewPagerProfile.currentItem = 1
                    binding.first.visibility = View.INVISIBLE
                    binding.second.visibility = View.INVISIBLE
                    binding.third.visibility = View.VISIBLE
                }

                R.id.tvStatistiques -> {
                    binding.pos = 3
                    binding.viewPagerProfile.currentItem = 2
                    binding.first.visibility = View.VISIBLE
                    binding.second.visibility = View.INVISIBLE
                    binding.third.visibility = View.INVISIBLE
                }

                R.id.tvInventaire -> {
                    binding.pos = 4
                    binding.viewPagerProfile.currentItem = 3
                    binding.first.visibility = View.VISIBLE
                    binding.second.visibility = View.VISIBLE
                    binding.third.visibility = View.INVISIBLE
                }

                R.id.cardView -> {
                    if (type) {
                        type = false
                        binding.textChange = 0
                    } else {
                        type = true
                        binding.textChange = 1
                    }

                }

                R.id.ivSettings -> {
                    val intent = Intent(requireContext(), UserProfileActivity::class.java)
                    intent.putExtra("userType", "settings")
                    startActivity(intent)
                    requireActivity().overridePendingTransition(
                        R.anim.slide_in_right, R.anim.slide_out_left
                    )
                }

                R.id.tvShare -> {
                    val intent = Intent(requireContext(), UserProfileActivity::class.java)
                    intent.putExtra("userType", "share")
                    startActivity(intent)
                    requireActivity().overridePendingTransition(
                        R.anim.slide_in_right, R.anim.slide_out_left
                    )
                }

                R.id.tvProfile -> {
                    val intent = Intent(requireContext(), UserProfileActivity::class.java)
                    intent.putExtra("userType", "EditProfile")
                    startActivity(intent)
                    requireActivity().overridePendingTransition(
                        R.anim.slide_in_right, R.anim.slide_out_left
                    )
                }

                R.id.tvPlayerTeam -> {
                    TeamFragment.teamType = 2
                    val intent = Intent(requireContext(), UserProfileActivity::class.java)
                    intent.putExtra("userType", "team")
                    startActivity(intent)
                    requireActivity().overridePendingTransition(
                        R.anim.slide_in_right, R.anim.slide_out_left
                    )
                }

                R.id.tvPlayerPosition -> {
                    PositionFragment.positionType = 1
                    val intent = Intent(requireContext(), UserProfileActivity::class.java)
                    intent.putExtra("userType", "Position")
                    startActivity(intent)
                    requireActivity().overridePendingTransition(
                        R.anim.slide_in_right, R.anim.slide_out_left
                    )

                }

                R.id.tvPlayerAge -> {
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
                            binding.tvPlayerAge.text = formattedDate


                            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                            formatter.timeZone = TimeZone.getTimeZone("UTC")

                            birthDate = formatter.format(selectedCalendar.time)


                        }, year, month, day
                    )

                    datePickerDialog.datePicker.maxDate = maxDateCalendar.timeInMillis


                    datePickerDialog.setOnDismissListener {
                        val data = HashMap<String, RequestBody>()
                        data["birthDate"] =
                            birthDate.toRequestBody("text/plain".toMediaTypeOrNull())
                        viewModel.updateProfileApi(Constants.CREATE_PROFILE, data, null)
                    }
                    datePickerDialog.show()
                }

                R.id.tvPlayerHeight -> {
                    showGenderPicker()
                }

                R.id.tvChangeEque -> {
                    binding.pos = 4
                    binding.viewPagerProfile.currentItem = 3
                    binding.first.visibility = View.VISIBLE
                    binding.second.visibility = View.VISIBLE
                    binding.third.visibility = View.INVISIBLE
                }

                R.id.tvChangePhoto -> {
                    val intent = Intent(requireContext(), UserProfileActivity::class.java)
                    intent.putExtra("userType", "editImage")
                    startActivity(intent)
                    requireActivity().overridePendingTransition(
                        R.anim.slide_in_right, R.anim.slide_out_left
                    )
                }

                R.id.tvTotalFollowersCount->{
                    val intent = Intent(requireContext(), FollowersAndFollowingActivity::class.java)
                    intent.putExtra("FollowersType", "Followers")
                    startActivity(intent)
                    requireActivity().overridePendingTransition(
                        R.anim.slide_in_right, R.anim.slide_out_left
                    )
                }
                R.id.tvTotalFollowing->{
                    val intent = Intent(requireContext(), FollowersAndFollowingActivity::class.java)
                    intent.putExtra("FollowersType", "Following")
                    startActivity(intent)
                    requireActivity().overridePendingTransition(
                        R.anim.slide_in_right, R.anim.slide_out_left
                    )
                }
            }
        }
    }

    /** height option list **/
    private val heightOptions = (100..300).map { "$it cm" }


    /** picker **/
    private fun showGenderPicker() {
        genderBottomSheet = CommonBottomSheet(requireContext(), R.layout.gender_bottom_sheet_item) {
            when (it?.id) {
                R.id.btnOk -> {
                    val selectedValue =
                        heightOptions[genderBottomSheet.binding?.genderPicker?.value ?: 0]
                    heightCount = selectedValue
                    binding.tvPlayerHeight.text = selectedValue
                    genderBottomSheet.dismiss()
                }

                R.id.btnCancel -> {
                    genderBottomSheet.dismiss()
                }
            }
        }

        val picker = genderBottomSheet.binding?.genderPicker
        picker?.minValue = 0
        picker?.wrapSelectorWheel = false
        picker?.textSize = 30f
        picker?.maxValue = heightOptions.size - 1
        picker?.displayedValues = heightOptions.toTypedArray()
        picker?.textColor = ContextCompat.getColor(requireContext(), R.color.black_000000)
        genderBottomSheet.binding.btnCancel.text = "Cancel"
        genderBottomSheet.binding.btnOk.text = "Save"
        genderBottomSheet.binding.btnCancel.visibility = View.VISIBLE
        genderBottomSheet.behavior.isDraggable = true
        genderBottomSheet.setCancelable(true)
        genderBottomSheet.show()


        genderBottomSheet.setOnDismissListener {
            val data = HashMap<String, RequestBody>()
            val userHeight = heightCount.replace("cm", "").trim()
            data["height"] = userHeight.toRequestBody("text/plain".toMediaTypeOrNull())
            viewModel.updateProfileApi(Constants.CREATE_PROFILE, data, null)
        }
    }

    /** welcome setting dialog item **/
    private fun welcomeDialogItem() {
        welcomeDialogItem = BaseCustomDialog<ProfileWelcomeDialogItemBinding>(
            requireContext(), R.layout.profile_welcome_dialog_item
        ) {
            when (it?.id) {
                R.id.btnNext -> {
                    welcomeDialogItem.dismiss()
                    unLockBatchDialogItem()
                }
            }
        }
        welcomeDialogItem.create()
        welcomeDialogItem.show()
    }

    /** unlock dialog item **/
    private fun unLockBatchDialogItem() {
        unLockBatchDialogItem = BaseCustomDialog<UnlockBatchDialogItemBinding>(
            requireContext(), R.layout.unlock_batch_dialog_item
        ) {
            when (it?.id) {
                R.id.ivBack -> {
                    unLockBatchDialogItem.dismiss()
                }
            }
        }
        unLockBatchDialogItem.create()
        unLockBatchDialogItem.show()

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
                                        binding.bean = myDataModel.data.user
                                        sharedPrefManager.setProfileData(myDataModel)
                                        if (Constants.welcomeDialog == 0) {
                                            Constants.welcomeDialog = 1
                                            welcomeDialogItem()
                                        }
                                        DashboardActivity.userImageFragment.postValue(
                                            Resource.success(
                                                "changeInmage",
                                                myDataModel.data.user?.profilePicture
                                            )
                                        )

                                        val heightCm = myDataModel.data.user?.height ?: 0
                                        val date = myDataModel.data.user?.birthDate ?: ""
                                        binding.tvPlayerHeight.text = BindingUtils.convertCmToFeetInchesFormatted(heightCm)
                                        val age = BindingUtils.calculateAgeFromIsoLegacy(date)
                                        binding.tvPlayerAge.text = "$age"

                                    }
                                }

                            } catch (e: Exception) {
                                Log.e("error", "getProfileApi: $e")
                            } finally {
                                binding.clProfile.visibility = View.VISIBLE
                                hideLoading()
                            }
                        }

                        "updateProfileApi" -> {
                            try {
                                val myDataModel: CommonResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    if (myDataModel.message?.isNotEmpty() == true) {
                                        // api call
                                        val data = HashMap<String, Any>()
                                        viewModel.getProfileApi(Constants.USER_PROFILE,data)
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("error", "callSignUpApi: $e")
                            }
                        }
                    }
                }

                Status.ERROR -> {
                    hideLoading()
                    binding.clProfile.visibility = View.GONE
                    showErrorToast(it.message.toString())
                }

                else -> {
                }
            }
        }
    }
}