package com.hbeonlabs.driversalerts.ui.fragment.notification

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hbeonlabs.driversalerts.data.local.db.models.Warning
import com.hbeonlabs.driversalerts.data.mappers.toNotification
import com.hbeonlabs.driversalerts.data.repository.AppRepository
import com.hbeonlabs.driversalerts.utils.network.onError
import com.hbeonlabs.driversalerts.utils.network.onException
import com.hbeonlabs.driversalerts.utils.network.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WarningViewModel @Inject constructor(
    private val repository: AppRepository

) : ViewModel() {

    private val _notificationEvent = MutableSharedFlow<NotificationEvents>()
    val notificationEvent: SharedFlow<NotificationEvents> = _notificationEvent
    fun getWarningList() = repository.getNotificationList()

    fun getAllNotificationsFromApi (){
        viewModelScope.launch {
            repository.getAllNotificationsFromApi().onSuccess {
                Log.d("TAG", "getAllNotificationsFromApi: "+it)
                _notificationEvent.emit(NotificationEvents.LoadingEvent(false))
                val list = arrayListOf<Warning>()

                it.forEach {notificationResponseItem ->
                  list.add(  notificationResponseItem.toNotification())
                }
                _notificationEvent.emit(NotificationEvents.NotificationListEvents(list))
            }.onError { code, message ->
                _notificationEvent.emit(NotificationEvents.LoadingEvent(false))
                _notificationEvent.emit(NotificationEvents.ErrorEvent(message?:"Some Error Occured"))

            }.onException {
                _notificationEvent.emit(NotificationEvents.LoadingEvent(false))
                _notificationEvent.emit(NotificationEvents.ErrorEvent(it.localizedMessage?:"Some Error Occured"))
            }
        }
    }

    sealed class NotificationEvents{
        class ErrorEvent(val message:String) : NotificationEvents()
        class LoadingEvent(val isLoading:Boolean) : NotificationEvents()
        class NotificationListEvents(val notifications:List<Warning>) :NotificationEvents()

    }

}