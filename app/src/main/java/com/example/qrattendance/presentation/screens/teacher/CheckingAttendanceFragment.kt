package com.example.qrattendance.presentation.screens.teacher

import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.example.qrattendance.R
import com.example.qrattendance.presentation.base.BaseFragment
import com.example.qrattendance.databinding.FragmentCheckingAttendanceBinding
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckingAttendanceFragment :
    BaseFragment<FragmentCheckingAttendanceBinding>(FragmentCheckingAttendanceBinding::inflate) {

    private var selectedDay: String? = null
    private var selectedNumerator: String? = null
    private var selectedClassTime: String? = null

    override fun onBindView() {
        super.onBindView()

        val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")
        val numeratorOptions = listOf("Numerator", "Denominator")
        val classTimes = listOf("08:30 - 09:20", "09:30 - 10:20", "10:30 - 11:20")

        with(binding) {
            dayDropdown.setOnClickListener {
                showSimplePicker("Select Day", daysOfWeek) { selected ->
                    selectedDay = selected
                    setDropdownText(dayDropdownText, selected)
                }
            }

            numeratorDropdown.setOnClickListener {
                showSimplePicker("Numerator or Denominator", numeratorOptions) { selected ->
                    selectedNumerator = selected
                    setDropdownText(numeratorDropdownText, selected)
                }
            }

            classtimeDropdown.setOnClickListener {
                showSimplePicker("Select Class Time", classTimes) { selected ->
                    selectedClassTime = selected
                    setDropdownText(classtimeDropdownText, selected)
                }
            }

            generateQRBtn.setOnClickListener {
                val minutes = minuteEt.text.toString()
                val seconds = secondEt.text.toString()

                if (selectedDay == null || selectedNumerator == null || selectedClassTime == null || minutes.isEmpty() || seconds.isEmpty()) {
                    Toast.makeText(requireContext(), "Please complete all fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val qrContent = """
                    Day: $selectedDay
                    Week Type: $selectedNumerator
                    Time: $selectedClassTime
                    Check Time: $minutes:$seconds
                """.trimIndent()

                findNavController().navigate(R.id.action_checkingAttendanceFragment_to_qrShowingFragment)
                Log.d("QR_CONTENT", qrContent)
            }
        }
    }

    private fun setDropdownText(textView: TextView, text: String) {
        textView.text = text
    }

    private fun showSimplePicker(title: String, options: List<String>, onSelected: (String) -> Unit) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setItems(options.toTypedArray()) { _, which ->
                onSelected(options[which])
            }
            .show()
    }
}

