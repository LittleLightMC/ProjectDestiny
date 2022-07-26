package pro.darc.projectm.utils

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.util.Vector
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method


/**
 * Small util to load and save structures<br></br>
 * Currently tested with 1.10 and 1.11
 */
object StructureUtil {
    lateinit var blockPositionClass: Class<*>
        private set
    var blockPositionConstructor: Constructor<*>? = null
        private set
    lateinit var worldServerClass: Class<*>
        private set
    lateinit var craftWorldClass: Class<*>
        private set
    var getHandleMethod: Method? = null
        private set
    var minecraftServerClass: Class<*>? = null
        private set
    var getMinecraftServerMethod: Method? = null
        private set
    lateinit var definedStructureManagerClass: Class<*>
        private set
    var getStructureManagerMethod: Method? = null
        private set
    lateinit var definedStructureClass: Class<*>
        private set
    lateinit var minecraftKeyClass: Class<*>
        private set
    var minecraftKeyConstructor: Constructor<*>? = null
        private set
    var getStructureMethod: Method? = null
        private set
    lateinit var blocksClass: Class<*>
        private set
    var blockClass: Class<*>? = null
        private set
    var worldClass: Class<*>? = null
        private set
    var structureVoidBlock: Any? = null
        private set
    var setPosMethod: Method? = null
        private set
    var setAuthorMethod: Method? = null
        private set
    var saveMethod: Method? = null
        private set
    var loadInfoMethod: Method? = null
        private set
    lateinit var definedStructureInfoClass: Class<*>
        private set
    var definedStructureInfoConstructor: Constructor<*>? = null
        private set
    lateinit var enumBlockMirrorClass: Class<*>
        private set
    var enumBlockMirrorValueOfMethod: Method? = null
        private set
    var mirrorMethod: Method? = null
        private set
    lateinit var enumBlockRotationClass: Class<*>
        private set
    var enumBlockRotationValueOfMethod: Method? = null
        private set
    var rotationMethod: Method? = null
        private set
    var ignoreEntitiesMethod: Method? = null
        private set
    var loadMethod: Method? = null
        private set

    init {
        try {
            var name: String
            blockPositionClass = Class.forName("net.minecraft.server.$version.BlockPosition")
            blockPositionConstructor = blockPositionClass.getConstructor(
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType
            )
            worldServerClass = Class.forName("net.minecraft.server.$version.WorldServer")
            craftWorldClass = Class.forName("org.bukkit.craftbukkit.$version.CraftWorld")
            getHandleMethod = craftWorldClass.getMethod("getHandle")
            minecraftServerClass = Class.forName("net.minecraft.server.$version.MinecraftServer")
            getMinecraftServerMethod = worldServerClass.getMethod("getMinecraftServer")
            definedStructureManagerClass = Class.forName("net.minecraft.server.$version.DefinedStructureManager")
            getStructureManagerMethod = worldServerClass.getMethod("y") //PAIL: rename getWorldServer
            definedStructureClass = Class.forName("net.minecraft.server.$version.DefinedStructure")
            minecraftKeyClass = Class.forName("net.minecraft.server.$version.MinecraftKey")
            minecraftKeyConstructor = minecraftKeyClass.getConstructor(String::class.java)
            getStructureMethod = definedStructureManagerClass.getMethod(
                "a",
                minecraftServerClass,
                minecraftKeyClass
            ) // PAIL: rename getStructure
            blocksClass = Class.forName("net.minecraft.server.$version.Blocks")
            blockClass = Class.forName("net.minecraft.server.$version.Block")
            structureVoidBlock =
                blocksClass.getField("dj")[null] // PAIL: rename STRUCTURE_VOID
            worldClass = Class.forName("net.minecraft.server.$version.World")
            setPosMethod = definedStructureClass.getMethod(
                "a", worldClass, blockPositionClass, blockPositionClass,
                Boolean::class.javaPrimitiveType, blockClass
            ) // PAIL: rename setPos
            setAuthorMethod = definedStructureClass.getMethod("a", String::class.java) // PAIL: rename setAuthor
            // PAIL: rename save, 1.10: d, 1.11 c
            name = "d"
            if (version.startsWith("v1_11")) {
                name = "c"
            }
            saveMethod = definedStructureManagerClass.getMethod(name, minecraftServerClass, minecraftKeyClass)
            loadInfoMethod = definedStructureManagerClass.getMethod(
                "b",
                minecraftServerClass,
                minecraftKeyClass
            ) // PAIL: rename loadInfo
            definedStructureInfoClass = Class.forName("net.minecraft.server.$version.DefinedStructureInfo")
            definedStructureInfoConstructor = definedStructureInfoClass.getConstructor()
            enumBlockMirrorClass = Class.forName("net.minecraft.server.$version.EnumBlockMirror")
            enumBlockMirrorValueOfMethod = enumBlockMirrorClass.getMethod("valueOf", String::class.java)
            mirrorMethod = definedStructureInfoClass.getMethod("a", enumBlockMirrorClass) // PAIL: rename mirror
            enumBlockRotationClass = Class.forName("net.minecraft.server.$version.EnumBlockRotation")
            enumBlockRotationValueOfMethod = enumBlockRotationClass.getMethod(
                "valueOf",
                String::class.java
            )
            rotationMethod = definedStructureInfoClass.getMethod("a", enumBlockRotationClass) // PAIL: rename rotation
            ignoreEntitiesMethod = definedStructureInfoClass.getMethod(
                "a",
                Boolean::class.javaPrimitiveType
            ) // PAIL: rename ignoreEntities
            loadMethod = definedStructureClass.getMethod(
                "a",
                worldClass,
                blockPositionClass,
                definedStructureInfoClass
            ) // PAIL: rename load
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    /**
     * Saves a structure
     *
     * @param start           the start location
     * @param size            the size
     * @param name            the name
     * @param author          the author
     * @param includeEntities if entities should be included in the structure
     * @return if is was successful
     */
    fun save(start: Location, size: Vector, name: String?, author: String?, includeEntities: Boolean): Boolean {
        try {
            val startPos = blockPositionConstructor!!.newInstance(start.blockX, start.blockY, start.blockZ)
            val sizePos = blockPositionConstructor!!.newInstance(size.blockX, size.blockY, size.blockZ)
            val world = getHandleMethod!!.invoke(craftWorldClass.cast(start.world))
            val server = getMinecraftServerMethod!!.invoke(world)
            val structureManager = getStructureManagerMethod!!.invoke(world)
            val key = minecraftKeyConstructor!!.newInstance(name)
            val structure = getStructureMethod!!.invoke(structureManager, server, key)
            setPosMethod!!.invoke(structure, world, startPos, sizePos, includeEntities, structureVoidBlock)
            setAuthorMethod!!.invoke(structure, author)
            return saveMethod!!.invoke(structureManager, server, key) as Boolean
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * Loads a structure
     *
     * @param origin          the origin location
     * @param name            the name
     * @param mirror          how the structure should be mirrored (FRONT_BACK, LEFT_RIGHT or NONE)
     * @param rotate          how the structure should be rotated (CLOCKWISE_90, CLOCKWISE_180,
     * COUNTERCLOCKWISE_90 or NONE)
     * @param includeEntities if entities should be included
     * @return if is was successful
     */
    fun load(origin: Location, name: String?, mirror: String?, rotate: String?, includeEntities: Boolean): Boolean {
        try {
            val originPos = blockPositionConstructor!!.newInstance(origin.blockX, origin.blockY, origin.blockZ)
            val world = getHandleMethod!!.invoke(craftWorldClass.cast(origin.world))
            val server = getMinecraftServerMethod!!.invoke(world)
            val structureManager = getStructureManagerMethod!!.invoke(world)
            val key = minecraftKeyConstructor!!.newInstance(name)
            val structure = loadInfoMethod!!.invoke(structureManager, server, key)
            return if (structure == null) {
                false
            } else {
                val structureInfo = definedStructureInfoConstructor!!.newInstance()
                mirrorMethod!!.invoke(structureInfo, enumBlockMirrorValueOfMethod!!.invoke(null, mirror))
                rotationMethod!!.invoke(structureInfo, enumBlockRotationValueOfMethod!!.invoke(null, rotate))
                ignoreEntitiesMethod!!.invoke(structureInfo, includeEntities)
                loadMethod!!.invoke(structure, world, originPos, structureInfo)
                true
            }
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
        return false
    }
}

val version by lazy {
    Bukkit.getServer().javaClass.getPackage().name.replace(".", ",").split(",".toRegex())
    .dropLastWhile { it.isEmpty() }
    .toTypedArray()[3]
}