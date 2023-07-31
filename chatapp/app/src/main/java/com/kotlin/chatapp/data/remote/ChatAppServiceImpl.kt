package com.kotlin.chatapp.data.remote

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import com.kotlin.chatapp.data.remote.dto.ChatsModelDto
import com.kotlin.chatapp.data.remote.dto.MessageDto
import com.kotlin.chatapp.data.remote.dto.MessagesDto
import com.kotlin.chatapp.di.AppModule
import com.kotlin.chatapp.domain.model.AuthUserResponse
import com.kotlin.chatapp.domain.model.ChatsModel
import com.kotlin.chatapp.domain.model.CreateChatModel
import com.kotlin.chatapp.domain.model.CreateUser
import com.kotlin.chatapp.domain.model.FriendsModel
import com.kotlin.chatapp.domain.model.LoginUserModel
import com.kotlin.chatapp.domain.model.LoginUserResponse
import com.kotlin.chatapp.domain.model.Message
import com.kotlin.chatapp.domain.model.MessagesModel
import com.kotlin.chatapp.domain.model.SendRequestModel
import com.kotlin.chatapp.storage.SharedPrefs
import com.kotlin.chatapp.utils.Resource
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.url
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class ChatAppServiceImpl(
    private val client: HttpClient,
) : ChatAppService {

    override suspend fun registerUser(user: CreateUser): Resource<AuthUserResponse> {
        return try {
            val url = ChatAppService.Endpoints.RegisterUser.url
            val response = client.post<AuthUserResponse> {
                url(url)
                body = user
                contentType(ContentType.Application.Json)
            }
            when (response.status) {
                201 -> Resource.Success(response)
                401 -> Resource.Error("Username already exists!")
                else -> Resource.Error("Something went wrong!")
            }
        } catch (e: Exception) {
            println(e.localizedMessage)
            Resource.Error("Username already exists!")
        }
    }

    override suspend fun loginUser(user: LoginUserModel): Resource<LoginUserResponse> {
        return try {
            val url = ChatAppService.Endpoints.LoginUser.url
            val response = client.post<LoginUserResponse> {
                url(url)
                body = user
                contentType(ContentType.Application.Json)
            }
            when (response.status) {
                201 -> Resource.Success(response)
                401 -> Resource.Error(response.message)
                else -> Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error("Something went wrong!")
        }
    }

    override suspend fun getChats(user_uuid: String, token: String): Resource<ChatsModel> {
        return try {
            val url = ChatAppService.Endpoints.GetChats.url
            val response = client.get<ChatsModelDto>(url) {
                url {
                    parameters.append("user_uuid", user_uuid)
                }
                headers {
                    append(HttpHeaders.Authorization, token)
                }
            }
            when (response.status) {
                200 -> Resource.Success(response.toChats())
                else -> Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error("Something went wrong!")
        }
    }

    override suspend fun getFriends(user_uuid: String, token: String): Resource<FriendsModel> {
        return try {
            val url = ChatAppService.Endpoints.GetFriends.url
            val response = client.get<FriendsModel>(url) {
                parameter("user_uuid", user_uuid)
                header("Authorization", token)
            }
            when (response.status) {
                200 -> Resource.Success(response)
                else -> Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error("Something went wrong!")
        }
    }

    override suspend fun getRequests(user_uuid: String, token: String): Resource<FriendsModel> {
        return try {
            val url = ChatAppService.Endpoints.GetRequests.url
            val response = client.get<FriendsModel>(url) {
                parameter("user_uuid", user_uuid)
                header("Authorization", token)
            }
            when (response.status) {
                200 -> Resource.Success(response)
                else -> Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error("Something went wrong!")
        }
    }

    override suspend fun getAllMessages(chat_uuid: String, user_uuid: String, token: String): Resource<MessagesModel> {
        return try {
            val url = ChatAppService.Endpoints.GetAllMessages.url.replace("{chat_uuid}", chat_uuid)
            val response = client.get<MessagesDto>(url) {
                parameter("user_uuid", user_uuid)
                header("Authorization", token)
            }
            when (response.status) {
                200 -> Resource.Success(response.toMessagesModel())
                else -> Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error("Something went wrong!")
        }
    }

    override suspend fun sendRequest(
        user_uuid: String,
        sender_uuid: String,
        token: String
    ): Resource<FriendsModel> {
        return try {
            var reqBody = SendRequestModel(
                user_uuid, sender_uuid
            )
            val url = ChatAppService.Endpoints.SendRequest.url
            val response = client.post<FriendsModel>(url) {
                header("Authorization", token)
                body = reqBody
                contentType(ContentType.Application.Json)
            }
            when (response.status) {
                201 -> Resource.Success(response)
                else -> Resource.Error(response.message)
            }
        } catch (e: Exception) {
            println(e.localizedMessage)
            Resource.Error("Something went wrong!")
        }
    }

    override suspend fun acceptOrDecline(
        reqBody: SendRequestModel,
        token: String,
        accept: Boolean
    ): Resource<FriendsModel> {
        return try {
            val url = ChatAppService.Endpoints.AcceptOrDecline.url
            val response = client.put<FriendsModel>(url) {
                headers {
                    append(HttpHeaders.Authorization, token)
                }
                url {
                    parameters.append("accept", accept.toString())
                }
                contentType(ContentType.Application.Json)
                body = reqBody
            }
            when (response.status) {
                201 -> Resource.Success(response)
                else -> Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error("Something went wrong!")
        }
    }

    override suspend fun createChat(reqBody: CreateChatModel, token: String): Resource<FriendsModel> {
        return try {
            val url = ChatAppService.Endpoints.CreateChat.url
            val response = client.post<FriendsModel>(url) {
                header("Authorization", token)
                body = reqBody
            }
            when (response.status) {
                201 -> Resource.Success(response)
                else -> Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error("Something went wrong!")
        }
    }

    override suspend fun getUsers(user_uuid: String, token: String, searchQuery: String): Resource<FriendsModel> {
        return try {
            val url = ChatAppService.Endpoints.GetUsers.url.replace("{user_uuid}", user_uuid)
            val response = client.get<FriendsModel>(url) {
                headers {
                    append(HttpHeaders.Authorization, token)
                }
                url {
                    parameters.append("search", searchQuery)
                }
            }
            when (response.status) {
                200 -> Resource.Success(response)
                else -> Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error("Something went wrong!")
        }
    }
}