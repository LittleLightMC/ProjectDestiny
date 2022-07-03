package pro.darc.projectm.extension

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.SimpleCommandMap
import org.bukkit.plugin.Plugin
import java.lang.reflect.Field
import java.util.concurrent.ConcurrentHashMap

private val serverCommands: SimpleCommandMap by lazy {
    val server = Bukkit.getServer()
    server::class.java.getDeclaredField("commandMap").apply {
        isAccessible = true
    }.get(server) as SimpleCommandMap
}

private val knownCommandsField: Field by lazy {
    SimpleCommandMap::class.java.getDeclaredField("knownCommands").apply {
        isAccessible = true
    }
}

/* Command register by this extension */
private val commandDelegated: MutableMap<String, MutableList<Command>> = ConcurrentHashMap()

fun Command.register(plugin: Plugin) {
    serverCommands.register(plugin.name, this)
    val commands = commandDelegated[plugin.name] ?: mutableListOf()
    commands.add(this)
    commandDelegated[plugin.name] = commands
}

fun Command.unregister() {
    try {
        val knownCommands = knownCommandsField.get(serverCommands) as MutableMap<String, Command>
        val toRemove = ArrayList<String>()
        for ((key, value) in knownCommands) {
            if (value === this) {
                toRemove.add(key)
            }
        }
        for (str in toRemove) {
            knownCommands.remove(str)
        }
        commandDelegated.values.forEach {
            it.removeIf { this === it }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
