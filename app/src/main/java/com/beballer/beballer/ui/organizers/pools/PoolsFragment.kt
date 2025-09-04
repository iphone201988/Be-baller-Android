package com.beballer.beballer.ui.organizers.pools

import android.view.View
import androidx.fragment.app.viewModels
import com.beballer.beballer.BR
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.base.SimpleRecyclerViewAdapter
import com.beballer.beballer.utils.BaseCustomBottomSheet
import com.beballer.beballer.utils.BaseCustomDialog
import com.beballer.beballer.utils.CampsCardAdapter
import com.beballer.beballer.data.model.GameModeModel
import com.beballer.beballer.data.model.PoolModel
import com.beballer.beballer.databinding.AddPoolsDialogItemBinding
import com.beballer.beballer.databinding.DeleteDailogItemBinding
import com.beballer.beballer.databinding.FragmentPoolsBinding
import com.beballer.beballer.databinding.RvGameModeItemBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PoolsFragment : BaseFragment<FragmentPoolsBinding>(), CampsCardAdapter.OnItemClickListener {
    private val viewModel: PoolsFragmentVM by viewModels()
    private lateinit var poolsAdapter: SimpleRecyclerViewAdapter<GameModeModel, RvGameModeItemBinding>
    private lateinit var addPoolsBottomSheet: BaseCustomBottomSheet<AddPoolsDialogItemBinding>
    private lateinit var deleteDialogDialogItem: BaseCustomDialog<DeleteDailogItemBinding>
    private lateinit var customAdapter: CampsCardAdapter
    override fun getLayoutResource(): Int {
        return R.layout.fragment_pools
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnCLick()
        /***  Adapter initialize  ***/
        customAdapter = CampsCardAdapter(this)
        binding.rvPool.adapter = customAdapter
        customAdapter.setList(getList())
    }


    /***   all click handel   ***/
    private fun initOnCLick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.ivPlus -> {
                    addPoolsBottomSheet()
                }
            }
        }
    }


    /**** delete dialog item ****/
    private fun deleteDialogItem(type:Int) {
        deleteDialogDialogItem = BaseCustomDialog<DeleteDailogItemBinding>(
            requireContext(), R.layout.delete_dailog_item
        ) {
            when (it?.id) {
                // let,s go button click
                R.id.btnConfirm -> {
                    customAdapter.removeItemAt(type)
                    customAdapter.notifyDataSetChanged()
                    deleteDialogDialogItem.dismiss()
                }
                R.id.btnCancel->{
                    deleteDialogDialogItem.dismiss()
                }
            }

        }
        deleteDialogDialogItem.create()
        deleteDialogDialogItem.show()

    }



    /** game mode bottom sheet **/
    private fun addPoolsBottomSheet() {
        addPoolsBottomSheet =
            BaseCustomBottomSheet(requireContext(), R.layout.add_pools_dialog_item) {
                when (it?.id) {

                }
            }
        addPoolsBottomSheet.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        addPoolsBottomSheet.behavior.isDraggable = true
        addPoolsBottomSheet.create()
        addPoolsBottomSheet.show()

        initGameModeAdapter()
    }

    /** handle game mode adapter **/
    private fun initGameModeAdapter() {
        poolsAdapter = SimpleRecyclerViewAdapter(R.layout.rv_game_mode_item, BR.bean) { v, m, pos ->
            when (v.id) {
                R.id.clGame -> {
                    addPoolsBottomSheet.dismiss()
                }
            }
        }
        poolsAdapter.list = getListGame()
        addPoolsBottomSheet.binding.rvGameModel.adapter = poolsAdapter
    }

    // add list game mode
    private fun getListGame(): ArrayList<GameModeModel> {
        return arrayListOf(
            GameModeModel("Pool A"),
            GameModeModel("Pool B"),
            GameModeModel("Pool C"),
            GameModeModel("Pool D"),
            GameModeModel("Pool E"),
            GameModeModel("Pool F"),

            )
    }


    private fun getList(): ArrayList<PoolModel> {
        val budgetList = ArrayList<PoolModel>()
        budgetList.add(PoolModel("1.", "Team 1", "1"))
        budgetList.add(PoolModel("2.", "Team 2", "2"))
        budgetList.add(PoolModel("3.", "New team 1", "3"))
        budgetList.add(PoolModel("4.", "Team 3", "4"))
        budgetList.add(PoolModel("5.", "New team 2", "5"))
        budgetList.add(PoolModel("6.", "Team 4", "6"))

        return budgetList
    }

    override fun onItemClick(view: View, item: PoolModel?, position: Int) {
        when (view.id) {
            R.id.tvDelete -> {
                deleteDialogItem(position)

            }


        }
    }
}