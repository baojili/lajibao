package com.ln.ljb.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ln.base.fragment.BaseFragment
import com.ln.ljb.R

class HomeFragment : BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

}