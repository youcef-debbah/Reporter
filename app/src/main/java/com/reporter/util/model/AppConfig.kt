package com.reporter.util.model

import android.annotation.SuppressLint
import androidx.annotation.AnyThread
import androidx.compose.runtime.*
import com.google.common.collect.ImmutableMap
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigValue
import com.reporter.common.*
import com.reporter.util.BuildTypeSettings
import com.reporter.util.ui.AbstractApplication
import com.tencent.mmkv.MMKV
import java.util.concurrent.ConcurrentHashMap

@AnyThread
object AppConfig {

    @Volatile
    @SuppressLint("StaticFieldLeak")
    private var remoteConfig: FirebaseRemoteConfig? = null

//    private lateinit var context: AbstractApplication
    private lateinit var localConfig: MMKV

    private val booleanStates = ConcurrentHashMap<String, MutableState<Boolean>>()
    private val longStates = ConcurrentHashMap<String, MutableState<Long>>()
    private val intStates = ConcurrentHashMap<String, MutableState<Int>>()
    private val stringStates = ConcurrentHashMap<String, MutableState<String>>()

    private val remoteConfigCache = ConcurrentHashMap<String, FirebaseRemoteConfigValue>()

    fun init(
        app: dagger.Lazy<FirebaseApp>,
        vararg remoteConfigDefaults: Map<String, Any>,
    ) {
        val context = AbstractApplication.INSTANCE
        MMKV.initialize(context)
        localConfig = MMKV.mmkvWithID("local_config")

        ioLaunch {

            val config = FirebaseRemoteConfig.getInstance(app.get())
            remoteConfig = config

            config.activate().addOnCompleteListener {
                if (get(LOCAL_NO_CONFIG_CACHE) || BuildTypeSettings.DEBUG) {
                    config.fetch(0).addOnCompleteListener { fetch ->
                        if (fetch.isSuccessful)
                            set(LOCAL_NO_CONFIG_CACHE, false)
                    }
                } else {
                    config.fetch()
                }

            }

            val allRemoteConfigDefaults = ImmutableMap.Builder<String, Any>()
            for (values in remoteConfigDefaults) {
                allRemoteConfigDefaults.putAll(values)
            }

            config.setDefaultsAsync(allRemoteConfigDefaults.build()).addOnCompleteListener {
                Teller.debug {
                    "app config initialized with version: " + get(REMOTE_CONFIG_VERSION)
                }
            }
        }
    }

    private fun getRemoteValue(key: String): FirebaseRemoteConfigValue? =
        remoteConfigCache.calcIfNull(key) {
            remoteConfig?.let {
                it.getValue(key).let { value ->
                    if (value.source == FirebaseRemoteConfig.VALUE_SOURCE_REMOTE)
                        return@calcIfNull value
                }
            }
            return@calcIfNull null
        }

    @PublicAPI
    fun get(remoteString: RemoteConfig<String>): String =
        getRemoteValue(remoteString.key)?.asString() ?: remoteString.default

    @PublicAPI
    fun get(remoteInt: RemoteConfig<Int>): Int =
        getRemoteValue(remoteInt.key)?.asLong()?.toInt() ?: remoteInt.default

    @PublicAPI
    fun get(remoteLong: RemoteConfig<Long>): Long =
        getRemoteValue(remoteLong.key)?.asLong() ?: remoteLong.default

    @PublicAPI
    fun get(remoteBoolean: RemoteConfig<Boolean>): Boolean =
        getRemoteValue(remoteBoolean.key)?.asBoolean() ?: remoteBoolean.default

    @PublicAPI
    fun get(localString: LocalConfig<String>): String = get(localString, localString.default)

    @PublicAPI
    fun get(localInt: LocalConfig<Int>): Int = get(localInt, localInt.default)

    @PublicAPI
    fun get(localLong: LocalConfig<Long>): Long = get(localLong, localLong.default)

    @PublicAPI
    fun get(localBoolean: LocalConfig<Boolean>): Boolean = get(localBoolean, localBoolean.default)

    @PublicAPI
    fun stringState(localString: LocalConfig<String>): State<String> =
        mutableStringState(localString)

    private fun mutableStringState(localString: LocalConfig<String>) =
        stringStates.calcIfAbsent(localString.key) {
            mutableStateOf(get(localString))
        }

    @PublicAPI
    fun intState(localInt: LocalConfig<Int>): State<Int> = intMutableState(localInt)

    private fun intMutableState(localInt: LocalConfig<Int>) =
        intStates.calcIfAbsent(localInt.key) {
            mutableStateOf(get(localInt))
        }

    @PublicAPI
    fun longState(localLong: LocalConfig<Long>): State<Long> = longMutableState(localLong)

    private fun longMutableState(localLong: LocalConfig<Long>) =
        longStates.calcIfAbsent(localLong.key) {
            mutableStateOf(get(localLong))
        }

    @PublicAPI
    fun booleanState(localBoolean: LocalConfig<Boolean>): State<Boolean> =
        booleanMutableState(localBoolean)

    private fun booleanMutableState(localBoolean: LocalConfig<Boolean>) =
        booleanStates.calcIfAbsent(localBoolean.key) {
            mutableStateOf(get(localBoolean))
        }

    @PublicAPI
    fun set(localString: LocalConfig<String>, value: String): String {
        val newValue = try {
            localConfig.putString(localString.key, value)
            value
        } catch (e: Exception) { // not suspended
            Teller.warn("failed to set local string config: $localString to: $value", e)
            get(localString)
        }

        stringStates[localString.key]?.value = newValue
        return newValue
    }

    @PublicAPI
    fun set(localInt: LocalConfig<Int>, value: Int): Int {
        val newValue = try {
            localConfig.putInt(localInt.key, value)
            value
        } catch (e: Exception) { // not suspended
            Teller.warn("failed to set local int config: $localInt to: $value", e)
            get(localInt)
        }

        intStates[localInt.key]?.value = newValue
        return newValue
    }

    @PublicAPI
    fun set(localLong: LocalConfig<Long>, value: Long): Long {
        val newValue = try {
            localConfig.putLong(localLong.key, value)
            value
        } catch (e: Exception) { // not suspended
            Teller.warn("failed to set local long config: $localLong to: $value", e)
            get(localLong)
        }

        longStates[localLong.key]?.value = newValue
        return newValue
    }

    @PublicAPI
    fun set(localBoolean: LocalConfig<Boolean>, value: Boolean): Boolean {
        val newValue = try {
            localConfig.putBoolean(localBoolean.key, value)
            value
        } catch (e: Exception) { // not suspended
            Teller.warn("failed to set local boolean config: $localBoolean to: $value", e)
            get(localBoolean)
        }

        booleanStates[localBoolean.key]?.value = newValue
        return newValue
    }

    @PublicAPI
    fun get(localString: LocalConfig<String>, defaultString: String): String {
        try {
            return localConfig.getString(localString.key, defaultString)!!
        } catch (e: Exception) { // not suspended
            Teller.warn("failed to load local string config: $localString", e)
        }
        return defaultString
    }

    @PublicAPI
    fun get(localInt: LocalConfig<Int>, defaultInt: Int): Int {
        try {
            return localConfig.getInt(localInt.key, defaultInt)
        } catch (e: Exception) { // not suspended
            Teller.warn("failed to load local int config: $localInt", e)
        }
        return defaultInt
    }

    @PublicAPI
    fun get(localLong: LocalConfig<Long>, defaultLong: Long): Long {
        try {
            return localConfig.getLong(localLong.key, defaultLong)
        } catch (e: Exception) { // not suspended
            Teller.warn("failed to load local long config: $localLong", e)
        }
        return defaultLong
    }

    @PublicAPI
    fun get(localBoolean: LocalConfig<Boolean>, defaultBoolean: Boolean): Boolean {
        try {
            return localConfig.getBoolean(localBoolean.key, defaultBoolean)
        } catch (e: Exception) { // not suspended
            Teller.warn("failed to load local boolean config: $localBoolean", e)
        }
        return defaultBoolean
    }
}

interface Config<T> {
    val key: String
    val default: T
}

class RemoteConfig<T>(override val key: String, override val default: T) : Config<T> {
    override fun toString() = key
}

class LocalConfig<T>(override val key: String, override val default: T) : Config<T> {
    override fun toString() = key
}

fun ImmutableMap.Builder<String, Any>.putConfig(config: Config<out Any>): ImmutableMap.Builder<String, Any> =
    put(config.key, config.default)