package edu.gatech.cog.ipglasses

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.zxing.integration.android.IntentIntegrator
import edu.gatech.cog.ipglasses.databinding.FragmentConnectBinding

private val TAG = ConnectFragment::class.java.simpleName

/**
 * A simple [Fragment] subclass.
 * Use the [ConnectFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ConnectFragment : Fragment(R.layout.fragment_connect) {

    private var _binding: FragmentConnectBinding? = null
    private val binding get() = _binding!!

    private fun startQrCodeScanner() {
        IntentIntegrator.forSupportFragment((this as Fragment))
            .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            .setPrompt("Scan the QR code provided by the researcher")
            .setBeepEnabled(false)
            .setBarcodeImageEnabled(false)
            .initiateScan()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "In connectFragment")

        _binding = FragmentConnectBinding.bind(view)

        startQrCodeScanner()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        IntentIntegrator.parseActivityResult(requestCode, resultCode, data)?.let { barcodeResult ->
            barcodeResult.contents?.let { barcodeContents ->
                requireActivity().runOnUiThread {
                    binding.connectionStatus.text =
                        "Attempting to connect to \"$barcodeContents...\""
                }
                connectToDevice(barcodeContents)
            } ?: run {
                requireActivity().runOnUiThread {
                    binding.connectionStatus.text = getString(R.string.connection_error)
                }
            }
        }
    }

    private fun connectToDevice(barcodeContents: String) {

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment ConnectFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            ConnectFragment().apply {
                arguments = Bundle()
            }
    }
}