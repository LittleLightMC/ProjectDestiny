package pro.darc.projectm.services.moderator

import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.permissions.Permissible
import pro.darc.projectm.ProjectMCoreMain
import pro.darc.projectm.dsl.command.command
import pro.darc.projectm.dsl.command.parameter.gameMode
import pro.darc.projectm.dsl.command.parameter.playerOrNull
import pro.darc.projectm.dsl.server
import pro.darc.projectm.extension.toComponent
import pro.darc.projectm.extension.withColor
import pro.darc.projectm.extension.withErrorColor
import pro.darc.projectm.extension.withPrefix

val Permissible.isStuff: Boolean
    get() = hasPermission("projectm.stuff") || isOp

class PlayerGM: Listener {

    // if game isn't started
    // player aren't allowed to perform some action
    var gameStarted = false
        private set

    var teamService: TeamService = TeamService()

    init {
        server.pluginManager.registerEvents(this, ProjectMCoreMain.instance)

        command("gm", plugin = ProjectMCoreMain.instance) {
            permission = "projectm.stuff"
            permissionMessage("只有工作人员能用这个命令".toComponent().withErrorColor().withPrefix())

            command("gamemode") {
                executorPlayer {
                    val mode = gameMode(0)
                    val target = playerOrNull(1)
                    this.sender.gameMode = mode
                }
            }

            command("startgame") {
                executor {
                    if (!gameStarted) {
                        if (teamService.needInit) {
                            teamService.makeTeam()
                        }
                        gameStarted = true
                    }
                    sender.sendMessage("游戏已开始".toComponent().withColor(0x7fffd4).withPrefix())
                }
            }

            command("stopgame") {
                executor {
                    if (gameStarted) {
                        gameStarted = false
                    }
                    sender.sendMessage("游戏已停止".toComponent().withColor(0x7fffd4).withPrefix())
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun evtPlayerMove(event: PlayerMoveEvent) {
        if (event.player.isStuff) return

        if (!gameStarted) event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun evtEntityDamage(event: EntityDamageEvent) {
        if (!gameStarted) event.isCancelled = true
    }
}
