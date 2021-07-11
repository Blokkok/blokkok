package com.blokkok.app.fragments.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.blokkok.app.EditorActivity
import com.blokkok.app.R
import com.blokkok.app.viewmodels.main.HomeViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val newProjectFab = root.findViewById<FloatingActionButton>(R.id.newProject)

        newProjectFab.setOnClickListener {
            startActivity(
                Intent()
                    .setClass(requireActivity(), EditorActivity::class.java)
            )

            requireActivity().finish()
        }

        return root
    }

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}