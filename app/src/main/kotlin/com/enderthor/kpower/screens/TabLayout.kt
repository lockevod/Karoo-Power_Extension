package com.enderthor.kpower.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun TabLayout(
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Power", "Help")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth(),
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(text = title, fontSize = 11.sp) },
                )
            }
        }

        when (selectedTabIndex) {

            0 -> Help()
            1 -> ConfigDataAppNavHost()
        }
    }
}

@Composable
fun Help() {
    Column(
        modifier = Modifier
            .padding(5.dp)
            .verticalScroll(rememberScrollState())
            .fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)
       ,
    ) {
        val helpText = """
            Help for Calculating Cycling Wattage
            
            To calculate cycling wattage, you need to add a sensor (scan sensors) and provide the following parameters:

            - Weight of Bike: Include the weight of your bike along with any additional gear.
            - Rolling Resistance Coefficient: Depends on the type of surface and the tires you are using.
            - Aerodynamic Drag Coefficient: Depends on your position on the bike and your frontal area
            - Frontal Area: The area of your body that is exposed to the wind (m2)
            - Power Losses: Includes losses due to chain resistance and derailleur pulleys.
            - Headwind: The wind speed in the opposite direction of your movement. You can insert a constant headwind, use automatic option with openmeteo or use your api key from openweathermap.
            - FTP: Your Functional Threshold Power (in watts). If you don't know your FTP, you can use the default value of 200 watts.
   
           Read documentation for more information about typical parameters.(github)
           KPower use your imperial or metric units to calculate the power. Please fill weight in the same unit. 
           
        """.trimIndent()
        Text(text = helpText)
    }
}

@Preview(name = "karoo", device = "spec:width=480px,height=800px,dpi=300")
@Composable
private fun PreviewTabLayout() {
    TabLayout(
    )
}