package com.example.qrattendance.presentation.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.qrattendance.R
import com.example.qrattendance.data.models.LoginRequest
import com.example.qrattendance.presentation.MainActivity
import com.example.qrattendance.presentation.base.BaseFragment
import com.example.qrattendance.databinding.FragmentLoginStudentBinding
import com.example.qrattendance.presentation.viewmodel.QrViewModel
import com.example.qrattendance.presentation.viewmodel.UiState
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginStudentFragment : BaseFragment<FragmentLoginStudentBinding>(FragmentLoginStudentBinding::inflate) {

    private val viewModel: QrViewModel by activityViewModels()
    private var isLoginDialogShown = false

    override var showBottomNavigation = false

    override fun onBindView() {
        super.onBindView()

        binding.loginBtn.setOnClickListener {
            val username = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            isLoginDialogShown = true

            val loginRequest = LoginRequest(username, password)
            viewModel.login(loginRequest)

        }

        observeLoginState()
    }

    private fun observeLoginState() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.loginState.collect { state ->
                when (state) {
                    is UiState.Loading -> {
                    }
                    is UiState.Success -> {
                        showCustomDialog("Success!", "Login")
                        if (!isLoginDialogShown) {
                            showCustomDialog("Success!", "Login")
                            isLoginDialogShown = true
                        }
                        viewModel.fetchAndStoreUserInfo()
                    }
                    is UiState.Error -> {
                        Toast.makeText(requireContext(), "Login failed: ${state.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}




