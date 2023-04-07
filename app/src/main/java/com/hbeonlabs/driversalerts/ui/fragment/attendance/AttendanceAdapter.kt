package com.hbeonlabs.driversalerts.ui.fragment.attendance

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hbeonlabs.driversalerts.R

class AttendanceAdapter(private val itemAttendances: List<ItemAttendance>):
    RecyclerView.Adapter<AttendanceAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.Name)
        val classTextView: TextView = view.findViewById(R.id.Class)
        val timeIn: TextView = view.findViewById(R.id.in_time)
        val timeOut: TextView = view.findViewById(R.id.out_time);
        val bgColor: LinearLayout = view.findViewById(R.id.background)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_attendance, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemAttendances[position]
        holder.nameTextView.text = item.name
        holder.classTextView.text = item.classname
        holder.timeIn.text = item.time_in
        holder.timeOut.text = item.time_out
        holder.bgColor.setBackgroundColor(item.bgColor)
    }

    override fun getItemCount(): Int {
        return itemAttendances.size
    }
}