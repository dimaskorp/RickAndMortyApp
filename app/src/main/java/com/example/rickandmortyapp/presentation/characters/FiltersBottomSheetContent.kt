package com.example.rickandmortyapp.presentation.characters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rickandmortyapp.data.CharactersFilters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersBottomSheetContent(
    currentFilters: CharactersFilters,
    onFiltersApplied: (CharactersFilters) -> Unit,
    onFiltersReset: () -> Unit,
    onDismiss: () -> Unit
) {
    var status by remember { mutableStateOf(currentFilters.status ?: "") }
    var species by remember { mutableStateOf(currentFilters.species ?: "") }
    var gender by remember { mutableStateOf(currentFilters.gender ?: "") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 24.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Filters",
                style = MaterialTheme.typography.headlineSmall
            )
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close filters")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {

            Text(
                text = "Status",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            var statusExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = statusExpanded,
                onExpandedChange = { statusExpanded = !statusExpanded }
            ) {
                OutlinedTextField(
                    value = status,
                    onValueChange = {},
                    label = { Text("Select status") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    readOnly = true,
                    placeholder = { Text("Any status") }
                )
                ExposedDropdownMenu(
                    expanded = statusExpanded,
                    onDismissRequest = { statusExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Any") },
                        onClick = {
                            status = ""
                            statusExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Alive") },
                        onClick = {
                            status = "alive"
                            statusExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Dead") },
                        onClick = {
                            status = "dead"
                            statusExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Unknown") },
                        onClick = {
                            status = "unknown"
                            statusExpanded = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Species",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            var speciesExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = speciesExpanded,
                onExpandedChange = { speciesExpanded = !speciesExpanded }
            ) {
                OutlinedTextField(
                    value = species,
                    onValueChange = {},
                    label = { Text("Select species") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = speciesExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    readOnly = true,
                    placeholder = { Text("Any species") }
                )
                ExposedDropdownMenu(
                    expanded = speciesExpanded,
                    onDismissRequest = { speciesExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Any") },
                        onClick = {
                            species = ""
                            speciesExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Human") },
                        onClick = {
                            species = "human"
                            speciesExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Alien") },
                        onClick = {
                            species = "alien"
                            speciesExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Unknown") },
                        onClick = {
                            species = "unknown"
                            speciesExpanded = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Gender",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            var genderExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = genderExpanded,
                onExpandedChange = { genderExpanded = !genderExpanded }
            ) {
                OutlinedTextField(
                    value = gender,
                    onValueChange = {},
                    label = { Text("Select gender") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    readOnly = true,
                    placeholder = { Text("Any gender") }
                )
                ExposedDropdownMenu(
                    expanded = genderExpanded,
                    onDismissRequest = { genderExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Any") },
                        onClick = {
                            gender = ""
                            genderExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Male") },
                        onClick = {
                            gender = "male"
                            genderExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Female") },
                        onClick = {
                            gender = "female"
                            genderExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Genderless") },
                        onClick = {
                            gender = "genderless"
                            genderExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Unknown") },
                        onClick = {
                            gender = "unknown"
                            genderExpanded = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextButton(
                    onClick = onFiltersReset,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reset All")
                }

                Button(
                    onClick = {
                        onFiltersApplied(
                            CharactersFilters(
                                name = currentFilters.name,
                                status = status.ifBlank { null },
                                species = species.ifBlank { null },
                                gender = gender.ifBlank { null }
                            )
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Apply")
                }
            }
        }
    }
}