package io.parser.lora.provider

class RangeBasedRandomProvider<T : Comparable<T>>(
    private val range: ClosedRange<T>,
    private val step: T? = null
) : RandomProvider<T> {
    init {
        require(range.start <= range.endInclusive) { "Invalid range: start must be less than or equal to end" }
    }

    override fun getRandomValue(): T {
        return when (range.start) {
            is Int -> getRandomInt(range as ClosedRange<Int>) as T
            is Double -> getRandomDouble(range as ClosedRange<Double>) as T
            is Long -> getRandomLong(range as ClosedRange<Long>) as T
            else -> throw IllegalArgumentException("Unsupported type for range-based random provider")
        }
    }

    private fun getRandomInt(range: ClosedRange<Int>): Int {
        return if (step != null && step is Int) {
            val steps = ((range.endInclusive - range.start) / step) + 1
            range.start + (0 until steps).random() * step
        } else {
            (range.start..range.endInclusive).random()
        }
    }

    private fun getRandomDouble(range: ClosedRange<Double>): Double {
        val randomValue = range.start + Math.random() * (range.endInclusive - range.start)
        return if (step != null && step is Double) {
            (randomValue / step).toInt() * step
        } else {
            randomValue
        }
    }

    private fun getRandomLong(range: ClosedRange<Long>): Long {
        return if (step != null && step is Long) {
            val steps = ((range.endInclusive - range.start) / step) + 1
            range.start + (0 until steps).random() * step
        } else {
            (range.start..range.endInclusive).random()
        }
    }
}