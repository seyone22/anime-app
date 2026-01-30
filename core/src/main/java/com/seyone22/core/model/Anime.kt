package com.seyone22.core.model

data class Anime(
    val id: Int,
    val title: String,
    val coverUrl: String,
    val rating: Int,
    val description: String = "",
    val season: String = "",
    val seasonYear: String = ""
)