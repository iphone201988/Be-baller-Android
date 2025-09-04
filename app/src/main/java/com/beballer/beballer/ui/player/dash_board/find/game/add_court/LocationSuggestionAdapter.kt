package com.beballer.beballer.ui.player.dash_board.find.game.add_court


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatTextView
import com.beballer.beballer.R
class LocationSuggestionAdapter(
    context: Context,
    private val items: MutableList<Pair<String, Coordinate>>
) : ArrayAdapter<Pair<String, Coordinate>>(context, 0, items)
{

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): Pair<String, Coordinate>? {
        return if (position in items.indices) items[position] else null
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val safePosition = position.coerceIn(items.indices)
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_location_suggestion, parent, false)

        val tvAddress = view.findViewById<AppCompatTextView>(R.id.tvAddress)
        val item = getItem(safePosition)
        tvAddress?.text = item?.first ?: ""
        val backgroundRes = when (safePosition) {
            0 -> if (count == 1) R.drawable.bg_dropdown_item_top else R.drawable.bg_dropdown_item_top
            count - 1 -> R.drawable.bg_dropdown_item_bottom
            else -> R.drawable.bg_dropdown_item_middle
        }
        view.setBackgroundResource(backgroundRes)
        return view
    }
    fun updateData(newItems: List<Pair<String, Coordinate>>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }



}

