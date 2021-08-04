package com.blokkok.app.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.blokkok.app.R
import com.blokkok.app.databinding.FragmentAboutBinding
import com.blokkok.app.ui.adapters.GitHubUsersRecyclerViewAdapter
import com.blokkok.app.ui.viewmodels.AboutViewModel
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class AboutFragment : Fragment(R.layout.fragment_about) {
    private val viewModel: AboutViewModel by viewModels()
    private val binding by viewBinding(FragmentAboutBinding::bind)

    private val teamAdapter = GitHubUsersRecyclerViewAdapter()
    private val contributorsAdapter = GitHubUsersRecyclerViewAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.aboutTeamRv.layoutManager = LinearLayoutManager(requireContext())
        binding.aboutTeamRv.adapter = teamAdapter

        binding.aboutContributorsRv.layoutManager = LinearLayoutManager(requireContext())
        binding.aboutContributorsRv.adapter = contributorsAdapter

        val description = binding.aboutDescription

        viewModel.description.observe(viewLifecycleOwner) {
            description.text = it
        }

        viewModel.members.observe(viewLifecycleOwner) {
            teamAdapter.users = it
            teamAdapter.notifyDataSetChanged()
        }

        viewModel.contributors.observe(viewLifecycleOwner) {
            contributorsAdapter.users = it
            contributorsAdapter.notifyDataSetChanged()
        }

        viewModel.loadDescription(requireContext().assets)
        viewModel.fetchTeam()
        viewModel.fetchContributors()
    }
}