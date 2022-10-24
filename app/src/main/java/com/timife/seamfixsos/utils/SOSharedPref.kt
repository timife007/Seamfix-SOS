package com.timife.seamfixsos.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.core.content.edit
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.timife.seamfixsos.domain.model.SOSItem

class SOSharedPref {

    companion object {

        private var prefs: SharedPreferences? = null
        private const val SOSITEM = "SOSITEM"

        @Volatile
        private var instance: SOSharedPref? = null

        //initialize Moshi
        private val moshi: Moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        /**
         * This checks if there is an existing instance of the [SOSharedPref] in the
         * specified [context] and creates one if there isn't or else, it returns the
         * already existing instance. This function ensures that the [SOSharedPref] is
         * accessed at any instance by a single thread.
         */
        fun getInstance(context: Context): SOSharedPref {
            synchronized(this) {
                val inst = instance
                if (inst == null) {
                    prefs = PreferenceManager.getDefaultSharedPreferences(context)
                    instance = inst
                }
                return SOSharedPref()
            }
        }
    }

    /**
     * This function saves a [SOSITEM]
     */
    fun saveSOSItem(sosItem: SOSItem) {
        prefs?.edit(commit = true) {
            val jsonAdapter = moshi.adapter(SOSItem::class.java)
            val json = jsonAdapter.toJson(sosItem)
            putString(SOSITEM,json)
        }
    }

    /**
     * This function gets the value of the saved [SOSITEM]
     */
    fun getSOSItem(): SOSItem? {
        val json = prefs?.getString(SOSITEM, null)
        val adapter = moshi.adapter(SOSItem::class.java)
        return adapter.fromJson(json)
    }
}