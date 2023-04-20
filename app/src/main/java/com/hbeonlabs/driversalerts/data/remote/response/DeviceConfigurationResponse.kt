package com.hbeonlabs.driversalerts.data.remote.response

import com.google.gson.annotations.SerializedName

data class DeviceConfigurationResponse(

	@field:SerializedName("licenceKey")
	val licenceKey: String? = null,

	@field:SerializedName("modelNo")
	val modelNo: String? = null,

	@field:SerializedName("deviceId")
	val deviceId: String? = null,

	@field:SerializedName("serialNo")
	val serialNo: String? = null,

	@field:SerializedName("expiryDate")
	val expiryDate: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("schoolId")
	val schoolId: Int? = null,

	@field:SerializedName("vehicleId")
	val vehicleId: Int? = null,

	@field:SerializedName("tbl_vehicle")
	val tblVehicle: TblVehicle? = null,

	@field:SerializedName("tbl_type")
	val tblType: TblType? = null,

	@field:SerializedName("startDate")
	val startDate: String? = null,

	@field:SerializedName("status")
	val status: Int? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)

data class TblAddress(

	@field:SerializedName("pincode")
	val pincode: Int? = null,

	@field:SerializedName("address")
	val address: String? = null,

	@field:SerializedName("city")
	val city: String? = null,

	@field:SerializedName("contact")
	val contact: String? = null,

	@field:SerializedName("state")
	val state: String? = null,

	@field:SerializedName("type")
	val type: String? = null
)

data class TblTypeMasters(

	@field:SerializedName("type")
	val type: String? = null
)

data class TblRoute(

	@field:SerializedName("endPoint")
	val endPoint: String? = null,

	@field:SerializedName("startPoint")
	val startPoint: String? = null,

	@field:SerializedName("tbl_route_stops")
	val tblRouteStops: List<TblRouteStopsItem?>? = null,

	@field:SerializedName("supportedVehicles")
	val supportedVehicles: List<String?>? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class TblType(

	@field:SerializedName("type")
	val type: String? = null
)

data class TblDocumentsItem(

	@field:SerializedName("path")
	val path: String? = null,

	@field:SerializedName("size")
	val size: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("type")
	val type: String? = null
)

data class TblStaffItem(

	@field:SerializedName("gender")
	val gender: String? = null,

	@field:SerializedName("tbl_type_masters")
	val tblTypeMasters: TblTypeMasters? = null,

	@field:SerializedName("tbl_address")
	val tblAddress: TblAddress? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("aadhar")
	val aadhar: String? = null,

	@field:SerializedName("experiance")
	val experiance: String? = null,

	@field:SerializedName("licenceNo")
	val licenceNo: String? = null,

	@field:SerializedName("age")
	val age: Int? = null,

	@field:SerializedName("tbl_documents")
	val tblDocuments: List<TblDocumentsItem?>? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class TblStudentsItem(

	@field:SerializedName("gender")
	val gender: String? = null,

	@field:SerializedName("dob")
	val dob: String? = null,

	@field:SerializedName("father")
	val father: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("monther")
	val monther: String? = null,

	@field:SerializedName("rollNo")
	val rollNo: Int? = null,

	@field:SerializedName("class")
	val jsonMemberClass: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class Owner(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("aadhar")
	val aadhar: String? = null
)

data class TblVehicle(

	@field:SerializedName("owner")
	val owner: Owner? = null,

	@field:SerializedName("tbl_staff")
	val tblStaff: List<TblStaffItem?>? = null,

	@field:SerializedName("tbl_route")
	val tblRoute: TblRoute? = null,

	@field:SerializedName("registrationNo")
	val registrationNo: String? = null,

	@field:SerializedName("pollutionCertificateNo")
	val pollutionCertificateNo: String? = null,

	@field:SerializedName("occurances")
	val occurances: Int? = null,

	@field:SerializedName("tbl_students")
	val tblStudents: List<TblStudentsItem?>? = null,

	@field:SerializedName("fitnessCertificateNo")
	val fitnessCertificateNo: String? = null,

	@field:SerializedName("startTime")
	val startTime: String? = null,

	@field:SerializedName("endTime")
	val endTime: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class TblRouteStopsItem(

	@field:SerializedName("departureTime")
	val departureTime: String? = null,

	@field:SerializedName("arrivalTime")
	val arrivalTime: String? = null,

	@field:SerializedName("arrivalOrder")
	val arrivalOrder: Int? = null,

	@field:SerializedName("point")
	val point: String? = null
)
