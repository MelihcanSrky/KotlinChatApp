package com.kotlin.chatapp.di

import android.content.Context
import com.kotlin.chatapp.data.remote.ChatAppService
import com.kotlin.chatapp.data.remote.ChatAppServiceImpl
import com.kotlin.chatapp.data.remote.ChatSocketService
import com.kotlin.chatapp.data.remote.ChatSocketServiceImpl
import com.kotlin.chatapp.domain.model.ErrorExceptionModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.HttpResponseValidator
import io.ktor.client.features.ResponseException
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.Logging
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.statement.request
import io.ktor.client.statement.response
import io.ktor.http.HttpStatusCode
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(CIO) {
            install(Logging)
            install(WebSockets)
            install(JsonFeature) {
                serializer = KotlinxSerializer(
                    json = kotlinx.serialization.json.Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
        }
    }

    @Provides
    @Singleton
    fun provideMessageService(client: HttpClient): ChatAppService {
        return ChatAppServiceImpl(client)
    }

    @Provides
    @Singleton
    fun provideChatSocketService(client: HttpClient): ChatSocketService {
        return ChatSocketServiceImpl(client)
    }
}