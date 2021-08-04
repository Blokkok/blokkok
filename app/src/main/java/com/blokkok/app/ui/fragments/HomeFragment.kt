package com.blokkok.app.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.blokkok.app.R
import com.blokkok.app.databinding.FragmentHomeBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class HomeFragment(
    private val openModuleManagerCallback: () -> Unit
) : Fragment(R.layout.fragment_home) {

    private val binding by viewBinding(FragmentHomeBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.homeOpenModuleManager.setOnClickListener {
            openModuleManagerCallback()
        }
    }
}