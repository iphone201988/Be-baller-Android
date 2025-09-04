package com.beballer.beballer.ui.player.dash_board.profile.position

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
import com.beballer.beballer.data.model.CommonResponse
import com.beballer.beballer.data.model.OptionModel
import com.beballer.beballer.databinding.FragmentPositionBinding
import com.beballer.beballer.databinding.RvPositionItemBinding
import com.beballer.beballer.ui.player.dash_board.profile.edit_profile.EditProfileFragment
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

@AndroidEntryPoint
class PositionFragment : BaseFragment<FragmentPositionBinding>() {
    private val viewModel: PositionFragmentVM by viewModels()
    private lateinit var positionAdapter: SimpleRecyclerViewAdapter<OptionModel, RvPositionItemBinding>
    private var positionName = ""
    private var positionId = 0

    companion object {
        var positionType = 0
    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_position
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

    /** handle click **/
    private fun initClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.ivBack -> {
                    requireActivity().finish()
                }

                R.id.btnNext -> {
                    if (positionName.isNotEmpty()) {
                        if (positionType == 1) {
                            val data = HashMap<String, RequestBody>()
                            data["position"] =
                                positionName.toRequestBody("text/plain".toMediaTypeOrNull())
                            data["playPositionId"] = positionId.toString()
                                .toRequestBody("text/plain".toMediaTypeOrNull())
                            viewModel.updateProfileApi(Constants.CREATE_PROFILE, data, null)
                        } else if (positionType == 2) {
                            EditProfileFragment.positionId = positionId.toString()
                            EditProfileFragment.positionName = positionName
                            requireActivity().finish()
                        }

                    } else {
                        showInfoToast("Please pick any position")
                    }

                }
            }
        }
    }

    /** handle adapter **/
    private fun initAdapter() {
        positionAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_position_item, BR.bean) { v, m, pos ->
                when (v.id) {
                    R.id.clMain -> {
                        for (i in positionAdapter.list) {
                            i.check = i.positionId == m.positionId
                        }
                        binding.buttonCheck = true
                        positionName = m.positionCode
                        positionId = m.positionId
                        positionAdapter.notifyDataSetChanged()

                    }
                }
            }
        positionAdapter.list = getList()
        binding.rvPosition.adapter = positionAdapter
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
                        "updateProfileApi" -> {
                            try {
                                val myDataModel: CommonResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    if (myDataModel.message?.isNotEmpty() == true) {
                                        requireActivity().finish()
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("error", "updateProfileApi: $e")
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

    // add List in data
    private fun getList(): ArrayList<OptionModel> {
        return arrayListOf(
            OptionModel("1️⃣", "Point Guard (PG)", 1, "PG"),
            OptionModel("2️⃣", "Shooting Guard (SG)", 2, "SG"),
            OptionModel("3️⃣", "Small Forward (SF)", 3, "SF"),
            OptionModel("4️⃣", "Power Forward (PF)", 4, "PF"),
            OptionModel("5️⃣", "Center (C)", 5, "C"),
            OptionModel("🤷", "I don't know", 6, "Idk")
        )
    }
}