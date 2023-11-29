package com.hbeonlabs.driversalerts.ui.fragment.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.databinding.FragmentHistoryBinding
import com.hbeonlabs.driversalerts.databinding.FragmentHistoryVideoPlayerBinding
import com.hbeonlabs.driversalerts.databinding.FragmentWatcherBinding
import com.hbeonlabs.driversalerts.ui.base.BaseFragment
import com.hbeonlabs.driversalerts.ui.fragment.notification.WarningsFragment

class HistoryVideoPlayerFragment : BaseFragment<FragmentHistoryVideoPlayerBinding>() {

    lateinit var frontExoplayer: ExoPlayer
    lateinit var backExoplayer: ExoPlayer

    override fun initView() {
        super.initView()
        binding.include.titleFrag.text = "Video Recording"

        val frontCameraUrl = ""
        val backCameraUrl = ""

        frontExoplayer = ExoPlayer.Builder(requireContext()).build()
        frontExoplayer.addMediaItem(MediaItem.fromUri(frontCameraUrl.toUri()))
        frontExoplayer.prepare()
        frontExoplayer.playWhenReady = true

        backExoplayer = ExoPlayer.Builder(requireContext()).build()
        backExoplayer.addMediaItem(MediaItem.fromUri(backCameraUrl.toUri()))
        backExoplayer.prepare()
        backExoplayer.playWhenReady = true


    }

    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_history_video_player
    }

    fun releaseExoplayer() {
        frontExoplayer.pause()
        backExoplayer.pause()

        frontExoplayer.release()
        backExoplayer.release()
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseExoplayer()
    }

    override fun onPause() {
        super.onPause()
        releaseExoplayer()
    }
}