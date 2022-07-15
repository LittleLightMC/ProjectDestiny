package pro.darc.projectm.dsl.command.parameter

import net.kyori.adventure.text.Component
import org.bukkit.*
import org.bukkit.entity.Player
import pro.darc.projectm.dsl.command.Executor
import pro.darc.projectm.dsl.command.TabCompleter
import pro.darc.projectm.dsl.command.fail
import pro.darc.projectm.extension.onlinePlayers
import pro.darc.projectm.extension.toComponent
import pro.darc.projectm.extension.withErrorColor
import pro.darc.projectm.extension.withPrefix
import java.util.*

// PLAYER

val PLAYER_MISSING_PARAMETER = "未输入玩家参数".toComponent().withErrorColor().withPrefix()
val PLAYER_NOT_ONLINE = "错误的玩家参数".toComponent().withErrorColor().withPrefix()

/**
 * returns a [Player] or null if the player is not online.
 */
fun Executor<*>.playerOrNull(
    index: Int,
    argMissing: Component = PLAYER_MISSING_PARAMETER
): Player? = string(index, argMissing).let { Bukkit.getPlayerExact(it) }

fun Executor<*>.player(
    index: Int,
    argMissing: Component = PLAYER_MISSING_PARAMETER,
    notOnline: Component = PLAYER_NOT_ONLINE
): Player = playerOrNull(index, argMissing) ?: fail(notOnline)

fun TabCompleter.player(
    index: Int
): List<String> = argumentCompleteBuilder(index) { arg ->
    onlinePlayers.mapNotNull {
        if(it.name.startsWith(arg, true)) it.name else null
    }
}

// OFFLINE PLAYER

fun Executor<*>.offlinePlayer(
    index: Int,
    argMissing: Component = PLAYER_MISSING_PARAMETER
): OfflinePlayer = string(index, argMissing).let {
    runCatching { UUID.fromString(it) }.getOrNull()?.let { Bukkit.getOfflinePlayer(it) }
        ?: Bukkit.getOfflinePlayer(it)
}

// GAMEMODE

val GAMEMODE_MISSING_PARAMETER = "缺少游戏模式参数".toComponent().withErrorColor().withPrefix()
val GAMEMODE_NOT_FOUND = "不存在的游戏模式".toComponent().withErrorColor().withPrefix()

/**
 * returns a [GameMode] or null if was not found.
 */
fun Executor<*>.gameModeOrNull(
    index: Int,
    argMissing: Component = GAMEMODE_MISSING_PARAMETER
): GameMode? = string(index, argMissing).run {
    toIntOrNull()?.let { GameMode.getByValue(it) } ?: runCatching { GameMode.valueOf(this.uppercase(Locale.getDefault())) }.getOrNull()
}

fun Executor<*>.gameMode(
    index: Int,
    argMissing: Component = GAMEMODE_MISSING_PARAMETER,
    notFound: Component = GAMEMODE_NOT_FOUND
): GameMode = gameModeOrNull(index, argMissing) ?: fail(notFound)

fun TabCompleter.gameMode(
    index: Int
): List<String> = argumentCompleteBuilder(index) { arg ->
    GameMode.values().mapNotNull {
        if(it.name.startsWith(arg, true)) it.name.lowercase(Locale.getDefault()) else null
    }
}