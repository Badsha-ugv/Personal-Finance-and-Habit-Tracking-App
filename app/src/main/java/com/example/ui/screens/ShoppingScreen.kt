package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ShoppingItemEntity
import com.example.data.model.ShoppingTripEntity
import com.example.ui.components.appTextFieldColors
import com.example.ui.theme.EmeraldPositive
import com.example.ui.theme.HairlineBorder
import com.example.ui.theme.SlateOutline
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.SurfaceCanvas
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.CalmClarityUiState
import com.example.ui.viewmodel.CalmClarityViewModel

@Composable
fun ShoppingScreen(
    state: CalmClarityUiState,
    selectedTripId: Long?,
    currentTripItems: List<ShoppingItemEntity>,
    onSelectTrip: (Long?) -> Unit,
    onAddTrip: (title: String, budget: Double, targetAccount: String) -> Unit,
    onDeleteTrip: (ShoppingTripEntity) -> Unit,
    onAddItem: (tripId: Long, name: String, quantity: String, estPrice: Double) -> Unit,
    onToggleItemPurchased: (ShoppingItemEntity) -> Unit,
    onDeleteItem: (ShoppingItemEntity) -> Unit,
    onCompleteTrip: (trip: ShoppingTripEntity, finalTotal: Double, deductFromFinance: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    if (selectedTripId == null) {
        ShoppingTripsList(
            trips = state.shoppingTrips,
            isPrivacyMasked = state.isPrivacyMasked,
            onSelectTrip = onSelectTrip,
            onAddTrip = onAddTrip,
            onDeleteTrip = onDeleteTrip,
            modifier = modifier
        )
    } else {
        val trip = state.shoppingTrips.find { it.id == selectedTripId }
        if (trip != null) {
            ShoppingTripDetail(
                trip = trip,
                items = currentTripItems,
                isPrivacyMasked = state.isPrivacyMasked,
                onBack = { onSelectTrip(null) },
                onAddItem = { name, qty, price -> onAddItem(trip.id, name, qty, price) },
                onToggleItemPurchased = onToggleItemPurchased,
                onDeleteItem = onDeleteItem,
                onCompleteTrip = { finalTotal, deduct -> onCompleteTrip(trip, finalTotal, deduct) },
                modifier = modifier
            )
        } else {
            onSelectTrip(null)
        }
    }
}

@Composable
private fun ShoppingTripsList(
    trips: List<ShoppingTripEntity>,
    isPrivacyMasked: Boolean,
    onSelectTrip: (Long) -> Unit,
    onAddTrip: (title: String, budget: Double, targetAccount: String) -> Unit,
    onDeleteTrip: (ShoppingTripEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCreateDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
            .testTag("shopping_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Market & Shopping",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Plan items, track budget & sync expenses",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { showCreateDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.testTag("new_shopping_list_btn")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New List", fontWeight = FontWeight.Bold)
                }
            }
        }

        if (trips.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No shopping lists planned. Tap '+ New List' to begin.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(trips) { trip ->
                ShoppingTripCard(
                    trip = trip,
                    isPrivacyMasked = isPrivacyMasked,
                    onClick = { onSelectTrip(trip.id) },
                    onDelete = { onDeleteTrip(trip) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }

    if (showCreateDialog) {
        CreateShoppingTripDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { title, budget, account ->
                onAddTrip(title, budget, account)
                showCreateDialog = false
            }
        )
    }
}

@Composable
private fun ShoppingTripCard(
    trip: ShoppingTripEntity,
    isPrivacyMasked: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("shopping_trip_card_${trip.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (trip.isCompleted) Color(0xFFECFDF5) else Color(0xFFEFF6FF)
                ) {
                    Text(
                        text = if (trip.isCompleted) "COMPLETED & RECORDED" else "ACTIVE CHECKLIST",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (trip.isCompleted) EmeraldPositive else Color(0xFF2563EB),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = trip.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Budget Target", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        CalmClarityViewModel.formatTaka(trip.budget, isPrivacyMasked),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(if (trip.isCompleted) "Total Spent" else "Est. Cost", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        CalmClarityViewModel.formatTaka(trip.totalSpent, isPrivacyMasked),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun ShoppingTripDetail(
    trip: ShoppingTripEntity,
    items: List<ShoppingItemEntity>,
    isPrivacyMasked: Boolean,
    onBack: () -> Unit,
    onAddItem: (name: String, qty: String, price: Double) -> Unit,
    onToggleItemPurchased: (ShoppingItemEntity) -> Unit,
    onDeleteItem: (ShoppingItemEntity) -> Unit,
    onCompleteTrip: (finalTotal: Double, deductFromFinance: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddItemDialog by remember { mutableStateOf(false) }
    var showCompleteDialog by remember { mutableStateOf(false) }

    val totalPurchasedCost = items.filter { it.isPurchased }.sumOf { if (it.actualPrice > 0) it.actualPrice else it.estimatedPrice }
    val totalEstimatedCost = items.sumOf { it.estimatedPrice }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
            .testTag("shopping_detail_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("back_from_shopping_detail")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = trip.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    Text(
                        text = "Budget: ${CalmClarityViewModel.formatTaka(trip.budget, isPrivacyMasked)} · ${items.count { it.isPurchased }}/${items.size} bought",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (!trip.isCompleted) {
                    Button(
                        onClick = { showCompleteDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPositive),
                        modifier = Modifier.testTag("finish_shopping_btn")
                    ) {
                        Text("Finish", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }

        // Action: Quick Add Item
        if (!trip.isCompleted) {
            item {
                OutlinedButton(
                    onClick = { showAddItemDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_shopping_item_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Needed Item to List", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        item {
            Text(
                text = "ITEMS TO PURCHASE (${items.size})",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        if (items.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("No items added yet. Tap '+ Add Needed Item' to write your market items.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(items) { item ->
                ShoppingItemRow(
                    item = item,
                    isPrivacyMasked = isPrivacyMasked,
                    onToggle = { onToggleItemPurchased(item) },
                    onDelete = { onDeleteItem(item) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }

    if (showAddItemDialog) {
        AddItemDialog(
            onDismiss = { showAddItemDialog = false },
            onConfirm = { name, qty, price ->
                onAddItem(name, qty, price)
                showAddItemDialog = false
            }
        )
    }

    if (showCompleteDialog) {
        CompleteTripDialog(
            suggestedTotal = if (totalPurchasedCost > 0) totalPurchasedCost else trip.budget,
            targetAccount = trip.targetAccountName,
            onDismiss = { showCompleteDialog = false },
            onConfirm = { finalTotal, deduct ->
                onCompleteTrip(finalTotal, deduct)
                showCompleteDialog = false
            }
        )
    }
}

@Composable
private fun ShoppingItemRow(
    item: ShoppingItemEntity,
    isPrivacyMasked: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isPurchased) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.isPurchased,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(checkedColor = EmeraldPositive)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = if (item.isPurchased) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (item.isPurchased) TextDecoration.LineThrough else TextDecoration.None
                )
                Text(
                    text = "Qty: ${item.quantity} · Est: ${CalmClarityViewModel.formatTaka(item.estimatedPrice, isPrivacyMasked)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = CalmClarityViewModel.formatTaka(
                    if (item.actualPrice > 0) item.actualPrice else item.estimatedPrice,
                    isPrivacyMasked
                ),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (item.isPurchased) EmeraldPositive else MaterialTheme.colorScheme.onSurface
            )

            IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                Icon(
                    imageVector = Icons.Outlined.DeleteOutline,
                    contentDescription = "Delete Item",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun AddItemDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, quantity: String, estimatedPrice: Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var priceText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Market Item", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item Name *") },
                    placeholder = { Text("e.g. Fresh Milk, Monitor Cable") },
                    colors = appTextFieldColors(),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantity / Size") },
                    placeholder = { Text("e.g. 2 kg, 1 unit") },
                    colors = appTextFieldColors(),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Estimated Price (৳)") },
                    placeholder = { Text("e.g. 1200") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = appTextFieldColors(),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = priceText.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank()) onConfirm(name, quantity, price)
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Add Item", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun CompleteTripDialog(
    suggestedTotal: Double,
    targetAccount: String,
    onDismiss: () -> Unit,
    onConfirm: (finalTotal: Double, deductFromFinance: Boolean) -> Unit
) {
    var totalText by remember { mutableStateOf(suggestedTotal.toInt().toString()) }
    var deductFromFinance by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Finish Shopping Trip", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Enter the total actual cost incurred during this shopping trip:", style = MaterialTheme.typography.bodyMedium)

                OutlinedTextField(
                    value = totalText,
                    onValueChange = { totalText = it },
                    label = { Text("Final Total Cost (৳ BDT)") },
                    leadingIcon = { Text("৳", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 12.dp)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = appTextFieldColors(),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { deductFromFinance = !deductFromFinance }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = deductFromFinance,
                        onCheckedChange = { deductFromFinance = it },
                        colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Record expense of ৳ $totalText into '$targetAccount'",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalTotal = totalText.toDoubleOrNull() ?: suggestedTotal
                    onConfirm(finalTotal, deductFromFinance)
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPositive)
            ) {
                Text("Complete Trip", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun CreateShoppingTripDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, budget: Double, account: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var budgetText by remember { mutableStateOf("") }
    var account by remember { mutableStateOf("Operating Liquid") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Shopping Trip", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Trip Title *") },
                    placeholder = { Text("e.g. Weekly Market, Tech Gear") },
                    colors = appTextFieldColors(),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = budgetText,
                    onValueChange = { budgetText = it },
                    label = { Text("Planned Budget (৳ BDT) *") },
                    placeholder = { Text("e.g. 15000") },
                    leadingIcon = { Text("৳", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 12.dp)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = appTextFieldColors(),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val budget = budgetText.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && budget > 0) onConfirm(title, budget, account)
                },
                enabled = title.isNotBlank() && (budgetText.toDoubleOrNull() ?: 0.0) > 0,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Create List", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
