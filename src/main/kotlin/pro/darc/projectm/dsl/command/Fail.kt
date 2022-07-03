package pro.darc.projectm.dsl.command

import net.kyori.adventure.text.Component
import pro.darc.projectm.extension.asText

typealias ErrorHandler = Executor<*>.(Throwable) -> Unit

val defaultErrorHandler: ErrorHandler = {
    it.printStackTrace()
}

class CommandFailException(
    val senderMessage: Component? = null,
    val argMissing: Boolean = false,
    inline val execute: suspend () -> Unit = {},
): RuntimeException()

fun Executor<*>.fail(
    senderMessage: Component? = null,
    execute: suspend () -> Unit = {},
): Nothing = throw CommandFailException(senderMessage, execute = execute)

fun Executor<*>.fail(
    senderMessage: String = "",
    execute: suspend () -> Unit = {},
): Nothing = fail(senderMessage.takeIf { it.isNotEmpty() }?.asText(), execute = execute)


