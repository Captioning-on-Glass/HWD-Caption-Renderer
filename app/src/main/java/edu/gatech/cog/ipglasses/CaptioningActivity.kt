package edu.gatech.cog.ipglasses

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.gson.Gson
import edu.gatech.cog.ipglasses.renderingmethods.ContextualRenderer
import edu.gatech.cog.ipglasses.renderingmethods.DefaultRenderer
import edu.gatech.cog.ipglasses.ui.theme.IPGlassesTheme
import java.io.DataInputStream
import java.io.EOFException
import java.io.IOException
import java.net.Socket
import java.net.UnknownHostException
import kotlin.concurrent.thread
import kotlin.time.Duration

private val TAG = CaptioningActivity::class.java.simpleName
private const val DEFAULT_RENDERER = 1
private const val CONTEXTUAL_RENDERER = 2

class CaptioningActivity : ComponentActivity() {
    private val model: CaptioningViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val requestedRenderingMethod = intent.getIntExtra(RENDERING_METHOD, 0)
        val host = intent.getStringExtra(SERVER_HOST)
        val port = intent.getIntExtra(SERVER_PORT, 0)
        val gson = Gson()
        Log.d(TAG, "Requested rendering method is: $requestedRenderingMethod")

        thread {
            try {
                val socket = Socket(host, port)
                while (socket.isConnected) {
                    val messageInputStream = DataInputStream(socket.getInputStream())
                    val messageLength = messageInputStream.readInt()
                    val messageByteArray = ByteArray(messageLength)
                    messageInputStream.read(messageByteArray)
                    val message = String(messageByteArray)
                    val receivedCaptionMessage = gson.fromJson(message, CaptionMessage::class.java)
                    if (receivedCaptionMessage.text == "CLEAR") {
                        model.clearText()
                    } else {
                        model.replaceText(receivedCaptionMessage.text)
                    }
                }
            } catch (e: UnknownHostException) {
                runOnUiThread {
                    val toast = Toast.makeText(this, "Unknown host: $host", Toast.LENGTH_LONG)
                    toast.show()
                }
            } catch (e: IOException) {
                runOnUiThread {
                    val toast =
                        Toast.makeText(this, "I/O Exception: ${e.message}", Toast.LENGTH_LONG)
                    toast.show()
                }
            } catch (e: EOFException) {
                // This is a result of the server getting forcefully shut down.
                // TODO: figure out how to handle this more gracefully.

            }
        }
        setContent {
            IPGlassesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colors.background,
                    modifier = Modifier.fillMaxSize()
                ) {
                    when (requestedRenderingMethod) {
                        DEFAULT_RENDERER -> DefaultRenderer(model)
                        CONTEXTUAL_RENDERER -> ContextualRenderer(model)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    IPGlassesTheme {
        DefaultRenderer(CaptioningViewModel())
    }
}