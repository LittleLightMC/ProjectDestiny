package pro.darc.projectm.dsl.command.parameter

import net.kyori.adventure.text.Component
import pro.darc.projectm.dsl.command.CommandFailException
import pro.darc.projectm.dsl.command.Executor
import pro.darc.projectm.dsl.command.TabCompleter
import pro.darc.projectm.dsl.command.fail
import pro.darc.projectm.extension.*

val MISSING_STRING_PARAMETER = "<underlined>缺少字符串参数".toComponent().withErrorColor().withPrefix()

fun Executor<*>.string(
    index: Int,
    argMissing: Component = MISSING_STRING_PARAMETER
): String = args.getOrNull(index) ?: throw CommandFailException(argMissing, true)

val TEXT_STRING_PARAMETER = "<underlined>缺少字符串参数".toComponent().withErrorColor().withPrefix()

fun Executor<*>.text(
    startIndex: Int,
    endIndex: Int = args.size,
    separator: String = " ",
    argMissing: Component = TEXT_STRING_PARAMETER
): String {
    if(startIndex >= args.size) fail(argMissing)
    return array(startIndex, endIndex) { string(it) }.joinToString(separator)
}

// BOOLEAN

val MISSING_BOOLEAN_PARAMETER = "<underlined>缺少布尔参数".toComponent().withErrorColor().withPrefix()
val BOOLEAN_FORMAT = "<underlined>错误的布尔参数".toComponent().withErrorColor().withPrefix()

/**
 * Returns [Boolean] or null if was not able to parse to Boolean.
 */
fun Executor<*>.booleanOrNull(
    index: Int,
    argMissing: Component = MISSING_BOOLEAN_PARAMETER,
    trueCases: Array<String> = TRUE_CASES,
    falseCases: Array<String> = FALSE_CASES
): Boolean? = string(index, argMissing).toBooleanOrNull(trueCases, falseCases)

fun Executor<*>.boolean(
    index: Int,
    argMissing: Component = MISSING_BOOLEAN_PARAMETER,
    booleanFormat: Component = BOOLEAN_FORMAT,
    trueCases: Array<String> = TRUE_CASES,
    falseCases: Array<String> = FALSE_CASES
): Boolean = booleanOrNull(index, argMissing, trueCases, falseCases) ?: fail(booleanFormat)

fun TabCompleter.boolean(
    index: Int,
    trueCases: Array<String> = TRUE_CASES,
    falseCases: Array<String> = FALSE_CASES
): List<String> = argumentCompleteBuilder(index) { arg ->
    listOf(*trueCases, *falseCases).filter { it.startsWith(arg, true) }
}

val MISSING_NUMBER_PARAMETER = "<underlined>缺少数字参数".toComponent().withErrorColor().withPrefix()
val NUMBER_FORMAT = "<underlined>错误的数字参数".toComponent().withErrorColor().withPrefix()

// INT

/**
 * Returns [Int] or null if was not able to parse to Int.
 */
fun Executor<*>.intOrNull(
    index: Int,
    argMissing: Component = MISSING_NUMBER_PARAMETER
): Int? = string(index, argMissing).toIntOrNull()

fun Executor<*>.int(
    index: Int,
    argMissing: Component = MISSING_NUMBER_PARAMETER,
    numberFormat: Component = NUMBER_FORMAT
): Int = intOrNull(index, argMissing) ?: fail(numberFormat)

// DOUBLE

/**
 * Returns [Double] or null if was not able to parse to Double.
 */
fun Executor<*>.doubleOrNull(
    index: Int,
    argMissing: Component = MISSING_NUMBER_PARAMETER
): Double? = string(index, argMissing).toDoubleOrNull()?.takeIf { it.isFinite() }

fun Executor<*>.double(
    index: Int,
    argMissing: Component = MISSING_NUMBER_PARAMETER,
    numberFormat: Component = NUMBER_FORMAT
): Double = doubleOrNull(index, argMissing) ?: fail(numberFormat)
