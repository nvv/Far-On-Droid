package com.openfarmanager.android.model

interface Entity {

    fun name(): String

    fun path(): String

    fun size(): Long

    fun isDirectory(): Boolean

    fun canAccess(): Boolean
}