package com.example.ddsapptest
/**
 * Gradle JDK : jbr-17   JetBrains Runtime version 17.06
 * Jetpack Compose 构建，使用 kotlin 语言
 * Jetpack Compose 是 Google 推荐用于构建原生 Android 界面的新工具包。
 * 它可简化并加快 Android 上的界面开发，
 * 使用更少的代码、强大的工具和直观的 Kotlin API，快速打造生动而精彩的应用。
 */
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ddsapptest.ui.theme.DdsapptestTheme
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.Socket


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 设置内容视图
        setContent {
            DdsapptestTheme {
                // 使用主题中的 'background' 颜色创建一个表面容器
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 调用 SimplePage() 函数显示简单页面
                    SimplePage()
                }
            }
        }
    }
}




@Composable
fun SimplePage() {
    // 用于控制显示页面的状态变量
    var showPageOne by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        Column (
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            // 根据 showPageOne 变量的值，决定显示 WelcomeScreen 还是 InstrumentControl
            if (showPageOne) {
                WelcomeScreen()
            } else {
                InstrumentControl()
            }

            Spacer(modifier = Modifier.height(15.dp))

            // IconButton 组件用于切换 showPageOne 变量的值
            IconButton(
                onClick = { showPageOne = !showPageOne },
                modifier = Modifier.size(30.dp),
            ) {
                if (showPageOne) {
                    // 当 showPageOne 为 true 时显示 Build 图标
                    Icon(
                        imageVector = Icons.Outlined.Build,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp), // 设置图标大小
                        tint = Color.Magenta
                    )
                } else {
                    // 当 showPageOne 为 false 时显示 Home 图标
                    Icon(
                        imageVector = Icons.Outlined.Home,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp),
                        tint = Color.Magenta
                    )
                }
            }
        }
    }
}


@Composable
fun WelcomeScreen() {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 显示Logo图像
        Image(
            painter = painterResource(id = R.drawable.ic_android_black_24dp),
            contentDescription = "Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(200.dp)
        )

        // 显示标题文本
        Text(
            text = "远程式信号发生器",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(66.dp))

        // 显示作者信息
        Text(
            text = "作者1信息",
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = "作者2信息",
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = "作者3信息",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}


/**
 * 仪器控制界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstrumentControl() {
    var ip by remember { mutableStateOf("") }
    var port by remember { mutableStateOf("") }
    var waveform1 by remember { mutableStateOf(Waveform.Sine) }
    var waveform2 by remember { mutableStateOf(Waveform.Sine) }
    var frequency1 by remember { mutableStateOf("") }
    var frequency2 by remember { mutableStateOf("") }
    var amplitude1 by remember { mutableStateOf("") }
    var amplitude2 by remember { mutableStateOf("") }
    var phase1 by remember { mutableStateOf("") }
    var phase2 by remember { mutableStateOf("") }
    val isConnected = remember { mutableStateOf(false) }
    val client = remember { mutableStateOf<Socket?>(null) }

    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Signal 1")
        WaveformSelector(waveform1) { waveform1 = it }
        FrequencyInput(frequency1) { frequency1 = it }
        AmplitudeInput(amplitude1) { amplitude1 = it }
        PhaseInput(phase1) { phase1 = it }

        Spacer(modifier = Modifier.height(2.dp))

        Text("Signal 2")
        WaveformSelector(waveform2) { waveform2 = it }
        FrequencyInput(frequency2) { frequency2 = it }
        AmplitudeInput(amplitude2) { amplitude2 = it }
        PhaseInput(phase2) { phase2 = it }

        Spacer(modifier = Modifier.height(2.dp))

        Text("TCP Connection")
        TextField(
            value = ip,
            onValueChange = { ip = it },
            label = { Text("IP Address") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        TextField(
            value = port,
            onValueChange = { port = it },
            label = { Text("Port") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row {
            Button(
                onClick = {
                    if (isConnected.value) {
                        disconnect(client, isConnected)
                    } else {
                        connect(ip, port, client, isConnected)
                    }
                },
                modifier = Modifier.size(width = 100.dp, height = 40.dp)
            ) {
                Text(if (isConnected.value) "断开" else "连接")
            }
            Button(
                onClick = { sendData(ip, port, client.value,
                    isConnected.value, waveform1, frequency1, amplitude1,
                    phase1, waveform2, frequency2, amplitude2, phase2) },
                modifier = Modifier.size(width = 100.dp, height = 40.dp)
            ) {
                Text("发送")
            }
        }
    }
}

@Composable
fun WaveformSelector(selected: Waveform, onSelected: (Waveform) -> Unit) {
    Column {
        Row {
            RadioButton(
                selected = selected == Waveform.Sine,
                onClick = { onSelected(Waveform.Sine) },
                modifier = Modifier.padding(start = 2.dp)
            )
            Text("Sine", modifier = Modifier.padding(start = 4.dp))
            RadioButton(
                selected = selected == Waveform.Square,
                onClick = { onSelected(Waveform.Square) },
                modifier = Modifier.padding(start = 30.dp)
            )
            Text("Square", modifier = Modifier.padding(start = 4.dp))
        }
        Row {
            RadioButton(
                selected = selected == Waveform.Triangle,
                onClick = { onSelected(Waveform.Triangle) },
                modifier = Modifier.padding(start = 2.dp)
            )
            Text("Triangle", modifier = Modifier.padding(start = 4.dp))
            RadioButton(
                selected = selected == Waveform.Sawtooth,
                onClick = { onSelected(Waveform.Sawtooth) },
                modifier = Modifier.padding(start = 4.dp)
            )
            Text("Sawtooth", modifier = Modifier.padding(start = 4.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FrequencyInput(value: String, onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Frequency (Hz)") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number) ,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmplitudeInput(value: String, onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Amplitude (V)") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhaseInput(value: String, onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Phase (degrees)") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    )
}

enum class Waveform {
    Sine,
    Square,
    Triangle,
    Sawtooth
}


/**
 * 在 Jetpack Compose 中与服务器建立连接、发送数据以及断开连接的一些基本操作。
 * 根据传入的参数和相关状态，执行相应的操作并进行错误处理。
 */

@OptIn(DelicateCoroutinesApi::class)
fun connect(
    ip: String,
    port: String,
    client: MutableState<Socket?>,
    isConnected: MutableState<Boolean>
) {
    if (ip.isEmpty() || port.isEmpty()) {
        // IP地址或端口号为空，进行错误处理
        return
    }

    GlobalScope.launch(Dispatchers.IO) {
        try {
            val socket = Socket(ip, port.toInt())
            client.value = socket
            isConnected.value = true
            socket.outputStream.write("Hello from the client!".toByteArray())
        } catch (e: IOException) {
            e.printStackTrace()
            // 处理连接异常
        }
    }
}

fun disconnect(client: MutableState<Socket?>, isConnected: MutableState<Boolean>) {
    client.value?.let { socket ->
        if (!socket.isClosed) {
            socket.close()
        }
        isConnected.value = false
        client.value = null
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun sendData(
    ip: String,
    port: String,
    client: Socket?,
    isConnected: Boolean,
    waveform1: Waveform,
    frequency1: String,
    amplitude1: String,
    phase1: String,
    waveform2: Waveform,
    frequency2: String,
    amplitude2: String,
    phase2: String
) {
    if (ip.isEmpty() || port.isEmpty()) {
        // IP地址或端口号为空，进行错误处理
        return
    }

    if (!isConnected) {
        // 如果未连接，进行错误处理或其他操作
        return
    }

    GlobalScope.launch(Dispatchers.IO) {
        try {
            client?.let { socket ->
                val outputStream = socket.getOutputStream()
                outputStream.write(
                    ("$waveform1,$frequency1,$amplitude1,$phase1," +
                            "$waveform2,$frequency2,$amplitude2,$phase2").toByteArray()
                )
                outputStream.flush()
                // 不关闭outputStream，保持连接
            }
        } catch (e: IOException) {
            e.printStackTrace()
            // 处理发送数据异常
        }
    }
}


/**
 * 展示预览界面
 */


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DdsapptestTheme {
        SimplePage()
    }
}
