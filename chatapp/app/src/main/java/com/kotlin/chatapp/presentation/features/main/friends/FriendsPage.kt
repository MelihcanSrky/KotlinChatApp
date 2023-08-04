package com.kotlin.chatapp.presentation.features.main.friends

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kotlin.chatapp.data.remote.dto.UserModelDto
import com.kotlin.chatapp.presentation.features.main.friends.components.FetchedUsersListTile
import com.kotlin.chatapp.presentation.features.main.friends.components.FriendsListTile
import com.kotlin.chatapp.presentation.features.main.friends.components.RequestsListTile
import com.kotlin.chatapp.presentation.features.main.friends.components.UserSearchTextField
import com.kotlin.chatapp.presentation.navigation.Screens
import com.kotlin.chatapp.presentation.theme.ChatAppTypo
import com.kotlin.chatapp.storage.SharedPrefs
import com.kotlin.chatapp.utils.IsSuccess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsPage(
    navController: NavController,
    mainPAgeNavController: NavController,
    viewModel: FriendsPageViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val ctx = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var hasFocus = remember {
        mutableStateOf(false)
    }

    LaunchedEffect(state) {
        if (state.token == "" && state.user_uuid == "") {
            val token = SharedPrefs.getInstance(ctx).token
            val user_uuid = SharedPrefs.getInstance(ctx).user_uuid
            viewModel.commit(state.copy(user_uuid = user_uuid, token = token))
        }
        if (state.isSuccess == IsSuccess.NONE && state.token != "") {
            viewModel.dispatch(FriendsPageAction.GetFriends)
            viewModel.dispatch(FriendsPageAction.GetRequests)
        }
        if (state.isSuccess == IsSuccess.SUCCESS && state.chatCreated && state.chatInfo != null) {
            navController.navigate(Screens.ChatPage.route + "/${state.chatInfo.chat_uuid}/${state.chatInfo.chatname}")
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    UserSearchTextField(
                        viewModel = viewModel,
                        focusRequester = focusRequester,
                        hasFocus = hasFocus,
                        focusManager = focusManager
                    )
                }
                if (state.searchQuery.isNotEmpty() || hasFocus.value) {
                    IconButton(onClick = {
                        viewModel.commit(state.copy(searchQuery = "", fetchedUsers = emptyList()))
                        focusManager.clearFocus()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            tint = MaterialTheme.colorScheme.error,
                            contentDescription = "clear"
                        )
                    }
                }
            }
        }
        if (hasFocus.value && state.fetchedUsers.isNotEmpty()) {
            items(state.fetchedUsers) { user ->
                FetchedUsersListTile(user = user, viewModel = viewModel)
            }
        } else if (!hasFocus.value && state.fetchedUsers.isNotEmpty() && state.searchQuery.isNotEmpty()) {
            items(state.fetchedUsers) { user ->
                FetchedUsersListTile(user = user, viewModel = viewModel)
            }
        }
        if (state.requests.isNotEmpty()) {
            item {
                Text(
                    text = "Requests",
                    style = ChatAppTypo.headlineSmall,
                    color = MaterialTheme.colorScheme.onTertiary
                )
                Divider(color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.2f))
            }
            items(state.requests) { sender ->
                RequestsListTile(sender, viewModel)
            }
        }
        if (state.friends.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Friends",
                    style = ChatAppTypo.headlineSmall,
                    color = MaterialTheme.colorScheme.onTertiary
                )
                Divider(color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.2f))
            }
            items(state.friends) { friend ->
                FriendsListTile(friend = friend, viewModel = viewModel)
            }
        }
    }
}