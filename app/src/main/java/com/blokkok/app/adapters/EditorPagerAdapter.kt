package com.blokkok.app.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.blokkok.app.fragments.editor.JavaCodeFragment
import com.blokkok.app.fragments.editor.LayoutCodeFragment
import com.blokkok.app.fragments.editor.ManifestCodeFragment

class EditorPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val javaSaveCode: (String) -> Unit,
    private val initialJavaCode: String,
    private val layoutSaveCode: (String) -> Unit,
    private val initialLayoutCode: String,
    private val manifestSaveCode: (String) -> Unit,
    private val initialManifestCode: String,
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> LayoutCodeFragment(layoutSaveCode, initialLayoutCode)
            1 -> JavaCodeFragment(javaSaveCode, initialJavaCode)
            2 -> ManifestCodeFragment(manifestSaveCode, initialManifestCode)

            else -> throw IllegalArgumentException("getItem asks for fragment number $position, while we only have $itemCount")
        }
    }
}