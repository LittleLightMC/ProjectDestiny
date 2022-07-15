package pro.darc.projectm.services.moderator

import io.papermc.paper.event.player.AsyncChatEvent
import kotlinx.coroutines.flow.first
import org.bukkit.GameMode
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.permissions.Permissible
import pro.darc.projectm.ProjectMCoreMain
import pro.darc.projectm.dsl.command.command
import pro.darc.projectm.dsl.command.parameter.boolean
import pro.darc.projectm.dsl.command.parameter.gameMode
import pro.darc.projectm.dsl.command.parameter.playerOrNull
import pro.darc.projectm.dsl.flow.commandPlayerEventFlow
import pro.darc.projectm.dsl.server
import pro.darc.projectm.extension.*

val Permissible.isStuff: Boolean
    get() = hasPermission("projectm.stuff") || isOp

class PlayerGM: Listener {

    // if game isn't started
    // player aren't allowed to perform some action
    var gameStarted = false
        private set

    // should allow player damage each other?
    var allowPvP = false
        private set

    var teamService: TeamService = TeamService()

    init {
        server.pluginManager.registerEvents(this, ProjectMCoreMain.instance)
        server.pluginManager.registerEvents(teamService, ProjectMCoreMain.instance)

        command("gm", plugin = ProjectMCoreMain.instance) {
            permission = "projectm.stuff"
            permissionMessage("只有工作人员能用这个命令".toComponent().withErrorColor().withPrefix())

            command("spec") {
                description = "设置自己为旁观者模式"

                executorPlayer {
                    sender.gameMode = GameMode.SPECTATOR
                }
            }

            command("loadgroup") {
                permission = "projectm.operator"
                permissionMessage("只有服务器管理员有权限操作".toComponent().withErrorColor().withPrefix())

                executorPlayer {
                    sender.sendMessage("是否要重新导入组队表,请输入<是>来确认操作".toComponent().withPrefix())
                    val input = commandPlayerEventFlow<AsyncPlayerChatEvent>().first().message

                    if (input.contentEquals("是")) {
                        teamService.loadFromFile()
                        sender.sendMessage("导入分组成功".toComponent().withSuccessColor().withPrefix())
                    } else {
                        sender.sendMessage("已忽略操作".toComponent().withErrorColor().withPrefix())
                    }
                }
            }

            command("randomgroup") {
                permission = "projectm.operator"
                permissionMessage("只有服务器管理员有权限操作".toComponent().withErrorColor().withPrefix())

                executor {
                    if (!gameStarted && teamService.needInit) {
                        teamService.makeTeam()
                        sender.sendMessage("已随机分组".toComponent().withSuccessColor().withPrefix())
                    } else {
                        sender.sendMessage("游戏已开始或组队已初始化".toComponent().withErrorColor().withPrefix())
                    }
                }
            }

            command("startgame") {
                permission = "projectm.operator"
                permissionMessage("只有服务器管理员有权限操作".toComponent().withErrorColor().withPrefix())

                executor {
                    gameStarted = true
                    sender.sendMessage("游戏已开始".toComponent().withColor(0x7fffd4).withPrefix())
                }
            }

            command("stopgame") {
                permission = "projectm.operator"
                permissionMessage("只有服务器管理员有权限操作".toComponent().withErrorColor().withPrefix())

                executor {
                    if (gameStarted) {
                        gameStarted = false
                    }
                    sender.sendMessage("游戏已停止".toComponent().withColor(0x7fffd4).withPrefix())
                }
            }

            command("setpvp") {
                permission = "projectm.operator"
                permissionMessage("只有服务器管理员有权限操作".toComponent().withErrorColor().withPrefix())

                executor {
                    val flag = boolean(0)
                    allowPvP = flag

                    sender.sendMessage("PvP已被设置为${flag}".toComponent().withSuccessColor().withPrefix())
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun evtPlayerMove(event: PlayerMoveEvent) {
        if (event.player.isStuff) return

        if (!gameStarted) {
            event.isCancelled = true
            event.player.sendMessage("游戏已暂停, 请耐心等待".toComponent().withErrorColor().withPrefix())
            return
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun evtEntityDamage(event: EntityDamageEvent) {
        if (!gameStarted) {
            event.isCancelled = true
            return
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun evtEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        if (event.entityType == EntityType.PLAYER) {
            if (!allowPvP) {
                event.isCancelled = true
                if (event.damager is Player) {
                    (event.damager as Player).sendMessage("PvP禁用中".toComponent().withErrorColor().withPrefix())
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun evtPlayerJoin(event: PlayerJoinEvent) {
        if (event.player.isStuff) event.player.gameMode = GameMode.SPECTATOR
    }
}
