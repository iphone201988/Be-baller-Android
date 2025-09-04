package com.beballer.beballer.utils



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.beballer.beballer.R
import com.beballer.beballer.data.model.PoolModel
import com.zerobranch.layout.SwipeLayout


class CampsCardAdapter(
    private val itemClickListener: OnItemClickListener,
) : RecyclerView.Adapter<CampsCardAdapter.ViewHolder>() {
    private var itemList: MutableList<PoolModel?> = ArrayList()
    private var currentlyOpenSwipeLayout: SwipeLayout? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.rv_pools_items, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position:  Int) {
        val item = itemList[position]
        holder.bind(item, itemClickListener, position)
    }

    override fun getItemCount() = itemList.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTeam: AppCompatTextView = view.findViewById(R.id.tvTeam)
        val tvDelete: AppCompatTextView = view.findViewById(R.id.tvDelete)
        val tvCount: AppCompatTextView = view.findViewById(R.id.tvCount)
        val tvPoint: AppCompatTextView = view.findViewById(R.id.tvPoint)
        val favConstantLayout1: ConstraintLayout = view.findViewById(R.id.favConstantLayout1)
        val swipeLayout: SwipeLayout = view.findViewById(R.id.swipeLayout)

        fun bind(item: PoolModel?, listener: OnItemClickListener, position: Int) {
            item?.let {
                tvTeam.text = it.teamName
                tvCount.text = it.count
                tvPoint.text = it.point



                tvDelete.setOnClickListener { view ->
                    listener.onItemClick(view, it, position)
                    swipeLayout.close()
                }

                favConstantLayout1.setOnClickListener { view ->
                    listener.onItemClick(view, it, position)
                }
                swipeLayout.setOnActionsListener(object : SwipeLayout.SwipeActionsListener {
                    override fun onOpen(direction: Int, isContinuous: Boolean) {
                        if (direction == SwipeLayout.LEFT) {
                            closePreviouslyOpenSwipeLayout()
                            currentlyOpenSwipeLayout = swipeLayout
                        }
                    }
                    override fun onClose() {
                        if (currentlyOpenSwipeLayout == swipeLayout) {
                            currentlyOpenSwipeLayout = null
                        }
                    }
                })

            }
        }

        private fun closePreviouslyOpenSwipeLayout() {
            currentlyOpenSwipeLayout?.close()
        }
    }

    fun removeItemAt(position: Int) {
        if (position >= 0 && position < itemList.size) {
            itemList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(
                position, itemList.size
            ) // Update the range of items that have changed
        }
    }


    fun clearList() {
        itemList.clear()
        notifyDataSetChanged()
    }

    fun getList(): MutableList<PoolModel?> {
        return itemList
    }

    fun setList(newDataList: List<PoolModel?>?) {
        itemList.clear()
        if (newDataList != null) {
            itemList.addAll(newDataList)
        }
        notifyDataSetChanged()
    }


    fun addToList(list: List<PoolModel?>?) {
        val newDataList: List<PoolModel?>? = list
        if (newDataList != null) {
            val initialSize = itemList.size
            itemList.addAll(newDataList)
            notifyItemRangeInserted(
                initialSize,
                newDataList.size
            ) // Notify only the newly inserted range
        }
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, item: PoolModel?, position: Int)
    }
}
