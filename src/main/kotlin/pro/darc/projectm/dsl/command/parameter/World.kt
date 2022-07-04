package pro.darc.projectm.dsl.command.parameter

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import pro.darc.projectm.dsl.command.Executor
import pro.darc.projectm.dsl.command.TabCompleter
import pro.darc.projectm.dsl.command.fail
import pro.darc.projectm.extension.toComponent
import pro.darc.projectm.extension.withErrorColor
import pro.darc.projectm.extension.withPrefix

// WORLD

val MISSING_WORLD_ARGUMENT = "<underline>未输入世界参数".toComponent().withErrorColor().withPrefix()
val WORLD_NOT_FOUND = "<underline>未找到世界".toComponent().withErrorColor().withPrefix()

/**
 * Returns [World] or null if was not found.
 */
fun Executor<*>.worldOrNull(
    index: Int,
    argMissing: Component = MISSING_WORLD_ARGUMENT
): World? = string(index, argMissing).let { Bukkit.getWorld(it) }

fun Executor<*>.world(
    index: Int,
    argMissing: Component = MISSING_WORLD_ARGUMENT,
    notFound: Component = WORLD_NOT_FOUND
): World = worldOrNull(index, argMissing) ?: fail(notFound)

fun TabCompleter.world(
    index: Int
): List<String> = argumentCompleteBuilder(index) { arg ->
    Bukkit.getWorlds().mapNotNull {
        if(it.name.startsWith(arg, true)) it.name else null
    }
}

// COORDINATE

val MISSING_COORDINATE_ARGUMENT = "<underline>未找到坐标参数".toComponent().withErrorColor().withPrefix()
val COORDINATE_NUMBER_FORMAT = "<underline>错误的坐标参数".toComponent().withErrorColor().withPrefix()

fun Executor<Player>.coordinate(
    xIndex: Int, yIndex: Int, zIndex: Int,
    argMissing: Component = MISSING_COORDINATE_ARGUMENT,
    numberFormat: Component = COORDINATE_NUMBER_FORMAT
): Location = coordinate(xIndex, yIndex, zIndex, sender.world, argMissing, numberFormat)

fun Executor<*>.coordinate(
    xIndex: Int, yIndex: Int, zIndex: Int, world: World,
    argMissing: Component = MISSING_COORDINATE_ARGUMENT,
    numberFormat: Component = COORDINATE_NUMBER_FORMAT
): Location {

    fun double(index: Int) = double(index, argMissing, numberFormat)

    return Location(world, double(xIndex), double(yIndex), double(zIndex))
}
