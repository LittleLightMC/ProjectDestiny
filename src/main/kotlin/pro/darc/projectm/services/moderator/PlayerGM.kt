package pro.darc.projectm.services.moderator

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.permissions.Permissible
import pro.darc.projectm.ProjectMCoreMain
import pro.darc.projectm.dsl.ProjectMListener
import pro.darc.projectm.dsl.command.command
import pro.darc.projectm.dsl.command.parameter.gameMode
import pro.darc.projectm.dsl.server
import pro.darc.projectm.extension.toComponent
import pro.darc.projectm.extension.withErrorColor
import pro.darc.projectm.extension.withPrefix

val Permissible.isStuff: Boolean
    get() = hasPermission("projectm.stuff") || isOp

class PlayerGM: Listener {

    // if game isn't started
    // player aren't allowed to perform some action
    var gameStarted = false
        private set

    init {
        server.pluginManager.registerEvents(this, ProjectMCoreMain.instance)

        command("gm", plugin = ProjectMCoreMain.instance) {
            permission = "projectm.stuff"
            permissionMessage("<underline>只有工作人员能用这个命令".toComponent().withErrorColor().withPrefix())

            command("gamemode") {
                executorPlayer {
                    val mode = gameMode(0)
                    this.sender.gameMode = mode
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun evtPlayerMove(event: PlayerMoveEvent) {
        if (event.player.isStuff) return

        if (gameStarted) event.isCancelled = true
    }
}