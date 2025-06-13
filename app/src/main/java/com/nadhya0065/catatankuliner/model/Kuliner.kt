package com.nadhya0065.catatankuliner.model

data class Kuliner (
    val id: String,
    val nama_makanan: String,
    val lokasi: String,
    val review: String,
    val imageId: String
)
val Kuliner.gambarUrl: String
    get() = com.nadhya0065.catatankuliner.network.KulinerApi.getKulinerUrl(this.imageId)
