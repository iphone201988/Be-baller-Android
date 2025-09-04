package com.beballer.beballer.ui.player.auth.options

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import com.beballer.beballer.BR
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.base.SimpleRecyclerViewAdapter
import com.beballer.beballer.utils.BindingUtils
import com.beballer.beballer.utils.Status
import com.beballer.beballer.data.api.Constants
import com.beballer.beballer.data.model.LoginApiResponse
import com.beballer.beballer.data.model.OptionModel
import com.beballer.beballer.data.model.UserProfile
import com.beballer.beballer.databinding.FragmentOptionsBinding
import com.beballer.beballer.databinding.RvOptionItemBinding
import com.beballer.beballer.ui.player.dash_board.DashboardActivity
import com.beballer.beballer.ui.player.dash_board.profile.team.TeamFragment
import com.beballer.beballer.ui.player.dash_board.profile.user.UserProfileActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OptionsFragment : BaseFragment<FragmentOptionsBinding>() {
    private val viewModel: OptionsFragmentVM by viewModels()
    private lateinit var optionsAdapter: SimpleRecyclerViewAdapter<OptionModel, RvOptionItemBinding>
    var type = 0
    private var addData = ArrayList<String>()
    override fun getLayoutResource(): Int {
        return R.layout.fragment_options
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // adapter
        initAdapter()
        // click
        initClick()
        // observer
        initObserver()
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
                        "onBoardingAPi" -> {
                            try {
                                val myDataModel: LoginApiResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    // api call
                                    val data = HashMap<String, Any>()
                                    viewModel.getProfileApi(Constants.USER_PROFILE,data)

                                }
                            } catch (e: Exception) {
                                Log.e("error", "onBoardingAPi: $e")
                            }
                        }
                        "getProfileApi" -> {
                            try {
                                val myDataModel: UserProfile? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    if (myDataModel.data!=null) {
                                        sharedPrefManager.setProfileData(myDataModel)
                                        val intent = Intent(requireContext(), DashboardActivity::class.java)
                                        startActivity(intent)
                                        requireActivity().finishAffinity()
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("error", "getProfileApi: $e")
                            }finally {
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

    /** handle click **/
    private fun initClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.btnNext -> {
                    when (type) {
                        1 -> {
                            val data = HashMap<String, Any>()
                            viewModel.onBoardingAPi(data, Constants.ON_BOARDING)
                        }

                        2 -> {
                            TeamFragment.teamType = 1
                            val intent = Intent(requireContext(), UserProfileActivity::class.java)
                            intent.putExtra("userType", "team")
                            startActivity(intent)
                            requireActivity().overridePendingTransition(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left
                            )
                        }

                        else -> {
                            showInfoToast("Please select one option")
                        }
                    }

                }
            }
        }
    }


    /** handle adapter **/
    private fun initAdapter() {
        optionsAdapter = SimpleRecyclerViewAdapter(R.layout.rv_option_item, BR.bean) { v, m, pos ->
            when (v.id) {
                R.id.clMain -> {
                    m.check = !m.check
                    if (m.check) {
                        if (!addData.contains(m.title)) {
                            addData.add(m.title)
                        }
                    } else {
                        addData.remove(m.title)
                    }
                    // Determine type
                    type = when {
                        addData.isEmpty() -> 0
                        addData.contains("Follow professional teams") -> 2
                        else -> 1
                    }
                    optionsAdapter.notifyDataSetChanged()
                    binding.buttonCheck = addData.isNotEmpty()
                }
            }
        }
        optionsAdapter.list = getList()
        binding.rvOptions.adapter = optionsAdapter
    }


    // add List in data
    private fun getList(): ArrayList<OptionModel> {
        return arrayListOf(
            OptionModel("🔍", "Find/add court",1,"PG"),
            OptionModel("⛹️‍♂️", "Improve my game",2,"SG"),
            OptionModel("🏀", "Create/join Pickup Games",3,"SF"),
            OptionModel("🎫", "Follow professional teams",4,"PF"),
            OptionModel("💪", "Participate in tournaments",5,"C"),
            OptionModel("⛺️", "Participate in basketball camps",6,"Idk"),
            OptionModel("🤳", "Follow content about basketball",7,"Idk"),
            OptionModel("🤷", "Other",8,"Idk")
        )
    }

}