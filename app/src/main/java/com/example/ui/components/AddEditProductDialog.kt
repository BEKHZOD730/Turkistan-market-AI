package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.Dialog
import com.example.data.Product
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.VioletLight
import com.example.ui.theme.VioletPrimary

@Composable
fun AddEditProductDialog(
    initialProduct: Product? = null,
    onDismiss: () -> Unit,
    onSave: (Product) -> Unit,
    onDelete: ((Product) -> Unit)? = null
) {
    var name by remember { mutableStateOf(initialProduct?.name ?: "") }
    var barcode by remember { mutableStateOf(initialProduct?.barcode ?: "") }
    var category by remember { mutableStateOf(initialProduct?.category ?: "Sut mahsulotlari") }
    var supplierName by remember { mutableStateOf(initialProduct?.supplierName ?: "Nestle Uzbekistan") }
    var currentStock by remember { mutableStateOf(initialProduct?.currentStock?.toString() ?: "10") }
    var unit by remember { mutableStateOf(initialProduct?.unit ?: "dona") }
    var unitPrice by remember { mutableStateOf(initialProduct?.unitPrice?.toInt()?.toString() ?: "15000") }
    var dailySalesRate by remember { mutableStateOf(initialProduct?.dailySalesRate?.toString() ?: "12.0") }
    var leadTimeDays by remember { mutableStateOf(initialProduct?.leadTimeDays?.toString() ?: "2") }
    var safetyStock by remember { mutableStateOf(initialProduct?.safetyStock?.toString() ?: "15") }
    var minOrderBatch by remember { mutableStateOf(initialProduct?.minOrderBatch?.toString() ?: "24") }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = DarkSurface,
        unfocusedContainerColor = DarkSurface,
        focusedBorderColor = VioletLight,
        unfocusedBorderColor = DarkBorder,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedLabelColor = VioletLight,
        unfocusedLabelColor = TextMuted
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, DarkBorder),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (initialProduct == null) "Yangi Mahsulot Qo'shish" else "Mahsulotni Tahrirlash",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Mahsulot Nomi") },
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = barcode,
                        onValueChange = { barcode = it },
                        label = { Text("Shtrixkod") },
                        colors = fieldColors,
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Kategoriya") },
                        colors = fieldColors,
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = supplierName,
                    onValueChange = { supplierName = it },
                    label = { Text("Ta'minotchi Kompaniya") },
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = currentStock,
                        onValueChange = { currentStock = it },
                        label = { Text("Joriy Qoldiq") },
                        colors = fieldColors,
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Birligi (dona/kg)") },
                        colors = fieldColors,
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = unitPrice,
                        onValueChange = { unitPrice = it },
                        label = { Text("Narxi (So'm)") },
                        colors = fieldColors,
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = dailySalesRate,
                        onValueChange = { dailySalesRate = it },
                        label = { Text("Sotuv / kun") },
                        colors = fieldColors,
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = leadTimeDays,
                        onValueChange = { leadTimeDays = it },
                        label = { Text("Yetkazish (kun)") },
                        colors = fieldColors,
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = safetyStock,
                        onValueChange = { safetyStock = it },
                        label = { Text("Xavfsiz Qoldiq") },
                        colors = fieldColors,
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (initialProduct != null && onDelete != null) {
                        OutlinedButton(
                            onClick = {
                                onDelete(initialProduct)
                                onDismiss()
                            },
                            border = BorderStroke(1.dp, StatusRed),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusRed),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }

                    Button(
                        onClick = {
                            val prod = Product(
                                id = initialProduct?.id ?: 0,
                                barcode = barcode.ifBlank { "478000999000" },
                                name = name.ifBlank { "Noma'lum Mahsulot" },
                                category = category.ifBlank { "Boshqa" },
                                supplierName = supplierName.ifBlank { "Turkistan Distributor" },
                                currentStock = currentStock.toIntOrNull() ?: 0,
                                unit = unit.ifBlank { "dona" },
                                unitPrice = unitPrice.toDoubleOrNull() ?: 10000.0,
                                dailySalesRate = dailySalesRate.toDoubleOrNull() ?: 10.0,
                                leadTimeDays = leadTimeDays.toIntOrNull() ?: 2,
                                safetyStock = safetyStock.toIntOrNull() ?: 10,
                                minOrderBatch = minOrderBatch.toIntOrNull() ?: 24
                            )
                            onSave(prod)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = VioletPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = "Save", tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Saqlash", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
