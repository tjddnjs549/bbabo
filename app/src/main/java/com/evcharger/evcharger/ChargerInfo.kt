package com.evcharger.evcharger


import com.squareup.moshi.Json

data class ChargerInfo(
    @field:Json(name = "currentCount")
    val currentCount: Int?,
    @field:Json(name = "data")
    val `data`: List<Data?>?,
    @field:Json(name = "matchCount")
    val matchCount: Int?,
    @field:Json(name = "page")
    val page: Int?,
    @field:Json(name = "perPage")
    val perPage: Int?,
    @field:Json(name = "totalCount")
    val totalCount: Int?
)

data class Data(
    @field:Json(name = "addr")
    val addr: String?,
    @field:Json(name = "chargeTp")
    val chargeTp: String?,
    @field:Json(name = "cpId")
    val cpId: Int?,
    @field:Json(name = "cpNm")
    val cpNm: String?,
    @field:Json(name = "cpStat")
    val cpStat: String?,
    @field:Json(name = "cpTp")
    val cpTp: String?,
    @field:Json(name = "csId")
    val csId: Int?,
    @field:Json(name = "csNm")
    val csNm: String?,
    @field:Json(name = "lat")
    val lat: String?,
    @field:Json(name = "longi")
    val longi: String?,
    @field:Json(name = "statUpdatetime")
    val statUpdatetime: String?
)