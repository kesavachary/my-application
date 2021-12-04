package com.example.criclivescore

import android.R
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.smitpatel.cricketscore.ListItem

class MyAdapter(
    private val listItems: List<ListItem>,
    private val context: Context,
    private val user: String
) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.activity_list_item, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val listItem = listItems[i]
        viewHolder.textViewHead.text = listItem.header
        viewHolder.textViewDescription.text = listItem.description
        viewHolder.linearLayout.setOnClickListener {
            if (user == "MainActivity") {
                matchInfo(listItem)
            }
        }
        viewHolder.textViewDetails.text = listItem.details
    }

    fun matchInfo(listItem: ListItem) {
        val intent = Intent(context, matchInfo::class.java)
        intent.putExtra("matchId", listItem.matchId)
        context.startActivity(intent)
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewHead: TextView
        var textViewDescription: TextView
        var linearLayout: LinearLayout
        var textViewDetails: TextView

        init {
            textViewHead = itemView.findViewById<View>(R.id.textViewHead) as TextView
            textViewDescription = itemView.findViewById<View>(R.id.textViewDescription) as TextView
            textViewDetails = itemView.findViewById(R.id.textViewDetails)
            linearLayout = itemView.findViewById<View>(R.id.linearLayout) as LinearLayout
        }
    }
}