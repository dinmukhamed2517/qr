package com.example.qrattendance.presentation.screens.teacher

import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qrattendance.presentation.adapters.AttendanceAdapter
import com.example.qrattendance.presentation.base.BaseFragment
import com.example.qrattendance.data.models.toAttendanceSummaryList
import com.example.qrattendance.databinding.FragmentAttendanceTrackingBinding
import dagger.hilt.android.AndroidEntryPoint


import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.example.qrattendance.R
import com.example.qrattendance.data.storage.UserPreferences
import com.example.qrattendance.presentation.viewmodel.QrViewModel
import com.example.qrattendance.presentation.viewmodel.UiState
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class AttendanceTrackingFragment :
    BaseFragment<FragmentAttendanceTrackingBinding>(FragmentAttendanceTrackingBinding::inflate) {

    private val viewModel: QrViewModel by viewModels()
    private lateinit var adapter: AttendanceAdapter

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onBindView() {
        super.onBindView()

        binding.disciplineText.text = "Startups and entrepreneurship"
        binding.groupText.text = "IT1"
        binding.dateText.text = "September 2025"

        adapter = AttendanceAdapter(emptyList())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        binding.filterBtn.setOnClickListener {
            showFilterDialog()
        }


        observeAttendance()

        lifecycleScope.launch {
            userPreferences.getUserLogin().collect { login ->
                if(login != null)
                viewModel.loadGroupAttendance(
                    professor = login ,
                    subjectId = 4,
                    groupId = 1
                )
                else{
                    Log.e("Attendance tracking", "Login is null")
                }
            }
        }


        binding.materialButton.setOnClickListener {
            Toast.makeText(requireContext(), "Downloading report...", Toast.LENGTH_SHORT).show()
            downloadReport()
        }
    }

    private fun observeAttendance() {
        lifecycleScope.launchWhenStarted {
            viewModel.groupAttendanceState.collect { state ->
                when (state) {
                    is UiState.Loading -> {
                        binding.loadingView.isVisible = true
                    }

                    is UiState.Success -> {
                        binding.loadingView.isVisible = false
                        val summaries = state.data.toAttendanceSummaryList()
                        adapter = AttendanceAdapter(summaries)
                        binding.recyclerView.adapter = adapter
                    }

                    is UiState.Error -> {
                        binding.loadingView.isVisible = false
                        Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    private fun showFilterDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_attendance_filter, null)
        val subjectEditText = dialogView.findViewById<EditText>(R.id.et_subject_id)
        val groupEditText = dialogView.findViewById<EditText>(R.id.et_group_id)

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Filter Attendance")
            .setView(dialogView)
            .setPositiveButton("Apply") { _, _ ->
                val subjectId = subjectEditText.text.toString().toIntOrNull()
                val groupId = groupEditText.text.toString().toIntOrNull()

                if (subjectId != null && groupId != null) {
                    viewModel.loadGroupAttendance(
                        professor = "Aitim Aigerim",
                        subjectId = subjectId,
                        groupId = groupId
                    )
                } else {
                    Toast.makeText(requireContext(), "Enter valid IDs", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun downloadReport() {
        val reportData = adapter.getData()
        if (reportData.isEmpty()) {
            Toast.makeText(requireContext(), "No data to export", Toast.LENGTH_SHORT).show()
            return
        }

        val reportBuilder = StringBuilder()
        reportBuilder.append("Student Attendance Report\n")
        reportBuilder.append("Discipline: ${binding.disciplineText.text}\n")
        reportBuilder.append("Group: ${binding.groupText.text}\n")
        reportBuilder.append("Date: ${binding.dateText.text}\n\n")
        reportBuilder.append("Name\tPresent\tAbsent\tExcused\tTotal\n")

        for (record in reportData) {
            reportBuilder.append("${record.name}\t${record.attended}\t${record.absent}\t${record.excused}\t${record.total}\n")
        }

        try {
            val fileName = "attendance_report_${System.currentTimeMillis()}.txt"
            val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)
            file.writeText(reportBuilder.toString())

            Toast.makeText(requireContext(), "Report saved to Downloads:\n${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Failed to save report: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }



}
