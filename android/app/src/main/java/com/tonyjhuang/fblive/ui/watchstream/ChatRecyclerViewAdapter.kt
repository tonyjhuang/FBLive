package com.tonyjhuang.fblive.ui.watchstream


import android.text.Spannable
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.bold
import androidx.recyclerview.widget.RecyclerView
import com.tonyjhuang.fblive.R
import com.tonyjhuang.fblive.ui.watchstream.ChatFragment.OnListFragmentInteractionListener
import com.tonyjhuang.fblive.ui.watchstream.dummy.DummyContent.DummyItem
import kotlinx.android.synthetic.main.list_item_message.view.*


/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class ChatRecyclerViewAdapter(
    private val messages: List<DummyItem>,
    private val listener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<ChatRecyclerViewAdapter.ViewHolder>() {

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
            .inflate(R.layout.list_item_message, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = messages[position]
        holder.content.text = getFormattedText(item.authorName, item.body)

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    private fun getFormattedText(authorName: String, messageBody: String): Spannable {
        return SpannableStringBuilder()
            .bold { append("$authorName: ") }
            .append(messageBody)
    }

    override fun getItemCount(): Int = messages.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val content: TextView = mView.content

        override fun toString(): String {
            return super.toString() + " '" + content.text + "'"
        }
    }
}
