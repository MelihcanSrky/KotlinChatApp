package com.kotlin.chatapp.presentation.features.main.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kotlin.chatapp.domain.model.BottomBarItems
import com.kotlin.chatapp.presentation.features.main.chats.ChatsPage
import com.kotlin.chatapp.presentation.features.main.friends.FriendsPage
import com.kotlin.chatapp.presentation.navigation.Screens
import com.kotlin.chatapp.presentation.theme.ChatAppTypo
import com.kotlin.chatapp.storage.SharedPrefs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(
    navController: NavController,
    viewModel: MainPageViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val ctx = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mainPageNavController = rememberNavController()

    LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_CREATE) {
            viewModel.commit(state.copy(token = SharedPrefs.getInstance(ctx).token))
        }
    }.also { observer ->
        lifecycleOwner.lifecycle.addObserver(observer)
    }

    val bottomBarItems = listOf(
        BottomBarItems(
            name = "Chats",
            route = Screens.ChatsPage.route,
            icon = Icons.Rounded.Call
        ),
        BottomBarItems(
            name = "Friends",
            route = Screens.FriendsPage.route,
            icon = Icons.Rounded.Face
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.97f),
                    titleContentColor = MaterialTheme.colorScheme.onTertiary
                ),
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Chat App", style = ChatAppTypo.headlineSmall)
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                contentColor = Color.Transparent,
                tonalElevation = 0.dp
            ) {
                bottomBarItems.forEach { item ->
                    val selected =
                        item.route == mainPageNavController.currentBackStackEntryAsState().value?.destination?.route

                    NavigationBarItem(
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.secondary,
                            unselectedTextColor = MaterialTheme.colorScheme.secondary,
                            indicatorColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        ),
                        selected = selected,
                        onClick = { mainPageNavController.navigate(item.route) },
                        label = {
                            Text(text = item.name)
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = "${item.route} icon"
                            )
                        })
                }
            }
        },
        contentWindowInsets = WindowInsets.safeContent
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            NavHost(
                navController = mainPageNavController,
                startDestination = Screens.ChatsPage.route
            ) {
                composable(Screens.ChatsPage.route) {
                    ChatsPage(navController = navController)
                }
                composable(Screens.FriendsPage.route) {
                    FriendsPage(navController = mainPageNavController)
                }
            }
        }
    }
}