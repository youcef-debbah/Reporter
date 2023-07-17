package dz.nexatech.reporter.util.model

import android.annotation.SuppressLint
import androidx.annotation.AnyThread
import androidx.compose.runtime.*
import com.alorma.compose.settings.storage.base.SettingValueState
import com.google.common.collect.ImmutableMap
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigValue
import com.tencent.mmkv.MMKV
import dagger.hilt.android.internal.ThreadUtil
import dz.nexatech.reporter.client.common.ioLaunch
import dz.nexatech.reporter.client.common.mainLaunch
import dz.nexatech.reporter.client.model.REPORTER_LOCAL_CONFIGS
import dz.nexatech.reporter.client.model.REPORTER_REMOTE_CONFIGS_DEFAULTS
import dz.nexatech.reporter.util.BuildTypeSettings
import dz.nexatech.reporter.util.ui.AbstractApplication
import java.util.concurrent.atomic.AtomicReference

private lateinit var localConfig: MMKV

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

    private val booleanStates: ImmutableMap<String, BooleanConfigState>
    private val longStates: ImmutableMap<String, LongConfigState>
    private val intStates: ImmutableMap<String, IntConfigState>
    private val stringStates: ImmutableMap<String, StringConfigState>

    private val remoteConfigCache: ImmutableMap<String, AtomicReference<FirebaseRemoteConfigValue?>>

    init {
        val localBooleans: ImmutableMap.Builder<String, BooleanConfigState> =
            ImmutableMap.builder()
        val localLongs: ImmutableMap.Builder<String, LongConfigState> =
            ImmutableMap.builder()
        val localInts: ImmutableMap.Builder<String, IntConfigState> =
            ImmutableMap.builder()
        val localStrings: ImmutableMap.Builder<String, StringConfigState> =
            ImmutableMap.builder()

        for (entry in LOCAL_CONFIGS) {
            when (val config = entry.value) {
                is LocalConfig.Boolean -> localBooleans.put(entry.key, BooleanConfigState(config))
                is LocalConfig.Long -> localLongs.put(entry.key, LongConfigState(config))
                is LocalConfig.Int -> localInts.put(entry.key, IntConfigState(config))
                is LocalConfig.String -> localStrings.put(entry.key, StringConfigState(config))
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
            state.updateInMemoryState(mmkv.decodeBool(entry.key, state.value))
        }
        for (entry in longStates) {
            val state = entry.value
            state.updateInMemoryState(mmkv.decodeLong(entry.key, state.value))
        }
        for (entry in intStates) {
            val state = entry.value
            state.updateInMemoryState(mmkv.decodeInt(entry.key, state.value))
        }
        for (entry in stringStates) {
            val state = entry.value
            state.updateInMemoryState(mmkv.decodeString(entry.key, state.value)!!)
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

    fun stringState(localString: LocalConfig<String>): StringConfigState =
        stringStates[localString.key]
            ?: throw IllegalArgumentException("String state not found: ${localString.key}")

    fun intState(localInt: LocalConfig<Int>): IntConfigState =
        intStates[localInt.key]
            ?: throw IllegalArgumentException("Int state not found: ${localInt.key}")

    fun longState(localLong: LocalConfig<Long>): LongConfigState =
        longStates[localLong.key]
            ?: throw IllegalArgumentException("Long state not found: ${localLong.key}")

    fun booleanState(localBoolean: LocalConfig<Boolean>): BooleanConfigState =
        booleanStates[localBoolean.key]
            ?: throw IllegalArgumentException("Boolean state not found: ${localBoolean.key}")

    fun set(localString: LocalConfig<String>, value: String): String =
        set(localString, value) {
            mainLaunch {
                stringStates[localString.key]?.updateInMemoryState(it)
            }
        }

    fun set(localInt: LocalConfig<Int>, value: Int): Int =
        set(localInt, value) {
            mainLaunch {
                intStates[localInt.key]?.updateInMemoryState(it)
            }
        }

    fun set(localLong: LocalConfig<Long>, value: Long): Long =
        set(localLong, value) {
            mainLaunch {
                longStates[localLong.key]?.updateInMemoryState(it)
            }
        }

    fun set(localBoolean: LocalConfig<Boolean>, value: Boolean): Boolean =
        set(localBoolean, value) {
            mainLaunch {
                booleanStates[localBoolean.key]?.updateInMemoryState(it)
            }
        }

    fun get(localString: LocalConfig<String>, defaultString: String = localString.default): String {
        try {
            return localConfig.decodeString(localString.key, defaultString)!!
        } catch (e: Exception) { // not suspended
            Teller.warn("failed to load local string config: $localString", e)
        }
        return defaultString
    }

    fun get(localInt: LocalConfig<Int>, defaultInt: Int = localInt.default): Int {
        try {
            return localConfig.decodeInt(localInt.key, defaultInt)
        } catch (e: Exception) { // not suspended
            Teller.warn("failed to load local int config: $localInt", e)
        }
        return defaultInt
    }

    fun get(localLong: LocalConfig<Long>, defaultLong: Long = localLong.default): Long {
        try {
            return localConfig.decodeLong(localLong.key, defaultLong)
        } catch (e: Exception) { // not suspended
            Teller.warn("failed to load local long config: $localLong", e)
        }
        return defaultLong
    }

    fun get(
        localBoolean: LocalConfig<Boolean>,
        defaultBoolean: Boolean = localBoolean.default,
    ): Boolean {
        try {
            return localConfig.decodeBool(localBoolean.key, defaultBoolean)
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


private fun set(
    localString: LocalConfig<String>,
    value: String,
    stateUpdater: (String) -> Unit,
): String {
    val newValue = try {
        localConfig.encode(localString.key, value)
        value
    } catch (e: Exception) { // not suspended
        Teller.warn("failed to set local string config: $localString to: $value", e)
        AppConfig.get(localString)
    }
    stateUpdater.invoke(newValue)
    return newValue
}

private fun set(
    localInt: LocalConfig<Int>,
    value: Int,
    stateUpdater: (Int) -> Unit,
): Int {
    val newValue = try {
        localConfig.encode(localInt.key, value)
        value
    } catch (e: Exception) { // not suspended
        Teller.warn("failed to set local int config: $localInt to: $value", e)
        AppConfig.get(localInt)
    }
    stateUpdater.invoke(newValue)
    return newValue
}

private fun set(
    localLong: LocalConfig<Long>,
    value: Long,
    stateUpdater: (Long) -> Unit,
): Long {
    val newValue = try {
        localConfig.encode(localLong.key, value)
        value
    } catch (e: Exception) { // not suspended
        Teller.warn("failed to set local long config: $localLong to: $value", e)
        AppConfig.get(localLong)
    }
    stateUpdater.invoke(newValue)
    return newValue
}

private fun set(
    localBoolean: LocalConfig<Boolean>,
    value: Boolean,
    stateUpdater: (Boolean) -> Unit,
): Boolean {
    val newValue = try {
        localConfig.encode(localBoolean.key, value)
        value
    } catch (e: Exception) { // not suspended
        Teller.warn("failed to set local boolean config: $localBoolean to: $value", e)
        AppConfig.get(localBoolean)
    }
    stateUpdater.invoke(newValue)
    return newValue
}

sealed class AbstractConfigState<T>(val config: LocalConfig<T>) : MutableState<T>,
    SettingValueState<T> {

    protected val state: MutableState<T> = mutableStateOf(config.default)

    val setter: (T) -> Unit = {
        ThreadUtil.ensureMainThread()
        setNewValue(it)
    }

    override var value: T
        get() {
            ThreadUtil.ensureMainThread()
            return state.value
        }
        set(newValue) {
            setter.invoke(newValue)
        }

    protected abstract fun setNewValue(newValue: T)

    fun updateInMemoryState(newValue: T) {
        state.value = newValue
    }

    override fun reset() {
        value = config.default
    }

    override fun component1(): T = value

    override fun component2(): (T) -> Unit = setter
}

class BooleanConfigState(config: LocalConfig<Boolean>) : AbstractConfigState<Boolean>(config) {
    override fun setNewValue(newValue: Boolean) {
        set(config, newValue) {
            updateInMemoryState(it)
        }
    }
}

class LongConfigState(config: LocalConfig<Long>) : AbstractConfigState<Long>(config) {
    override fun setNewValue(newValue: Long) {
        set(config, newValue) {
            updateInMemoryState(it)
        }
    }
}

class IntConfigState(config: LocalConfig<Int>) : AbstractConfigState<Int>(config) {
    override fun setNewValue(newValue: Int) {
        set(config, newValue) {
            updateInMemoryState(it)
        }
    }
}

class StringConfigState(config: LocalConfig<String>) : AbstractConfigState<String>(config) {
    override fun setNewValue(newValue: String) {
        set(config, newValue) {
            updateInMemoryState(it)
        }
    }
}