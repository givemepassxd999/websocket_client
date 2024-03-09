package websocket.client.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import websocket.client.R
import websocket.client.databinding.ActivityMainBinding
import websocket.client.viewmodel.MainViewModel

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
        }
    }

    @Composable
    fun MainView(viewModel: MainViewModel) {
        val connectionState = viewModel.connectionState.collectAsState().value
        Column(modifier = Modifier.padding(start = 10.dp)) {
            Text(
                modifier = Modifier.padding(top = 10.dp),
                text = if (connectionState.connected) {
                    ""
                    //do connection thing
                } else {
                    ""
                    //do disconnection thing
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
            Row(modifier = Modifier.padding(top = 10.dp)) {
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
                Button(
                    onClick = {
                        viewModel.send()
                    },
                    modifier = Modifier.padding(start = 10.dp),
                    enabled = connectionState.connected
                ) {
                    Text(text = "send")
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
}