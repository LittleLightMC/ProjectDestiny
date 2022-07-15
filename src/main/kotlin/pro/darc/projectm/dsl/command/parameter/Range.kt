package pro.darc.projectm.dsl.command.parameter

import net.kyori.adventure.text.Component
import pro.darc.projectm.dsl.command.Executor
import pro.darc.projectm.dsl.command.fail
import pro.darc.projectm.extension.toComponent
import pro.darc.projectm.extension.withErrorColor
import pro.darc.projectm.extension.withPrefix

val MISSING_RANGE_PARAMETER = "未找到范围参数".toComponent().withErrorColor().withPrefix()
val INT_RANGE_FORMAT = "范围参数错误".toComponent().withErrorColor().withPrefix()

/**
 * Returns [IntRange] or null if was not able to parse to IntRange given the [separator].
 */
fun Executor<*>.intRangeOrNull(
    index: Int,
    argMissing: Component = MISSING_RANGE_PARAMETER,
    separator: String = ".."
): IntRange? {
    val slices = string(index, argMissing).split(separator)
    val min = slices.getOrNull(0)?.toIntOrNull()
    val max = slices.getOrNull(1)?.toIntOrNull()

    return max?.let { min?.rangeTo(it) }
}

fun Executor<*>.intRange(
    index: Int,
    argMissing: Component = MISSING_RANGE_PARAMETER,
    rangeFormat: Component = INT_RANGE_FORMAT,
    separator: String = ".."
): IntRange = intRangeOrNull(index, argMissing, separator)
    ?: fail(rangeFormat)