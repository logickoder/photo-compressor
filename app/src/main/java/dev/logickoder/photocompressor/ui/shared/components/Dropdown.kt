package dev.logickoder.photocompressor.ui.shared.components

import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.MaterialTheme as Theme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T> DropdownField(
    suggested: T,
    suggestions: List<T>,
    modifier: Modifier = Modifier,
    onSuggestionSelected: ((T) -> Unit),
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = suggested.toString(),
            onValueChange = {},
            textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Medium),
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            shape = Theme.shapes.medium,
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            suggestions.forEach { suggestion ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onSuggestionSelected(suggestion)
                    }
                ) {
                    Text(text = suggestion.toString())
                }
            }
        }
    }
}