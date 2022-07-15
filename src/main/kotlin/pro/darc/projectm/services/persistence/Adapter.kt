package pro.darc.projectm.services.persistence

interface Adapter {

    fun getKeys()

    fun <T> get(key: String): T
    fun <T> set(key: String, value: T)

}

fun <T> String.setFlagValue(adapter: Adapter, value: T) = adapter.set(this, value)
