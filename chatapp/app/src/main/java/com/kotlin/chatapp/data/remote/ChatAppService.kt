package com.kotlin.chatapp.data.remote

import com.kotlin.chatapp.domain.model.AuthUserResponse
import com.kotlin.chatapp.domain.model.ChatCreatedModel
import com.kotlin.chatapp.domain.model.ChatsModel
import com.kotlin.chatapp.domain.model.CreateChatModel
import com.kotlin.chatapp.domain.model.CreateUser
import com.kotlin.chatapp.domain.model.FriendsModel
import com.kotlin.chatapp.domain.model.LoginUserModel
import com.kotlin.chatapp.domain.model.LoginUserResponse
import com.kotlin.chatapp.domain.model.Message
import com.kotlin.chatapp.domain.model.MessagesModel
import com.kotlin.chatapp.domain.model.SendRequestModel
import com.kotlin.chatapp.utils.Resource

interface ChatAppService {

    suspend fun registerUser(user: CreateUser) : Resource<AuthUserResponse>

    suspend fun loginUser(user: LoginUserModel) : Resource<LoginUserResponse>

    suspend fun getChats(user_uuid: String, token: String) : Resource<ChatsModel>

    suspend fun getFriends(user_uuid: String, token: String) : Resource<FriendsModel>

    suspend fun getRequests(user_uuid: String, token: String) : Resource<FriendsModel>

    suspend fun getAllMessages(chat_uuid: String, user_uuid: String, token: String): Resource<MessagesModel>

    suspend fun sendRequest(user_uuid: String, sender_uuid: String, token: String) : Resource<FriendsModel>

    suspend fun acceptOrDecline(reqBody: SendRequestModel, token: String, accept: Boolean) : Resource<FriendsModel>

    suspend fun createChat(reqBody: CreateChatModel, token: String) : Resource<ChatCreatedModel>

    suspend fun getUsers(user_uuid: String, token: String, searchQuery: String) : Resource<FriendsModel>


    companion object {
        const val BASE_URL = "http://192.168.1.84:5000"
    }

    sealed class Endpoints(val url: String) {
        object GetAllMessages: Endpoints("$BASE_URL/api/chat/{chat_uuid}/messages")
        object RegisterUser: Endpoints("$BASE_URL/api/users/create")
        object LoginUser: Endpoints("$BASE_URL/api/users/login")
        object GetChats: Endpoints("$BASE_URL/api/chats")
        object GetFriends: Endpoints("$BASE_URL/api/friends")
        object GetRequests: Endpoints("$BASE_URL/api/requests")
        object SendRequest : Endpoints("$BASE_URL/api/friends/send")
        object AcceptOrDecline : Endpoints("$BASE_URL/api/friends/accept")
        object CreateChat : Endpoints("$BASE_URL/api/chats/create")
        object GetUsers : Endpoints("$BASE_URL/api/users/{user_uuid}")
    }
}