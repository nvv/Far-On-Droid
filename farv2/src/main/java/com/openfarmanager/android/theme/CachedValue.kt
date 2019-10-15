package com.openfarmanager.android.theme

import android.annotation.SuppressLint
import android.content.SharedPreferences
import java.lang.RuntimeException

class CachedValue<T>(val name: String, value: T? = null, private val defValue: T? = null, private val type: Class<T>) {

    companion object {

        private var sharedPref: SharedPreferences? = null

        fun initialize(sp: SharedPreferences) {
            sharedPref = sp
        }

    }

    constructor(name: String, type: Class<T>) : this(name, null, type)

    constructor(name: String, defValue: T?, type: Class<T>) : this(name, null, defValue, type)

    private val lock = Any()
    private var loaded = false
    private var sharedPreferences: SharedPreferences? = null

    var value: T? = null
        set(value) {
            synchronized(lock) {
                loaded = true
                field = value
                write(field)
            }
        }
        get() {
            synchronized(lock) {
                if (!loaded) {
                    field = load()
                    loaded = true
                }
                return field
            }
        }

    fun safeValue(): T {
        return value ?: defValue ?: throw RuntimeException()
    }

    init {
        sharedPreferences = sharedPref
        this.value = value
        loaded = value != null
    }

    private fun write(value: T?) {
        val editor = sharedPreferences?.edit()
        editor?.let {
            when (value) {
                is String -> it.putString(name, value as String)
                is Int -> it.putInt(name, value as Int)
                is Float -> it.putFloat(name, value as Float)
                is Long -> it.putLong(name, value as Long)
                is Boolean -> it.putBoolean(name, value as Boolean)
                else -> {
                    //nop
                }
            }
        }
        editor?.apply()
    }

    @Suppress("UNCHECKED_CAST")
    private fun load(): T? {
        return when (type) {
            String::class.java -> sharedPreferences?.getString(name, defValue as String?) as T
            Int::class.java -> sharedPreferences?.getInt(name, defValue as Int? ?: 0) as T
            Float::class.java -> sharedPreferences?.getFloat(name, defValue as Float? ?: 0f) as T
            Long::class.java -> sharedPreferences?.getLong(name, defValue as Long? ?: 0L) as T
            Boolean::class.java -> sharedPreferences?.getBoolean(name, defValue as Boolean? ?: false) as T
            else -> null
        }
    }

    @SuppressLint("CommitPrefEdits")
    fun delete() {
        synchronized(lock) {
            sharedPreferences?.edit()?.remove(name)?.commit()
            clear()
        }
    }

    private fun clear() {
        synchronized(lock) {
            loaded = false
            this.value = null
        }
    }

}