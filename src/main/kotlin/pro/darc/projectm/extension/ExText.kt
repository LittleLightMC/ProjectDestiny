package pro.darc.projectm.extension

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.util.RGBLike

val prefix: Component
    get() = "『<color:#00FF7F>ProjectM</color>』 ".toComponent()

fun String.toComponent() = MiniMessage.miniMessage().deserialize(this)

fun String.withPrefix() = prefix.append(this.toComponent())

fun Component.withPrefix() = prefix.append(this)

fun Component.withColor(color: Int) = color(TextColor.color(color))

fun Component.withErrorColor() = withColor(0xFF6347)

fun Component.withSuccessColor() = withColor(0x7fffd4)
