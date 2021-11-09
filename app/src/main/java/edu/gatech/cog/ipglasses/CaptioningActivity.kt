package edu.gatech.cog.ipglasses

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import com.google.gson.Gson
import edu.gatech.cog.ipglasses.renderingmethods.*
import edu.gatech.cog.ipglasses.ui.theme.IPGlassesTheme
import java.io.DataInputStream
import java.io.EOFException
import java.io.IOException
import java.net.Socket
import java.net.UnknownHostException
import kotlin.concurrent.thread

private val TAG = CaptioningActivity::class.java.simpleName

/**
 * List of all the possible renderers that the <a href="https://github.com/SaltyQuetzals/cog-group-convo">server</a> can request.
 */
object Renderers {
    /**
     * Show captions only on the computer monitors. Since no display behavior is happening on the
     * head-worn display, nothing should happen if the renderer is set to this value.
     */
    const val MONITOR_ONLY = 1

    /**
     * Show all spoken language on the head-worn display, regardless of speaker.
     */
    const val GLOBAL_ONLY = 2

    /**
     * Show all spoken language on the head-worn display. Anything dependent on this value should behave
     * identically to its behavior when [MONITOR_ONLY] is set.
     */
    const val MONITOR_AND_GLOBAL = 3

    /**
     * Show all spoken language on the head-worn display, with indicators to show which direction the speaker is speaking from.
     */
    const val GLOBAL_WITH_DIRECTION_INDICATORS = 4

    /**
     * Show all spoken language as text on the head-worn display with indicators as to who said what.
     */
    const val WHO_SAID_WHAT = 5

    /**
     * Anything dependent on this value should behave identically to its behavior when [GLOBAL_WITH_DIRECTION_INDICATORS] is set.
     */
    const val MONITOR_AND_GLOBAL_WITH_DIRECTION_INDICATORS = 6

    /**
     * Show only the spoken language of the person being focused upon on the head-worn-display.
     */
    const val FOCUSED_SPEAKER_ONLY = 8

    /**
     * Show the spoken language of the person being focused upon in a primary position on the
     * head-worn display, but also show all spoken language in a secondary position.
     */
    const val FOCUSED_SPEAKER_AND_GLOBAL = 9
}

object Speakers {
    const val JUROR_A = "juror-a"
    const val JUROR_B = "juror-b"
    const val JUROR_C = "juror-c"
    const val JURY_FOREMAN = "jury-foreman"
}


/**
 * A map from the requested rendering method to a renderer. If a [Renderers] value is not in this function's `when` statement, it is not supported yet.
 */
@Composable
fun RendererForRequestedMethod(requestedRenderingMethod: Int, model: CaptioningViewModel) {
    when (requestedRenderingMethod) {
        Renderers.MONITOR_ONLY -> {
        } // Monitor-only is a no-op, nothing to do
        Renderers.GLOBAL_ONLY -> GlobalRenderer(model)
        Renderers.MONITOR_AND_GLOBAL -> GlobalRenderer(model)
        Renderers.GLOBAL_WITH_DIRECTION_INDICATORS -> GlobalWithDirectionIndicatorsRenderer(model)
        Renderers.WHO_SAID_WHAT -> WhoSaidWhatRenderer(model)
        Renderers.MONITOR_AND_GLOBAL_WITH_DIRECTION_INDICATORS -> GlobalWithDirectionIndicatorsRenderer(
            model
        )
        Renderers.FOCUSED_SPEAKER_ONLY -> FocusedSpeakerRenderer(model)
        Renderers.FOCUSED_SPEAKER_AND_GLOBAL -> FocusedSpeakerAndGlobalRenderer(
            model
        )
        else -> {
            Log.d(
                TAG,
                "Received unknown renderer value: $requestedRenderingMethod, using FocusedSpeakerOnlyRenderer instead."
            )
        }
    }
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
            intent.getIntExtra(RENDERING_METHOD, Renderers.FOCUSED_SPEAKER_ONLY)
        val host = intent.getStringExtra(SERVER_HOST)
        val port = intent.getIntExtra(SERVER_PORT, 0)
        beginStreamingCaptionsFromServer(host, port)
        Log.d(TAG, "Requested rendering method is: $requestedRenderingMethod")
        model.renderingMethodToUse = requestedRenderingMethod
        setContent {
            IPGlassesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    RendererForRequestedMethod(
                        requestedRenderingMethod = requestedRenderingMethod,
                        model = model
                    )
                }
            }
        }
    }

    /**
     * Attempts to create a connection to the given host and port. If successful, begins streaming
     * data from the server, transforming them into [CaptionMessage]s and adding them to the [CaptioningViewModel].
     */
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

@Preview(showBackground = false, widthDp = 480, heightDp = 480)
@Composable
fun DefaultPreview() {
    val viewModel = CaptioningViewModel()
    viewModel.renderingMethodToUse = Renderers.FOCUSED_SPEAKER_ONLY
    viewModel.addMessage(
        CaptionMessage(
            messageId = 0,
            chunkId = 0,
            text = LoremIpsum().values.first(),
            speakerId = Speakers.JUROR_A,
            focusedId = Speakers.JUROR_A
        )
    )
    IPGlassesTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            RendererForRequestedMethod(
                requestedRenderingMethod = viewModel.renderingMethodToUse,
                model = viewModel
            )
        }
    }
}