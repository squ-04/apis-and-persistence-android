package com.uniquindio.thecatapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "breeds")
data class BreedEntity(
	@PrimaryKey val id: String,
	val name: String,
	val updatedAt: Long
)
