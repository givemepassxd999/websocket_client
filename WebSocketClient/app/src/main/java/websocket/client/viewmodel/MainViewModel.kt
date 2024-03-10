package websocket.client.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import websocket.client.data.Client
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.sqrt

@HiltViewModel
class MainViewModel @Inject constructor(private val client: Client) : ViewModel() {

    private val _connectionState = MutableStateFlow(UiState())
    val connectionState: StateFlow<UiState> = _connectionState.asStateFlow()

    private var _host = mutableStateOf("")
    val host: State<String> = _host

    private var _input = mutableStateOf("")
    val input: State<String> = _input

    private val prePoint = mutableStateOf(0 to 0)
    fun setPoint(x: Int, y: Int) {
        //calculate distance
        val (px, py) = prePoint.value
        val distance = sqrt((x - px).toDouble().pow(2.0) + (y - py).toDouble().pow(2.0))
        if (distance > 10) {
            prePoint.value = x to y
        } else {
            return
        }
        client.setPoint(x, y)
        client.setPointChange(true)
    }

    fun goWeb() = client.goWeb()

    fun clearInput() {
        _input.value = ""
        client.clear()
    }

    fun setHost(host: String) {
        _host.value = host
    }

    fun send(msg: String) {
        _input.value = msg
        viewModelScope.launch {
            client.setMsg(msg)
        }
    }

    fun connection(host: String) {
        host.split(":").let {
            if (it.size != 2) return
            val ip = it[0]
            val port = it[1].toInt()
            viewModelScope.launch {
                client.connection(host = ip, port = port) { connected ->
                    _connectionState.value = UiState(connected = connected)
                }
            }
        }
    }

    fun disconnection() {
        viewModelScope.launch {
            client.disconnection()
            _connectionState.value = UiState(connected = false)
        }
    }
}

data class UiState(val connected: Boolean = false)