package websocket.client.data

import com.google.gson.annotations.SerializedName

data class ApiData(
    @SerializedName("data_type") val dataType: String = INPUT,
    @SerializedName("input_data") val data: InputData = InputData(),
    @SerializedName("clear_data") val clear: Clear = Clear(),
    @SerializedName("move_data") val move: Move = Move(),
    @SerializedName("go_web_data") val goWeb: GoWeb = GoWeb()
) {
    companion object {
        const val INPUT = "input"
        const val CLEAR = "clear"
        const val MOVE = "move"
        const val GO_WEB = "go_web"
    }
}
