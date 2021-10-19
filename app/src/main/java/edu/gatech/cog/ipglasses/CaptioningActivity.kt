package edu.gatech.cog.ipglasses

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import edu.gatech.cog.ipglasses.renderingmethods.ContextualRenderer
import edu.gatech.cog.ipglasses.renderingmethods.DefaultRenderer
import edu.gatech.cog.ipglasses.ui.theme.IPGlassesTheme
import kotlin.concurrent.thread

private val TAG = CaptioningActivity::class.java.simpleName
private const val DEFAULT_RENDERER = 1
private const val CONTEXTUAL_RENDERER = 2

class CaptioningActivity : ComponentActivity() {
    private val model: CaptioningViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val requestedRenderingMethod = intent.getIntExtra(RENDERING_METHOD, 0)
        Log.d(TAG, "Requested rendering method is: $requestedRenderingMethod")
        val captionContent = "This is the default caption"
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

//
//private const val RENDERING_MESSAGE_TYPE = 1
//private const val CAPTION_MESSAGE_TYPE = 2
//
//
//class CaptioningActivity : FragmentActivity() {
//    private lateinit var captioningServerAddress: InetSocketAddress
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_captioning)
//        val host = intent.getStringExtra(SERVER_HOST)
//        val port = intent.getIntExtra(SERVER_PORT, 0)
//        val renderingMethod = intent.getIntExtra(RENDERING_METHOD, 0)
//        renderRequestedRenderingMethod(renderingMethod)
//    }
//
//    private fun renderRequestedRenderingMethod(renderingMethod: Int) {
//
//    }
//
//    //            val messageInputStream = DataInputStream(socket.getInputStream())
////            var message: String
////            var shouldEnd = false
////            while (!shouldEnd) {
////                Log.d("$TAG:thread", "Waiting for message type.")
////                when (val messageType = messageInputStream.readInt()) {
////                    RENDERING_MESSAGE_TYPE -> {
////                        Log.d("$TAG:thread", "Received rendering style message type.")
////                        val presentationMethodType = messageInputStream.readInt()
////                        Log.d(
////                            "$TAG:thread",
////                            "presentation method selected: $presentationMethodType"
////                        )
////                        switchToCaptionFragment(presentationMethodType)
////                    }
//////                    CAPTION_MESSAGE_TYPE -> {
//////                        Log.d("$TAG:thread", "Received caption message type.")
//////                        val length = messageInputStream.readInt()
//////                        val byteArray = ByteArray(length)
//////                        messageInputStream.read(byteArray)
//////                        message = String(byteArray)
//////                    }
////                    else -> {
////                        Log.d("$TAG:thread", "got an unexpected message type: $messageType")
////                        shouldEnd = true
////                    }
////                }
////            }
////        }
//
//}