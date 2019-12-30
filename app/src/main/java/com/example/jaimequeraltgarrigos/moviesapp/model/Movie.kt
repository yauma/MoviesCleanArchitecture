package com.example.jaimequeraltgarrigos.moviesapp.model

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(primaryKeys = ["id"])
data class Movie constructor(
    @field:SerializedName("id")
    val id: String,
    @field:SerializedName("original_title")
    val title: String?,
    @field:SerializedName("backdrop_path")
    val imageUrl: String?
) {
}