package com.blokkok.app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewInflater = inflater.inflate(R.layout.fragment_home, container, false)
        val newProjectFab: FloatingActionButton = viewInflater.findViewById(R.id.newProject)
        newProjectFab.setOnClickListener { v: View? ->
            val intent = Intent()
            intent.setClass(requireActivity(), EditorActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        return viewInflater
    }

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}