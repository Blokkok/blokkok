package com.blokkok.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ModulesFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewInflater = inflater.inflate(R.layout.fragment_modules, container, false)
        val addModuleFab: FloatingActionButton = viewInflater.findViewById(R.id.addModule)
        addModuleFab.setOnClickListener { }
        return viewInflater
    }

    companion object {
        fun newInstance(): ModulesFragment {
            return ModulesFragment()
        }
    }
}