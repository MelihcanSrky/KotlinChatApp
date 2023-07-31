package com.kotlin.chatapp.presentation.features.auth.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kotlin.chatapp.presentation.features.auth.components.CATextField
import com.kotlin.chatapp.presentation.features.auth.register.RegisterAction
import com.kotlin.chatapp.presentation.navigation.Screens
import com.kotlin.chatapp.presentation.theme.ChatAppTypo
import com.kotlin.chatapp.presentation.theme.MCSRadiusMedium
import com.kotlin.chatapp.storage.SharedPrefs
import com.kotlin.chatapp.utils.IsSuccess
import com.kotlin.chatapp.utils.extensions.isPasswordValid
import com.kotlin.chatapp.utils.extensions.isUsernameValid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(
    navController: NavController,
    viewModel: LoginPageViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val ctx = LocalContext.current

    LaunchedEffect(state) {
        if (state.isSuccess == IsSuccess.SUCCESS) {
            SharedPrefs.getInstance(ctx).token = state.token
            SharedPrefs.getInstance(ctx).user_uuid = state.user_uuid
            navController.navigate(Screens.MainPage.route)
        }
    }

    Scaffold(
        modifier = Modifier.padding(horizontal = 30.dp)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                text = "Sign In",
                style = ChatAppTypo.headlineLarge,
                color = MaterialTheme.colorScheme.surface
            )
            Spacer(modifier = Modifier.height(24.dp))
            CATextField(
                value = state.username,
                onValueChange = {
                    viewModel.commit(state.copy(username = it))
                    viewModel.buttonState()
                },
                isError = if (state.username == "") false else !state.username.isUsernameValid(),
                label = "User Name"
            )
            Spacer(modifier = Modifier.height(8.dp))
            CATextField(
                value = state.password,
                onValueChange = {
                    viewModel.commit(state.copy(password = it))
                    viewModel.buttonState()
                },
                isError = if (state.password == "") false else !state.password.isPasswordValid(),
                isPassword = true,
                label = "Password"
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (state.isSuccess == IsSuccess.ERROR) state.errorMessage else "",
                    style = ChatAppTypo.bodyMedium.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.error,
                )
                TextButton(onClick = {
                    navController.navigate(Screens.Register.route)
                }) {
                    Text(
                        text = "Create account!",
                        style = ChatAppTypo.bodyMedium.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 16.dp),
                shape = MCSRadiusMedium,
                enabled = state.isButtonEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.background,
                    disabledContainerColor = MaterialTheme.colorScheme.onBackground,
                    disabledContentColor = MaterialTheme.colorScheme.surface
                ),
                onClick = {
                    viewModel.dispatch(LoginAction.LoginUser)
                }) {
                if (state.isSuccess == IsSuccess.LOADING) CircularProgressIndicator(
                    modifier = Modifier
                        .height(18.dp)
                        .width(18.dp),
                    color = MaterialTheme.colorScheme.surface
                )
                else Text(text = "Sign In", style = ChatAppTypo.bodyMedium)
            }
        }
    }
}