package com.blokkok.app.fragments.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blokkok.app.R
import com.blokkok.app.adapters.StoreItemAdapter
import com.blokkok.app.adapters.StoreItemMetadata
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class StoreFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val root = inflater.inflate(R.layout.fragment_store, container, false);

        val db = Firebase.firestore;
        val sharedContent = db.collection("shared");

        val newModulesRV = root.findViewById<RecyclerView>(R.id.new_modules_recyclerview);
        val trendModulesRV = root.findViewById<RecyclerView>(R.id.trending_modules_recyclerview);
        val newProjectsRV = root.findViewById<RecyclerView>(R.id.new_projects_recyclerview);
        val trendProjectsRV = root.findViewById<RecyclerView>(R.id.trending_projects_recyclerview);

        newModulesRV.layoutManager    = LinearLayoutManager(context);
        trendModulesRV.layoutManager  = LinearLayoutManager(context);
        newProjectsRV.layoutManager   = LinearLayoutManager(context);
        trendProjectsRV.layoutManager = LinearLayoutManager(context);

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

        return root;
    }
}