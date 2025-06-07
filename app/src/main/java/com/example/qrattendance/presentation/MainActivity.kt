package com.example.qrattendance.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.qrattendance.R
import com.example.qrattendance.data.storage.UserPreferences
import com.example.qrattendance.databinding.ActivityMainBinding
import com.example.qrattendance.presentation.utils.BottomNavigationViewListener
import com.example.qrattendance.presentation.viewmodel.QrViewModel
import com.example.qrattendance.presentation.viewmodel.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), BottomNavigationViewListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val viewModel: QrViewModel by viewModels()

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController


        binding.bottomNavigation.setupWithNavController(navController)
        observeUserRole()

    }



    private fun observeUserRole() {
        lifecycleScope.launchWhenStarted {
            // Check if the token exists
            val token = userPreferences.getAccessToken().first()
            Log.d("main activity", "Access Token: $token")

            if (token.isNullOrEmpty()) {
                Log.d("main activity", "Token is empty, navigating to login")
                navController.setGraph(R.navigation.login_nav_graph)
                return@launchWhenStarted
            }

            // If token is valid, fetch user info
            viewModel.userInfoState.collect { state ->
                when (state) {
                    is UiState.Loading -> {
                        Log.d("main activity", "Fetching user info...")
                    }
                    is UiState.Success -> {
                        val role = state.data.role
                        val graph = when (role) {
                            "admin" -> R.navigation.student_nav_graph
                            "student" -> R.navigation.student_nav_graph
                            "teacher" -> R.navigation.teacher_nav_graph
                            else -> R.navigation.login_nav_graph
                        }
                        if (navController.graph.id != graph) {
                            Log.d("main activity", "Navigating to graph: $graph")
                            navController.setGraph(graph)
                        }
                        val menu = when (role) {
                            "student" -> R.menu.menu
                            "teacher" -> R.menu.menu_teacher
                            else -> R.menu.menu
                        }
                        binding.bottomNavigation.menu.clear()
                        binding.bottomNavigation.inflateMenu(menu)
                    }
                    is UiState.Error -> {
                        Log.d("main activity", "Error fetching user info, navigating to login")
                        navController.setGraph(R.navigation.login_nav_graph)
                    }
                    else -> {
                        Log.d("main activity", "User info state is still loading, waiting...")
                    }
                }
            }
        }
    }



    override fun showBottomNavigationView(show: Boolean) {
        if (show) {
            binding.bottomNavigation.visibility = View.VISIBLE
        } else {
            binding.bottomNavigation.visibility = View.GONE
        }
    }
}
