package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.data.Product
import com.example.ui.InventoryViewModel
import com.example.ui.components.AddEditProductDialog
import com.example.ui.screens.AiAssistantScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ProductListScreen
import com.example.ui.screens.SupplierOrdersScreen
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.TurkistanMarketTheme

class MainActivity : ComponentActivity() {

    private val viewModel: InventoryViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TurkistanMarketTheme {
                var selectedTab by remember { mutableIntStateOf(0) }
                var showAddEditDialog by remember { mutableStateOf(false) }
                var editingProduct by remember { mutableStateOf<Product?>(null) }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                 Text(
                                     text = "Sultan Market AI Agent",
                                     fontSize = 18.sp,
                                     fontWeight = FontWeight.Bold,
                                     color = Color.White
                                 )
                             },
                             colors = TopAppBarDefaults.topAppBarColors(
                                 containerColor = com.example.ui.theme.DarkSurface
                             )
                         )
                     },
                     bottomBar = {
                         NavigationBar(
                             containerColor = com.example.ui.theme.DarkSurface,
                             contentColor = Color.White
                         ) {
                             NavigationBarItem(
                                 selected = (selectedTab == 0),
                                 onClick = { selectedTab = 0 },
                                 icon = { Icon(imageVector = Icons.Default.Dashboard, contentDescription = "Dashboard") },
                                 label = { Text("Bosh sahifa", fontSize = 11.sp) },
                                 modifier = Modifier.testTag("tab_dashboard"),
                                 colors = NavigationBarItemDefaults.colors(
                                     selectedIconColor = Color.White,
                                     selectedTextColor = com.example.ui.theme.VioletLight,
                                     indicatorColor = com.example.ui.theme.VioletPrimary,
                                     unselectedIconColor = com.example.ui.theme.TextDim,
                                     unselectedTextColor = com.example.ui.theme.TextDim
                                 )
                             )
                             NavigationBarItem(
                                 selected = (selectedTab == 1),
                                 onClick = { selectedTab = 1 },
                                 icon = { Icon(imageVector = Icons.Default.Inventory2, contentDescription = "Products") },
                                 label = { Text("Ombor", fontSize = 11.sp) },
                                 modifier = Modifier.testTag("tab_products"),
                                 colors = NavigationBarItemDefaults.colors(
                                     selectedIconColor = Color.White,
                                     selectedTextColor = com.example.ui.theme.VioletLight,
                                     indicatorColor = com.example.ui.theme.VioletPrimary,
                                     unselectedIconColor = com.example.ui.theme.TextDim,
                                     unselectedTextColor = com.example.ui.theme.TextDim
                                 )
                             )
                             NavigationBarItem(
                                 selected = (selectedTab == 2),
                                 onClick = { selectedTab = 2 },
                                 icon = { Icon(imageVector = Icons.Default.LocalShipping, contentDescription = "Orders") },
                                 label = { Text("Ta'minot", fontSize = 11.sp) },
                                 modifier = Modifier.testTag("tab_suppliers"),
                                 colors = NavigationBarItemDefaults.colors(
                                     selectedIconColor = Color.White,
                                     selectedTextColor = com.example.ui.theme.VioletLight,
                                     indicatorColor = com.example.ui.theme.VioletPrimary,
                                     unselectedIconColor = com.example.ui.theme.TextDim,
                                     unselectedTextColor = com.example.ui.theme.TextDim
                                 )
                             )
                             NavigationBarItem(
                                 selected = (selectedTab == 3),
                                 onClick = { selectedTab = 3 },
                                 icon = { Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "AI Agent") },
                                 label = { Text("AI Agent", fontSize = 11.sp) },
                                 modifier = Modifier.testTag("tab_ai_agent"),
                                 colors = NavigationBarItemDefaults.colors(
                                     selectedIconColor = Color.White,
                                     selectedTextColor = com.example.ui.theme.VioletLight,
                                     indicatorColor = com.example.ui.theme.VioletPrimary,
                                     unselectedIconColor = com.example.ui.theme.TextDim,
                                     unselectedTextColor = com.example.ui.theme.TextDim
                                 )
                             )
                         }
                     }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (selectedTab) {
                            0 -> DashboardScreen(
                                viewModel = viewModel,
                                onNavigateToProducts = { selectedTab = 1 },
                                onNavigateToSuppliers = { selectedTab = 2 },
                                onEditProduct = {
                                    editingProduct = it
                                    showAddEditDialog = true
                                }
                            )
                            1 -> ProductListScreen(
                                viewModel = viewModel,
                                onAddProductClick = {
                                    editingProduct = null
                                    showAddEditDialog = true
                                },
                                onEditProduct = {
                                    editingProduct = it
                                    showAddEditDialog = true
                                }
                            )
                            2 -> SupplierOrdersScreen(viewModel = viewModel)
                            3 -> AiAssistantScreen(viewModel = viewModel)
                        }
                    }

                    if (showAddEditDialog) {
                        AddEditProductDialog(
                            initialProduct = editingProduct,
                            onDismiss = {
                                showAddEditDialog = false
                                editingProduct = null
                            },
                            onSave = { product ->
                                viewModel.saveProduct(product)
                            },
                            onDelete = { product ->
                                viewModel.deleteProduct(product)
                            }
                        )
                    }
                }
            }
        }
    }
}
