package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Product
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusAmberBg
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusGreenBg
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusRedBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.VioletLight

@Composable
fun ProductItemCard(
    product: Product,
    onEditClick: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    val (statusLabel, statusBg, statusText) = when {
        product.isOutOfStock -> Triple("🔴 TUGAGAN", StatusRedBg, StatusRed)
        product.isReorderNeeded -> Triple("⚠️ KRITIK QOLDIQ", StatusAmberBg, StatusAmber)
        else -> Triple("🟢 YETARLI", StatusGreenBg, StatusGreen)
    }

    val formattedPrice = String.format("%,.0f", product.unitPrice).replace(',', ' ')

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onEditClick(product) },
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, DarkBorder),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusLabel,
                        color = statusText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = product.category,
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Shtrixkod: ${product.barcode} • Ta'minotchi: ${product.supplierName}",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                IconButton(
                    onClick = { onEditClick(product) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Product",
                        tint = VioletLight,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurfaceVariant)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Qoldiq",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                    Text(
                        text = "${product.currentStock} ${product.unit}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (product.isReorderNeeded) statusText else Color.White
                    )
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = VioletLight
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "Sotuv tezligi",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                    Text(
                        text = "${product.dailySalesRate} ${product.unit}/kun",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = EmeraldPrimary
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "Yetkazish",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                    Text(
                        text = "${product.leadTimeDays} kun",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Narxi",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                    Text(
                        text = "$formattedPrice so'm",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    )
                }
            }

            if (product.isReorderNeeded) {
                Spacer(modifier = Modifier.height(10.dp))
                val suggestedQty = product.calculateSuggestedOrderQty()
                val estCost = String.format("%,.0f", suggestedQty * product.unitPrice).replace(',', ' ')
                Text(
                    text = "🤖 AI Buyurtma Taklifi: $suggestedQty ${product.unit} (taxminan $estCost so'm)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = VioletLight
                )
            }
        }
    }
}
