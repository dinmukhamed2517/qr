package com.example.qrattendance.presentation.base

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.qrattendance.data.models.StudentPerformanceResponse

abstract class BaseViewHolder<VB : ViewBinding, T>(protected open val binding: VB) :
    RecyclerView.ViewHolder(binding.root) {
    abstract fun bindView(item: T)
}

abstract class BaseStudentPerformanceItemViewHolder<VB : ViewBinding>(override val binding: VB) :
    BaseViewHolder<VB, StudentPerformanceResponse>(binding)

