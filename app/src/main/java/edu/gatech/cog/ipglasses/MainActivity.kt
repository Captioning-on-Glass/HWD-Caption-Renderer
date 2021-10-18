package edu.gatech.cog.ipglasses

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator

private val TAG = MainActivity::class.java.simpleName
const val SERVER_HOST = "edu.gatech.cog.ipglasses.SERVER_HOST"
const val SERVER_PORT = "edu.gatech.cog.ipglasses.SERVER_PORT"
const val RENDERING_METHOD = "edu.gatech.cog.ipglasses.RENDERING_METHOD"

class MainActivity : AppCompatActivity() {
    private val zxingActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val intentResult = IntentIntegrator.parseActivityResult(it.resultCode, it.data)
            if (intentResult != null) {
                val addressAndMethod = intentResult.contents.split(" ")
                val address = addressAndMethod[0]
                val renderingMethod =
                    addressAndMethod[1].toInt() // The rendering method to render captions with.
                val split = address.split(":")
                val host = split[0]
                val port = split[1].toInt()
                val intent = Intent(this, CaptioningActivity::class.java).apply {
                    putExtra(SERVER_HOST, host)
                    putExtra(SERVER_PORT, port)
                    putExtra(RENDERING_METHOD, renderingMethod)
                }
                startActivity(intent)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Start QR code scanning
        zxingActivityResultLauncher.launch(
            IntentIntegrator(this)
                .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                .setPrompt("Scan the QR code provided by the researcher")
                .setBeepEnabled(false)
                .setBarcodeImageEnabled(false).createScanIntent()
        )
    }
}