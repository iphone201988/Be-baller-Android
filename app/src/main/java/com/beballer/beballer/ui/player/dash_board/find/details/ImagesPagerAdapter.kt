package com.beballer.beballer.ui.player.dash_board.find.details

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.beballer.beballer.R
import com.beballer.beballer.data.api.Constants
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory

class ImagesPagerAdapter(
    private var mContext: Context,
    private var mUri: ArrayList<String>
) : RecyclerView.Adapter<ImagesPagerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(mContext)
            .inflate(R.layout.recycler_images_pager_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImagesPagerAdapter.ViewHolder, position: Int) {
        val uri = mUri[position]
        val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
        Glide.with(mContext).load(Constants.IMAGE_URL+uri.toString()).fitCenter()
            .transition(DrawableTransitionOptions.withCrossFade(factory)).into(holder.pagerImage)

    }

    override fun getItemCount(): Int {
        return mUri.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var pagerImage: ImageView = itemView.findViewById(R.id.pager_image)

    }
}