package com.hbeonlabs.driversalerts.ui.fragment.notification

import android.content.ClipData
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hbeonlabs.driversalerts.R

class NotificationAdapter(private val items: List<Item>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener{
        val uptext:TextView = view.findViewById(R.id.textView2)
        val titleTextView: TextView = view.findViewById(R.id.cause)
        val personimageView: ImageView = view.findViewById(R.id.profile_image);
        val location: TextView = view.findViewById(R.id.reason)
        val date: TextView = view.findViewById(R.id.time)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.uptext.text = item.uptext
        holder.titleTextView.text = item.title
        holder.personimageView.setImageResource(item.image)
        holder.location.text = item.location
        holder.date.text = item.date
    }


    override fun getItemCount(): Int {
        return items.size
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}



