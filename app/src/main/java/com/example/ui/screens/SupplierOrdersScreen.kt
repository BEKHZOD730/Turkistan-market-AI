package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SupplierGroup
import com.example.ui.InventoryViewModel
import com.example.ui.components.ExportReportDialog
import com.example.ui.components.SupplierOrderGroupCard
import com.example.ui.components.TelegramPreviewDialog
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.TextMuted

@Composable
fun SupplierOrdersScreen(
    viewModel: InventoryViewModel,
    modifier: Modifier = Modifier
) {
    val supplierGroups by viewModel.supplierGroups.collectAsState()
    val ordersHistory by viewModel.allOrders.collectAsState()

    var activeTelegramGroup by remember { mutableStateOf<SupplierGroup?>(null) }
    var activeExportGroup by remember { mutableStateOf<SupplierGroup?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Ta'minotchilar Bo'yicha Buyurtmalar",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "AI Agent tomonidan guruhlangan tayyor buyurtma paketlari",
                fontSize = 12.sp,
                color = TextMuted
            )
        }

        if (supplierGroups.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, DarkBorder),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "✅ Hozirda ta'minotchilarga shoshilinch buyurtma yo'q!",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Barcha 1,340+ mahsulot zaxirasi yetarli darajada saqlanmoqda.",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(supplierGroups) { group ->
                    SupplierOrderGroupCard(
                        group = group,
                        onTelegramSendClick = { activeTelegramGroup = it },
                        onExportExcelClick = { activeExportGroup = it },
                        onFulfillClick = { viewModel.fulfillSupplierOrder(it.supplierName) },
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    activeTelegramGroup?.let { group ->
        val msg = viewModel.getTelegramMessageForGroup(group)
        TelegramPreviewDialog(
            formattedMessage = msg,
            onDismiss = { activeTelegramGroup = null },
            onConfirmSent = {
                viewModel.createAndSaveOrderHistory(group, "Telegram")
                activeTelegramGroup = null
            }
        )
    }

    activeExportGroup?.let { group ->
        val csv = viewModel.getCsvExportForGroup(group)
        ExportReportDialog(
            csvContent = csv,
            supplierName = group.supplierName,
            onDismiss = { activeExportGroup = null }
        )
    }
}
