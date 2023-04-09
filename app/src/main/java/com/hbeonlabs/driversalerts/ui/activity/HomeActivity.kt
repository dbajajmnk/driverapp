package com.hbeonlabs.driversalerts.ui.activity

import android.Manifest
import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.os.Build
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
import com.hbeonlabs.driversalerts.utils.DailyAlarmReceiver
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




    fun setADailyAlarm()
    {
        val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Intent part
        val intent = Intent(applicationContext, DailyAlarmReceiver::class.java)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(applicationContext,
                1212121, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getBroadcast(
                applicationContext,
                1212121, intent, PendingIntent.FLAG_IMMUTABLE
            )
        }
        val calander = Calendar.getInstance()
        calander.set(Calendar.HOUR_OF_DAY,8)
        calander.set(Calendar.MINUTE,0)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calander.timeInMillis,
            AlarmManager.INTERVAL_DAY ,
            pendingIntent)
    }

}