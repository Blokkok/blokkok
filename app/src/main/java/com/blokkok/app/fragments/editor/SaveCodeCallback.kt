package com.blokkok.app.fragments.editor

/**
 * Used to transfer code between from the fragment to the parent activity
 */
interface SaveCodeCallback {
    fun onSaved(code: String)
}