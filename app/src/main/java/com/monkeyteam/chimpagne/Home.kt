package com.monkeyteam.chimpagne

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.monkeyteam.chimpagne.ui.components.ChimpagneButton
import com.monkeyteam.chimpagne.ui.theme.ChimpagneTheme

@Composable
fun HomeScreen(){
    ChimpagneTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            ChimpagneButton(onClick = { /*TODO*/ })
            ChimpagneButton(onClick = { /*TODO*/ })
            ChimpagneButton(onClick = { /*TODO*/ })
        }

    }
}

@Preview
@Composable
fun PreviewHomeScreen() {
    ChimpagneTheme {
        HomeScreen()
    }
}

