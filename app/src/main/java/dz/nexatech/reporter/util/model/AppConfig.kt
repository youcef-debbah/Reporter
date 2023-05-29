package dz.nexatech.reporter.util.model

import android.annotation.SuppressLint
import androidx.annotation.AnyThread
import androidx.compose.runtime.*
import com.google.common.collect.ImmutableMap
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigValue
import com.tencent.mmkv.MMKV
import dz.nexatech.reporter.client.common.ioLaunch
import dz.nexatech.reporter.client.common.mainLaunch
import dz.nexatech.reporter.client.model.REPORTER_LOCAL_CONFIGS
import dz.nexatech.reporter.client.model.REPORTER_REMOTE_CONFIGS_DEFAULTS
import dz.nexatech.reporter.util.BuildTypeSettings
import dz.nexatech.reporter.util.ui.AbstractApplication
import java.util.concurrent.atomic.AtomicReference

@AnyThread
object AppConfig {

    val REMOTE_CONFIGS_DEFAULTS = ImmutableMap.builder<String, Any>()
        .putAll(GLOBAL_REMOTE_CONFIG_DEFAULTS)
        .putAll(REPORTER_REMOTE_CONFIGS_DEFAULTS)
        .build()

    val LOCAL_CONFIGS = ImmutableMap.builder<String, LocalConfig<*>>()
        .putAll(GLOBAL_LOCAL_CONFIGS)
        .putAll(REPORTER_LOCAL_CONFIGS)
        .build()

    @Volatile
    @SuppressLint("StaticFieldLeak")
    private var remoteConfig: FirebaseRemoteConfig? = null
    private lateinit var localConfig: MMKV

    private val booleanStates: ImmutableMap<String, MutableState<Boolean>>
    private val longStates: ImmutableMap<String, MutableState<Long>>
    private val intStates: ImmutableMap<String, MutableState<Int>>
    private val stringStates: ImmutableMap<String, MutableState<String>>

    private val remoteConfigCache: ImmutableMap<String, AtomicReference<FirebaseRemoteConfigValue?>>

    init {
        val localBooleans: ImmutableMap.Builder<String, MutableState<Boolean>> =
            ImmutableMap.builder()
        val localLongs: ImmutableMap.Builder<String, MutableState<Long>> = ImmutableMap.builder()
        val localInts: ImmutableMap.Builder<String, MutableState<Int>> = ImmutableMap.builder()
        val localStrings: ImmutableMap.Builder<String, MutableState<String>> =
            ImmutableMap.builder()

        for (entry in LOCAL_CONFIGS) {
            when (val config = entry.value) {
                is LocalConfig.Boolean -> localBooleans.put(
                    entry.key,
                    mutableStateOf(config.default)
                )

                is LocalConfig.Long -> localLongs.put(
                    entry.key,
                    mutableStateOf(config.default)
                )

                is LocalConfig.Int -> localInts.put(
                    entry.key,
                    mutableStateOf(config.default)
                )

                is LocalConfig.String -> localStrings.put(
                    entry.key,
                    mutableStateOf(config.default)
                )
            }
        }

        booleanStates = localBooleans.build()
        longStates = localLongs.build()
        intStates = localInts.build()
        stringStates = localStrings.build()
    }

    init {
        val remoteConfigs =
            ImmutableMap.Builder<String, AtomicReference<FirebaseRemoteConfigValue?>>()

        for (entry in REMOTE_CONFIGS_DEFAULTS) {
            remoteConfigs.put(entry.key, AtomicReference<FirebaseRemoteConfigValue?>())
        }

        remoteConfigCache = remoteConfigs.build()
    }

    fun init(
        context: AbstractApplication,
        app: dagger.Lazy<FirebaseApp>,
    ) {
        initLocalConfig(context)

        ioLaunch {
            initRemoteConfig(app)
        }
    }

    private fun initLocalConfig(context: AbstractApplication) {
        MMKV.initialize(context)
        val mmkv = MMKV.mmkvWithID("local_config")
        for (entry in booleanStates) {
            val state = entry.value
            state.value = mmkv.getBoolean(entry.key, state.value)
        }
        for (entry in longStates) {
            val state = entry.value
            state.value = mmkv.getLong(entry.key, state.value)
        }
        for (entry in intStates) {
            val state = entry.value
            state.value = mmkv.getInt(entry.key, state.value)
        }
        for (entry in stringStates) {
            val state = entry.value
            state.value = mmkv.getString(entry.key, state.value)!!
        }
        localConfig = mmkv
    }

    private fun initRemoteConfig(app: dagger.Lazy<FirebaseApp>) {
        val config = FirebaseRemoteConfig.getInstance(app.get())
        remoteConfig = config

        config.activate().addOnCompleteListener {
            if (get(NO_CONFIG_CACHE) || BuildTypeSettings.DEBUG) {
                config.fetch(0).addOnCompleteListener { fetch ->
                    if (fetch.isSuccessful)
                        set(NO_CONFIG_CACHE, false)
                }
            } else {
                config.fetch()
            }

        }

        val allRemoteConfigDefaults = ImmutableMap.Builder<String, Any>()
        allRemoteConfigDefaults.putAll(REMOTE_CONFIGS_DEFAULTS)

        config.setDefaultsAsync(allRemoteConfigDefaults.build()).addOnCompleteListener {
            Teller.debug {
                "app config initialized with version: " + get(CONFIG_VERSION)
            }
        }
    }

    private fun getRemoteValue(key: String): FirebaseRemoteConfigValue? =
        getRemoteValue(key, remoteConfigCache[key]!!)

    private fun getRemoteValue(
        key: String,
        cacheRef: AtomicReference<FirebaseRemoteConfigValue?>,
    ) = cacheRef.get() ?: cacheRef.updateAndGet {
        remoteConfig?.let {
            it.getValue(key).let { value ->
                if (value.source == FirebaseRemoteConfig.VALUE_SOURCE_REMOTE)
                    return@updateAndGet value
            }
        }
        return@updateAndGet null
    }

    fun get(remoteString: RemoteConfig<String>): String =
        getRemoteValue(remoteString.key)?.asString() ?: remoteString.default

    fun get(remoteInt: RemoteConfig<Int>): Int =
        getRemoteValue(remoteInt.key)?.asLong()?.toInt() ?: remoteInt.default

    fun get(remoteLong: RemoteConfig<Long>): Long =
        getRemoteValue(remoteLong.key)?.asLong() ?: remoteLong.default

    fun get(remoteBoolean: RemoteConfig<Boolean>): Boolean =
        getRemoteValue(remoteBoolean.key)?.asBoolean() ?: remoteBoolean.default

    fun stringState(localString: LocalConfig<String>): State<String> =
        mutableStringState(localString)

    private fun mutableStringState(localString: LocalConfig<String>) =
        stringStates[localString.key]
            ?: throw IllegalArgumentException("String state not found: ${localString.key}")

    fun intState(localInt: LocalConfig<Int>): State<Int> = intMutableState(localInt)

    private fun intMutableState(localInt: LocalConfig<Int>) =
        intStates[localInt.key]
            ?: throw IllegalArgumentException("Int state not found: ${localInt.key}")

    fun longState(localLong: LocalConfig<Long>): State<Long> = longMutableState(localLong)

    private fun longMutableState(localLong: LocalConfig<Long>) =
        longStates[localLong.key]
            ?: throw IllegalArgumentException("Long state not found: ${localLong.key}")

    fun booleanState(localBoolean: LocalConfig<Boolean>): State<Boolean> =
        booleanMutableState(localBoolean)

    private fun booleanMutableState(localBoolean: LocalConfig<Boolean>) =
        booleanStates[localBoolean.key]
            ?: throw IllegalArgumentException("Boolean state not found: ${localBoolean.key}")

    fun set(localString: LocalConfig<String>, value: String): String {
        val newValue = try {
            localConfig.putString(localString.key, value)
            value
        } catch (e: Exception) { // not suspended
            Teller.warn("failed to set local string config: $localString to: $value", e)
            get(localString)
        }

        mainLaunch {
            stringStates[localString.key]?.value = newValue
        }
        return newValue
    }

    fun set(localInt: LocalConfig<Int>, value: Int): Int {
        val newValue = try {
            localConfig.putInt(localInt.key, value)
            value
        } catch (e: Exception) { // not suspended
            Teller.warn("failed to set local int config: $localInt to: $value", e)
            get(localInt)
        }

        mainLaunch {
            intStates[localInt.key]?.value = newValue
        }
        return newValue
    }

    fun set(localLong: LocalConfig<Long>, value: Long): Long {
        val newValue = try {
            localConfig.putLong(localLong.key, value)
            value
        } catch (e: Exception) { // not suspended
            Teller.warn("failed to set local long config: $localLong to: $value", e)
            get(localLong)
        }

        mainLaunch {
            longStates[localLong.key]?.value = newValue
        }
        return newValue
    }

    fun set(localBoolean: LocalConfig<Boolean>, value: Boolean): Boolean {
        val newValue = try {
            localConfig.putBoolean(localBoolean.key, value)
            value
        } catch (e: Exception) { // not suspended
            Teller.warn("failed to set local boolean config: $localBoolean to: $value", e)
            get(localBoolean)
        }

        mainLaunch {
            booleanStates[localBoolean.key]?.value = newValue
        }
        return newValue
    }

    fun get(localString: LocalConfig<String>, defaultString: String = localString.default): String {
        try {
            return localConfig.getString(localString.key, defaultString)!!
        } catch (e: Exception) { // not suspended
            Teller.warn("failed to load local string config: $localString", e)
        }
        return defaultString
    }

    fun get(localInt: LocalConfig<Int>, defaultInt: Int = localInt.default): Int {
        try {
            return localConfig.getInt(localInt.key, defaultInt)
        } catch (e: Exception) { // not suspended
            Teller.warn("failed to load local int config: $localInt", e)
        }
        return defaultInt
    }

    fun get(localLong: LocalConfig<Long>, defaultLong: Long = localLong.default): Long {
        try {
            return localConfig.getLong(localLong.key, defaultLong)
        } catch (e: Exception) { // not suspended
            Teller.warn("failed to load local long config: $localLong", e)
        }
        return defaultLong
    }

    fun get(
        localBoolean: LocalConfig<Boolean>,
        defaultBoolean: Boolean = localBoolean.default
    ): Boolean {
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

sealed class RemoteConfig<T>(override val key: kotlin.String, override val default: T) : Config<T> {
    override fun toString() = key
    final override fun hashCode() = key.hashCode()
    final override fun equals(other: Any?) =
        this === other || other is RemoteConfig<*> && this.key == other.key

    class Boolean(key: kotlin.String, default: kotlin.Boolean) :
        RemoteConfig<kotlin.Boolean>(key, default)

    class Long(key: kotlin.String, default: kotlin.Long) : RemoteConfig<kotlin.Long>(key, default)
    class Int(key: kotlin.String, default: kotlin.Int) : RemoteConfig<kotlin.Int>(key, default)
    class String(key: kotlin.String, default: kotlin.String) :
        RemoteConfig<kotlin.String>(key, default)
}

sealed class LocalConfig<T>(override val key: kotlin.String, override val default: T) : Config<T> {
    override fun toString() = key
    final override fun hashCode() = key.hashCode()
    final override fun equals(other: Any?) =
        this === other || other is LocalConfig<*> && this.key == other.key

    class Boolean(key: kotlin.String, default: kotlin.Boolean) :
        LocalConfig<kotlin.Boolean>(key, default)

    class Long(key: kotlin.String, default: kotlin.Long) : LocalConfig<kotlin.Long>(key, default)
    class Int(key: kotlin.String, default: kotlin.Int) : LocalConfig<kotlin.Int>(key, default)
    class String(key: kotlin.String, default: kotlin.String) :
        LocalConfig<kotlin.String>(key, default)
}

fun ImmutableMap.Builder<String, Any>.putRemoteConfigDefault(config: RemoteConfig<out Any>): ImmutableMap.Builder<String, Any> =
    put(config.key, config.default)

fun ImmutableMap.Builder<String, LocalConfig<*>>.putLocalConfig(config: LocalConfig<*>): ImmutableMap.Builder<String, LocalConfig<*>> =
    put(config.key, config)