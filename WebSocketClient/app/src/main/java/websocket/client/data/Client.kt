package websocket.client.data

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.launch


// ref https://ktor.io/docs/getting-started-ktor-client-chat.html
class Client {
    private var client: HttpClient? = null
    private val tag = "@@" + HttpHeaders.Server::class.java.simpleName
    private var isConnected = false

    private var _msg = mutableStateOf("")
    fun setMsg(msg: String) {
        _msg.value = msg
    }

    suspend fun connection(host: String, port: Int, connectionState: ConnectionState) {
        client = HttpClient {
            install(WebSockets)
        }
        try {
            client?.webSocket(method = HttpMethod.Get, host = host, port = port, path = "/") {
                Log.d(tag, "Started")
                connectionState.accept(connected = true)
                val userInputRoutine = launch { inputMessages() }
                userInputRoutine.join()
            }
        } catch (e: ClosedReceiveChannelException) {
            Log.d(tag, "ClosedReceiveChannelException")
            isConnected = false
            connectionState.accept(connected = false)
            client?.close()

        } catch (e: Throwable) {
            Log.d(tag, "Throwable")
            isConnected = false
            connectionState.accept(connected = false)
            client?.close()
        } finally {
            Log.d(tag, "Finally")
        }
    }

    private suspend fun DefaultClientWebSocketSession.inputMessages() {
        while (true) {
            if (_msg.value.isNotEmpty()) {
                try {
                    val frame = Frame.Text(_msg.value)
                    send(frame = frame)
                    _msg.value = ""
                } catch (e: Exception) {
                    println("Error while sending: " + e.localizedMessage)
                    return
                }
            }
        }
    }

    fun disconnection() {
        client?.close()
        isConnected = false
        Log.d(tag, "Stopped")
    }

    fun interface ConnectionState {
        fun accept(connected: Boolean)
    }
}