package pro.darc.projectm.dsl

import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import pro.darc.projectm.ProjectMCoreMain
import java.util.logging.Level

open class ServerDSL

val server: Server
    get() = Bukkit.getServer()

object Log: Plugin by ProjectMCoreMain.instance {
    // currying
    private val logger = { level: Level ->
        { msg: String -> getLogger().log(level, msg) }
    }
    val info = logger(Level.INFO)
    val warning = logger(Level.WARNING)
    val serve = logger(Level.SEVERE)
    val debug = logger(Level.FINE)
    val fine = logger(Level.FINE)
}

// notice the lifecycle of Plugin instance
open class ProjectMCore: Plugin by ProjectMCoreMain.instance
open class ProjectMListener: Listener, ProjectMCore()
