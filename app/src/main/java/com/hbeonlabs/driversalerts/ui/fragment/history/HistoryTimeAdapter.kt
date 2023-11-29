package com.hbeonlabs.driversalerts.ui.fragment.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hbeonlabs.driversalerts.R

class HistoryTimeAdapter(private val items: List<ItemT>, val onItemClick:(CameraType)->Unit) :
    RecyclerView.Adapter<HistoryTimeAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.recording_name)
        val timeTextView: TextView = view.findViewById(R.id.textView11)
        val front_cam: TextView = view.findViewById(R.id.tv_front_camera)
        val back_camera: TextView = view.findViewById(R.id.tv_back_camera)
        val both_camera: TextView = view.findViewById(R.id.both)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_time, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.titleTextView.text = item.title
        holder.timeTextView.text = item.time
        holder.front_cam.setOnClickListener {
            onItemClick(CameraType.FRONT_CAMERA)
        }
        holder.back_camera.setOnClickListener {
            onItemClick(CameraType.BACK_CAMERA)
        }
        holder.both_camera.setOnClickListener {
            onItemClick(CameraType.BOTH_CAMERAS)
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }
}
enum class CameraType{
    FRONT_CAMERA, BACK_CAMERA,BOTH_CAMERAS
}