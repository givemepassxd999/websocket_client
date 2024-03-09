package websocket.client.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import websocket.client.data.Client

@Module
@InstallIn(ActivityRetainedComponent::class)
object AppModule {
    @Provides
    fun provideClient() = Client()
}