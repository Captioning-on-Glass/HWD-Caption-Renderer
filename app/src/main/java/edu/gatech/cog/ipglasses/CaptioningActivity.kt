package edu.gatech.cog.ipglasses

import LiveTranscribeRenderer
import android.content.Context
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
import edu.gatech.cog.ipglasses.renderingmethods.DISPLAY_HEIGHT
import edu.gatech.cog.ipglasses.renderingmethods.DISPLAY_WIDTH
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

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)
    private var rotationVectorSensor: Sensor? = null

    private val model: CaptioningViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val requestedRenderingMethod =
            intent.getIntExtra(RENDERING_METHOD, Renderers.REGISTERED_GRAPHICS)
        val host = intent.getStringExtra(SERVER_HOST)
        val port = intent.getIntExtra(SERVER_PORT, 0)
        beginStreamingCaptionsFromServer(host, port, requestedRenderingMethod)
        Log.d(TAG, "Requested rendering method is: $requestedRenderingMethod")
            model.renderingMethodToUse = requestedRenderingMethod
        if (requestedRenderingMethod != Renderers.LIVE_TRANSCRIBE_SIMULATION) {
            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        }
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
        if (model.renderingMethodToUse == Renderers.LIVE_TRANSCRIBE_SIMULATION) {
            return
        }
        rotationVectorSensor?.also { rotationVector ->
            sensorManager.registerListener(
                this,
                rotationVector,
                8 * 1000, // us -> ms
            )
        }
    }

    override fun onPause() {
        super.onPause()
        if (model.renderingMethodToUse == Renderers.LIVE_TRANSCRIBE_SIMULATION) {
            return
        }
        // Don't receive any more updates from either sensor.
        sensorManager.unregisterListener(this)
    }

    private fun readCaptionFromServer(socket: DatagramSocket) {
        val messageByteArray =
            ByteArray(128) // Allocate a byte array of the given message length
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

    /**
     * Attempts to create a connection to the given host and port. If successful, begins streaming
     * data from the server, transforming them into [CaptionMessage]s and adding them to the [CaptioningViewModel].
     */
    private fun beginStreamingCaptionsFromServer(
        host: String?,
        port: Int,
        presentationMethod: Int
    ) {
        thread {
            val socket = DatagramSocket()
            socket.connect(
                InetAddress.getByName(host),
                port
            ) // Connect to captioning server, blocks thread until successful or errors.
            if (presentationMethod == Renderers.LIVE_TRANSCRIBE_SIMULATION) {
                thread {
                    while (socket.isConnected) {
                        readCaptionFromServer(socket)
                    }
                }
            }
            thread {
                val builder = FlatBufferBuilder(512)
                while (socket.isConnected) {
                    builder.clear()
                    writeOrientationToServer(socket, builder)
                    Thread.sleep(8)
                }
            }
        }
    }

    private fun writeOrientationToServer(socket: DatagramSocket, builder: FlatBufferBuilder) {
        if (socket.isConnected) {
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
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (model.renderingMethodToUse == Renderers.LIVE_TRANSCRIBE_SIMULATION) {
            return
        }
        when (event.sensor.type) {
            Sensor.TYPE_ROTATION_VECTOR -> {
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                SensorManager.getOrientation(rotationMatrix, orientationAngles)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // TODO("Not yet implemented")
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