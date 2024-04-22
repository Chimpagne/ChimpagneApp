package com.monkeyteam.chimpagne.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Title
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.ui.components.ChimpagneButton
import com.monkeyteam.chimpagne.ui.components.GoBackButton
import com.monkeyteam.chimpagne.ui.components.Legend
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.navigation.Route
import com.monkeyteam.chimpagne.viewmodels.MyEventsViewModel


@ExperimentalMaterial3Api
@Composable
fun MyEventScreen(
    navObject: NavigationActions,
    myEventsViewModel: MyEventsViewModel = viewModel()
){
    val uiState by myEventsViewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Events") },
                modifier = Modifier.shadow(4.dp),
                navigationIcon = {
                    IconButton(onClick = { navObject.goBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "back")
                    }
                })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            LazyColumn {
                item {
                    Legend(
                        text = "Created Events:",
                        imageVector = Icons.Rounded.Create,
                        contentDescription = "Created Events"
                    )
                }
                if (uiState.createdEvents.isEmpty()) {
                    item {
                        Text(
                            text = "You have not created any events yet",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    items(uiState.createdEvents.values.toList()) { event ->
                        ChimpagneButton(
                            text = event.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                .fillMaxWidth(),
                            onClick = {
                                /* TODO Handle event button click */
                                navObject.navigateTo(Route.VIEW_DETAIL_EVENT_SCREEN + "/${event.id}" + "/true")
                            }
                        )
                    }
                }
                item {
                    Spacer(Modifier.height(16.dp))
                    Legend(
                        text = "Joined Events:",
                        imageVector = Icons.Rounded.Public,
                        contentDescription = "Joined Events"
                    )
                }
                if (uiState.joinedEvents.isEmpty()) {
                    item {
                        Text(
                            text = "You have not joined any events yet",
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                } else {
                    items(uiState.joinedEvents.values.toList()) { event ->
                        ChimpagneButton(
                            text = event.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                .fillMaxWidth(),
                            onClick = {
                                /* TODO Handle event button click */
                                navObject.navigateTo(Route.VIEW_DETAIL_EVENT_SCREEN + "/${event.id}" + "/false")
                            }
                        )
                    }
                }
            }
        }
    }
}