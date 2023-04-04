package com.hbeonlabs.driversalerts.ui.activity

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.data.local.db.LocationAndSpeedDao
import com.hbeonlabs.driversalerts.databinding.FragmentHomeBinding
import com.hbeonlabs.driversalerts.ui.fragment.dialogs.dialogDrowsinessAlert
import com.hbeonlabs.driversalerts.utils.DriverLocationProvider
import com.hbeonlabs.driversalerts.utils.constants.AppConstants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() , View.OnClickListener{

    @Inject
    lateinit var speedDao: LocationAndSpeedDao

    lateinit var binding: FragmentHomeBinding
    lateinit var locationProvider : DriverLocationProvider
    lateinit var dialog : Dialog

    private var locationPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialog =  dialogDrowsinessAlert(headerText = "Alert!", "You have crossed the Speed Limit")
        //askLocationPermission()
        binding = DataBindingUtil.setContentView(this, R.layout.fragment_home)
        binding.btnDriver.setOnClickListener(this)
        binding.btnAdmin.setOnClickListener(this)

        if (checkPermissions())
        {
            doOnLocationPermissionAvailable()
        }
        else{
            requestPermissions()
        }

        lifecycleScope.launchWhenStarted {
          speedDao.getAllCommunityChat().collectLatest {
              Log.d("TAG", "List: "+it.toString())
          }
        }
        }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_driver->{
                startActivity(Intent(this, MainActivity::class.java))
            }
            R.id.btn_admin->{
                startActivity(Intent(this, AdminActivity::class.java))
            }
        }
    }


        private fun checkPermissions(): Boolean {
            return checkSelfPermission(
                 Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(
                 Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }

        // request for permissions
        private fun requestPermissions() {
            permissionRequest.launch(locationPermissions)
        }

        // Permission result
        private val permissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all {
                it.value
            }
            permissions.entries.forEach {
                Log.e("LOG_TAG", "${it.key} = ${it.value}")
            }
            if (granted) {
                // your code if permission granted
                doOnLocationPermissionAvailable()
            } else {
                // your code if permission denied
                requestPermissions()
            }
    }

    private fun doOnLocationPermissionAvailable() {
        locationProvider = DriverLocationProvider(this){
            Log.d("TAG", "doOnLocationPermissionAvailable: "+it.toString())
            lifecycleScope.launchWhenStarted {
                speedDao.addData(it)
            }
        }
        locationProvider.calculateAccelerationWithinThreshold()
        lifecycleScope.launchWhenStarted {
            locationProvider.speedEvent.collectLatest {
                binding.tvSpeed.text = " Car Speed $it"
                if (it>50f)
                {
                  dialog.show()
                }
                else{
                    dialog.dismiss()
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            locationProvider.accelerationEvent.collect {
                binding.tvAcceleration.text = "Acceleration $it"
            }
        }
    }

}