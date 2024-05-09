package com.monkeyteam.chimpagne.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material.icons.rounded.HourglassBottom
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.utils.timestampToStringWithDateAndTime
import com.monkeyteam.chimpagne.ui.components.Legend
import com.monkeyteam.chimpagne.ui.components.SimpleTagChip
import com.monkeyteam.chimpagne.ui.components.SmallSimpleTagChip
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.navigation.Route
import com.monkeyteam.chimpagne.ui.theme.ChimpagneFontFamily
import com.monkeyteam.chimpagne.viewmodels.MyEventsViewModel

@ExperimentalMaterial3Api
@Composable
fun MyEventsScreen(navObject: NavigationActions, myEventsViewModel: MyEventsViewModel) {
  val uiState by myEventsViewModel.uiState.collectAsState()
  Scaffold(
      topBar = {
        TopAppBar(
            title = {
              Text(
                  stringResource(id = R.string.my_events_screen_name),
                  Modifier.testTag("screen title"))
            },
            modifier = Modifier.shadow(4.dp),
            navigationIcon = {
              IconButton(onClick = { navObject.goBack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "back")
              }
            })
      }) { innerPadding ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Spacer(Modifier.height(16.dp))

              LazyColumn {
                item {
                  Legend(
                      text = stringResource(id = R.string.my_events_screen_created_event_list_name),
                      imageVector = Icons.Rounded.Create,
                      contentDescription = "Created Events")
                }
                if (uiState.createdEvents.isEmpty()) {
                  item {
                    Text(
                        text =
                            stringResource(id = R.string.my_events_screen_empty_create_event_list),
                        modifier = Modifier.padding(16.dp).testTag("empty create event list"))
                  }
                } else {
                  items(uiState.createdEvents.values.toList()) { event ->
                    EventCard(
                        event = event,
                        modifier = Modifier.testTag("a created event")) {
                          navObject.navigateTo(Route.VIEW_DETAIL_EVENT_SCREEN + "/${event.id}")
                        }
                  }
                }
                item {
                  Spacer(Modifier.height(16.dp))
                  Legend(
                      text = stringResource(id = R.string.my_events_screen_joined_event_list_name),
                      imageVector = Icons.Rounded.Public,
                      contentDescription = "Joined Events")
                }
                if (uiState.joinedEvents.isEmpty()) {
                  item {
                    Text(
                        text = stringResource(id = R.string.my_events_screen_empty_join_event_list),
                        modifier = Modifier.padding(16.dp).testTag("empty join event list"))
                  }
                } else {
                  items(uiState.joinedEvents.values.toList()) { event ->
                    EventCard(
                        event = event,
                        modifier = Modifier.testTag("a joined event")) {
                          navObject.navigateTo(Route.VIEW_DETAIL_EVENT_SCREEN + "/${event.id}")
                        }
                  }
                }

                item {
                  Spacer(Modifier.height(16.dp))
                  Legend(
                      text = stringResource(id = R.string.my_events_screen_past_events_list_name),
                      imageVector = Icons.Rounded.HourglassBottom,
                      "")
                }
                if (uiState.pastEvents.isEmpty()) {
                  item {
                    Text(
                        text =
                            stringResource(id = R.string.my_events_screen_empty_past_events_list),
                        modifier = Modifier.padding(16.dp).testTag("empty_past_events_list"))
                  }
                } else {
                  items(uiState.pastEvents.values.toList()) { event ->
                    EventCard(
                        event = event,
                        modifier = Modifier.testTag("past_event_card")) {
                          navObject.navigateTo(Route.VIEW_DETAIL_EVENT_SCREEN + "/${event.id}")
                        }
                  }
                }
              }
            }
      }
}


@Composable
fun EventCard(
    event: ChimpagneEvent,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickable { onClick() }
            .aspectRatio(1.6f),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                ImageWithBlackFilterOverlay(event.image)
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = timestampToStringWithDateAndTime(event.startsAtTimestamp),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = event.location.name,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TagList(tags = event.tags, Modifier.weight(1f).padding(end = 8.dp))
                    Icon(
                        imageVector = if (event.public) Icons.Filled.Public else Icons.Filled.Lock,
                        contentDescription = if (event.public) "Public Event" else "Private Event",
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

@Composable
fun ImageWithBlackFilterOverlay(imageString: String = "") {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        if (imageString.isEmpty()) {
            Image(
                painter = painterResource(id = R.drawable.chimpagne_app_logo),
                contentDescription = "Default Logo",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
            )
        } else {
            Image(
                painter = rememberAsyncImagePainter(model = imageString),
                contentDescription = "Event Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Black.copy(alpha = 0.5f))
        )
    }
}


@Composable
fun TagList(tags: List<String>, modifier: Modifier = Modifier) {
    LazyRow(modifier = modifier) {
        items(tags) { tag ->
            SmallSimpleTagChip(tag = tag)
        }
    }
}