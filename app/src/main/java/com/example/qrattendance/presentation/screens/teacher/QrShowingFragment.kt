package com.example.qrattendance.presentation.screens.teacher

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.example.qrattendance.presentation.base.BaseFragment
import com.example.qrattendance.databinding.FragmentQrShowingBinding

class QrShowingFragment: BaseFragment<FragmentQrShowingBinding>(FragmentQrShowingBinding::inflate) {
    private var secondsElapsed = 0
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var timerRunnable: Runnable
    private var timerRunning = false

    override fun onBindView() {
        super.onBindView()

        timerRunnable = object : Runnable {
            override fun run() {
                secondsElapsed++
                val minutes = secondsElapsed / 60
                val seconds = secondsElapsed % 60
                binding.textView12.text = String.format("%02d:%02d", minutes, seconds)
                handler.postDelayed(this, 1000)
            }
        }

        binding.generateQRBtn.setOnClickListener {
            if (!timerRunning) {
                startTimer()
                timerRunning = true
                Toast.makeText(requireContext(), "QR Code Generated", Toast.LENGTH_SHORT).show()
                // Insert QR generation logic here if needed
            }
        }
    }

    private fun startTimer() {
        secondsElapsed = 0
        binding.textView12.text = "00:00"
        handler.post(timerRunnable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(timerRunnable)
    }
}