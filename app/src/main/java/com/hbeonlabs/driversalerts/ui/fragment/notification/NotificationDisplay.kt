package com.hbeonlabs.driversalerts.ui.fragment.notification

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.databinding.FragmentNotificationDisplayWithMapBinding
import com.hbeonlabs.driversalerts.databinding.FragmentWarningsBinding
import com.hbeonlabs.driversalerts.ui.base.BaseFragment
import com.hbeonlabs.driversalerts.utils.constants.AppConstants
import com.hbeonlabs.driversalerts.utils.makeToast
import com.hbeonlabs.driversalerts.utils.snackBar
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class NotificationDisplay : BaseFragment<FragmentNotificationDisplayWithMapBinding>(),
    OnMapReadyCallback {

    val args by navArgs<NotificationDisplayArgs>()
    private lateinit var mMap : GoogleMap
    lateinit var mapFragment : SupportMapFragment

    override fun initView() {
        super.initView()
        val data = args.locationData

        snackBar("${data.locationLongitude}")
        mapFragment = childFragmentManager.findFragmentById(R.id.route_map) as SupportMapFragment

        mapFragment.getMapAsync(this)


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

    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_notification_display_with_map

    }

    override fun onMapReady(map: GoogleMap) {
        mMap = map
        Log.d("TAG", "onMapReady: ${args.locationData.locationLatitude.toDouble()}  ${args.locationData.locationLongitude.toDouble()} ")
        val originLocation = LatLng(args.locationData.locationLatitude.toDouble(), args.locationData.locationLongitude.toDouble())
        mMap.addMarker(MarkerOptions().position(originLocation))

        val cameraPosition = CameraPosition.Builder()
            .target(originLocation)
            .zoom(14f)
            .build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

}