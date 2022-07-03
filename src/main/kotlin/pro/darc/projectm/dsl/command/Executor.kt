package pro.darc.projectm.dsl.command

import kotlinx.coroutines.CoroutineScope
import org.bukkit.command.CommandSender

class Executor<E: CommandSender> (
    val sender: E,
    val label: String,
    val args: Array<out String>,
    val command: CommandDSL,
    val scope: CoroutineScope,
)
