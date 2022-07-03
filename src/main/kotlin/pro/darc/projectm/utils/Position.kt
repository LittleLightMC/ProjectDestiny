package pro.darc.projectm.utils

import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import kotlin.math.sqrt

fun blockPosOf(x: Int, y: Int, z: Int) = BlockPos(x, y, z)
fun locationPosOf(x: Double, y: Double, z: Double, yaw: Float = 0f, pitch: Float = 0f)
        = LocationPos(x, y, z, yaw, pitch)
fun chunkPosOf(x: Int, z: Int) = ChunkPos(x, z)

fun Location.asPos() = LocationPos(x, y, z, yaw, pitch)
fun LocationPos.asBukkitBlock(world: World) = world.getBlockAt(x.toInt(), y.toInt(), z.toInt())
fun LocationPos.asBukkitLocation(world: World) = Location(world, x, y, z)
fun LocationPos.asBlockPos() = BlockPos(x.toInt(), y.toInt(), z.toInt())

fun Block.asPos() = BlockPos(x, y, z)
fun Location.asBlockPos() = BlockPos(blockX, blockY, blockZ)
fun BlockPos.asBukkitBlock(world: World) = world.getBlockAt(x, y, z)
fun BlockPos.asBukkitLocation(world: World) = Location(world, x.toDouble(), y.toDouble(), z.toDouble())
fun BlockPos.asLocationPos() = LocationPos(x.toDouble(), y.toDouble(), z.toDouble(), 0f, 0f)
fun BlockPos.asChunkPos() = ChunkPos(x shr 4, z shr 4)

fun Chunk.asPos() = ChunkPos(x, z)
fun ChunkPos.asBukkitChunk(world: World) = world.getChunkAt(x, z)

data class BlockPos(
    var x: Int,
    var y: Int,
    var z: Int
) : VectorComparable<BlockPos> {
    override fun axis(): DoubleArray = doubleArrayOf(x.toDouble(), y.toDouble(), z.toDouble())
    override fun factor(axis: IntArray) = BlockPos(axis[0], axis[1], axis[2])
}

data class LocationPos(
    var x: Double,
    var y: Double,
    var z: Double,
    val yaw: Float,
    val pitch: Float
) : VectorComparable<LocationPos> {
    override fun axis(): DoubleArray = doubleArrayOf(x, y, z)
    override fun factor(axis: IntArray) = LocationPos(axis[0].toDouble(), axis[1].toDouble(), axis[2].toDouble(), yaw, pitch)
}

data class ChunkPos(
    var x: Int,
    var z: Int
) : VectorComparable<ChunkPos> {
    override fun axis(): DoubleArray = doubleArrayOf(x.toDouble(), z.toDouble())
    override fun factor(axis: IntArray) = ChunkPos(axis[0], axis[1])
}

interface VectorComparable<T : VectorComparable<T>> : Comparable<T> {
    fun axis(): DoubleArray
    fun factor(axis: IntArray): T

    operator fun rangeTo(other: T): PosRange<T, T> {
        return PosRange(this as T, other) { PosRangeIterator(this, other, ::factor) }
    }

    override fun compareTo(other: T): Int {
        val selfAxis = axis()
        val otherAxis = other.axis()
        val pairAxis = selfAxis.mapIndexed { index, axis -> axis to otherAxis[index] }
        val (d1, d2) = calculatePythagoras(*pairAxis.toTypedArray())
        return d1.compareTo(d2)
    }
}

fun Pair<Int, Int>.toDouble() = first.toDouble() to second.toDouble()
fun Pair<Double, Double>.toInt() = first.toInt() to second.toInt()
fun calculatePythagoras(vararg positions: Pair<Double, Double>): Pair<Double, Double> {
    val pow = positions.map { (x1, x2) -> (x1 * x1) to (x2 * x2) }

    val x1Sum = pow.sumOf { (x, _) -> x }
    val x2Sum = pow.sumOf { (_, x) -> x }

    val d1 = sqrt(x1Sum)
    val d2 = sqrt(x2Sum)

    return d1 to d2
}

operator fun PosRange<*, BlockPos>.contains(other: Location) = contains(other.asPos())
operator fun PosRange<*, BlockPos>.contains(other: Block) = contains(other.asPos())
operator fun PosRange<*, ChunkPos>.contains(other: Chunk) = contains(other.asPos())

class PosRange<T, POS : VectorComparable<POS>>(
    private val first: POS,
    private val last: POS,
    val buildIterator: () -> Iterator<T>
) : ClosedRange<POS>, Iterable<T> {
    override val endInclusive: POS get() = last
    override val start: POS get() = first

    override fun contains(value: POS): Boolean {
        val firstAxis = first.axis()
        val lastAxis = last.axis()
        return value.axis().withIndex().all { (index, it) ->
            it >= firstAxis[index] && it <= lastAxis[index]
        }
    }

    override fun iterator(): Iterator<T> = buildIterator()
}

class PosRangeIterator<T : VectorComparable<T>>(
    first: T,
    last: T,
    val factor: (axis: IntArray) -> T
) : Iterator<T> {
    private val firstAxis = first.axis()
    private val lastAxis = last.axis()
    private val closedAxisRanges = firstAxis.mapIndexed { index, it ->
        IntProgression.fromClosedRange(it.toInt(), lastAxis[index].toInt(), 1)
    }
    private val iteratorAxis = closedAxisRanges.map { it.iterator() }.toTypedArray()

    private val actualAxis = iteratorAxis.toList().subList(0, iteratorAxis.size-1)
        .map { it.nextInt() }
        .toTypedArray()

    override fun hasNext(): Boolean {
        return iteratorAxis.any { it.hasNext() }
    }

    override fun next(): T {
        val lastIndex = iteratorAxis.size-1
        val last = iteratorAxis[lastIndex]
        if(last.hasNext()) {
            val axis = IntArray(actualAxis.size) { actualAxis[it] } + last.nextInt()
            return factor(axis)
        }
        for(i in lastIndex-1 downTo 0) {
            val axis = iteratorAxis[i]
            if(axis.hasNext()) {
                actualAxis[i] = axis.nextInt()
                iteratorAxis[i+1] = closedAxisRanges[i+1].iterator()
                break
            }
        }
        return next()
    }
}

class RangeIteratorWithFactor<T, POS : VectorComparable<POS>>(
    start: T,
    end: T,
    private val factor: (POS) -> T,
    posFactor: (T) -> POS
) : Iterator<T> {
    val iterator = PosRangeIterator(posFactor(start), posFactor(end), posFactor(start)::factor)

    override fun hasNext() = iterator.hasNext()
    override fun next() = factor(iterator.next())
}
