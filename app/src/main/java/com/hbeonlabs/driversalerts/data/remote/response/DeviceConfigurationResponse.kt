package com.hbeonlabs.driversalerts.data.remote.response

import com.google.gson.annotations.SerializedName

data class DeviceConfigurationResponse(

	@field:SerializedName("deviceType")
	val deviceType: Int? = null,

	@field:SerializedName("modelNo")
	val modelNo: String? = null,

	@field:SerializedName("uuid")
	val uuid: String? = null,

	@field:SerializedName("deviceId")
	val deviceId: String? = null,

	@field:SerializedName("serialNo")
	val serialNo: String? = null,

	@field:SerializedName("expiryDate")
	val expiryDate: String? = null,

	@field:SerializedName("Vehicle")
	val vehicle: Vehicle? = null,

	@field:SerializedName("licenseKey")
	val licenseKey: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("School")
	val school: School? = null,

	@field:SerializedName("schoolId")
	val schoolId: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("vehicleId")
	val vehicleId: Int? = null,

	@field:SerializedName("startDate")
	val startDate: String? = null,

	@field:SerializedName("status")
	val status: Int? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null,

	@field:SerializedName("bluetoothId")
	val bluetoothId: String? = null
)

data class SupportedVehicles(
	val any: Any? = null
)

data class RouteStopsItem(

	@field:SerializedName("departureTime")
	val departureTime: String? = null,

	@field:SerializedName("arrivalTime")
	val arrivalTime: String? = null,

	@field:SerializedName("arrivalOrder")
	val arrivalOrder: Int? = null,

	@field:SerializedName("point")
	val point: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class Route(

	@field:SerializedName("endPoint")
	val endPoint: String? = null,

	@field:SerializedName("Students")
	val students: List<StudentsItem?>? = null,

	@field:SerializedName("startPoint")
	val startPoint: String? = null,

	@field:SerializedName("RouteStops")
	val routeStops: List<RouteStopsItem?>? = null,

	@field:SerializedName("supportedVehicles")
	val supportedVehicles: SupportedVehicles? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class Vehicle(

	@field:SerializedName("occurrences")
	val occurrences: Int? = null,

	@field:SerializedName("helperDetails")
	val helperDetails: HelperDetails? = null,

	@field:SerializedName("driverDetails")
	val driverDetails: DriverDetails? = null,

	@field:SerializedName("registrationNo")
	val registrationNo: String? = null,

	@field:SerializedName("startTime")
	val startTime: String? = null,

	@field:SerializedName("endTime")
	val endTime: String? = null,

	@field:SerializedName("Route")
	val route: Route? = null
)

data class StudentsItem(

	@field:SerializedName("standard")
	val standard: String? = null,

	@field:SerializedName("Addresses")
	val addresses: List<AddressesItem?>? = null,

	@field:SerializedName("mother")
	val mother: String? = null,

	@field:SerializedName("dob")
	val dob: String? = null,

	@field:SerializedName("father")
	val father: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("rollNo")
	val rollNo: String? = null
)

data class AddressesItem(

	@field:SerializedName("pincode")
	val pincode: Int? = null,

	@field:SerializedName("address2")
	val address2: Any? = null,

	@field:SerializedName("city")
	val city: String? = null,

	@field:SerializedName("address1")
	val address1: String? = null,

	@field:SerializedName("state")
	val state: String? = null,

	@field:SerializedName("type")
	val type: Int? = null
)

data class DriverDetails(

	@field:SerializedName("licenseNo")
	val licenseNo: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("aadhar")
	val aadhar: String? = null,

	@field:SerializedName("experience")
	val experience: String? = null
)

data class School(

	@field:SerializedName("devices")
	val devices: Int? = null,

	@field:SerializedName("registrationNo")
	val registrationNo: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("vehicles")
	val vehicles: Int? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class HelperDetails(

	@field:SerializedName("licenseNo")
	val licenseNo: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("aadhar")
	val aadhar: String? = null,

	@field:SerializedName("experience")
	val experience: String? = null
)
