package com.example.deisaapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deisaapplication.R
import com.example.deisaapplication.ui.theme.*

// ─── Modern Stat Card ───────────────────────────────────────────────────────

@Composable
fun ModernStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.16f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Box(
                Modifier.size(42.dp).background(color.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(16.dp))
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = OnAppBackground)
            Text(title, fontSize = 12.sp, color = MutedText, fontWeight = FontWeight.Medium)
        }
    }
}

// ─── Quick Action Button ─────────────────────────────────────────────────────

@Composable
fun QuickActionButton(
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier.size(56.dp).background(color.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(label, fontSize = 12.sp, color = OnAppBackground, fontWeight = FontWeight.SemiBold)
    }
}

// ─── Section Header ──────────────────────────────────────────────────────────

@Composable
fun SectionHeader(
    title: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = OnAppBackground)
        if (actionText != null && onActionClick != null) {
            TextButton(onClick = onActionClick) {
                Text(actionText, color = Primary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        } else if (action != null) {
            action()
        }
    }
}

// ─── Status Badge ─────────────────────────────────────────────────────────────

@Composable
fun StatusBadge(status: String, label: String) {
    val badgeColor: Color
    val textColor: Color
    when (status) {
        "observed"          -> { badgeColor = AppWarning.copy(alpha = 0.15f); textColor = AppWarning }
        "handled"           -> { badgeColor = Secondary.copy(alpha = 0.15f); textColor = Secondary }
        "recovered"         -> { badgeColor = Primary.copy(alpha = 0.15f); textColor = Primary }
        "referred"          -> { badgeColor = AppError.copy(alpha = 0.15f); textColor = AppError }
        "aman"              -> { badgeColor = Primary.copy(alpha = 0.15f); textColor = Primary }
        "stok_kritis"       -> { badgeColor = AppError.copy(alpha = 0.15f); textColor = AppError }
        "kadaluarsa"        -> { badgeColor = AppError.copy(alpha = 0.15f); textColor = AppError }
        "segera_kadaluarsa" -> { badgeColor = AppWarning.copy(alpha = 0.15f); textColor = AppWarning }
        "available"         -> { badgeColor = Primary.copy(alpha = 0.15f); textColor = Primary }
        "occupied"          -> { badgeColor = AppError.copy(alpha = 0.15f); textColor = AppError }
        "maintenance"       -> { badgeColor = Secondary.copy(alpha = 0.15f); textColor = Secondary }
        "approved", "admin", "super_admin", "L" -> { badgeColor = Primary.copy(alpha = 0.15f); textColor = Primary }
        "pending", "petugas_kesehatan", "P" -> { badgeColor = AppWarning.copy(alpha = 0.15f); textColor = AppWarning }
        "rejected"          -> { badgeColor = AppError.copy(alpha = 0.15f); textColor = AppError }
        else                -> { badgeColor = MutedText.copy(alpha = 0.15f); textColor = MutedText }
    }
    Surface(shape = RoundedCornerShape(20.dp), color = badgeColor) {
        Text(
            label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = textColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}

// ─── DeisaTopBar ─────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeisaTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    onOpenDrawer: (() -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
) {
    CenterAlignedTopAppBar(
        title = { Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = OnAppBackground) },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(androidx.compose.material.icons.Icons.Filled.ArrowBack, "Kembali", tint = OnAppBackground)
                }
            } else if (onOpenDrawer != null) {
                IconButton(onClick = onOpenDrawer) {
                    Icon(androidx.compose.material.icons.Icons.Filled.Menu, "Menu", tint = OnAppBackground)
                }
            }
        },
        actions = { actions?.invoke(this) },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = AppBackground.copy(alpha = 0.98f),
            scrolledContainerColor = AppBackground,
        ),
    )
}

// ─── Loading & Error State ───────────────────────────────────────────────────

@Composable
fun LoadingBox(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Primary, strokeWidth = 3.dp)
    }
}

@Composable
fun DeisaLoadingBar(modifier: Modifier = Modifier) {
    LinearProgressIndicator(
        modifier = modifier.fillMaxWidth().height(3.dp),
        color = Primary,
        trackColor = AppSurfaceVariant.copy(alpha = 0.3f)
    )
}

@Composable
fun ErrorBox(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.ErrorOutline, null, tint = AppError, modifier = Modifier.size(48.dp))
            Spacer(Modifier.height(16.dp))
            Text(message, color = MutedText, fontSize = 14.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onRetry, 
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.retry), color = OnPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ─── Surface Card ────────────────────────────────────────────────────────────

@Composable
fun DeisaCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        elevation = CardDefaults.cardElevation(0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AppSurfaceVariant.copy(alpha = 0.65f))
    ) {
        Column(Modifier.padding(18.dp), content = content)
    }
}

// ─── Divider ────────────────────────────────────────────────────────────────

@Composable
fun DeisaDivider() = HorizontalDivider(color = AppSurfaceVariant, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

// ─── Form Components ─────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DeisaDropdown(
    label: String,
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit,
    itemLabel: (T) -> String,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedItem?.let { itemLabel(it) } ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = AppSurfaceVariant,
                focusedTextColor = OnAppBackground,
                unfocusedTextColor = OnAppBackground,
                focusedLabelColor = Primary,
                unfocusedLabelColor = MutedText,
                focusedContainerColor = AppSurface,
                unfocusedContainerColor = AppSurface,
            ),
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(AppSurface)
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(itemLabel(item), color = OnAppBackground) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DeisaSearchableDropdown(
    label: String,
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit,
    itemLabel: (T) -> String,
    modifier: Modifier = Modifier,
    placeholder: String = "Cari...",
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxWidth().clickable { showDialog = true }) {
        OutlinedTextField(
            value = selectedItem?.let { itemLabel(it) } ?: "",
            onValueChange = {},
            readOnly = true,
            enabled = false,
            label = { Text(label) },
            trailingIcon = { Icon(Icons.Default.Search, null, tint = MutedText) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = AppSurfaceVariant,
                disabledTextColor = OnAppBackground,
                disabledLabelColor = MutedText,
                disabledTrailingIconColor = MutedText
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }

    if (showDialog) {
        var searchQuery by remember { mutableStateOf("") }
        val filteredItems = remember(searchQuery, items) {
            if (searchQuery.isEmpty()) items
            else items.filter { itemLabel(it).contains(searchQuery, ignoreCase = true) }
        }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = AppSurface,
            title = { Text("Pilih $label", color = OnAppBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(Modifier.fillMaxWidth().fillMaxHeight(0.7f)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        placeholder = { Text(placeholder, fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = MutedText) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant,
                            focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    if (filteredItems.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Tidak ada hasil", color = MutedText)
                        }
                    } else {
                        LazyColumn(Modifier.fillMaxSize()) {
                            items(filteredItems) { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onItemSelected(item)
                                            showDialog = false
                                        }
                                        .padding(vertical = 12.dp, horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(itemLabel(item), color = OnAppBackground, fontSize = 15.sp)
                                }
                                DeisaDivider()
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) { Text("Tutup", color = MutedText) }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DeisaMultiSearchableDropdown(
    label: String,
    items: List<T>,
    selectedItems: List<T>,
    onItemsSelected: (List<T>) -> Unit,
    itemLabel: (T) -> String,
    modifier: Modifier = Modifier,
    placeholder: String = "Cari...",
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxWidth().clickable { showDialog = true }) {
        OutlinedTextField(
            value = if (selectedItems.isEmpty()) "" else "${selectedItems.size} Terpilih",
            onValueChange = {},
            readOnly = true,
            enabled = false,
            label = { Text(label) },
            trailingIcon = { Icon(Icons.Default.Search, null, tint = MutedText) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = AppSurfaceVariant,
                disabledTextColor = OnAppBackground,
                disabledLabelColor = MutedText,
                disabledTrailingIconColor = MutedText
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }

    if (showDialog) {
        var searchQuery by remember { mutableStateOf("") }
        val filteredItems = remember(searchQuery, items) {
            if (searchQuery.isEmpty()) items
            else items.filter { itemLabel(it).contains(searchQuery, ignoreCase = true) }
        }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = AppSurface,
            title = { Text("Pilih $label", color = OnAppBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(Modifier.fillMaxWidth().fillMaxHeight(0.7f)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        placeholder = { Text(placeholder, fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = MutedText) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant,
                            focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    if (filteredItems.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Tidak ada hasil", color = MutedText)
                        }
                    } else {
                        LazyColumn(Modifier.fillMaxSize()) {
                            items(filteredItems) { item ->
                                val isSelected = selectedItems.contains(item)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (isSelected) onItemsSelected(selectedItems - item)
                                            else onItemsSelected(selectedItems + item)
                                        }
                                        .background(if (isSelected) Primary.copy(alpha = 0.1f) else Color.Transparent)
                                        .padding(vertical = 12.dp, horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(itemLabel(item), color = if (isSelected) Primary else OnAppBackground, fontSize = 15.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                    Spacer(Modifier.weight(1f))
                                    if (isSelected) Icon(Icons.Default.Check, null, tint = Primary)
                                }
                                DeisaDivider()
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) { Text("Selesai", color = Primary, fontWeight = FontWeight.Bold) }
            }
        )
    }
}

@Composable
fun DeisaRadioGroup(
    label: String,
    options: List<Pair<String, String>>, // value to label
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxWidth()) {
        Text(label, fontSize = 14.sp, color = OnAppBackground, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
        Column(Modifier.fillMaxWidth()) {
            options.forEach { (value, text) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically, 
                    modifier = Modifier.fillMaxWidth().clickable { onOptionSelected(value) }.padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = selectedOption == value,
                        onClick = { onOptionSelected(value) },
                        colors = RadioButtonDefaults.colors(selectedColor = Primary, unselectedColor = MutedText)
                    )
                    Text(text, color = OnAppBackground, fontSize = 14.sp)
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeisaDatePicker(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    val dateStr = if (value.isNotEmpty()) value else "Pilih Tanggal"

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = dateStr,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            leadingIcon = { Icon(Icons.Default.CalendarMonth, null, tint = MutedText) },
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.DateRange, null, tint = Primary)
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = AppSurfaceVariant,
                focusedTextColor = OnAppBackground,
                unfocusedTextColor = OnAppBackground,
                focusedLabelColor = Primary,
                unfocusedLabelColor = MutedText,
            ),
            modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }
        )

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let {
                            val date = java.time.Instant.ofEpochMilli(it)
                                .atZone(java.time.ZoneId.of("UTC"))
                                .toLocalDate()
                            onValueChange(date.toString())
                        }
                        showDatePicker = false
                    }) { Text("Pilih", color = Primary) }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Batal", color = MutedText) }
                },
                colors = DatePickerDefaults.colors(containerColor = AppSurface)
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}
