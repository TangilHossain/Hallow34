package com.shawonshagor0.hallow34.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelector(
    label: String,
    options: List<String>,
    selectedValue: String,
    placeholder: String = "Select",
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    // Filter options based on search text
    val filteredOptions = remember(searchText, options) {
        if (searchText.isBlank()) {
            options
        } else {
            options.filter { it.contains(searchText, ignoreCase = true) }
        }
    }

    // Reset search text when dropdown closes
    LaunchedEffect(expanded) {
        if (!expanded) {
            searchText = ""
        }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = if (expanded) searchText else (if (selectedValue.isBlank()) "" else selectedValue),
            onValueChange = {
                searchText = it
                if (!expanded) {
                    expanded = true
                }
            },
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryEditable)
                .fillMaxWidth(),
            singleLine = true
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (filteredOptions.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("No results found") },
                    onClick = { },
                    enabled = false
                )
            } else {
                filteredOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onValueChange(option)
                            searchText = ""
                            expanded = false
                        }
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(8.dp))
}
