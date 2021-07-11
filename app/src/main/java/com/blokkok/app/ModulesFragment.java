package com.blokkok.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ModulesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ModulesFragment extends Fragment {

    public ModulesFragment() {
        // Required empty public constructor
    }

    public static ModulesFragment newInstance() {
        ModulesFragment fragment = new ModulesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewInflater = inflater.inflate(R.layout.fragment_modules, container, false);

        FloatingActionButton addModuleFab = viewInflater.findViewById(R.id.addModule);

        addModuleFab.setOnClickListener(v -> {
            // add stuff here!!
        });

        return viewInflater;
    }
}