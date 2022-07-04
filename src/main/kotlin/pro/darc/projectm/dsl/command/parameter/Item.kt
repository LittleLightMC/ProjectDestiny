package pro.darc.projectm.dsl.command.parameter

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.block.data.BlockData
import org.bukkit.material.MaterialData
import pro.darc.projectm.dsl.command.Executor
import pro.darc.projectm.dsl.command.TabCompleter
import pro.darc.projectm.dsl.command.fail
import pro.darc.projectm.extension.*
import java.util.*

// MATERIAL

val MATERIAL_NOT_FOUND = "<underline>未找到该物品".toComponent().withErrorColor().withPrefix()
val MATERIAL_MISSING_PARAMETER = "<underline>未输入物品参数".toComponent().withErrorColor().withPrefix()

private fun toMaterial(string: String) = Material.getMaterial(string.uppercase(Locale.getDefault()))

/**
 * Returns [Material] or null if the Material was not found.
 */
fun Executor<*>.materialOrNull(
    index: Int,
    argMissing: Component = MATERIAL_MISSING_PARAMETER
): Material? = string(index, argMissing).run {
    toMaterial(this)
}

fun Executor<*>.material(
    index: Int,
    argMissing: Component = MATERIAL_MISSING_PARAMETER,
    notFound: Component = MATERIAL_NOT_FOUND
): Material = materialOrNull(index, argMissing) ?: fail(notFound)

fun TabCompleter.material(
    index: Int
): List<String> = argumentCompleteBuilder(index) { arg ->
    Material.values().mapNotNull {
        if(it.name.startsWith(arg, true)) it.name.lowercase(Locale.getDefault()) else null
    }
}

// MATERIAL DATA

val DATA_FORMAT = "<underline>物品数据必须为数字".toComponent().withErrorColor().withPrefix()

fun Executor<*>.blockDataOrNull(
    index: Int,
    argMissing: Component = MATERIAL_MISSING_PARAMETER,
): BlockData? = string(index, argMissing).run {
    val sliced = this.split(':')
    sliced.getOrNull(1)?.run {
        (toMaterial(sliced[0]))
            ?.asBlockData(this)
    } ?: materialOrNull(index, argMissing)?.asBlockData()
}

fun Executor<*>.blockData(
    index: Int,
    argMissing: Component = MATERIAL_MISSING_PARAMETER,
    notFound: Component = MATERIAL_NOT_FOUND,
    dataFormat: Component = DATA_FORMAT,
): BlockData = blockDataOrNull(index, argMissing) ?: fail(notFound)

/**
 * Returns [MaterialData] or null if the Material was not found.
 */
fun Executor<*>.materialDataOrNull(
    index: Int,
    argMissing: Component = MATERIAL_MISSING_PARAMETER,
    dataFormat: Component = DATA_FORMAT
): MaterialData? = string(index, argMissing).run {
    val sliced = this.split(":")
    sliced.getOrNull(1)?.run {
        (toMaterial(sliced[0]))
            ?.asMaterialData(toIntOrNull()?.toByte() ?: fail(dataFormat))
    } ?: materialOrNull(index, argMissing)?.asMaterialData()
}

fun Executor<*>.materialData(
    index: Int,
    argMissing: Component = MATERIAL_MISSING_PARAMETER,
    notFound: Component = MATERIAL_NOT_FOUND,
    dataFormat: Component = DATA_FORMAT
): MaterialData = materialDataOrNull(index, argMissing, dataFormat) ?: fail(notFound)
