package pro.darc.projectm.extension

import com.okkero.skedule.BukkitDispatcher
import com.okkero.skedule.BukkitSchedulerController
import com.okkero.skedule.SynchronizationContext
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import pro.darc.projectm.utils.getTakeValuesOrNull
import pro.darc.projectm.utils.registerCoroutineContextTakes
import pro.darc.projectm.utils.unregisterCoroutineContextTakes
import kotlin.time.Duration

val BukkitSchedulerController.contextSync get() = SynchronizationContext.SYNC
val BukkitSchedulerController.contextAsync get() = SynchronizationContext.ASYNC

suspend fun BukkitSchedulerController.switchToSync() = switchContext(contextSync)
suspend fun BukkitSchedulerController.switchToAsync() = switchContext(contextAsync)

@JvmInline
value class PluginDispatcher(val plugin: JavaPlugin) {
    val ASYNC get() = BukkitDispatcher(plugin, true)
    val SYNC get() = BukkitDispatcher(plugin, false)
}

val Plugin.BukkitDispatchers get() = PluginDispatcher(this as JavaPlugin)

suspend fun BukkitSchedulerController.takeMaxPerTick(time: Duration) {
    when (val takeValues = getTakeValuesOrNull(context)) {
        null -> registerCoroutineContextTakes(context, time)
        else -> if (takeValues.wasTimeExceeded()) {
            unregisterCoroutineContextTakes(context)
            waitFor(1)
        }
    }
}
