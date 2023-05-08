package com.hbeonlabs.driversalerts.data.remote.response

import com.google.gson.annotations.SerializedName

data class AttendanceListResponse(

    @field:SerializedName("AttendanceListResponse")
    val attendanceListResponse: List<AttendanceListResponseItem?>? = null
)

data class TblStudent(

    @field:SerializedName("mother")
    val mother: String? = null,

    @field:SerializedName("gender")
    val gender: String? = null,

    @field:SerializedName("dob")
    val dob: String? = null,

    @field:SerializedName("father")
    val father: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("rollNo")
    val rollNo: Int? = null,

    @field:SerializedName("class")
    val jsonMemberClass: String? = null,

    @field:SerializedName("status")
    val status: Int? = null
)

data class AttendanceListResponseItem(

    @field:SerializedName("studentId")
    val studentId: Int? = null,

    @field:SerializedName("date")
    val date: String? = null,

    @field:SerializedName("inTime")
    val inTime: String? = null,

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("tbl_student")
    val tblStudent: TblStudent? = null,

    @field:SerializedName("isPresent")
    val isPresent: Boolean? = null,

    @field:SerializedName("outTime")
    val outTime: String? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("updatedAt")
    val updatedAt: String? = null
)