package com.example.deisaapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
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
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredItems = remember(searchQuery, items) {
        if (searchQuery.isEmpty()) items
        else items.filter { itemLabel(it).contains(searchQuery, ignoreCase = true) }
    }

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
            onDismissRequest = { 
                expanded = false
                searchQuery = ""
            },
            modifier = Modifier.background(AppSurface).widthIn(max = 300.dp)
        ) {
            // Search field at the top
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                placeholder = { Text(placeholder, fontSize = 14.sp) },
                leadingIcon = { Icon(androidx.compose.material.icons.Icons.Default.Search, null, tint = MutedText) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = AppSurfaceVariant,
                    focusedTextColor = OnAppBackground,
                    unfocusedTextColor = OnAppBackground,
                    focusedContainerColor = AppSurfaceVariant.copy(alpha = 0.3f),
                    unfocusedContainerColor = AppSurfaceVariant.copy(alpha = 0.1f),
                ),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )
            
            if (filteredItems.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("Tidak ada hasil", color = MutedText, fontSize = 14.sp) },
                    onClick = { },
                    enabled = false
                )
            } else {
                filteredItems.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(itemLabel(item), color = OnAppBackground) },
                        onClick = {
                            onItemSelected(item)
                            expanded = false
                            searchQuery = ""
                        }
                    )
                }
            }
        }
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
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            options.forEach { (value, text) ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onOptionSelected(value) }) {
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
