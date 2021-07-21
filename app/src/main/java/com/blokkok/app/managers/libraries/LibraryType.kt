package com.blokkok.app.managers.libraries

enum class LibraryType {
    /**
     * Means that this aar library isn't cached (just aar)
     */
    NOT_CACHED,
    /**
     * Means that this aar library is cached (aar + cache)
     */
    CACHED,
    /**
     * Means that this library is precompiled and we don't have the aar of it (just cache)
     */
    PRECOMPILED,
}