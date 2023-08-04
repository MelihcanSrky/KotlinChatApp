package com.kotlin.chatapp.presentation.features.main.friends.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import com.kotlin.chatapp.presentation.features.main.friends.FriendsPageAction
import com.kotlin.chatapp.presentation.features.main.friends.FriendsPageViewModel
import com.kotlin.chatapp.presentation.theme.ChatAppTypo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSearchTextField(
    viewModel: FriendsPageViewModel,
    focusRequester: FocusRequester,
    hasFocus: MutableState<Boolean>,
    focusManager: FocusManager
) {
    OutlinedTextField(
        value = viewModel.state.value.searchQuery,
        onValueChange = {
            viewModel.commit(viewModel.state.value.copy(searchQuery = it))
        },
        textStyle = ChatAppTypo.bodyLarge.copy(color = MaterialTheme.colorScheme.onTertiary),
        label = {
            Text(text = "Search a User", color = MaterialTheme.colorScheme.onTertiary)
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