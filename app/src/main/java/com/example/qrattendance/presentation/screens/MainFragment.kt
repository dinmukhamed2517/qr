package com.example.qrattendance.presentation.screens

import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.example.qrattendance.R
import com.example.qrattendance.data.storage.UserPreferences
import com.example.qrattendance.presentation.base.BaseFragment
import com.example.qrattendance.databinding.FragmentMainBinding
import com.example.qrattendance.presentation.viewmodel.QrViewModel
import com.example.qrattendance.presentation.viewmodel.UiState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>(FragmentMainBinding::inflate) {

    @Inject
    lateinit var userPreferences: UserPreferences

    private val viewModel: QrViewModel by viewModels()

    override var showBottomNavigation: Boolean = false

    override fun onBindView() {
        super.onBindView()

        viewModel.fetchAndStoreUserInfo()

        lifecycleScope.launchWhenStarted {
            viewModel.userInfoState.collect { state ->
                when (state) {
                    is UiState.Loading -> {
                        binding.loadingView.isVisible = false
                    }
                    is UiState.Success -> {
                        binding.loadingView.isVisible = true
                        val role = state.data.role
                        setupNavGraph(role)
                    }
                    is UiState.Error -> {
                        binding.loadingView.isVisible = true
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setupNavGraph(role: String) {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val graphInflater = navController.navInflater
        val graph = when {
            role.equals("admin", ignoreCase = true) -> graphInflater.inflate(R.navigation.teacher_nav_graph)
            role.equals("teacher", ignoreCase = true) -> graphInflater.inflate(R.navigation.teacher_nav_graph) // Teacher Flow
            role.equals("student", ignoreCase = true) -> graphInflater.inflate(R.navigation.student_nav_graph) // Student Flow
            else -> graphInflater.inflate(R.navigation.login_nav_graph)
        }

        navController.graph = graph
    }
}
