package websocket.client.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import websocket.client.R
import websocket.client.databinding.ActivityMainBinding
import websocket.client.viewmodel.MainViewModel
import kotlin.math.roundToInt

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        binding.composeView.setContent {
            MainView(viewModel = viewModel)
            DraggableCursor(viewModel = viewModel)
        }
    }

    @Composable
    fun MainView(viewModel: MainViewModel) {
        val connectionState = viewModel.connectionState.collectAsState().value
        Column {
            Text(
                modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                text = if (connectionState.connected) {
                    getString(
                        R.string.connected,
                        viewModel.host.value,
                    )
                } else {
                    getString(R.string.disconnected)
                }
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Textarea(
                    value = viewModel.host.value,
                    onValueChange = { viewModel.setHost(it) },
                    placeholderText = stringResource(id = R.string.input_hint),
                    modifier = Modifier
                        .padding(top = 10.dp, start = 10.dp)
                        .width(300.dp),
                )
            }
            Row(modifier = Modifier.padding(start = 10.dp, top = 10.dp)) {
                Button(onClick = {
                    viewModel.connection(viewModel.host.value)
                }, enabled = connectionState.connected.not()) {
                    Text(text = stringResource(R.string.start))
                }
                Button(
                    onClick = {
                        viewModel.disconnection()
                    },
                    modifier = Modifier.padding(start = 10.dp),
                    enabled = connectionState.connected
                ) {
                    Text(text = stringResource(R.string.stop))
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Textarea(
                    value = viewModel.input.value,
                    onValueChange = { viewModel.send(it) },
                    placeholderText = stringResource(id = R.string.input_search_keyword),
                    modifier = Modifier
                        .padding(top = 10.dp, start = 10.dp)
                        .width(300.dp),
                )
            }
            Row(modifier = Modifier.padding(start = 10.dp, top = 10.dp)) {
                Button(onClick = {
                    viewModel.goWeb()
                }) {
                    Text(text = stringResource(R.string.go))
                }
                Button(
                    onClick = {
                        viewModel.clearInput()
                    },
                    modifier = Modifier.padding(start = 10.dp)
                ) {
                    Text(text = stringResource(R.string.clear))
                }
            }
        }
    }

    @Composable
    fun Textarea(
        value: String,
        onValueChange: (String) -> Unit,
        placeholderText: String,
        modifier: Modifier = Modifier,
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            placeholder = {
                Text(
                    modifier = Modifier.background(color = Color.Transparent),
                    text = placeholderText,
                )
            },
        )
    }

    @Composable
    private fun DraggableCursor(viewModel: MainViewModel) {
        Box(modifier = Modifier.fillMaxSize()) {
            var offsetX by remember { mutableStateOf(100f) }
            var offsetY by remember { mutableStateOf(1000f) }

            Image(
                painterResource(id = R.drawable.ic_android),
                modifier = Modifier
                    .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                    .size(50.dp)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                            viewModel.setPoint(offsetX.roundToInt(), offsetY.roundToInt())
                        }
                    }, contentDescription = stringResource(id = R.string.desc)
            )
        }
    }
}