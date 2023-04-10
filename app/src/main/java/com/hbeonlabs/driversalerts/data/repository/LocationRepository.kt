package com.hbeonlabs.driversalerts.data.repository

import com.hbeonlabs.driversalerts.data.local.db.LocationAndSpeedDao
import com.hbeonlabs.driversalerts.data.local.db.WarningsDao
import com.hbeonlabs.driversalerts.data.local.db.models.LocationAndSpeed
import com.hbeonlabs.driversalerts.data.local.db.models.Warning
import com.hbeonlabs.driversalerts.ui.fragment.notification.NotificationSubType
import com.hbeonlabs.driversalerts.utils.constants.AppConstants.OVERSPEEDING_MESSAGE
import javax.inject.Inject

class LocationRepository @Inject constructor(
    val locationDao : LocationAndSpeedDao,
    val warningsDao: WarningsDao

) {

    suspend fun addLocationData(locationAndSpeed: LocationAndSpeed)
    {
        locationDao.addData(locationAndSpeed)

    }

    suspend fun addWarnings(warning: Warning)
    {
        warningsDao.addWarning(warning)
    }

    fun getWarningsList() = warningsDao.getAllWarnings()


}