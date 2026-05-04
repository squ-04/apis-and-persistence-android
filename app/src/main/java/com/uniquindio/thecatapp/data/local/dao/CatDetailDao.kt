package com.uniquindio.thecatapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.uniquindio.thecatapp.data.local.entity.CatDetailEntity

@Dao
interface CatDetailDao {

    @Query("SELECT * FROM cat_detail WHERE catId = :catId LIMIT 1")
    suspend fun getById(catId: String): CatDetailEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: CatDetailEntity)
}

