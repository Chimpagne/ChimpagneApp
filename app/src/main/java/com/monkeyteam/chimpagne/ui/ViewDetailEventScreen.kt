package com.monkeyteam.chimpagne.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.monkeyteam.chimpagne.ui.components.ChimpagneButton
import com.monkeyteam.chimpagne.ui.components.GoBackButton
import com.monkeyteam.chimpagne.ui.components.Legend
import com.monkeyteam.chimpagne.ui.components.SimpleTagChip
import com.monkeyteam.chimpagne.ui.components.TagChip
import com.monkeyteam.chimpagne.viewmodels.EventViewModel
import java.text.DateFormat
import java.util.Calendar
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.navigation.Route
import com.monkeyteam.chimpagne.ui.theme.ChimpagneFontFamily

@Composable
fun ViewDetailEventScreen(
    navObject: NavigationActions,
    eventViewModel: EventViewModel = viewModel(),
    canEditEvent: Boolean = false
) {
    val uiState by eventViewModel.uiState.collectAsState()

    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            GoBackButton(navigationActions = navObject)
        }
        Spacer(Modifier.height(16.dp))
        LazyColumn {
            item {
                Text(
                    /*TODO USE VIEWMODEL*/
                    text = "EVENT TITLE EXAMPLE",
                    fontSize = 30.sp,
                    fontFamily = ChimpagneFontFamily,
                    modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    listOf("tag1", "tag2","tag3","tag4","tag5","tag6","tag7",).forEach { tag -> SimpleTagChip(tag)}
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    /*TODO USE VIEWMODEL*/
                    text = "We hold these truths to be self-evident, that all men are created equal, that they are endowed, by their Creator, with certain unalienable rights, that among these are life, liberty, and the pursuit of happiness.--That to secure these rights, governments are instituted among men, deriving their just powers from the consent of the governed, that whenever any form of government becomes destructive of these ends, it is the right of the people to alter or to abolish it, and to institute new government, laying its foundation on such principles, and organizing its powers in such form, as to them shall seem most likely to effect their safety and happiness. Prudence, indeed, will dictate that Governments long established should not be changed for light and transient causes; and accordingly all experience hath shewn, that mankind are more disposed to suffer, while evils are sufferable, than to right themselves by abolishing the forms to which they are accustomed. But when a long train of abuses and usurpations, pursuing invariably the same Object evinces a design to reduce them under absolute Despotism, it is their right, it is their duty, to throw off such Government, and to provide new Guards for their future security.--Such has been the patient sufferance of these Colonies; and such is now the necessity which constrains them to alter their former Systems of Government. The history of the present King of Great Britain is a history of repeated injuries and usurpations, all having in direct object the establishment of an absolute Tyranny over these States. To prove this, let Facts be submitted to a candid world.",
                    fontSize = 20.sp,
                    fontFamily = ChimpagneFontFamily,
                    color = Color.DarkGray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .absolutePadding(left = 16.dp, right = 16.dp)
                )
                Spacer(Modifier.height(16.dp))
                Legend(
                    /*TODO USE VIEWMODEL*/
                    text = " From " + DateFormat.getDateInstance(DateFormat.MEDIUM).format(Calendar.getInstance().time) +
                            " at " + DateFormat.getTimeInstance(DateFormat.SHORT).format(Calendar.getInstance().time) +
                            "\n until " + DateFormat.getDateInstance(DateFormat.MEDIUM).format(Calendar.getInstance().time) +
                            " at " + DateFormat.getTimeInstance(DateFormat.SHORT).format(Calendar.getInstance().time),
                    imageVector = Icons.Rounded.CalendarToday,
                    contentDescription = "event date"
                )
                Spacer(Modifier.height(16.dp))
                Legend(
                    /*TODO USE VIEWMODEL*/
                    text = "EPFL",
                    imageVector = Icons.Rounded.LocationOn,
                    contentDescription = "event location"
                )
                Spacer(Modifier.height(16.dp))
                if(canEditEvent){
                    ChimpagneButton(
                        text = "EDIT THIS EVENT",
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
                        onClick = {
                            /* TODO Handle event button click */

                        }
                    )
                }
            }

        }

    }

}