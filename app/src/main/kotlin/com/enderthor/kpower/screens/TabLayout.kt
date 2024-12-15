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
            0 -> ConfigDataAppNavHost()
            1 -> Help()
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
            
            To calculate cycling wattage, you need to provide the following parameters:

            - **Weight of Bike**: Include the weight of your bike along with any additional gear (in kg).
            - **Rolling Resistance Coefficient**: Depends on the type of surface and the tires you are using.
            - **Aerodynamic Drag Coefficient**: Depends on your position on the bike and your frontal area
            - **Frontal Area**: The area of your body that is exposed to the wind (m2)
            - **Power Losses**: Includes losses due to chain resistance and derailleur pulleys.
            - **Headwind**: The wind speed in the opposite direction of your movement. You can insert a constant headwind or check automatic option.
            - **FTP**: Your Functional Threshold Power (in watts). If you don't know your FTP, you can use the default value of 200 watts.
            Automatic is not available at this moment (beta) then 0.0 m/s is used.
           
            Tested with karoo 3 (version > 1.524) and metric configuration.

            Here are some typical values for these parameters:

            _Air Drag / Frontal Area_

            0.25 / 0.30 AEROBARS COM BIKE
            0.35 / 0.40 DROPS BIKE
            0.45 / 0.55 HOODS BIKE
            0.60 / 0.75 TOPS BIKE
            0.80 / 0.90 MTB BIKE

            _Rolling Resistance_
            0.0045 TOP RANGE ROAD TIRES
            0.0065 MEDIUM RANGE ROAD TIRES
            0.0085 LOW RANGE ROAD TIRES
            0.0095 MTB TIRES

            _Power Losses_
            1.0% SRAM CERAMIC / FORCE
            1.3% SHIMANO ULTEGRA - DURACE
            2.0% SRAM EAGLE
            2.2% SHIMANO XTR
            3%-4% SHIMANO OTHER

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