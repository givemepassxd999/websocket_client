package websocket.client.data

import android.graphics.Point
import android.util.Log
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import websocket.client.data.ApiData.Companion.CLEAR
import websocket.client.data.ApiData.Companion.GO_WEB
import websocket.client.data.ApiData.Companion.INPUT
import websocket.client.data.ApiData.Companion.MOVE


// ref https://ktor.io/docs/getting-started-ktor-client-chat.html
class Client {
    private var client: HttpClient? = null
    private val tag = "@@" + Client::class.java.simpleName
    private var isConnected = false
    private val gson = Gson()
    private var _msg = MutableStateFlow("")
    private var _isClear = MutableStateFlow(false)
    private var _goWeb = MutableStateFlow(false)
    private val _point = MutableStateFlow(Point(0, 0))
    private val _isPointChange = MutableStateFlow(false)

    fun setPointChange(isChange: Boolean) {
        _isPointChange.value = isChange
    }

    fun setPoint(x: Int, y: Int) {
        _point.value = Point(x, y)
    }

    fun goWeb() {
        _goWeb.value = true
    }

    fun setMsg(msg: String) {
        _msg.value = msg
    }

    fun clear() {
        _isClear.value = true
    }

    suspend fun connection(host: String, port: Int, connectionState: ConnectionState) {
        client = HttpClient {
            install(WebSockets)
        }
        try {
            client?.webSocket(method = HttpMethod.Get, host = host, port = port, path = "/") {
                Log.d(tag, "Started")
                connectionState.accept(connected = true)
                val userRoutine = launch {
                    event()
                }
                userRoutine.join()
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

    private suspend fun DefaultClientWebSocketSession.event() {
        while (true) {
            //user input msg
            if (_msg.value.isNotEmpty()) {
                try {
                    InputData(_msg.value).let {
                        val frame = Frame.Text(gson.toJson(ApiData(dataType = INPUT, data = it)))
                        send(frame = frame)
                        _msg.value = ""
                    }
                } catch (e: Exception) {
                    println("Error while sending: " + e.localizedMessage)
                    return
                }
            }
            //clear text
            if (_isClear.value) {
                try {
                    Clear().let {
                        val frame = Frame.Text(gson.toJson(ApiData(dataType = CLEAR, clear = it)))
                        send(frame = frame)
                        _isClear.value = false
                    }
                } catch (e: Exception) {
                    println("Error while sending: " + e.localizedMessage)
                    return
                }
            }
            if (_goWeb.value) {
                try {
                    GoWeb().let {
                        val frame = Frame.Text(gson.toJson(ApiData(dataType = GO_WEB, goWeb = it)))
                        send(frame = frame)
                        _goWeb.value = false
                    }
                } catch (e: Exception) {
                    println("Error while sending: " + e.localizedMessage)
                    return
                }
            }
            if (_isPointChange.value) {
                _point.value.let { point ->
                    try {
                        _isPointChange.value = false
                        Move(point.x, point.y).let {
                            val frame = Frame.Text(
                                gson.toJson(ApiData(dataType = MOVE, move = it))
                            )
                            send(frame = frame)
                        }
                    } catch (e: Exception) {
                        println("Error while sending: " + e.localizedMessage)
                    }
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