package websocket.client.data

import com.google.gson.annotations.SerializedName

data class InputData(
    @SerializedName("input") val input: String = ""
)

class Clear(empty: String = "")
data class GoWeb(
    @SerializedName("url") val url: String = ""
)

data class Move(
    @SerializedName("x") val x: Int = 0,
    @SerializedName("y") val y: Int = 0
)