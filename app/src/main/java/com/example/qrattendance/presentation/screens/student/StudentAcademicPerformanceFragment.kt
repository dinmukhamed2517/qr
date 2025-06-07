package com.example.qrattendance.presentation.screens.student

import com.example.qrattendance.databinding.FragmentStudentAcademicPerformanceBinding
import com.example.qrattendance.presentation.base.BaseFragment
import com.example.qrattendance.presentation.viewmodel.UiState


import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qrattendance.data.models.StudentPerformanceResponse
import com.example.qrattendance.data.storage.UserPreferences
import com.example.qrattendance.presentation.adapters.StudentPerformanceAdapter
import com.example.qrattendance.presentation.viewmodel.QrViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject


@AndroidEntryPoint
class StudentAcademicPerformanceFragment : BaseFragment<FragmentStudentAcademicPerformanceBinding>(
    FragmentStudentAcademicPerformanceBinding::inflate
) {

    private val viewModel: QrViewModel by viewModels()
    private lateinit var adapter: StudentPerformanceAdapter

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()

        lifecycleScope.launch {
            userPreferences.getUserLogin().collect { login ->
                if (login != null){
                    viewModel.getAllPerformanceForStudent(login)
                }
            }
        }

        binding.materialButton.setOnClickListener {
            Toast.makeText(requireContext(), "Downloading report...", Toast.LENGTH_SHORT).show()

            viewModel.studentPerformance.value?.let { state ->
                when (state) {
                    is UiState.Success -> {
                        val reportData = prepareReportData(state.data)
                        saveReportToDownloads(reportData)
                    }
                    else -> {
                        Toast.makeText(requireContext(), "Data not available for download", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = StudentPerformanceAdapter()
        binding.studentPerformanceRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.studentPerformanceRecycler.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.studentPerformance.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.loadingView.isVisible = true
                }

                is UiState.Success -> {
                    binding.loadingView.isVisible = false
                    adapter.submitList(state.data)
                    binding.studentPerformanceRecycler.visibility = View.VISIBLE
                }

                is UiState.Error -> {
                    binding.loadingView.isVisible = false
                    Toast.makeText(
                        requireContext(),
                        "Failed to load data: ${state.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun prepareReportData(data: List<StudentPerformanceResponse>): String {
        val reportBuilder = StringBuilder()

        reportBuilder.append("Student Performance Report\n\n")

        data.forEach { performance ->
            reportBuilder.append("Discipline: ${performance.title}\n")
            reportBuilder.append("Professor: ${performance.professor_subject.firstOrNull()?.users?.name ?: "Unknown"}\n")
            reportBuilder.append("Reporting Type: ${performance.reporting_type}\n")

//            performance.student_performance.forEach {
//                reportBuilder.append("\nMT: ${it.mt}")
//                reportBuilder.append("\nET: ${it.et}")
//                reportBuilder.append("\nAVG: ${it.avg}")
//                reportBuilder.append("\nFinal: ${it.finalExam}")
//                reportBuilder.append("\nTTL: ${it.ttl}")
//                reportBuilder.append("\nResult: ${it.result}\n")
//            }

            reportBuilder.append("\n-----------------------------------\n")
        }

        return reportBuilder.toString()
    }

    private fun saveReportToDownloads(reportData: String) {
        try {
            val fileName = "student_performance_report.txt"
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)

            FileOutputStream(file).use {
                it.write(reportData.toByteArray())
            }

            Toast.makeText(requireContext(), "Report saved to Downloads", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

}

