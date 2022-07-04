package pro.darc.projectm.dsl.command.parameter

import net.kyori.adventure.text.Component
import pro.darc.projectm.dsl.command.Executor
import pro.darc.projectm.dsl.command.fail
import pro.darc.projectm.extension.getIgnoreCase
import pro.darc.projectm.extension.toComponent
import pro.darc.projectm.extension.withErrorColor
import pro.darc.projectm.extension.withPrefix

val MISSING_ENUM_PARAMETER = "<underline>错误的选项".toComponent().withErrorColor().withPrefix()
val ENUM_VALUE_NOT_FOUND = "<underline>填写了不存在的选项".toComponent().withErrorColor().withPrefix()

/**
 * Returns [T] or null if was not able to find in the [Enum].
 */
inline fun <reified T : Enum<T>> Executor<*>.enumOrNull(
    index: Int,
    argMissing: Component = MISSING_ENUM_PARAMETER,
    additionalNames: Map<String, T> = mapOf()
): T? {
    val name = string(index, argMissing)
    return enumValues<T>().find { it.name.equals(name, true) }
        ?: additionalNames.getIgnoreCase(name)
}

inline fun <reified T : Enum<T>> Executor<*>.enum(
    index: Int,
    argMissing: Component = MISSING_ENUM_PARAMETER,
    notFound: Component = ENUM_VALUE_NOT_FOUND,
    additionalNames: Map<String, T> = mapOf()
): T = enumOrNull(index, argMissing, additionalNames) ?: fail(notFound)
