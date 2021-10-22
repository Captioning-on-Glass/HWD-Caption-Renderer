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
import androidx.compose.ui.graphics.Color
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

private val TAG = CaptioningActivity::class.java.simpleName

/**
 * List with the supported renderers available to be requested.
 */
object Renderers {
    const val DEFAULT_RENDERER = 1
    const val CONTEXTUAL_RENDERER = 2
}

/**
 * Renders the rendering method indicated by the value of the intent extra [RENDERING_METHOD].
 * Then, creates a [Socket] connection to the captioning server on a separate thread,
 * and displays the messages received via the rendering method.
 */
class CaptioningActivity : ComponentActivity() {
    private val model: CaptioningViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val requestedRenderingMethod =
            intent.getIntExtra(RENDERING_METHOD, Renderers.DEFAULT_RENDERER)
        val host = intent.getStringExtra(SERVER_HOST)
        val port = intent.getIntExtra(SERVER_PORT, 0)
        beginStreamingCaptionsFromServer(host, port)
        Log.d(TAG, "Requested rendering method is: $requestedRenderingMethod")
        setContent {
            IPGlassesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    when (requestedRenderingMethod) {
                        Renderers.DEFAULT_RENDERER -> DefaultRenderer(model)
                        Renderers.CONTEXTUAL_RENDERER -> ContextualRenderer(model)
                        else -> {
                            Log.d(
                                TAG,
                                "Received unknown renderer value: $requestedRenderingMethod, using DefaultRenderer instead."
                            )
                            DefaultRenderer(viewModel = model)
                        }
                    }
                }
            }
        }
    }

    private fun beginStreamingCaptionsFromServer(host: String?, port: Int) {
        val gson = Gson()
        thread {
            try {
                val socket = Socket(
                    host,
                    port
                ) // Connect to captioning server, blocks thread until successful or errors.
                while (socket.isConnected) {
                    val messageInputStream = DataInputStream(socket.getInputStream())
                    val messageLength =
                        messageInputStream.readInt() // Read the length of the upcoming message (in bytes)
                    val messageByteArray =
                        ByteArray(messageLength) // Allocate a byte array of the given message length
                    messageInputStream.read(messageByteArray) // Read the message content (as bytes) into the new array
                    val messageAsJsonString =
                        String(messageByteArray) // Convert the array into a JSON string
                    val captionMessage = gson.fromJson(
                        messageAsJsonString,
                        CaptionMessage::class.java
                    ) // Transform the JSON string into a CaptionMessage instance
                    model.addMessage(captionMessage = captionMessage)
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
                // TODO: figure out how to handle this more gracefully. Ideally, we'd close the socket connection from the server side.
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