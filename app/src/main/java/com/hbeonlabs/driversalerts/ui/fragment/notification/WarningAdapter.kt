package com.hbeonlabs.driversalerts.ui.fragment.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hbeonlabs.driversalerts.data.local.db.models.Warning
import com.hbeonlabs.driversalerts.databinding.ItemNotificationBinding
import javax.inject.Inject


class WarningAdapter @Inject constructor(): RecyclerView.Adapter<WarningAdapter.NotificationViewHolder>() {

    

    inner class NotificationViewHolder(val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Warning) {

            binding.tvWarningTitle.text = NotificationSubType.values()[data.notificationSubType].toString()
            binding.tvWarningMessage.text = data.message


        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<Warning>() {
        override fun areItemsTheSame(
            oldItem: Warning,
            newItem: Warning
        ): Boolean {
            return oldItem.timeInMills == newItem.timeInMills
        }

        override fun areContentsTheSame(
            oldItem: Warning,
            newItem: Warning
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(
            ItemNotificationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val data = differ.currentList[position]
        holder.bind(data)
        holder.itemView.setOnClickListener {
            onClickListener?.let { it(data) }
        }

    }

    private var onClickListener: ((Warning) -> Unit)? = null

    fun setOnItemClickListener(listener: (Warning) -> Unit) {
        onClickListener = listener
    }


}