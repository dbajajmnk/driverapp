package com.hbeonlabs.driversalerts.ui.activity.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.data.local.persistance.PrefManager
import com.hbeonlabs.driversalerts.databinding.ActivitySettingsBinding


class SettingsActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var prefManager: PrefManager
    private var soundCounter = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        prefManager = PrefManager(this)
        handleDuration()
        handleSound()
        handleCamera()
        handleAutoStart()
        handleLocationFrequency()
    }

    private fun handleDuration() {
        val durations = listOf("0.5 Second", "1 Second", "2 Second", "5 Second", "10 Second")
        val adapter = ArrayAdapter(this, R.layout.spinner_item, durations)
        binding.durationSp.adapter = adapter
        binding.durationSp.onItemSelectedListener = this
        binding.durationSp.setSelection(getPositionForDuration(prefManager.getDuration()))
    }

    private fun handleSound() {
        val items = listOf("App Sound", "System Sound")
        val adapter = ArrayAdapter(this, R.layout.spinner_item, items)
        binding.soundSp.adapter = adapter
        binding.soundSp.onItemSelectedListener = this
        val selectedPos = if (prefManager.getAudioUri() == null) 0 else 1
        binding.soundSp.setSelection(selectedPos)
    }

    private fun handleCamera() {
        val items = listOf("Front Camera", "Back Camera")
        val adapter = ArrayAdapter(this, R.layout.spinner_item, items)
        binding.cameraSp.adapter = adapter
        binding.cameraSp.onItemSelectedListener = this
        binding.cameraSp.setSelection(prefManager.getCameraSelected())
    }

    private fun handleAutoStart() {
        val items = listOf("Enabled", "Disabled")
        val adapter = ArrayAdapter(this, R.layout.spinner_item, items)
        binding.rebootSp.adapter = adapter
        binding.rebootSp.onItemSelectedListener = this
        val pos = if (prefManager.getAutoStartEnabled()) 0 else 1
        binding.rebootSp.setSelection(pos)
    }

    private fun handleLocationFrequency() {
        val items =
            listOf("5 second", "10 seconds", "15 second", "30 seconds", "60 seconds", "2 minutes")
        val adapter = ArrayAdapter(this, R.layout.spinner_item, items)
        binding.locationFrequencySp.adapter = adapter
        binding.locationFrequencySp.onItemSelectedListener = this
        val pos = getPositionForFrequency(prefManager.getLocationFrequency())
        binding.locationFrequencySp.setSelection(pos)
    }

    private fun openRingtonePicker() {
        val audioIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(Intent.createChooser(audioIntent, "Select Audio"), 1)
    }

    override fun onItemSelected(adapterView: AdapterView<*>?, spinner: View?, pos: Int, p3: Long) {
        if (adapterView?.id == binding.durationSp.id) {
            prefManager.saveDuration(getDurationPerPosition(pos))
        } else if (adapterView?.id == binding.soundSp.id) {
            if (soundCounter == 0) {
                soundCounter = 1
                return
            }
            if (pos == 1) {
                openRingtonePicker()
            } else {
                prefManager.saveAudioUri(null)
            }
        } else if (adapterView?.id == binding.cameraSp.id) {
            prefManager.saveCameraSelected(pos)
        } else if (adapterView?.id == binding.rebootSp.id) {
            prefManager.saveAutoStartEnabled(pos == 0)
        } else if (adapterView?.id == binding.locationFrequencySp.id) {
            prefManager.saveLocationFrequency(getFequencyByPosition(pos))
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                //the selected audio.
                val uri: Uri? = data?.data
                if (uri != null) {
                    prefManager.saveAudioUri(uri)

                }
            }
        }
    }

    private fun getDurationPerPosition(pos: Int): Int {
        return when (pos) {
            0 ->
                500
            1 ->
                1000
            2 ->
                2000
            3 ->
                5000
            4 ->
                10000
            else ->
                500
        }
    }


    private fun getPositionForDuration(duration: Int): Int {
        return when (duration) {
            500 ->
                0
            1000 ->
                1
            2000 ->
                2
            5000 ->
                3
            10000 ->
                4
            else ->
                0
        }
    }


    private fun getFequencyByPosition(pos: Int): Int {
        return when (pos) {
            0 ->
                5000
            1 ->
                10000
            2 ->
                15000
            3 ->
                30000
            4 ->
                60000
            5 ->
                120000
            else ->
                5000
        }
    }


    private fun getPositionForFrequency(duration: Int): Int {
        return when (duration) {
            5000 ->
                0
            10000 ->
                1
            15000 ->
                2
            30000 ->
                3
            60000 ->
                4
            120000 ->
                5
            else ->
                0
        }
    }
}