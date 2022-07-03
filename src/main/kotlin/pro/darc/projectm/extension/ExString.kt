package pro.darc.projectm.extension

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

fun String.asText(): Component = MiniMessage.miniMessage().deserialize(this)

fun List<String>.asText(): Component = reduce { acc, s -> "${acc}<newline>${s}" }.asText()
