package com.example.data

data class SupplierGroup(
    val supplierName: String,
    val supplierPhone: String,
    val items: List<ProductOrderRecommendation>,
    val totalEstimatedCost: Double
)

data class ProductOrderRecommendation(
    val product: Product,
    val suggestedQty: Int,
    val estimatedCost: Double,
    val reason: String
)
