package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.math.ceil
import kotlin.math.max

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val barcode: String,
    val name: String,
    val category: String,
    val supplierName: String,
    val supplierPhone: String = "+998 90 123 45 67",
    val currentStock: Int,
    val unit: String,
    val unitPrice: Double,
    val dailySalesRate: Double,
    val leadTimeDays: Int,
    val safetyStock: Int,
    val minOrderBatch: Int,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    val reorderPoint: Int
        get() = ceil((dailySalesRate * leadTimeDays) + safetyStock).toInt()

    val isReorderNeeded: Boolean
        get() = currentStock <= reorderPoint

    val isOutOfStock: Boolean
        get() = currentStock <= 0

    val daysRemaining: Double
        get() = if (dailySalesRate > 0) currentStock / dailySalesRate else 999.0

    fun calculateSuggestedOrderQty(reviewPeriodDays: Int = 7): Int {
        if (!isReorderNeeded) return 0
        val targetStock = (dailySalesRate * (leadTimeDays + reviewPeriodDays)) + safetyStock
        val deficit = targetStock - currentStock
        val rawQty = ceil(max(0.0, deficit)).toInt()
        return max(minOrderBatch, rawQty)
    }
}
