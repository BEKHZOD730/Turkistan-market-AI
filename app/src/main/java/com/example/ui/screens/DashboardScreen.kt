package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Product
import com.example.ui.InventoryViewModel
import com.example.ui.components.HeaderBanner
import com.example.ui.components.KpiCard
import com.example.ui.components.ProductItemCard
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.MintContainer
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusAmberBg
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusRedBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.VioletBadge
import com.example.ui.theme.VioletContainer
import com.example.ui.theme.VioletLight
import com.example.ui.theme.VioletPrimary

@Composable
fun DashboardScreen(
    viewModel: InventoryViewModel,
    onNavigateToProducts: () -> Unit,
    onNavigateToSuppliers: () -> Unit,
    onEditProduct: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    val products by viewModel.allProducts.collectAsState()
    val supplierGroups by viewModel.supplierGroups.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val aiResult by viewModel.aiAnalysisResult.collectAsState()

    val totalStockValue = products.sumOf { it.currentStock * it.unitPrice }
    val formattedTotalValue = String.format("%,.0f", totalStockValue).replace(',', ' ')
    val reorderNeededCount = products.count { it.isReorderNeeded }
    val outOfStockCount = products.count { it.isOutOfStock }
    val totalDailySales = products.sumOf { it.dailySalesRate }

    val urgentProducts = products.filter { it.isReorderNeeded }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        item {
            HeaderBanner(
                totalProductsCount = products.size,
                reorderCount = reorderNeededCount,
                onRunAiClick = { viewModel.runAiInventoryAnalysis() }
            )
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiCard(
                        title = "QOLDIQ QIYMATI",
                        value = "$formattedTotalValue so'm",
                        subtitle = "${products.size} ta turdagi tovar",
                        icon = Icons.Default.Inventory2,
                        iconBgColor = MintContainer,
                        iconTintColor = EmeraldPrimary,
                        modifier = Modifier.weight(1f)
                    )

                    KpiCard(
                        title = "KRITIK / TUGAGAN",
                        value = "$reorderNeededCount / $outOfStockCount ta",
                        subtitle = "Buyurtma berish shart",
                        icon = Icons.Default.Warning,
                        iconBgColor = StatusRedBg,
                        iconTintColor = StatusRed,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiCard(
                        title = "KUNLIK SOTUV",
                        value = "${totalDailySales.toInt()} dona/kun",
                        subtitle = "Aylanma tezligi yuqori",
                        icon = Icons.Default.ShowChart,
                        iconBgColor = StatusAmberBg,
                        iconTintColor = StatusAmber,
                        modifier = Modifier.weight(1f)
                    )

                    KpiCard(
                        title = "VAQT TEJOVI",
                        value = "3.5 soat / kun",
                        subtitle = "AI Agent avto-hisob",
                        icon = Icons.Default.HourglassTop,
                        iconBgColor = VioletBadge,
                        iconTintColor = VioletLight,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, DarkBorder),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(VioletBadge),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = VioletLight,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "AI Agent Aqlli Tahlili",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = { viewModel.runAiInventoryAnalysis() },
                            colors = ButtonDefaults.buttonColors(containerColor = VioletPrimary),
                            enabled = !isAnalyzing,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isAnalyzing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Tahlil qilish", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (aiResult != null) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(DarkSurfaceVariant)
                                .padding(14.dp)
                        ) {
                            Text(
                                text = aiResult!!,
                                color = Color.White.copy(alpha = 0.95f),
                                fontSize = 13.sp,
                                lineHeight = 19.sp
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tugmachani bosing: AI Agent ombor qoldiqlarini va ta'minot zanjirini chuqur tahlil qilib beradi.",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🚨 Shoshilinch Buyurtmalar (${urgentProducts.size})",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                OutlinedButton(
                    onClick = onNavigateToSuppliers,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, VioletLight)
                ) {
                    Text("Ta'minotchilar ->", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VioletLight)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        if (urgentProducts.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, DarkBorder),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🎉 Ajoyib! Omborda hamma mahsulot yetarli miqdorda.", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.White)
                    }
                }
            }
        } else {
            items(urgentProducts) { product ->
                ProductItemCard(
                    product = product,
                    onEditClick = onEditProduct,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, DarkBorder),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "⚡ Kunlik Sotuv Simulyatsiyasi",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "1 kunlik ombor savdosini simulyatsiya qilib, sotuv tezligi ta'sirini tekshiring.",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { viewModel.simulateSalesStep() },
                        colors = ButtonDefaults.buttonColors(containerColor = VioletPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("1 Kun", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
