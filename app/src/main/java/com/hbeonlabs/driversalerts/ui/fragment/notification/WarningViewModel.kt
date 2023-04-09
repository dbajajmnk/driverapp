package com.hbeonlabs.driversalerts.ui.fragment.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hbeonlabs.driversalerts.data.local.db.models.LocationAndSpeed
import com.hbeonlabs.driversalerts.data.local.db.models.Warning
import com.hbeonlabs.driversalerts.data.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WarningViewModel @Inject constructor(
    val repository: LocationRepository

) : ViewModel() {

    fun getWarningList() = repository.getWarningsList()

}