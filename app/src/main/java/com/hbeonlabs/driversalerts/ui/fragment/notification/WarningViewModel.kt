package com.hbeonlabs.driversalerts.ui.fragment.notification

import androidx.lifecycle.ViewModel
import com.hbeonlabs.driversalerts.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WarningViewModel @Inject constructor(
    private val repository: AppRepository

) : ViewModel() {

    fun getWarningList() = repository.getWarningsList()

}