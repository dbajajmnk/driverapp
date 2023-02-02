package com.example.driversalerts.ui.fragment.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.driversalerts.R
import com.example.driversalerts.databinding.DialogAlertBinding
import com.example.driversalerts.utils.setHeightWidthPercent

fun Fragment.dialogDosinessAlert(

): Dialog {
    val dialog = Dialog(requireContext())
    var mediaPlayer: MediaPlayer = MediaPlayer.create(requireContext(), R.raw.alarm)

    val binding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()), R.layout.dialog_alert, null, false) as DialogAlertBinding
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(binding.root)
    dialog.setHeightWidthPercent(90,50)

    dialog.setCancelable(false)
    dialog.setOnShowListener {
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.alarm)
        mediaPlayer.start()
    }

    binding.btnOk.setOnClickListener {
        dialog.dismiss()
    }

    dialog.setOnDismissListener {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }


    return dialog

}