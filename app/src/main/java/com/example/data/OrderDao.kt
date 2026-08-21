package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM generated_orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<GeneratedOrder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: GeneratedOrder)

    @Update
    suspend fun updateOrder(order: GeneratedOrder)

    @Query("DELETE FROM generated_orders WHERE id = :id")
    suspend fun deleteOrderById(id: Int)
}
