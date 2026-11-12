package com.craftworks.music.util

/**
 * A small QR encoder: byte mode, versions 1 to 10, all four error correction levels.
 *
 * It exists because sharing a Navidrome song needs a code the phone can scan, and the
 * app has no QR library (and is built offline). The output is a plain matrix of dark
 * modules, quiet zone excluded, ready to be drawn at any size.
 */
object QrCode {

    /** Error correction level, with the two bits the format information needs. */
    enum class Level(val indicator: Int) {
        LOW(1),
        MEDIUM(0),
        QUARTILE(3),
        HIGH(2)
    }

    /** Total codewords (data + error correction) for versions 1 to 10. */
    private val TOTAL_CODEWORDS = intArrayOf(26, 44, 70, 100, 134, 172, 196, 242, 292, 346)

    /** Error correction codewords per block: [version - 1][level.ordinal]. */
    private val ECC_PER_BLOCK = arrayOf(
        intArrayOf(7, 10, 13, 17),
        intArrayOf(10, 16, 22, 28),
        intArrayOf(15, 26, 18, 22),
        intArrayOf(20, 18, 26, 16),
        intArrayOf(26, 24, 18, 22),
        intArrayOf(18, 16, 24, 28),
        intArrayOf(20, 18, 18, 26),
        intArrayOf(24, 22, 22, 26),
        intArrayOf(30, 22, 20, 24),
        intArrayOf(18, 26, 24, 28)
    )

    /** Number of blocks: [version - 1][level.ordinal]. */
    private val BLOCKS = arrayOf(
        intArrayOf(1, 1, 1, 1),
        intArrayOf(1, 1, 1, 1),
        intArrayOf(1, 1, 2, 2),
        intArrayOf(1, 2, 2, 4),
        intArrayOf(1, 2, 4, 4),
        intArrayOf(2, 4, 4, 4),
        intArrayOf(2, 4, 6, 5),
        intArrayOf(2, 4, 6, 6),
        intArrayOf(2, 5, 8, 8),
        intArrayOf(4, 5, 8, 8)
    )

    /** Alignment pattern centres per version (version 1 has none). */
    private val ALIGNMENT = arrayOf(
        intArrayOf(),
        intArrayOf(6, 18),
        intArrayOf(6, 22),
        intArrayOf(6, 26),
        intArrayOf(6, 30),
        intArrayOf(6, 34),
        intArrayOf(6, 22, 38),
        intArrayOf(6, 24, 42),
        intArrayOf(6, 26, 46),
        intArrayOf(6, 28, 50)
    )

    /** Returns the module matrix for [text], or null when it does not fit a version 10 code. */
    fun encode(text: String, level: Level = Level.MEDIUM): Array<BooleanArray>? {
        val payload = text.toByteArray(Charsets.UTF_8)

        val version = (1..10).firstOrNull { candidate ->
            val countBits = if (candidate < 10) 8 else 16
            4 + countBits + payload.size * 8 <= dataCodewords(candidate, level) * 8
        } ?: return null

        val bits = BitBuffer()
        bits.append(0b0100, 4)
        bits.append(payload.size, if (version < 10) 8 else 16)
        payload.forEach { bits.append(it.toInt() and 0xFF, 8) }

        val capacity = dataCodewords(version, level) * 8
        bits.append(0, minOf(4, capacity - bits.size))
        while (bits.size % 8 != 0) bits.append(0, 1)

        var pad = 0xEC
        while (bits.size < capacity) {
            bits.append(pad, 8)
            pad = if (pad == 0xEC) 0x11 else 0xEC
        }

        val codewords = bits.toBytes()
        val blocks = BLOCKS[version - 1][level.ordinal]
        val eccLength = ECC_PER_BLOCK[version - 1][level.ordinal]
        val shortLength = codewords.size / blocks
        val longBlocks = codewords.size % blocks

        val dataBlocks = ArrayList<IntArray>(blocks)
        val eccBlocks = ArrayList<IntArray>(blocks)
        var offset = 0
        for (block in 0 until blocks) {
            val length = shortLength + if (block >= blocks - longBlocks) 1 else 0
            val chunk = IntArray(length) { codewords[offset + it].toInt() and 0xFF }
            offset += length
            dataBlocks.add(chunk)
            eccBlocks.add(reedSolomon(chunk, eccLength))
        }

        val interleaved = ArrayList<Int>()
        for (index in 0..shortLength) {
            dataBlocks.forEach { block -> if (index < block.size) interleaved.add(block[index]) }
        }
        for (index in 0 until eccLength) {
            eccBlocks.forEach { block -> interleaved.add(block[index]) }
        }

        return buildMatrix(version, level, interleaved.toIntArray())
    }

    private fun dataCodewords(version: Int, level: Level): Int {
        val ecc = ECC_PER_BLOCK[version - 1][level.ordinal] * BLOCKS[version - 1][level.ordinal]
        return TOTAL_CODEWORDS[version - 1] - ecc
    }

    // ---- Reed-Solomon over GF(256), polynomial 0x11D ----

    private val exponent = IntArray(512)
    private val logarithm = IntArray(256)

    init {
        var value = 1
        for (index in 0 until 255) {
            exponent[index] = value
            logarithm[value] = index
            value = value shl 1
            if (value and 0x100 != 0) value = value xor 0x11D
        }
        for (index in 255 until 512) exponent[index] = exponent[index - 255]
    }

    private fun multiply(left: Int, right: Int): Int =
        if (left == 0 || right == 0) 0 else exponent[logarithm[left] + logarithm[right]]

    private fun reedSolomon(data: IntArray, degree: Int): IntArray {
        var generator = intArrayOf(1)
        for (index in 0 until degree) {
            val next = IntArray(generator.size + 1)
            for (coefficient in generator.indices) {
                next[coefficient] = next[coefficient] xor generator[coefficient]
                next[coefficient + 1] = next[coefficient + 1] xor multiply(generator[coefficient], exponent[index])
            }
            generator = next
        }

        val remainder = IntArray(degree)
        for (byte in data) {
            val factor = byte xor remainder[0]
            for (index in 0 until degree - 1) {
                remainder[index] = remainder[index + 1] xor multiply(generator[index + 1], factor)
            }
            remainder[degree - 1] = multiply(generator[degree], factor)
        }
        return remainder
    }

    // ---- matrix ----

    private fun buildMatrix(version: Int, level: Level, codewords: IntArray): Array<BooleanArray> {
        val size = 17 + version * 4
        val modules = Array(size) { BooleanArray(size) }
        val reserved = Array(size) { BooleanArray(size) }

        fun set(x: Int, y: Int, dark: Boolean) {
            modules[y][x] = dark
            reserved[y][x] = true
        }

        // Timing patterns first: the finder patterns sit on top of them.
        for (index in 0 until size) {
            set(6, index, index % 2 == 0)
            set(index, 6, index % 2 == 0)
        }

        // Finder patterns and their separators.
        for ((cornerX, cornerY) in listOf(
            3 to 3,
            size - 4 to 3,
            3 to size - 4
        )) {
            for (dy in -4..4) {
                for (dx in -4..4) {
                    val x = cornerX + dx
                    val y = cornerY + dy
                    if (x !in 0 until size || y !in 0 until size) continue
                    val distance = maxOf(kotlin.math.abs(dx), kotlin.math.abs(dy))
                    set(x, y, distance != 2 && distance != 4)
                }
            }
        }

        // Alignment patterns, skipping the three finder corners.
        val centres = ALIGNMENT[version - 1]
        for (centreY in centres) {
            for (centreX in centres) {
                val atFinder = (centreX == 6 && centreY == 6) ||
                    (centreX == 6 && centreY == size - 7) ||
                    (centreX == size - 7 && centreY == 6)
                if (atFinder) continue
                for (dy in -2..2) {
                    for (dx in -2..2) {
                        val distance = maxOf(kotlin.math.abs(dx), kotlin.math.abs(dy))
                        set(centreX + dx, centreY + dy, distance != 1)
                    }
                }
            }
        }

        // Reserve the format areas so the data walk skips them.
        for (index in 0..8) {
            if (!reserved[8][index]) set(index, 8, false)
            if (!reserved[index][8]) set(8, index, false)
        }
        for (index in 0..7) {
            if (!reserved[8][size - 1 - index]) set(size - 1 - index, 8, false)
            if (!reserved[size - 1 - index][8]) set(8, size - 1 - index, false)
        }
        set(8, size - 8, true)

        if (version >= 7) {
            var remainder = version
            for (index in 0 until 12) {
                remainder = (remainder shl 1) xor ((remainder ushr 11) * 0x1F25)
            }
            val versionBits = (version shl 12) or remainder
            for (index in 0 until 18) {
                val dark = (versionBits ushr index) and 1 != 0
                val a = size - 11 + index % 3
                val b = index / 3
                set(a, b, dark)
                set(b, a, dark)
            }
        }

        // Data, in the two-column zigzag from the bottom right.
        var bitIndex = 0
        var right = size - 1
        while (right >= 1) {
            if (right == 6) right = 5
            for (vertical in 0 until size) {
                for (column in 0..1) {
                    val x = right - column
                    val upward = ((right + 1) and 2) == 0
                    val y = if (upward) size - 1 - vertical else vertical
                    if (reserved[y][x]) continue
                    val dark = if (bitIndex < codewords.size * 8) {
                        (codewords[bitIndex ushr 3] ushr (7 - (bitIndex and 7))) and 1 != 0
                    } else {
                        false
                    }
                    modules[y][x] = dark
                    bitIndex++
                }
            }
            right -= 2
        }

        // Pick the mask with the lowest penalty.
        var bestMask = 0
        var bestPenalty = Int.MAX_VALUE
        for (mask in 0 until 8) {
            applyMask(modules, reserved, mask)
            drawFormatBits(modules, size, level, mask)
            val penalty = penalty(modules)
            if (penalty < bestPenalty) {
                bestPenalty = penalty
                bestMask = mask
            }
            applyMask(modules, reserved, mask)
        }
        applyMask(modules, reserved, bestMask)
        drawFormatBits(modules, size, level, bestMask)

        return modules
    }

    private fun applyMask(modules: Array<BooleanArray>, reserved: Array<BooleanArray>, mask: Int) {
        for (y in modules.indices) {
            for (x in modules.indices) {
                if (reserved[y][x]) continue
                val invert = when (mask) {
                    0 -> (x + y) % 2 == 0
                    1 -> y % 2 == 0
                    2 -> x % 3 == 0
                    3 -> (x + y) % 3 == 0
                    4 -> (y / 2 + x / 3) % 2 == 0
                    5 -> (x * y) % 2 + (x * y) % 3 == 0
                    6 -> ((x * y) % 2 + (x * y) % 3) % 2 == 0
                    else -> ((x + y) % 2 + (x * y) % 3) % 2 == 0
                }
                if (invert) modules[y][x] = !modules[y][x]
            }
        }
    }

    private fun drawFormatBits(modules: Array<BooleanArray>, size: Int, level: Level, mask: Int) {
        val data = (level.indicator shl 3) or mask
        var remainder = data
        for (index in 0 until 10) {
            remainder = (remainder shl 1) xor ((remainder ushr 9) * 0x537)
        }
        val bits = ((data shl 10) or remainder) xor 0x5412

        fun bit(index: Int) = (bits ushr index) and 1 != 0

        for (index in 0..5) modules[index][8] = bit(index)
        modules[7][8] = bit(6)
        modules[8][8] = bit(7)
        modules[8][7] = bit(8)
        for (index in 9..14) modules[8][14 - index] = bit(index)

        for (index in 0..7) modules[8][size - 1 - index] = bit(index)
        for (index in 8..14) modules[size - 15 + index][8] = bit(index)
        modules[8][size - 8] = true
    }

    private fun penalty(modules: Array<BooleanArray>): Int {
        val size = modules.size
        var score = 0

        // Rule 1: runs of five or more identical modules.
        for (y in 0 until size) {
            var run = 1
            for (x in 1 until size) {
                if (modules[y][x] == modules[y][x - 1]) {
                    run++
                } else {
                    if (run >= 5) score += 3 + (run - 5)
                    run = 1
                }
            }
            if (run >= 5) score += 3 + (run - 5)
        }
        for (x in 0 until size) {
            var run = 1
            for (y in 1 until size) {
                if (modules[y][x] == modules[y - 1][x]) {
                    run++
                } else {
                    if (run >= 5) score += 3 + (run - 5)
                    run = 1
                }
            }
            if (run >= 5) score += 3 + (run - 5)
        }

        // Rule 2: 2x2 blocks of one colour.
        for (y in 0 until size - 1) {
            for (x in 0 until size - 1) {
                val value = modules[y][x]
                if (value == modules[y][x + 1] && value == modules[y + 1][x] && value == modules[y + 1][x + 1]) {
                    score += 3
                }
            }
        }

        // Rule 3: finder-like 1:1:3:1:1 patterns, with four light modules on one side.
        val pattern = booleanArrayOf(true, false, true, true, true, false, true)

        fun lights(get: (Int) -> Boolean, start: Int, step: Int): Boolean {
            for (offset in 0 until 4) {
                val index = start + offset * step
                if (index < 0 || index >= size) return false
                if (get(index)) return false
            }
            return true
        }

        fun matched(get: (Int) -> Boolean, start: Int, step: Int): Boolean {
            for (offset in pattern.indices) {
                val index = start + offset * step
                if (index < 0 || index >= size) return false
                if (get(index) != pattern[offset]) return false
            }
            val before = lights(get, start - step, step)
            val after = lights(get, start + 7 * step, step)
            return before || after
        }

        for (y in 0 until size) {
            for (x in 0..size - 7) {
                if (matched({ modules[y][it] }, x, 1)) score += 40
            }
        }
        for (x in 0 until size) {
            for (y in 0..size - 7) {
                if (matched({ modules[it][x] }, y, 1)) score += 40
            }
        }

        // Rule 4: deviation from an even split of dark and light.
        var dark = 0
        for (y in 0 until size) for (x in 0 until size) if (modules[y][x]) dark++
        val total = size * size
        val percent = dark * 100 / total
        score += ((kotlin.math.abs(percent - 50)) / 5) * 10

        return score
    }

    private class BitBuffer {
        private val bits = ArrayList<Boolean>(256)

        val size: Int get() = bits.size

        fun append(value: Int, length: Int) {
            for (index in length - 1 downTo 0) {
                bits.add((value ushr index) and 1 != 0)
            }
        }

        fun toBytes(): ByteArray {
            val bytes = ByteArray(bits.size / 8)
            bits.forEachIndexed { index, dark ->
                if (dark) bytes[index / 8] = (bytes[index / 8].toInt() or (1 shl (7 - index % 8))).toByte()
            }
            return bytes
        }
    }
}
