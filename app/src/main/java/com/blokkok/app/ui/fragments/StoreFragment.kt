package com.blokkok.app.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.blokkok.app.R
import com.blokkok.app.databinding.FragmentStoreBinding
import com.blokkok.app.ui.adapters.StoreItemAdapter
import com.blokkok.app.ui.adapters.StoreItemMetadata
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class StoreFragment : Fragment(R.layout.fragment_store) {

    private val binding by viewBinding(FragmentStoreBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = Firebase.firestore;
        val sharedContent = db.collection("shared");

        val newModulesRV    = binding.newModulesRecyclerview
        val trendModulesRV  = binding.trendingModulesRecyclerview
        val newProjectsRV   = binding.newProjectsRecyclerview
        val trendProjectsRV = binding.trendingProjectsRecyclerview

        newModulesRV.layoutManager    = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        trendModulesRV.layoutManager  = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        newProjectsRV.layoutManager   = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        trendProjectsRV.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

        sharedContent.get().addOnSuccessListener { document ->
            if (document != null) {
                val storeItemList: List<StoreItemMetadata> = document.map { it.toObject() }

                newModulesRV.adapter    = StoreItemAdapter(storeItemList);
                trendModulesRV.adapter  = StoreItemAdapter(storeItemList);
                newProjectsRV.adapter   = StoreItemAdapter(storeItemList);
                trendProjectsRV.adapter = StoreItemAdapter(storeItemList);
            } else {
                Log.d("DataBase", "No such document");
            }

        }.addOnFailureListener { exception -> Log.d("DataBase", "get failed with ", exception); };
    }
}