package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiAgentEngine
import com.example.data.AppDatabase
import com.example.data.GeneratedOrder
import com.example.data.InventoryRepository
import com.example.data.Product
import com.example.data.SupplierGroup
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ChatMessage(
    val sender: String, // "User" or "Agent"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class StockFilter {
    ALL,
    REORDER_NEEDED,
    OUT_OF_STOCK
}

class InventoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: InventoryRepository
    private val aiEngine = GeminiAgentEngine()

    init {
        val db = AppDatabase.getInstance(application)
        repository = InventoryRepository(db.productDao(), db.orderDao())
        viewModelScope.launch {
            repository.ensureSampleDataLoaded()
        }
    }

    val allProducts: StateFlow<List<Product>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrders: StateFlow<List<GeneratedOrder>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Barchasi")
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _stockFilter = MutableStateFlow(StockFilter.ALL)
    val stockFilter = _stockFilter.asStateFlow()

    val filteredProducts: StateFlow<List<Product>> = combine(
        allProducts,
        searchQuery,
        selectedCategory,
        stockFilter
    ) { products, query, cat, filter ->
        products.filter { p ->
            val matchesQuery = p.name.contains(query, ignoreCase = true) ||
                    p.barcode.contains(query, ignoreCase = true) ||
                    p.supplierName.contains(query, ignoreCase = true)

            val matchesCat = if (cat == "Barchasi") true else p.category == cat

            val matchesFilter = when (filter) {
                StockFilter.ALL -> true
                StockFilter.REORDER_NEEDED -> p.isReorderNeeded
                StockFilter.OUT_OF_STOCK -> p.isOutOfStock
            }

            matchesQuery && matchesCat && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val supplierGroups: StateFlow<List<SupplierGroup>> = allProducts
        .combine(MutableStateFlow(Unit)) { products, _ ->
            repository.calculateSupplierRecommendations(products)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _aiAnalysisResult = MutableStateFlow<String?>(null)
    val aiAnalysisResult = _aiAnalysisResult.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing = _isAnalyzing.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = "Agent",
                text = "Assalomu alaykum! Men Turkistan Market AI Agentiman. Ombordagi 1300+ mahsulot bo'yicha tahlillar yoki ta'minotchilar buyurtmasi haqida savol berishingiz mumkin."
            )
        )
    )
    val chatMessages = _chatMessages.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setStockFilter(filter: StockFilter) {
        _stockFilter.value = filter
    }

    fun runAiInventoryAnalysis() {
        viewModelScope.launch {
            _isAnalyzing.value = true
            val analysis = aiEngine.analyzeInventoryAndSuggestOrders(
                products = allProducts.value,
                supplierGroups = supplierGroups.value
            )
            _aiAnalysisResult.value = analysis
            _isAnalyzing.value = false
        }
    }

    fun sendUserChatMessage(messageText: String) {
        if (messageText.isBlank()) return
        val userMsg = ChatMessage(sender = "User", text = messageText)
        _chatMessages.value = _chatMessages.value + userMsg

        viewModelScope.launch {
            val replyText = aiEngine.answerAgentChatQuery(
                userQuery = messageText,
                products = allProducts.value,
                supplierGroups = supplierGroups.value
            )
            val agentMsg = ChatMessage(sender = "Agent", text = replyText)
            _chatMessages.value = _chatMessages.value + agentMsg
        }
    }

    fun simulateSalesStep() {
        viewModelScope.launch {
            repository.simulateOneDaySales()
        }
    }

    fun saveProduct(product: Product) {
        viewModelScope.launch {
            if (product.id == 0) {
                repository.addProduct(product)
            } else {
                repository.updateProduct(product)
            }
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    fun fulfillSupplierOrder(supplierName: String) {
        viewModelScope.launch {
            repository.fulfillSupplierOrders(supplierName)
        }
    }

    fun createAndSaveOrderHistory(group: SupplierGroup, channel: String = "Telegram") {
        viewModelScope.launch {
            val orderCode = "ORD-" + System.currentTimeMillis().toString().takeLast(6)
            val summaryText = repository.generateTelegramMessageForSupplier(group)
            val csvItems = repository.generateCsvExportForSupplier(group)
            val newOrder = GeneratedOrder(
                orderCode = orderCode,
                supplierName = group.supplierName,
                supplierPhone = group.supplierPhone,
                totalItemsCount = group.items.size,
                totalEstimatedCost = group.totalEstimatedCost,
                status = "$channel orqali yuborildi",
                summaryText = summaryText,
                itemsFormattedJson = csvItems
            )
            repository.saveOrder(newOrder)
        }
    }

    fun getTelegramMessageForGroup(group: SupplierGroup): String {
        return repository.generateTelegramMessageForSupplier(group)
    }

    fun getCsvExportForGroup(group: SupplierGroup): String {
        return repository.generateCsvExportForSupplier(group)
    }
}
