package com.blokkok.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.blokkok.app.adapters.EditorPagerAdapter
import com.blokkok.app.fragments.editor.SaveCodeCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class EditorActivity : AppCompatActivity() {

    private lateinit var editorAdapter: EditorPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        val actionBar = findViewById<Toolbar>(R.id.toolBar)
        val editorViewPager = findViewById<ViewPager2>(R.id.editor_viewpager)
        val tabLayout = findViewById<TabLayout>(R.id.tabs)

        setSupportActionBar(actionBar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        editorAdapter =
            EditorPagerAdapter(this,
                object : SaveCodeCallback { /* Java code save callback */
                    override fun onSaved(code: String) {
                        TODO("Not yet implemented")
                    }
                },
                object : SaveCodeCallback { /* Layout code save callback */
                    override fun onSaved(code: String) {
                        TODO("Not yet implemented")
                    }
                }
            )

        editorViewPager.adapter = editorAdapter

        TabLayoutMediator(tabLayout, editorViewPager) { tab, position ->
            when (position) {
                1 -> tab.text = "LAYOUT"
                2 -> tab.text = "CODE"
            }
        }.attach()
    }
}