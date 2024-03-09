package websocket.client.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import websocket.client.data.Client
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val client: Client) : ViewModel() {
}