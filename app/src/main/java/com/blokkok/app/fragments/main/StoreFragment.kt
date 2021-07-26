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
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.delay


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

        val recyclerView1 = root.findViewById<RecyclerView>(R.id.recyclerView1);
        val recyclerView2 = root.findViewById<RecyclerView>(R.id.recyclerView2);
        val recyclerView3 = root.findViewById<RecyclerView>(R.id.recyclerView3);
        val recyclerView4 = root.findViewById<RecyclerView>(R.id.recyclerView4);

        recyclerView1.layoutManager = LinearLayoutManager(context);
        recyclerView2.layoutManager = LinearLayoutManager(context);
        recyclerView3.layoutManager = LinearLayoutManager(context);
        recyclerView4.layoutManager = LinearLayoutManager(context);

        sharedContent.get().addOnSuccessListener { document ->
            if (document != null) {
                // TODO: FIX THIS SHT, ITS NOT WORKING

                val StoreItemList: List<StoreItemMetadata> = ArrayList();

                for (snapshot in document) {
                    StoreItemList.plus(snapshot.toObject(StoreItemMetadata::class.java));
                    Log.d("sus", StoreItemList.toString());
                }

                recyclerView1.adapter = StoreItemAdapter(StoreItemList);
                recyclerView2.adapter = StoreItemAdapter(StoreItemList);
                recyclerView3.adapter = StoreItemAdapter(StoreItemList);
                recyclerView4.adapter = StoreItemAdapter(StoreItemList);
            } else {
                Log.d("DataBase", "No such document");
            }
        }.addOnFailureListener { exception -> Log.d("DataBase", "get failed with ", exception); };

        return root;
    }

    companion object {
        @JvmStatic
        fun newInstance() = StoreFragment();
    }
}