package com.example.qrattendance.presentation.screens.teacher

import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qrattendance.R
import com.example.qrattendance.presentation.base.BaseFragment
import com.example.qrattendance.databinding.FragmentAcademicPerformanceBinding
import com.example.qrattendance.presentation.adapters.SubjectPerformanceAdapter
import com.example.qrattendance.presentation.viewmodel.QrViewModel
import com.example.qrattendance.presentation.viewmodel.UiState
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class AcademicPerformanceFragment: BaseFragment<FragmentAcademicPerformanceBinding>(FragmentAcademicPerformanceBinding::inflate) {
    private val viewModel: QrViewModel by viewModels()
    private lateinit var adapter: SubjectPerformanceAdapter

    override fun onBindView() {
        super.onBindView()

        setupRecycler()
        observePerformance()
        binding.groupText.text = "2"
        binding.subjectText.text = "1"


        viewModel.loadSubjectGroupPerformance(subjectId = 1, groupId = 2)

        binding.downloadReport.setOnClickListener {
            if (adapter.getData().isEmpty()) {
                Toast.makeText(requireContext(), "No data to export", Toast.LENGTH_SHORT).show()
            } else {
                exportPerformanceReport()
            }
        }

        binding.filterBtn.setOnClickListener {
            showFilterDialog()
        }
    }

    private fun setupRecycler() {
        adapter = SubjectPerformanceAdapter(emptyList())
        binding.performanceRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.performanceRecycler.adapter = adapter
    }

    private fun observePerformance() {
        lifecycleScope.launchWhenStarted {
            viewModel.subjectGroupPerformanceState.collect { state ->
                when (state) {
                    is UiState.Loading -> binding.loadingView.isVisible = true

                    is UiState.Success -> {
                        binding.loadingView.isVisible = false
                        adapter = SubjectPerformanceAdapter(state.data)
                        binding.performanceRecycler.adapter = adapter
                    }

                    is UiState.Error -> {
                        binding.loadingView.isVisible = false
                        Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun exportPerformanceReport() {
        val data = adapter.getData()
        val fileContent = buildString {
            append("Student Academic Performance\n\n")
            append("Name\tMidterm\tEndterm\tAverage\tFinal\tTotal\n")

            data.forEach {
                val perf = it.student_performance.firstOrNull()
                val mid = perf?.point1 ?: 0
                val end = perf?.point2 ?: 0
                val avg = perf?.point3 ?: 0
                val exam = perf?.exam_mark ?: 0
                val total = mid + end + avg + (exam * 0.4)
                append("${it.name}\t$mid\t$end\t$avg\t$exam\t${"%.2f".format(total)}\n")
            }
        }

        val file = File(requireContext().getExternalFilesDir(null), "academic_performance_${System.currentTimeMillis()}.txt")
        file.writeText(fileContent)
        Toast.makeText(requireContext(), "Saved to ${file.absolutePath}", Toast.LENGTH_LONG).show()
    }


    private fun showFilterDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_attendance_filter, null)
        val subjectEditText = dialogView.findViewById<EditText>(R.id.et_subject_id)
        val groupEditText = dialogView.findViewById<EditText>(R.id.et_group_id)

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Filter Academic Performance")
            .setView(dialogView)
            .setPositiveButton("Apply") { _, _ ->
                val subjectId = subjectEditText.text.toString().toIntOrNull()
                val groupId = groupEditText.text.toString().toIntOrNull()

                if (subjectId != null && groupId != null) {
                    viewModel.loadSubjectGroupPerformance(subjectId, groupId)
                    binding.groupText.text = "$groupId"
                    binding.subjectText.text = "$subjectId"
                } else {
                    Toast.makeText(requireContext(), "Enter valid IDs", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

}