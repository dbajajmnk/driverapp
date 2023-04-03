package com.hbeonlabs.driversalerts.ui.fragment.dialogs

import android.app.Activity
import android.app.Dialog
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.databinding.DialogAlertBinding
import com.hbeonlabs.driversalerts.utils.setHeightWidthPercent

fun Fragment.dialogDrowsinessAlert(
    headerText :String,
    descText:String
): Dialog {
    val dialog = Dialog(requireContext())
    var mediaPlayer: MediaPlayer = MediaPlayer.create(requireContext(), R.raw.alarm)

    val binding = DataBindingUtil.inflate(
        LayoutInflater.from(requireContext()),
        R.layout.dialog_alert,
        null,
        false
    ) as DialogAlertBinding

    binding.txtHead.text = headerText
    binding.txtDesc.text = descText
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(binding.root)
    dialog.setHeightWidthPercent(90, 50)

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

fun Activity.dialogDrowsinessAlert(
    headerText :String,
    descText:String
): Dialog {
    val dialog = Dialog(this)
    var mediaPlayer: MediaPlayer = MediaPlayer.create(this, R.raw.alarm)

    val binding = DataBindingUtil.inflate(
        LayoutInflater.from(this),
        R.layout.dialog_alert,
        null,
        false
    ) as DialogAlertBinding

    binding.txtHead.text = headerText
    binding.txtDesc.text = descText
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(binding.root)
    dialog.setHeightWidthPercent(90, 50)

    dialog.setCancelable(false)
    dialog.setOnShowListener {
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm)
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