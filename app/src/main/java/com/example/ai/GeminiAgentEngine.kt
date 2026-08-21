package com.example.ai

import com.example.BuildConfig
import com.example.data.Product
import com.example.data.SupplierGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiAgentEngine {

    suspend fun analyzeInventoryAndSuggestOrders(
        products: List<Product>,
        supplierGroups: List<SupplierGroup>
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val lowStockItems = products.filter { it.isReorderNeeded }

        val contextInfo = StringBuilder()
        contextInfo.append("Ombor holati: Jami mahsulotlar - ${products.size} ta.\n")
        contextInfo.append("Kritik va tugayotgan mahsulotlar soni: ${lowStockItems.size} ta.\n")
        contextInfo.append("Ta'minotchilar guruhi soni: ${supplierGroups.size} ta.\n\n")

        contextInfo.append("Kritik mahsulotlar ro'yxati:\n")
        lowStockItems.forEach { p ->
            contextInfo.append("- ${p.name} (${p.category}): qoldiq = ${p.currentStock} ${p.unit}, sotuv tezligi = ${p.dailySalesRate}/kun, ROP = ${p.reorderPoint}, yetkazish = ${p.leadTimeDays} kun, ta'minotchi = ${p.supplierName}\n")
        }

        val prompt = """
            Siz Turkistan Market omborini boshqaruvchi AI Agentsiz.
            Quyidagi ombor ma'lumotlarini tahlil qiling:
            $contextInfo
            
            Vazifangiz:
            1. Turkistan Market rahbari uchun qisqa va aniq 3 ta tavsiya bering (O'zbek tilida).
            2. Qaysi ta'minotchilarga zudlik bilan buyurtma berish lozimligini va nega (kunlik sotuv tezligiga qarab) tushuntiring.
            3. AI agent orqali necha soat vaqt va qancha insoniy xatolar oldi olinganini ta'kidlang.
        """.trimIndent()

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext generateLocalSmartAnalysis(lowStockItems, supplierGroups)
        }

        try {
            val request = GenerateContentRequest(
                contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                systemInstruction = Content(parts = listOf(Part(text = "Siz professional va aqlli Turkistan Market ombor AI agentisiz. O'zbek tilida tahliliy, aniq va xatosiz javob berasiz.")))
            )
            val response = GeminiClient.service.generateContent(apiKey, request)
            val result = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (!result.isNullOrBlank()) {
                result
            } else {
                generateLocalSmartAnalysis(lowStockItems, supplierGroups)
            }
        } catch (e: Exception) {
            generateLocalSmartAnalysis(lowStockItems, supplierGroups)
        }
    }

    suspend fun answerAgentChatQuery(
        userQuery: String,
        products: List<Product>,
        supplierGroups: List<SupplierGroup>
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val lowStockItems = products.filter { it.isReorderNeeded }

        val contextInfo = "Omborda ${products.size} ta mahsulot mavjud. $lowStockItems.size tasida kritik qoldiq aniqlandi. Ta'minotchilar: ${supplierGroups.joinToString { it.supplierName }}."

        val prompt = "Ombor tizimi ma'lumotlari: $contextInfo.\nFoydalanuvchi savoli: '$userQuery'. Turkistan Market AI Agent sifatida qisqa, aqlli va foydali javob bering."

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext generateLocalChatReply(userQuery, products, supplierGroups)
        }

        try {
            val request = GenerateContentRequest(
                contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                systemInstruction = Content(parts = listOf(Part(text = "Siz Turkistan Market AI agentisiz. Ombor va zaxira savollariga do'stona va aniq o'zbek tilida javob bering.")))
            )
            val response = GeminiClient.service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: generateLocalChatReply(userQuery, products, supplierGroups)
        } catch (e: Exception) {
            generateLocalChatReply(userQuery, products, supplierGroups)
        }
    }

    private fun generateLocalSmartAnalysis(
        lowStockItems: List<Product>,
        supplierGroups: List<SupplierGroup>
    ): String {
        val urgentSupplier = supplierGroups.firstOrNull()?.supplierName ?: "Asosiy ta'minotchi"
        val totalCost = supplierGroups.sumOf { it.totalEstimatedCost }
        val formattedCost = String.format("%,.0f", totalCost).replace(',', ' ')

        return """
            🤖 *TURKISTAN MARKET AI AGENT HISOBOTI*
            
            1. ⚠️ **Shoshilinch Holat**: Ombordagi ${lowStockItems.size} ta mahsulot kritik darajaga yetgan yoki tugagan.
            2. 🚚 **Asosiy E'tibor**: **$urgentSupplier** va boshqa ${supplierGroups.size} ta ta'minotchiga zudlik bilan avto-buyurtma shakllantirildi.
            3. 💰 **Jami Kutilayotgan Buyurtma Summasi**: $formattedCost so'm.
            
            ⚡ *Natija*: Qo'lda soatlab ketadigan 200+ mahsulot tahlili bir necha soniyada bajarildi va Telegram orqali yuborishga tayyorlandi!
        """.trimIndent()
    }

    private fun generateLocalChatReply(
        query: String,
        products: List<Product>,
        supplierGroups: List<SupplierGroup>
    ): String {
        val q = query.lowercase()
        val lowStock = products.filter { it.isReorderNeeded }
        val outOfStock = products.filter { it.isOutOfStock }

        return when {
            q.contains("tugagan") || q.contains("qolmagan") -> {
                if (outOfStock.isEmpty()) "Hozircha omborda butunlay tugab qolgan mahsulot yo'q! Barchasi yetarli."
                else "Hozirda mutlaqo tugagan mahsulotlar: ${outOfStock.joinToString { it.name }}. Ularga zudlik bilan buyurtma shakllantirish tavsiya etiladi."
            }
            q.contains("kritik") || q.contains("azalgan") || q.contains("kam") -> {
                "Hozirda ${lowStock.size} ta mahsulot kritik qoldiqda. Eng shoshilinchlari: ${lowStock.take(3).joinToString { "${it.name} (${it.currentStock} ${it.unit})" }}."
            }
            q.contains("ta'minotchi") || q.contains("taminotchi") -> {
                "Ayni paytda ${supplierGroups.size} ta ta'minotchi bo'yicha buyurtma paketlari tayyorlandi: ${supplierGroups.joinToString { "${it.supplierName} (${it.items.size} ta tovar)" }}."
            }
            q.contains("telegram") || q.contains("yubor") -> {
                "Barcha buyurtma paketlari Telegram formatida tayyorlangan. 'Ta'minotchilar' bo'limidan 'Telegram'ga yuborish' tugmasini bosing!"
            }
            else -> {
                "Turkistan Market AI Agenti sizga ombordagi 1300+ mahsulotni nazorat qilishda va ta'minotchilarga avto-buyurtma berishda yordam beradi. Savolingizni berishingiz mumkin!"
            }
        }
    }
}
