package com.enderthor.kpower.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.enderthor.kpower.data.ConfigData
import io.hammerhead.karooext.KarooSystemService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(isCreating: Boolean, configdata: ConfigData, onSubmit: (updatedConfigData: ConfigData?) -> Unit, onCancel: () -> Unit){
    val ctx = LocalContext.current
    val karooSystem = remember { KarooSystemService(ctx) }
    LaunchedEffect(Unit) {
        karooSystem.connect{}
    }


    var title by remember { mutableStateOf(configdata.name) }
    var bikeMass by remember { mutableStateOf(configdata.bikeMass) }
    var rollingResistanceCoefficient by remember { mutableStateOf(configdata.rollingResistanceCoefficient) }
    var dragCoefficient by remember { mutableStateOf(configdata.dragCoefficient) }
    var isActive by remember { mutableStateOf(configdata.isActive) }
    var deleteDialogVisible by remember { mutableStateOf(false) }
    var powerLoss by remember { mutableStateOf(configdata.powerLoss) }
    var frontalArea by remember { mutableStateOf(configdata.frontalArea) }
    var headwind by remember { mutableStateOf(configdata.headwind) }


    fun getUpdatedConfigData(): ConfigData = ConfigData(
        configdata.id, title, isActive, bikeMass, rollingResistanceCoefficient, dragCoefficient, frontalArea, powerLoss, headwind)

    Column(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)) {
        TopAppBar(title = { Text(if(isCreating) "Create Power Config" else "Edit Power Config") })
        Column(modifier = Modifier
            .padding(5.dp)
            .verticalScroll(rememberScrollState())
            .fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

            OutlinedTextField(value = bikeMass.toString(), modifier = Modifier.fillMaxWidth(),
                onValueChange = { bikeMass = it },
                label = { Text("Bike Mass") },
                suffix = { Text("kg") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            OutlinedTextField(value =rollingResistanceCoefficient.toString(), modifier = Modifier.fillMaxWidth(),
                onValueChange = {rollingResistanceCoefficient = it },
                label = { Text("Crr") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            OutlinedTextField(value = dragCoefficient.toString(), modifier = Modifier.fillMaxWidth(),
                onValueChange = { dragCoefficient = it },
                label = { Text("Cdr") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            OutlinedTextField(value = frontalArea.toString(), modifier = Modifier.fillMaxWidth(),
                onValueChange = { dragCoefficient = it },
                label = { Text("Frontal Area") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                suffix = { Text("m2") },
            )
            OutlinedTextField(value = powerLoss.toString(), modifier = Modifier.fillMaxWidth(),
                onValueChange = { powerLoss = it },
                label = { Text("Power Loss") },
                suffix = { Text("%") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            OutlinedTextField(value = headwind.toString(), modifier = Modifier.fillMaxWidth(),
                onValueChange = { headwind = it },
                label = { Text("Headwind") },
                suffix = { Text("m/s") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                enabled = !isActive
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(checked = isActive, onCheckedChange = { isActive = it})
                Spacer(modifier = Modifier.width(10.dp))
                Text("Headwind automatic?")
            }

            FilledTonalButton(modifier = Modifier
                .fillMaxWidth()
                .height(50.dp), onClick = {
                onSubmit(getUpdatedConfigData())
            }) {
                Icon(Icons.Default.Done, contentDescription = "Save Power Config")
                Spacer(modifier = Modifier.width(5.dp))
                Text("Save")
            }

            FilledTonalButton(modifier = Modifier
                .fillMaxWidth()
                .height(50.dp), onClick = {
                onCancel()
            }) {
                Icon(Icons.Default.Close, contentDescription = "Cancel Editing")
                Spacer(modifier = Modifier.width(5.dp))
                Text("Cancel")
            }
            /*
            if (!isCreating) {
                FilledTonalButton(modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp), onClick = {
                    deleteDialogVisible = true
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Power Config")
                    Text("Delete")
                }
            }

            if (deleteDialogVisible){
                AlertDialog(onDismissRequest = { deleteDialogVisible = false },
                    confirmButton = { Button(onClick = {
                        deleteDialogVisible = false
                        onSubmit(null)
                    }) { Text("OK") }
                    },
                    dismissButton = { Button(onClick = {
                        deleteDialogVisible = false
                    }) { Text("Cancel") }
                },
                title = { Text("Delete Power Config") }, text = { Text("Really delete this config?") })
            }
            */
        }
    }
}