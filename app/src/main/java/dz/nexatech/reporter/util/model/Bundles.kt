package dz.nexatech.reporter.util.model

import android.os.PersistableBundle
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.core.os.toPersistableBundle

val stringToStringSnapshotStateMapSaver = Saver<SnapshotStateMap<String, String>, PersistableBundle>(
    save = { map -> map.toPersistableBundle() },
    restore = { bundle -> bundle.toStringSnapshotStateMap { getString(it) } },
)

fun <T: Any> PersistableBundle.toStringSnapshotStateMap(loader: PersistableBundle.(String) -> T?): SnapshotStateMap<String, T> {
    val map = SnapshotStateMap<String, T>()
    for (key in this.keySet()) {
        val value = loader.invoke(this, key)
        if (value != null) {
            map[key] = value
        }
    }
    return map
}