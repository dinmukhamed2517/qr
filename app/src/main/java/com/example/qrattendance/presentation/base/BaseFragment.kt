package com.example.qrattendance.presentation.base



import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.airbnb.lottie.LottieAnimationView
import com.example.qrattendance.R
import com.example.qrattendance.presentation.utils.BottomNavigationViewListener


typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T


abstract class BaseFragment<VB : ViewBinding>(private val inflate: Inflate<VB>) : Fragment() {
    private var _binding: VB? = null
    val binding get() = _binding ?: throw RuntimeException()

    private lateinit var bottomNavigationViewListener: BottomNavigationViewListener
    open var showBottomNavigation: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            bottomNavigationViewListener.showBottomNavigationView(showBottomNavigation)
            onInit()
            onBindView()
            bindViewModel()
        } catch (e: Exception) {
            Log.e("OnViewCreated", "Exception by view binding: ${e.message}")
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BottomNavigationViewListener) {
            bottomNavigationViewListener = context
        } else {
            throw RuntimeException("$context, error")
        }

    }
    protected fun showCustomDialog(title: String, content: String) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_success_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val animationView: LottieAnimationView = dialog.findViewById(R.id.animation)
        val titleTextView: TextView = dialog.findViewById(R.id.title)
        val contentTextView: TextView = dialog.findViewById(R.id.content)

        animationView.playAnimation()
        titleTextView.text = title
        contentTextView.text = content
        dialog.show()
        val button: Button = dialog.findViewById(R.id.ok_btn)
        button.setOnClickListener {
            dialog.dismiss()
        }
    }


    open fun onInit() {}
    open fun onBindView() {}
    open fun bindViewModel() {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}