package com.monkeyteam.chimpagne.ui.theme

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.location.Location

@Preview
@Composable
fun AccountCreation(){
    // Make location available
    // Make image available
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var preferredLanguageEnglish by remember { mutableStateOf(false) }
    var location by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
        //verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.padding(10.dp))
        Text(
            text = if (preferredLanguageEnglish) "Create your Account" else "Créer votre compte",
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.padding(16.dp))
        IconButton(
            onClick = { /* TODO showPhotoChooser*/},
            modifier = Modifier
                .size(100.dp)
                .border(1.dp, Color.Black, CircleShape)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_placeholder_profile),
                contentDescription = "Placeholder for user icon"
            )
        }
        Spacer(modifier = Modifier.padding(16.dp))
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = {
                if (preferredLanguageEnglish) {
                    Text("First Name")
                } else {
                    Text("Prénom")
                }
            }
        )
        Spacer(modifier = Modifier.padding(16.dp))
        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = {
                if (preferredLanguageEnglish) {
                    Text("Last Name")
                } else {
                    Text("Nom de famille")
                }
            }
        )
        Spacer(modifier = Modifier.padding(16.dp))

        OutlinedTextField(
            value = location,
            onValueChange = {location = it},
            label = {
                if (preferredLanguageEnglish) {
                    Text("Choose your City")
                } else {
                    Text("Choisissez votre ville")
                }
            }
            )
        Spacer(modifier = Modifier.padding(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Language")
            Spacer(modifier = Modifier.padding(16.dp))
            Switch(
                checked = preferredLanguageEnglish,
                onCheckedChange = { preferredLanguageEnglish = it },
                thumbContent = {
                    if (preferredLanguageEnglish) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_english),
                            contentDescription = "English"
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_french),
                            contentDescription = "French"
                        )
                    }
                }
            )
        }
        Spacer(modifier =  Modifier.padding(16.dp))
        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .width(210.dp)
                .height(50.dp)
            ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_logout),
                contentDescription = "Logout icon"
            )
            Spacer(modifier = Modifier.padding(8.dp))
            if (preferredLanguageEnglish) {
                Text("Create Account")
            } else {
                Text("Créer un compte")
            }
        }

    }
}