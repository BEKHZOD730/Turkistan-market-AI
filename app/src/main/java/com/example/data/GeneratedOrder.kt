package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "generated_orders")
data class GeneratedOrder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val orderCode: String,
    val supplierName: String,
    val supplierPhone: String,
    val createdAt: Long = System.currentTimeMillis(),
    val totalItemsCount: Int,
    val totalEstimatedCost: Double,
    val status: String,
    val summaryText: String,
    val itemsFormattedJson: String
)
