@file:OptIn(ExperimentalTime::class)

package pro.darc.projectm.utils

import kotlin.time.*
import kotlin.time.Duration.Companion.milliseconds

fun now(): Long = System.currentTimeMillis()
fun nowNano(): Long = System.nanoTime()

val Long.ticks: Duration get() = toDouble().ticks
val Int.ticks: Duration get() = toDouble().ticks
val Double.ticks: Duration get() = tickToMilliseconds(this).milliseconds

val Duration.inTicks: Double get() = millisecondsToTick(toDouble(DurationUnit.MILLISECONDS))

fun Duration.toLongTicks(): Long = inTicks.toLong()

private fun tickToMilliseconds(value: Double): Double = value * 50.0
private fun millisecondsToTick(value: Double): Double = value / 50.0
