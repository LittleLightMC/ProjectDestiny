package pro.darc.projectm.extension

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

fun String.asText(): Component = MiniMessage.miniMessage().deserialize(this)

fun List<String>.asText(): Component = reduce { acc, s -> "${acc}<newline>${s}" }.asText()

private val unicodeRegex = "((\\\\u)([0-9]{4}))".toRegex()

fun String.javaUnicodeToCharacter(): String = unicodeRegex.replace(this) {
    String(charArrayOf(it.destructured.component3().toInt(16).toChar()))
}

fun <T> T.print(): T = also { println(it) }

fun String.centralize(
    length: Int,
    spacer: String = " ",
    prefix: String = "",
    suffix: String = ""
): String {
    if (this.length >= length) return this
    val part = prefix + spacer.repeat((length - this.length) / 2) + suffix
    return part + this + part
}

val TRUE_CASES = arrayOf("true", "正确", "是", "yes")
    get() = field.clone()
val FALSE_CASES = arrayOf("false", "错误", "否", "no")
    get() = field.clone()

fun String.toBooleanOrNull(
    trueCases: Array<String> = TRUE_CASES,
    falseCases: Array<String> = FALSE_CASES
): Boolean? = when {
    trueCases.any { it.equals(this, true) } -> true
    falseCases.any { it.equals(this, true) } -> false
    else -> null
}
