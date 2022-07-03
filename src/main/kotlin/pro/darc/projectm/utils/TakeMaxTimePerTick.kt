package pro.darc.projectm.utils

import org.bukkit.plugin.Plugin
import pro.darc.projectm.extension.task
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.time.Duration

internal val coroutineContextTakes = ConcurrentHashMap<CoroutineContext, TakeValues>()
internal data class TakeValues(val startTimeMilliseconds: Long, val takeTimeMillisecond: Long) {
    fun wasTimeExceeded() = System.currentTimeMillis() - startTimeMilliseconds - takeTimeMillisecond >= 0
}

internal fun getTakeValuesOrNull(
    coroutineContext: CoroutineContext,
): TakeValues? = coroutineContextTakes[coroutineContext]

internal fun registerCoroutineContextTakes(
    coroutineContext: CoroutineContext,
    time: Duration,
) {
    coroutineContextTakes[coroutineContext] = TakeValues(System.currentTimeMillis(), time.inWholeMilliseconds)
}

internal fun unregisterCoroutineContextTakes(
    coroutineContext: CoroutineContext,
) {
    coroutineContextTakes.remove(coroutineContext)
}

/* Limit the time cost */
suspend fun Plugin.takeMaxPerTick(time: Duration) {
    val takeValues = getTakeValuesOrNull(coroutineContext)

    if (takeValues == null) {
        registerCoroutineContextTakes(coroutineContext, time)
    } else {
        if (takeValues.wasTimeExceeded()) {
            unregisterCoroutineContextTakes(coroutineContext)
            suspendCoroutine { continuation ->
                task(1) {
                    continuation.resume(Unit)
                }
            }
        }
    }
}
