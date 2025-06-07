package com.example.qrattendance.presentation.screens

import com.example.qrattendance.presentation.base.BaseFragment
import com.example.qrattendance.databinding.FragmentProfileBinding


import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.qrattendance.R
import com.example.qrattendance.presentation.MainActivity
import com.example.qrattendance.presentation.viewmodel.QrViewModel
import com.example.qrattendance.presentation.viewmodel.UiState
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private val viewModel: QrViewModel by viewModels()
    private var loginUser = ""

    override fun onBindView() {
        super.onBindView()

        lifecycleScope.launchWhenStarted {
            val login = viewModel.getUserLoginFromPreferences()
            loginUser = login?: ""
            binding.etUsername.setText(login)
            viewModel.fetchAndStoreUserInfo()
        }
        binding.changePassword.setOnClickListener {
            showChangePasswordDialog()
        }


        binding.logout.setOnClickListener {
            viewModel.logout()

            lifecycleScope.launchWhenStarted {
                viewModel.userInfoState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {

                        }
                        is UiState.Success -> {
                            val role = state.data.role
                            if(role == "student"){
                                findNavController().navigate(R.id.action_profileFragment3_to_login_nav_graph)
                            }
                            else{
                                findNavController().navigate(R.id.action_profileFragment2_to_login_nav_graph)
                            }
                        }
                        is UiState.Error -> {
                        }
                    }
                }
            }
        }
    }
    private fun showChangePasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.change_password_dialog, null)
        val oldPasswordEditText = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_old_password)
        val newPasswordEditText = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_new_password)

        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Change Password")
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_submit).setOnClickListener {
            val oldPassword = oldPasswordEditText.text.toString().trim()
            val newPassword = newPasswordEditText.text.toString().trim()

            if (oldPassword.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in both fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.changePassword(loginUser, oldPassword, newPassword)

            dialog.dismiss()
        }

        dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_cancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}

