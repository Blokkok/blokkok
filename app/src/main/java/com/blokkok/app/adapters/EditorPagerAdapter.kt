package com.blokkok.app.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.blokkok.app.fragments.editor.JavaCodeFragment
import com.blokkok.app.fragments.editor.LayoutCodeFragment
import com.blokkok.app.fragments.editor.SaveCodeCallback

class EditorPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val javaCodeCallback: SaveCodeCallback,
    private val layoutCodeCallback: SaveCodeCallback,
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> LayoutCodeFragment(javaCodeCallback)
            2 -> JavaCodeFragment(layoutCodeCallback)

            else -> throw IllegalArgumentException("getItem asks for fragment number $position, while we only have $itemCount")
        }
    }
}