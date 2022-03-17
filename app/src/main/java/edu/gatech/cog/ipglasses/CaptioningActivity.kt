package edu.gatech.cog.ipglasses

import LiveTranscribeRenderer
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import com.google.flatbuffers.FlatBufferBuilder
import edu.gatech.cog.CaptionMessage
import edu.gatech.cog.Juror
import edu.gatech.cog.OrientationMessage
import edu.gatech.cog.ipglasses.renderingmethods.*
import edu.gatech.cog.ipglasses.ui.theme.IPGlassesTheme
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.Socket
import java.nio.ByteBuffer
import kotlin.concurrent.thread

private val TAG = CaptioningActivity::class.java.simpleName

/**
 * List of all the possible renderers that the <a href="https://github.com/SaltyQuetzals/cog-group-convo">server</a> can request.
 */
object Renderers {
    const val REGISTERED_GRAPHICS = 1
    const val NONREGISTERED_GRAPHICS = 2
    const val NONREGISTERED_GRAPHICS_WITH_ARROWS = 3
    const val LIVE_TRANSCRIBE_SIMULATION = 4
}


/**
 * A map from the requested rendering method to a renderer. If a [Renderers] value is not in this function's `when` statement, it is not supported yet.
 */
@Composable
fun RendererForRequestedMethod(requestedRenderingMethod: Int, model: CaptioningViewModel) {
    when (requestedRenderingMethod) {
        Renderers.REGISTERED_GRAPHICS -> {}
        Renderers.NONREGISTERED_GRAPHICS -> {}
        Renderers.NONREGISTERED_GRAPHICS_WITH_ARROWS -> {}
        Renderers.LIVE_TRANSCRIBE_SIMULATION -> LiveTranscribeRenderer(model)
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
class CaptioningActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)
    private val gameRotationVectorReading = FloatArray(3)

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    private val model: CaptioningViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val requestedRenderingMethod =
            intent.getIntExtra(RENDERING_METHOD, Renderers.REGISTERED_GRAPHICS)
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

    override fun onResume() {
        super.onResume()
        sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)?.also { gameRotationVector ->
            sensorManager.registerListener(
                this,
                gameRotationVector,
                SensorManager.SENSOR_DELAY_GAME,
                SensorManager.SENSOR_DELAY_GAME
            )
        }
    }

    override fun onPause() {
        super.onPause()

        // Don't receive any more updates from either sensor.
        sensorManager.unregisterListener(this)
    }

    private fun streamCaptionsFromServer(socket: DatagramSocket) {
        while (socket.isConnected) {
            val messageByteArray =
                ByteArray(1024) // Allocate a byte array of the given message length
            val packet = DatagramPacket(messageByteArray, messageByteArray.size)
            socket.receive(packet)
            val captionMessage: CaptionMessage =
                CaptionMessage.getRootAsCaptionMessage(ByteBuffer.wrap(packet.data)) // Load the CaptionMessage
            Log.d(
                TAG,
                "messageId = ${captionMessage.messageId}, chunkId = ${captionMessage.chunkId}"
            )
            model.addMessage(captionMessage = captionMessage)
        }
    }

    private fun streamOrientationToServer(socket: DatagramSocket) {
        try {
            while (socket.isConnected) {
                val builder = FlatBufferBuilder(1024)
                val orientationMessage = OrientationMessage.createOrientationMessage(
                    builder,
                    orientationAngles[0],
                    orientationAngles[1],
                    orientationAngles[2]
                )
                builder.finish(orientationMessage)
                val buf = builder.sizedByteArray()
//                Log.d(TAG, "buf size = ${buf.size}")
                val packet = DatagramPacket(buf, 0, buf.size)
                socket.send(packet)
            }
        } catch (e: Exception) {
            Log.e(TAG, e.stackTraceToString())
        }
    }

    /**
     * Attempts to create a connection to the given host and port. If successful, begins streaming
     * data from the server, transforming them into [CaptionMessage]s and adding them to the [CaptioningViewModel].
     */
    private fun beginStreamingCaptionsFromServer(host: String?, port: Int) {
        thread {
            try {
                val socket = DatagramSocket()
                socket.connect(
                    InetAddress.getByName(host),
                    port
                ) // Connect to captioning server, blocks thread until successful or errors.
                thread {
                    streamCaptionsFromServer(socket)
                }
                thread {
                    streamOrientationToServer(socket)
                }
            } catch (e: Exception) {
                val intent = Intent(this, MainActivity::class.java)
                Log.e(TAG, e.message!!)
                startActivity(intent)
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                System.arraycopy(
                    event.values,
                    0,
                    accelerometerReading,
                    0,
                    accelerometerReading.size
                )
            }
            Sensor.TYPE_GAME_ROTATION_VECTOR -> {
                System.arraycopy(
                    event.values,
                    0,
                    gameRotationVectorReading,
                    0,
                    gameRotationVectorReading.size
                )
            }
        }
        updateOrientationAngles()
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
//        TODO("Not yet implemented")
    }

    private fun updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrixFromVector(rotationMatrix, gameRotationVectorReading)
        // "rotationMatrix" now has up-to-date information.
        SensorManager.getOrientation(rotationMatrix, orientationAngles)
        // "orientationAngles" now has up-to-date information.
    }


}

@Preview(showBackground = false, widthDp = DISPLAY_WIDTH, heightDp = DISPLAY_HEIGHT)
@Composable
fun DefaultPreview() {
    val viewModel = CaptioningViewModel()
    viewModel.renderingMethodToUse = Renderers.REGISTERED_GRAPHICS
    val builder = FlatBufferBuilder(1024)
    val text = builder.createString(LoremIpsum().values.first())
    val captionMessageOffset =
        CaptionMessage.createCaptionMessage(builder, text, Juror.JurorA, Juror.JurorA, 0, 0)
    builder.finish(captionMessageOffset)
    val buf = builder.dataBuffer()
    val captionMessage = CaptionMessage.getRootAsCaptionMessage(buf)

    viewModel.addMessage(
        captionMessage
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