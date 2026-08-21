package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlin.math.roundToInt

class InventoryRepository(
    private val productDao: ProductDao,
    private val orderDao: OrderDao
) {
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()
    val allOrders: Flow<List<GeneratedOrder>> = orderDao.getAllOrders()

    suspend fun ensureSampleDataLoaded() {
        if (productDao.getProductCount() == 0) {
            val defaultCatalog = createDefaultCatalog()
            productDao.insertProducts(defaultCatalog)
        }
    }

    suspend fun addProduct(product: Product) {
        productDao.insertProduct(product)
    }

    suspend fun updateProduct(product: Product) {
        productDao.updateProduct(product)
    }

    suspend fun deleteProduct(product: Product) {
        productDao.deleteProduct(product)
    }

    suspend fun saveOrder(order: GeneratedOrder) {
        orderDao.insertOrder(order)
    }

    suspend fun updateOrder(order: GeneratedOrder) {
        orderDao.updateOrder(order)
    }

    suspend fun deleteOrder(orderId: Int) {
        orderDao.deleteOrderById(orderId)
    }

    suspend fun simulateOneDaySales() {
        val products = allProducts.first()
        val updated = products.map { p ->
            val soldToday = p.dailySalesRate.roundToInt()
            val newStock = (p.currentStock - soldToday).coerceAtLeast(0)
            p.copy(currentStock = newStock, lastUpdated = System.currentTimeMillis())
        }
        productDao.insertProducts(updated)
    }

    suspend fun fulfillSupplierOrders(supplierName: String) {
        val products = allProducts.first().filter { it.supplierName == supplierName }
        val updated = products.map { p ->
            if (p.isReorderNeeded) {
                val qty = p.calculateSuggestedOrderQty()
                p.copy(currentStock = p.currentStock + qty, lastUpdated = System.currentTimeMillis())
            } else p
        }
        productDao.insertProducts(updated)
    }

    fun calculateSupplierRecommendations(products: List<Product>): List<SupplierGroup> {
        val reorderItems = products.filter { it.isReorderNeeded }
        val grouped = reorderItems.groupBy { it.supplierName }

        return grouped.map { (supplierName, supplierProducts) ->
            val supplierPhone = supplierProducts.firstOrNull()?.supplierPhone ?: "+998 90 123 45 67"
            val recs = supplierProducts.map { prod ->
                val suggestedQty = prod.calculateSuggestedOrderQty()
                val estCost = suggestedQty * prod.unitPrice
                val reason = when {
                    prod.isOutOfStock -> "⚠️ Mutlaqo tugagan (0 ${prod.unit})! Zudlik bilan buyurtma berish shart."
                    prod.daysRemaining < 2.0 -> "🔥 Zaxira 2 kundan oz qoldi (${String.format("%.1f", prod.daysRemaining)} kun), kunlik sotuv: ${prod.dailySalesRate} ${prod.unit}."
                    else -> "📌 Kritik nuqtaga yetdi (${prod.currentStock} ${prod.unit} <= ROP ${prod.reorderPoint})."
                }
                ProductOrderRecommendation(
                    product = prod,
                    suggestedQty = suggestedQty,
                    estimatedCost = estCost,
                    reason = reason
                )
            }
            SupplierGroup(
                supplierName = supplierName,
                supplierPhone = supplierPhone,
                items = recs,
                totalEstimatedCost = recs.sumOf { it.estimatedCost }
            )
        }.sortedByDescending { it.items.size }
    }

    fun generateTelegramMessageForSupplier(group: SupplierGroup): String {
        val sb = StringBuilder()
        sb.append("🛒 *TURKISTAN MARKET* - MAHSULOT BUYURTMASI\n")
        sb.append("🏢 Ta'minotchi: *${group.supplierName}*\n")
        sb.append("📞 Tel: ${group.supplierPhone}\n")
        sb.append("📅 Sana: ${java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date())}\n")
        sb.append("────────────────────────\n")
        sb.append("🤖 *AI Agent tomonidan hisoblangan buyurtma:*\n\n")

        group.items.forEachIndexed { index, rec ->
            val p = rec.product
            val formattedCost = String.format("%,.0f", rec.estimatedCost).replace(',', ' ')
            sb.append("${index + 1}. *${p.name}*\n")
            sb.append("   • Hozirgi qoldiq: ${p.currentStock} ${p.unit}\n")
            sb.append("   • Kunlik sotuv tezligi: ${p.dailySalesRate} ${p.unit}/kun\n")
            sb.append("   • *BUYURTMA MIKDORI: ${rec.suggestedQty} ${p.unit}*\n")
            sb.append("   • Taxminiy summa: ${formattedCost} so'm\n\n")
        }

        val totalCostFormatted = String.format("%,.0f", group.totalEstimatedCost).replace(',', ' ')
        sb.append("────────────────────────\n")
        sb.append("💰 *JAMI BUYURTMA SUMMASI:* ${totalCostFormatted} so'm\n")
        sb.append("⚡ AI Agent: Hisobot xatosiz shakllantirildi.")
        return sb.toString()
    }

    fun generateCsvExportForSupplier(group: SupplierGroup): String {
        val sb = StringBuilder()
        sb.append("Tartib;Shtrixkod;Mahsulot Nomi;Kategoriya;Hozirgi Qoldiq;Kunlik Sotuv;Zaxira Kunlari;Buyurtma Miqdori;O'lchov Birligi;Birlik Narxi (So'm);Jami Summa (So'm)\n")
        group.items.forEachIndexed { index, rec ->
            val p = rec.product
            sb.append("${index + 1};${p.barcode};${p.name};${p.category};${p.currentStock};${p.dailySalesRate};${String.format("%.1f", p.daysRemaining)};${rec.suggestedQty};${p.unit};${p.unitPrice};${rec.estimatedCost}\n")
        }
        return sb.toString()
    }

    private fun createDefaultCatalog(): List<Product> {
        return listOf(
            Product(
                barcode = "478000100101",
                name = "Nestle Sut 1L 3.2%",
                category = "Sut mahsulotlari",
                supplierName = "Nestle Uzbekistan",
                supplierPhone = "+998 90 123 45 67",
                currentStock = 8,
                unit = "dona",
                unitPrice = 14500.0,
                dailySalesRate = 22.0,
                leadTimeDays = 2,
                safetyStock = 20,
                minOrderBatch = 36
            ),
            Product(
                barcode = "478000100102",
                name = "President Sariyog' 82% 200g",
                category = "Sut mahsulotlari",
                supplierName = "President Dairy",
                supplierPhone = "+998 91 234 56 78",
                currentStock = 3,
                unit = "dona",
                unitPrice = 28000.0,
                dailySalesRate = 12.0,
                leadTimeDays = 3,
                safetyStock = 15,
                minOrderBatch = 20
            ),
            Product(
                barcode = "478000200201",
                name = "Coca-Cola Classic 1.5L",
                category = "Ichimliklar",
                supplierName = "Coca-Cola Bottlers Tashkent",
                supplierPhone = "+998 71 200 11 22",
                currentStock = 14,
                unit = "dona",
                unitPrice = 12500.0,
                dailySalesRate = 45.0,
                leadTimeDays = 2,
                safetyStock = 40,
                minOrderBatch = 60
            ),
            Product(
                barcode = "478000200202",
                name = "Fanta Orange 1.5L",
                category = "Ichimliklar",
                supplierName = "Coca-Cola Bottlers Tashkent",
                supplierPhone = "+998 71 200 11 22",
                currentStock = 0,
                unit = "dona",
                unitPrice = 12500.0,
                dailySalesRate = 28.0,
                leadTimeDays = 2,
                safetyStock = 30,
                minOrderBatch = 48
            ),
            Product(
                barcode = "478000300301",
                name = "Tashkent Un 1-Nav 5kg",
                category = "Baqqollik / Un",
                supplierName = "Tashkent Grain Products",
                supplierPhone = "+998 93 345 67 89",
                currentStock = 5,
                unit = "qop",
                unitPrice = 38000.0,
                dailySalesRate = 8.0,
                leadTimeDays = 4,
                safetyStock = 10,
                minOrderBatch = 15
            ),
            Product(
                barcode = "478000300302",
                name = "Baraka Guruch Alanga 1kg",
                category = "Baqqollik / Un",
                supplierName = "Baraka Wholesale LLC",
                supplierPhone = "+998 94 456 78 90",
                currentStock = 2,
                unit = "dona",
                unitPrice = 22000.0,
                dailySalesRate = 18.0,
                leadTimeDays = 3,
                safetyStock = 20,
                minOrderBatch = 30
            ),
            Product(
                barcode = "478000400401",
                name = "Oltin Kalit O'simlik Yog'i 1L",
                category = "Yog'-moy",
                supplierName = "Baraka Wholesale LLC",
                supplierPhone = "+998 94 456 78 90",
                currentStock = 18,
                unit = "dona",
                unitPrice = 17500.0,
                dailySalesRate = 25.0,
                leadTimeDays = 2,
                safetyStock = 25,
                minOrderBatch = 40
            ),
            Product(
                barcode = "478000500501",
                name = "Lays Chips Smetana & Ko'kat 140g",
                category = "Shirinliklar",
                supplierName = "UzEko Foods",
                supplierPhone = "+998 95 567 89 01",
                currentStock = 12,
                unit = "dona",
                unitPrice = 16000.0,
                dailySalesRate = 30.0,
                leadTimeDays = 3,
                safetyStock = 25,
                minOrderBatch = 50
            ),
            Product(
                barcode = "478000500502",
                name = "Alpen Gold Shokolad 85g",
                category = "Shirinliklar",
                supplierName = "UzEko Foods",
                supplierPhone = "+998 95 567 89 01",
                currentStock = 65,
                unit = "dona",
                unitPrice = 13000.0,
                dailySalesRate = 14.0,
                leadTimeDays = 3,
                safetyStock = 20,
                minOrderBatch = 30
            ),
            Product(
                barcode = "478000600601",
                name = "Moyil Suyuq Sovun 500ml",
                category = "Xo'jalik mollari",
                supplierName = "Clean Care Uz",
                supplierPhone = "+998 97 678 90 12",
                currentStock = 6,
                unit = "dona",
                unitPrice = 18500.0,
                dailySalesRate = 9.0,
                leadTimeDays = 5,
                safetyStock = 12,
                minOrderBatch = 24
            ),
            Product(
                barcode = "478000700701",
                name = "Bonduelle Makkajo'xori 340g",
                category = "Konserva",
                supplierName = "UzEko Foods",
                supplierPhone = "+998 95 567 89 01",
                currentStock = 4,
                unit = "banka",
                unitPrice = 19000.0,
                dailySalesRate = 11.0,
                leadTimeDays = 3,
                safetyStock = 15,
                minOrderBatch = 24
            ),
            Product(
                barcode = "478000100103",
                name = "Musaffo Qatiq 1L 2.5%",
                category = "Sut mahsulotlari",
                supplierName = "President Dairy",
                supplierPhone = "+998 91 234 56 78",
                currentStock = 90,
                unit = "dona",
                unitPrice = 11000.0,
                dailySalesRate = 15.0,
                leadTimeDays = 2,
                safetyStock = 20,
                minOrderBatch = 30
            )
        )
    }
}
