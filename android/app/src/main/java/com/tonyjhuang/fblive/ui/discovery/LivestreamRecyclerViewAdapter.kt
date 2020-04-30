package com.tonyjhuang.fblive.ui.discovery

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.tonyjhuang.fblive.R


import com.tonyjhuang.fblive.ui.discovery.LivestreamListFragment.OnListFragmentInteractionListener
import com.tonyjhuang.fblive.ui.discovery.dummy.DummyContent.DummyItem
import kotlinx.android.synthetic.main.list_item_livestream.view.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class LivestreamRecyclerViewAdapter(
    private val values: List<DummyItem>,
    private val listener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<LivestreamRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as DummyItem
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            listener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_livestream, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]

        Glide.with(holder.thumbnail)
            .load(item.thumbnail)
            .into(holder.thumbnail)
        holder.streamName.text = item.streamName
        holder.creatorName.text = item.creatorName

        with(holder.view) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val thumbnail: ImageView = view.thumbnail
        val streamName: TextView = view.stream_name
        val creatorName: TextView = view.creator_name
    }
}
