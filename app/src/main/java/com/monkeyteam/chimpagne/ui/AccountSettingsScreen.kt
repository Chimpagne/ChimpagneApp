import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.navigation.Route
import com.monkeyteam.chimpagne.ui.theme.ChimpagneFontFamily
import com.monkeyteam.chimpagne.ui.theme.md_theme_light_primary
import com.monkeyteam.chimpagne.ui.utilities.ProfileImage
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettings(
    navObject: NavigationActions,
    accountViewModel: AccountViewModel,
    logout: () -> Unit = {}
) {
  val accountViewModelState by accountViewModel.uiState.collectAsState()

  Scaffold(
      topBar = {
        TopAppBar(
            title = {
              Row(
                  modifier = Modifier.fillMaxWidth(),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.Center) {
                    Text(text = "Account Settings", style = MaterialTheme.typography.titleLarge)
                  }
            },
            colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = md_theme_light_primary, // Purple
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White),
            navigationIcon = {
              IconButton(onClick = { navObject.goBack() }) {
                Icon(
                    painter = painterResource(id = R.drawable.go_back), contentDescription = "Back")
              }
            })
      },
      floatingActionButton = {
        // TODO has to be changed to edit
        FloatingActionButton(onClick = { navObject.navigateTo(Route.ACCOUNT_EDIT_SCREEN) }) {
          Icon(
              painter = painterResource(id = R.drawable.edit_pen),
              contentDescription = "Edit",
              modifier = Modifier.size(24.dp))
        }
      },
      floatingActionButtonPosition = FabPosition.End,
      containerColor = MaterialTheme.colorScheme.background) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Spacer(modifier = Modifier.height(10.dp))
              ProfileImage(imageUri = accountViewModelState.imageUri)

              SettingItem(
                  label = "First Name",
                  value = accountViewModelState.currentUserAccount?.firstName ?: "",
                  modifierText = Modifier.testTag("firstNameTextField"))
              SettingItem(
                  label = "Last Name",
                  value = accountViewModelState.currentUserAccount?.lastName ?: "",
                  modifierText = Modifier.testTag("lastNameTextField"))
              SettingItem(
                  label = "Location",
                  value = accountViewModelState.currentUserAccount?.location?.name ?: "",
                  modifierText = Modifier.testTag("locationTextField"))

              Spacer(modifier = Modifier.height(8.dp))
              // TODO has to be changed to log out
              Button(onClick = { logout() }) { Text("Log Out") }
            }
      }
}

@Composable
fun SettingItem(label: String, value: String, modifierText: Modifier) {
  Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
    Text(
        modifier = modifierText,
        text = label,
        style =
            androidx.compose.ui.text.TextStyle(
                fontSize = 18.sp, color = md_theme_light_primary, fontFamily = ChimpagneFontFamily))
    Text(
        text = value,
        style =
            androidx.compose.ui.text.TextStyle(
                fontSize = 23.sp, color = md_theme_light_primary, fontFamily = ChimpagneFontFamily))
    Spacer(modifier = Modifier.height(8.dp))
  }
}
