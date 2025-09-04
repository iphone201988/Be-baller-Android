package com.beballer.beballer.ui.player.dash_board.profile.settings

import android.content.Intent
import android.view.View
import androidx.fragment.app.viewModels
import com.beballer.beballer.BR
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.base.SimpleRecyclerViewAdapter
import com.beballer.beballer.utils.BaseCustomDialog
import com.beballer.beballer.data.model.SettingsModel
import com.beballer.beballer.databinding.FragmentSettingsBinding
import com.beballer.beballer.databinding.LogoutDialogItemBinding
import com.beballer.beballer.databinding.SettingsRvItemBinding
import com.beballer.beballer.ui.player.auth.AuthActivity
import com.beballer.beballer.ui.player.dash_board.profile.user.UserProfileActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {
    private val viewModel: SettingsFragmentVM by viewModels()
    private lateinit var settingsAdapter: SimpleRecyclerViewAdapter<SettingsModel, SettingsRvItemBinding>
    private lateinit var logoutDialogItem: BaseCustomDialog<LogoutDialogItemBinding>
    override fun getLayoutResource(): Int {
        return R.layout.fragment_settings
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initClick()
        // adapter
        initAdapter()
    }

    /** handle click **/
    private fun initClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.ivBack -> {
                    requireActivity().finish()
                }
            }
        }
    }

    /** handle adapter **/
    private fun initAdapter() {
        settingsAdapter =
            SimpleRecyclerViewAdapter(R.layout.settings_rv_item, BR.bean) { v, m, pos ->
                when (pos) {
                    0 -> {
                        val intent = Intent(requireContext(), UserProfileActivity::class.java)
                        intent.putExtra("userType", "EditProfile")
                        startActivity(intent)
                        requireActivity().overridePendingTransition(
                            R.anim.slide_in_right, R.anim.slide_out_left
                        )
                    }

                    1 -> {
                        val intent = Intent(requireContext(), UserProfileActivity::class.java)
                        intent.putExtra("userType", "Friend")
                        startActivity(intent)
                        requireActivity().overridePendingTransition(
                            R.anim.slide_in_right, R.anim.slide_out_left
                        )
                    }

                    2 -> {
                        val intent = Intent(requireContext(), UserProfileActivity::class.java)
                        intent.putExtra("userType", "suggestion")
                        startActivity(intent)
                        requireActivity().overridePendingTransition(
                            R.anim.slide_in_right, R.anim.slide_out_left
                        )
                    }

                    3 -> {
                        val intent = Intent(requireContext(), UserProfileActivity::class.java)
                        intent.putExtra("userType", "notification")
                        startActivity(intent)
                        requireActivity().overridePendingTransition(
                            R.anim.slide_in_right, R.anim.slide_out_left
                        )
                        requireActivity().finish()
                    }

                    4 -> {

                    }

                    6 -> {
                        val intent = Intent(requireContext(), UserProfileActivity::class.java)
                        intent.putExtra("userType", "policy")
                        startActivity(intent)
                        requireActivity().overridePendingTransition(
                            R.anim.slide_in_right, R.anim.slide_out_left
                        )
                    }

                    7 -> {
                        logoutDialogItem()

                    }
                }
            }
        settingsAdapter.list = getList()
        binding.rvSetting.adapter = settingsAdapter
    }


    /** logout dialog item **/
    private fun logoutDialogItem() {
        logoutDialogItem = BaseCustomDialog<LogoutDialogItemBinding>(
            requireContext(), R.layout.logout_dialog_item
        ) {
            when (it?.id) {
                R.id.btnCancel -> {
                    logoutDialogItem.dismiss()
                }
                R.id.btnConfirm -> {
                    sharedPrefManager.clear()
                    val intent = Intent(requireContext(), AuthActivity::class.java)
                    startActivity(intent)
                    requireActivity().finishAffinity()
                    requireActivity().overridePendingTransition(
                        R.anim.slide_in_right, R.anim.slide_out_left
                    )
                    logoutDialogItem.dismiss()
                }
            }

        }
        logoutDialogItem.create()
        logoutDialogItem.show()
    }

    // add List in data
    private fun getList(): ArrayList<SettingsModel> {
        return arrayListOf(
            SettingsModel(R.drawable.ic_round_person_40, "Edit profile", "1"),
            SettingsModel(R.drawable.friend_icon, "Sponsor a friend", "2"),
            SettingsModel(R.drawable.suggestion_icon, "Issue/Suggestion", "3"),
            SettingsModel(R.drawable.bell_circle, "Notification", "4"),
            SettingsModel(R.drawable.message, "Languages", "5"),
            SettingsModel(R.drawable.questionmark, "Help center", "6"),
            SettingsModel(R.drawable.shield, "Confidentiality policy", "7"),
            SettingsModel(R.drawable.person_mark, "Log out", "8"),

            )
    }


}
