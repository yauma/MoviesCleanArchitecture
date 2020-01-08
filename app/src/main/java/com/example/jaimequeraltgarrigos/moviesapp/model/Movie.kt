package com.example.jaimequeraltgarrigos.moviesapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.sql.Types.ROWID

@Entity()
data class Movie constructor(
    @field:SerializedName("id")
    val id: Int = 0,
    @field:SerializedName("original_title")
    val title: String?,
    @field:SerializedName("backdrop_path")
    val imageUrl: String?,
    @PrimaryKey(autoGenerate = true)
    var generatedId: Int = 0
) {
}