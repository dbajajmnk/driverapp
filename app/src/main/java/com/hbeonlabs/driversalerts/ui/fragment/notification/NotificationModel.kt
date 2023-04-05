package com.hbeonlabs.driversalerts.ui.fragment.notification

data class NotificationModel(val notificationType: NotificationType,val notificationSubType: NotificationSubType, val title:String, val subtitle:String, val datetime:String )


enum class NotificationType{
    LOG,WARNING
}

enum class NotificationSubType{
    RASHDRIVING,OVERSPEEDING,SEATBELT,DROWSNISS
}

object NotificationData{
    val notificationList = mutableListOf<NotificationModel>()

    fun log():List<NotificationModel>{
        val obj = NotificationModel(NotificationType.LOG, NotificationSubType.OVERSPEEDING, "Over speeding", "r","r")
        val obj2 = NotificationModel(NotificationType.LOG, NotificationSubType.OVERSPEEDING, "Over speeding", "r","r")
        notificationList.add(obj);
        notificationList.add(obj2);
        return  notificationList;
    }

    fun warnings():List<NotificationModel>{
        val obj = NotificationModel(NotificationType.WARNING, NotificationSubType.OVERSPEEDING, "Over speeding", "r","r")
        val obj2 = NotificationModel(NotificationType.LOG, NotificationSubType.OVERSPEEDING, "Over speeding", "r","r")
        notificationList.add(obj);
        notificationList.add(obj2);
        return  notificationList;
    }

}