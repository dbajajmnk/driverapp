package com.hbeonlabs.driversalerts.ui.fragment.notification

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hbeonlabs.driversalerts.data.local.db.models.Notification
import com.hbeonlabs.driversalerts.databinding.ItemNotificationBinding
import com.hbeonlabs.driversalerts.utils.constants.AppConstants
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject


class NotificationAdapter @Inject constructor(): RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    

    inner class NotificationViewHolder(val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Notification) {

            binding.tvWarningTitle.text = AppConstants.NotificationSubType.values()[data.notificationSubType].toString()
            binding.tvWarningMessage.text = data.message

            try {
                val dateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = data.timeInMills.toLong()

                val formattedDateTime = dateFormat.format(calendar.time)
                binding.tvTime.text = formattedDateTime

            }
            catch (
                e:Exception
            ){
                Log.d("TAG", "bind: ${e.localizedMessage}")
            }


        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<Notification>() {
        override fun areItemsTheSame(
            oldItem: Notification,
            newItem: Notification
        ): Boolean {
            return oldItem.timeInMills == newItem.timeInMills
        }

        override fun areContentsTheSame(
            oldItem: Notification,
            newItem: Notification
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

    private var onClickListener: ((Notification) -> Unit)? = null

    fun setOnItemClickListener(listener: (Notification) -> Unit) {
        onClickListener = listener
    }


}