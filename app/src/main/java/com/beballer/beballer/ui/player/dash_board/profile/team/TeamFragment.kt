package com.beballer.beballer.ui.player.dash_board.profile.team

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import com.beballer.beballer.BR
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.base.SimpleRecyclerViewAdapter
import com.beballer.beballer.utils.BaseCustomDialog
import com.beballer.beballer.utils.BindingUtils
import com.beballer.beballer.utils.Status
import com.beballer.beballer.data.api.Constants
import com.beballer.beballer.data.model.CommonResponse
import com.beballer.beballer.data.model.GetPlayerTeamResponse
import com.beballer.beballer.data.model.LoginApiResponse
import com.beballer.beballer.data.model.PlayerTeamData
import com.beballer.beballer.databinding.FragmentTeamBinding
import com.beballer.beballer.databinding.TeamCreateDialogItemBinding
import com.beballer.beballer.databinding.TeamRvItemBinding
import com.beballer.beballer.ui.player.dash_board.DashboardActivity
import com.beballer.beballer.ui.player.dash_board.profile.edit_profile.EditProfileFragment
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

@AndroidEntryPoint
class TeamFragment : BaseFragment<FragmentTeamBinding>() {
    private val viewModel: TeamFragmentVM by viewModels()
    private lateinit var teamAdapter: SimpleRecyclerViewAdapter<PlayerTeamData, TeamRvItemBinding>
    private lateinit var teamDialogItem: BaseCustomDialog<TeamCreateDialogItemBinding>
    private var addData = ArrayList<String>()
    private var favoriteProTeam = ""
    private lateinit var fullList: List<PlayerTeamData>

    companion object {
        var teamType = 0
    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_team
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
        // api call
        val data  = HashMap<String, Any>()
        viewModel.getTeamListAPi(Constants.PLAYER_TEAM,data)
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
                                val myDataModel: CommonResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    if (myDataModel.message?.isNotEmpty() == true) {
                                        // api call
                                        val data  = HashMap<String, Any>()
                                        viewModel.getProfileApi(Constants.USER_PROFILE,data)
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("error", "onBoardingAPi: $e")
                            }finally {
                                hideLoading()
                            }
                        }

                        "getProfileApi" -> {
                            try {
                                val myDataModel: LoginApiResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    if (myDataModel.data != null) {
                                        sharedPrefManager.setLoginData(myDataModel)
                                        val intent =
                                            Intent(requireContext(), DashboardActivity::class.java)
                                        startActivity(intent)
                                        requireActivity().finishAffinity()
                                    }
                                }
                                hideLoading()
                            } catch (e: Exception) {
                                Log.e("error", "getProfileApi: $e")
                            }
                        }

                        "getTeamListAPi" -> {
                            try {
                                val myDataModel: GetPlayerTeamResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    fullList = myDataModel.data as ArrayList<PlayerTeamData>
                                    teamAdapter.list = fullList
                                }
                                hideLoading()
                            } catch (e: Exception) {
                                Log.e("error", "getTeamListAPi: $e")
                            }
                        }

                        "updateProfileApi" -> {
                            try {
                                val myDataModel: CommonResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    if (myDataModel.message?.isNotEmpty() == true) {
                                        requireActivity().finish()
                                    }
                                }
                                hideLoading()
                            } catch (e: Exception) {
                                Log.e("error", "updateProfileApi: $e")
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
                R.id.ivBack -> {
                    requireActivity().finish()
                }

                R.id.btnNext -> {
                    if (addData.size > 0) {
                        when (teamType) {
                            1 -> {
                                val data = HashMap<String, Any>()
                                viewModel.onBoardingAPi(data, Constants.ON_BOARDING)
                            }

                            2 -> {
                                val data = HashMap<String, RequestBody>()
                                data["favoriteProTeam"] =
                                    favoriteProTeam.toRequestBody("text/plain".toMediaTypeOrNull())
                                viewModel.updateProfileApi(Constants.CREATE_PROFILE, data, null)
                                teamDialogItem()
                            }

                            3 -> {
                                EditProfileFragment.favoriteProTeam = favoriteProTeam
                                requireActivity().finish()
                            }
                        }
                    } else {
                        showInfoToast("Please select at least one option")
                    }
                }
            }
        }
    }

    /**** team dialog item ****/
    private fun teamDialogItem() {
        teamDialogItem = BaseCustomDialog<TeamCreateDialogItemBinding>(
            requireContext(), R.layout.team_create_dialog_item
        ) {
            when (it?.id) {
                // let,s go button click
                R.id.btnNext -> {

                }
            }

        }
        teamDialogItem.create()
        teamDialogItem.show()
    }


    /** handle adapter **/
    private fun initAdapter() {
        teamAdapter = SimpleRecyclerViewAdapter(R.layout.team_rv_item, BR.bean) { v, m, pos ->
            when (v.id) {
                R.id.clTeam -> {
                    if (teamType == 1) {
                        m.check = !m.check
                        if (m.check) {
                            if (!addData.contains(m.type)) {
                                m.name?.let { addData.add(it) }
                            }
                        } else {
                            addData.remove(m.type)
                        }
                        teamAdapter.notifyDataSetChanged()
                    } else {
                        for (i in teamAdapter.list) {
                            i.check = i.id == m.id
                        }
                        favoriteProTeam = m.id.toString()
                        EditProfileFragment.favoriteProTeamName = m.name.toString()
                        if (m.check) {
                            if (!addData.contains(m.type)) {
                                m.type?.let { addData.add(it) }
                            }
                        } else {
                            addData.remove(m.type)
                        }
                        teamAdapter.notifyDataSetChanged()
                    }

                    binding.buttonCheck = addData.size > 0

                }
            }
        }

        binding.rvOptions.adapter = teamAdapter
        setupSearch()
    }


    /*** add search ***/
    private fun setupSearch() {
        val searchView = binding.teamSearchView

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

                teamAdapter.list = filtered
                teamAdapter.notifyDataSetChanged()
                return true
            }
        })
    }

}