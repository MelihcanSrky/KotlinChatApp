package com.kotlin.chatapp.presentation.features.main.friends

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kotlin.chatapp.data.remote.dto.UserModelDto
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
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = {
                    viewModel.commit(state.copy(searchQuery = it))
                },
                label = {
                    Text(text = "Search a User")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        hasFocus.value = it.hasFocus
                    }
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                maxLines = 1,
                trailingIcon = {
                    IconButton(onClick = {
                        viewModel.dispatch(FriendsPageAction.GetUsers)
                        focusManager.clearFocus()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = "Send"
                        )
                    }
                }
            )
        }
        if (hasFocus.value && state.fetchedUsers.isNotEmpty()) {
            items(state.fetchedUsers) { user ->
                fetchedUsersListTile(user = user, viewModel = viewModel)
            }
        } else if (!hasFocus.value && state.fetchedUsers.isNotEmpty()) {
            items(state.fetchedUsers) { user ->
                fetchedUsersListTile(user = user, viewModel = viewModel)
            }
        }
//        else if (!hasFocus.value && state.fetchedUsers.isEmpty()) {
//            item {
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(
//                        text = "There is nothing to show!",
//                        style = ChatAppTypo.bodyMedium,
//                        color = MaterialTheme.colorScheme.onTertiary
//                    )
//                }
//            }
//        }
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
                requestsListTile(sender, viewModel)
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
                friendsListTile(friend = friend, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun requestsListTile(sender: UserModelDto, viewModel: FriendsPageViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = sender.username,
            style = ChatAppTypo.titleMedium,
            color = MaterialTheme.colorScheme.onTertiary
        )
        Row {
            IconButton(onClick = {
                viewModel.dispatch(
                    FriendsPageAction.AcceptOrDecline(
                        sender.uuid,
                        accept = true
                    )
                )
            }) {
                Icon(
                    imageVector = Icons.Default.Check,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "Send"
                )
            }
            IconButton(onClick = {
                viewModel.dispatch(
                    FriendsPageAction.AcceptOrDecline(
                        sender.uuid,
                        accept = false
                    )
                )
            }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    tint = MaterialTheme.colorScheme.error,
                    contentDescription = "Send"
                )
            }
        }
    }
    Divider(color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.2f))
}

@Composable
fun friendsListTile(friend: UserModelDto, viewModel: FriendsPageViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                viewModel.dispatch(FriendsPageAction.CreateChat(friend_uuid = friend.uuid))
            }
            .padding(vertical = 8.dp),
    ) {
        Text(
            text = friend.username,
            style = ChatAppTypo.titleMedium,
            color = MaterialTheme.colorScheme.onTertiary
        )
        Text(
            text = friend.firstname + " " + friend.lastname,
            style = ChatAppTypo.bodySmall,
            color = MaterialTheme.colorScheme.secondary
        )
    }
    Divider(color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.2f))
}

@Composable
fun fetchedUsersListTile(user: UserModelDto, viewModel: FriendsPageViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (viewModel.state.value.friends.map { it.username }.contains(user.username))
            IconButton(enabled = false, onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Check,
                    tint = MaterialTheme.colorScheme.secondary,
                    contentDescription = "exists"
                )
            }
        else
            IconButton(onClick = {
                viewModel.dispatch(FriendsPageAction.SendRequest(user.uuid))
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "Send"
                )
            }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        ) {
            Text(
                text = user.username,
                style = ChatAppTypo.titleMedium,
                color = MaterialTheme.colorScheme.onTertiary
            )
            Text(
                text = user.firstname + " " + user.lastname,
                style = ChatAppTypo.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
    Divider(color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.2f))
}