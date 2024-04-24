package com.dlifes.textsender.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.dlifes.textsender.screen.PA

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuWithLabel(label: String,itemList: List<PA>,onSelectedValueChange:(String) -> Unit) {
    var isExpanded by remember {
        mutableStateOf(false)
    }

    var value by remember {
        mutableStateOf("")
    }

    ExposedDropdownMenuBox(expanded = isExpanded, onExpandedChange = { isExpanded = it }) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            label = { Text(text = label) },
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier.menuAnchor().fillMaxWidth(.9f)
        )
        ExposedDropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
            itemList.forEach { item ->
                DropdownMenuItem(text = { Text(text = item.paId) }, onClick = {
                    value = item.paId
                    isExpanded = false
                    onSelectedValueChange(value)
                })
            }
        }
    }
}