package pro.darc.projectm.services

import pro.darc.projectm.services.social.SocialService
import kotlin.reflect.KClass


interface ICoreService {
    fun onLoad()
    fun onStart()
    fun onStop()
}

abstract class CoreService : ICoreService {
    override fun onLoad() {}
    override fun onStart() {}
    override fun onStop() {}
}

object ServiceManager {

    private val serviceClasses: MutableList<KClass<out ICoreService>> = mutableListOf()
    private val services: MutableMap<KClass<out ICoreService>, ICoreService> = mutableMapOf()

    init {
        // register global services here
        registerService(SocialService::class)
        // register end
    }

    private fun registerService(service: KClass<out ICoreService>) = serviceClasses.add(service)
    fun loadServices() {
        if (services.isNotEmpty()) {
            disableServices()
        }
        serviceClasses.forEach{ value ->
            run {
                services[value] = value.java.getConstructor().newInstance().apply { this.onLoad() }
            }
        }
    }
    fun enableServices() = services.forEach { (_, value) -> value.onStart() }
    fun disableServices() = services.forEach { (_, value) -> value.onStop() }

}
