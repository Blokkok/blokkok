package com.blokkok.app.ui.editor.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.blokkok.app.ui.editor.fragments.JavaCodeFragment
import com.blokkok.app.ui.editor.fragments.LayoutCodeFragment
import com.blokkok.app.ui.editor.fragments.ManifestCodeFragment

class EditorPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val initialJavaCode: String,
    private val javaSaveCode: (String) -> Unit,
    private val initialLayoutCode: String,
    private val layoutSaveCode: (String) -> Unit,
    private val initialManifestCode: String,
    private val manifestSaveCode: (String) -> Unit,
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