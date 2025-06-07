package com.example.qrattendance.presentation.screens.student

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.qrattendance.R
import com.example.qrattendance.presentation.base.BaseFragment
import com.example.qrattendance.databinding.FragmentQrScannerBinding
import com.example.qrattendance.presentation.viewmodel.QrViewModel
import com.example.qrattendance.presentation.viewmodel.UiState
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QrScannerFragment : BaseFragment<FragmentQrScannerBinding>(FragmentQrScannerBinding::inflate) {

    private val cameraPermissionCode = 101
    private val viewModel: QrViewModel by viewModels()
    private var isDialogShown = false

    override fun onBindView() {
        super.onBindView()

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                cameraPermissionCode
            )
        } else {
            startScanning()
        }
        observeAttendanceResult()
        binding.manualTv.setOnClickListener {
            if (!isDialogShown) {  // Only show dialog if not already shown
                showManualCodeEntryDialog()
            }
        }
    }

    private fun startScanning() {
        binding.barcodeScanner.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                result?.text?.let {
                    Log.d("QrScanner", "QR Code Scanned: $it")

                    val attendanceCode = parseQrCode(it)

                    lifecycleScope.launch {
                        val studentLogin = viewModel.getUserLoginFromPreferences()

                        if (attendanceCode != null && studentLogin != null) {
                            viewModel.checkAttendance(studentLogin, attendanceCode)
                        } else {
                            Log.e("QrScanner", "Failed to get attendance code or student login.")
                        }
                    }

                    binding.barcodeScanner.pause()
                }
            }

            override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
        })
        binding.barcodeScanner.resume()
    }

    override fun onResume() {
        super.onResume()
        binding.barcodeScanner.resume()
    }

    override fun onPause() {
        super.onPause()
        binding.barcodeScanner.pause()
    }
    private fun observeAttendanceResult() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.attendanceState.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is UiState.Loading -> {
                    }
                    is UiState.Success -> {
                        showCustomDialog("Success", "Attendance checked")
                    }
                    is UiState.Error -> {
                        Toast.makeText(requireContext(), "Failed to mark attendance", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    private fun showManualCodeEntryDialog() {
        isDialogShown = true // Set the flag to true when the dialog is shown

        val dialogView = layoutInflater.inflate(R.layout.dialog_manual_edit, null)
        val manualCodeEditText = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(
            R.id.edit_text_manual_code)

        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Enter Attendance Code")
            .setView(dialogView)
            .setPositiveButton("Submit") { _, _ ->
                val manualCode = manualCodeEditText.text.toString().trim()

                if (manualCode.length == 7) {
                    lifecycleScope.launch {
                        val studentLogin = viewModel.getUserLoginFromPreferences()

                        if (studentLogin != null) {
                            viewModel.checkAttendance(studentLogin, manualCode)
                        }
                    }
                    isDialogShown = false // Reset the flag when the dialog is submitted
                } else {
                    Toast.makeText(requireContext(), "Please enter a 7-character code", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { _, _ ->
                isDialogShown = false // Reset the flag when the dialog is canceled
            }
            .create()

        dialog.show()
    }

    private fun parseQrCode(qrCode: String): String? {
        val parts = qrCode.split("/")
        return if (parts.size >= 5) parts[4] else null
    }
}
