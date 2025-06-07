package com.example.qrattendance.presentation.screens.student

import android.util.Log
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qrattendance.R
import com.example.qrattendance.data.storage.UserPreferences
import com.example.qrattendance.presentation.adapters.CourseAdapter
import com.example.qrattendance.presentation.base.BaseFragment
import com.example.qrattendance.databinding.FragmentHomeBinding
import com.example.qrattendance.presentation.viewmodel.QrViewModel
import com.example.qrattendance.presentation.viewmodel.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val viewModel: QrViewModel by viewModels()
    private lateinit var adapter: CourseAdapter
    private var studentLogin: String = ""
    @Inject
    lateinit var userPreferences: UserPreferences
    override fun onBindView() {
        super.onBindView()

        setupRecyclerView()

        setupObservers()

        val startDate = "01-12-2024"
        val endDate = "31-12-2024"
        lifecycleScope.launch {
            userPreferences.getUserLogin().collect { login ->
                if (login != null){
                    viewModel.getStudentSchedule(login, startDate, endDate)
                    studentLogin = login
                }
            }
        }
        binding.dataFilterBtn.setOnClickListener {
            showSliderDateDialog()
        }
    }

    private fun setupRecyclerView() {
        adapter = CourseAdapter()
        binding.homeRecycler.adapter = adapter
        binding.homeRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private fun setupObservers() {
        viewModel.scheduleState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.loadingView.isVisible = true
                }

                is UiState.Success -> {
                    binding.loadingView.isVisible = false
                    val lessons = state.data.flatMap { it.lessons }
                    if (lessons.isEmpty()) {
                        Log.d("HomeFragment", "No lessons found")
                    } else {
                        Log.d("HomeFragment", "Lessons found: ${lessons.size}")
                    }
                    adapter.submitList(lessons)

                }

                is UiState.Error -> {
                    binding.loadingView.isVisible = false
                }
            }
        }
    }

    private fun showSliderDateDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_data_range, null)

        val startSlider = dialogView.findViewById<com.google.android.material.slider.Slider>(R.id.startSlider)
        val endSlider = dialogView.findViewById<com.google.android.material.slider.Slider>(R.id.endSlider)
        val selectedRangeText = dialogView.findViewById<TextView>(R.id.selectedRangeText)

        val monthPicker = dialogView.findViewById<NumberPicker>(R.id.monthPicker)
        val yearPicker = dialogView.findViewById<NumberPicker>(R.id.yearPicker)

        val months = arrayOf("January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December")

        monthPicker.minValue = 0
        monthPicker.maxValue = 11
        monthPicker.displayedValues = months
        monthPicker.value = LocalDate.now().monthValue - 1

        val currentYear = LocalDate.now().year
        yearPicker.minValue = currentYear - 1
        yearPicker.maxValue = currentYear + 5
        yearPicker.value = currentYear

        fun updateRangeText() {
            val startDay = startSlider.value.toInt()
            val endDay = endSlider.value.toInt()
            val selectedMonth = formatTwoDigits(monthPicker.value + 1) // +1 because picker is 0-based
            val selectedYear = yearPicker.value.toString()

            val formattedStart = "${formatTwoDigits(startDay)}-$selectedMonth-$selectedYear"
            val formattedEnd = "${formatTwoDigits(endDay)}-$selectedMonth-$selectedYear"

            selectedRangeText.text = "Selected: $formattedStart to $formattedEnd"
        }

        startSlider.addOnChangeListener { _, _, _ -> updateRangeText() }
        endSlider.addOnChangeListener { _, _, _ -> updateRangeText() }

        updateRangeText() // Initial call to display text

        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Select Date Range")
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                val startDay = startSlider.value.toInt()
                val endDay = endSlider.value.toInt()

                if (startDay > endDay) {
                    Toast.makeText(requireContext(), "Start day must be before end day", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val selectedMonth = formatTwoDigits(monthPicker.value + 1)
                val selectedYear = yearPicker.value.toString()

                val formattedStart = "${formatTwoDigits(startDay)}-$selectedMonth-$selectedYear"
                val formattedEnd = "${formatTwoDigits(endDay)}-$selectedMonth-$selectedYear"

                viewModel.getStudentSchedule(studentLogin, formattedStart, formattedEnd)
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun formatTwoDigits(number: Int): String = if (number < 10) "0$number" else "$number"

}
