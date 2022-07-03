package pro.darc.projectm.extension

import org.bukkit.Bukkit
import org.bukkit.event.*
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.plugin.Plugin
import pro.darc.projectm.ProjectMCoreMain
import pro.darc.projectm.dsl.ProjectMCore
import kotlin.reflect.KClass

fun <T : Event> KListener.event(
    type: KClass<T>,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    block: T.() -> Unit
) = event(ProjectMCoreMain.instance, type, priority, ignoreCancelled, block)

inline fun <reified T : Event> Listener.event(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    noinline block: T.() -> Unit
) {
    event(ProjectMCoreMain.instance, T::class, priority, ignoreCancelled, block)
}

fun <T : Event> Listener.event(
    plugin: Plugin,
    type: KClass<T>,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    block: T.() -> Unit
) {
    Bukkit.getServer().pluginManager.registerEvent(
        type.java,
        this,
        priority,
        { _, event ->
            if(type.isInstance(event))
                (event as? T)?.block()
        },
        plugin,
        ignoreCancelled
    )
}

inline fun Plugin.events(block: KListener.() -> Unit) = SimpleKListener().apply(block)

fun Listener.registerEvents(plugin: Plugin)
        = plugin.server.pluginManager.registerEvents(this, plugin)

fun Listener.unregisterListener() = HandlerList.unregisterAll(this)

fun Event.callEvent() = Bukkit.getServer().pluginManager.callEvent(this)

val PlayerMoveEvent.displaced: Boolean
    get() = this.from.x != this.to.x || this.from.y != this.to.y || this.from.z != this.to.z

interface KListener: Listener
class SimpleKListener : KListener, ProjectMCore()
