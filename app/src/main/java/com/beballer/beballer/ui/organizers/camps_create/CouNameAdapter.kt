package com.beballer.beballer.ui.organizers.camps_create


import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.beballer.beballer.R
import com.beballer.beballer.data.model.CreateTournamentModel


class CouNameAdapter(
    private val mContext: Context, private val eventCourtsList: ArrayList<CreateTournamentModel>
) : RecyclerView.Adapter<CouNameAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): CouNameAdapter.ViewHolder {
        val view =
            LayoutInflater.from(mContext).inflate(R.layout.rv_create_tournament, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return eventCourtsList.size
    }

    override fun onBindViewHolder(holder: CouNameAdapter.ViewHolder, position: Int) {
        val court = eventCourtsList[position]


        if (court.name.isNotBlank()) {
            holder.courtNameEditText.setText(court.name)
        } else {
            holder.courtNameEditText.setText("")
        }

        holder.courtNumberTV.text =
            mContext.getString(R.string.create_court, (position + 1).toString())
        holder.courtNameEditText.hint =
            mContext.getString(R.string.hint_court, (position + 1).toString())

        holder.courtNameEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                court.name = s.toString()
            }
        })
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var courtNumberTV: TextView = itemView.findViewById(R.id.tvTournamentCourtName)
        var courtNameEditText: EditText = itemView.findViewById(R.id.etTournamentCourtsName)
    }
}