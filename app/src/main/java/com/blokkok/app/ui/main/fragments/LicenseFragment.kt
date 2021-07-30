package com.blokkok.app.ui.main.fragments;

import android.os.Bundle
import android.text.util.Linkify
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.blokkok.app.R
import com.blokkok.app.databinding.FragmentLicenseBinding
import com.blokkok.app.ui.main.viewmodels.LicensesViewModel
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class LicenseFragment : Fragment(R.layout.fragment_license) {
    private val viewModel: LicensesViewModel by viewModels()
    private val binding by viewBinding(FragmentLicenseBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.licenseText.observe(viewLifecycleOwner) {
            binding.licenseText.text = it
            Linkify.addLinks(binding.licenseText, Linkify.ALL)
        }

        viewModel.loadLicense(requireContext().assets)
    }
}