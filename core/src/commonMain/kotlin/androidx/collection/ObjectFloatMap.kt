/*
 * Copyright 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress(
    "RedundantVisibilityModifier",
    "NOTHING_TO_INLINE"
)

package com.composables.core.androidx.collection

import com.composables.core.androidx.collection.internal.EMPTY_OBJECTS
import com.composables.core.androidx.collection.internal.requirePrecondition
import kotlin.jvm.JvmField
import kotlin.jvm.JvmOverloads

// -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
// DO NOT MAKE CHANGES to the kotlin source file.
//
// This file was generated from a template in the template directory.
// Make a change to the original template and run the generateCollections.sh script
// to ensure the change is available on all versions of the map.
//
// Note that there are 3 templates for maps, one for object-to-primitive, one
// for primitive-to-object and one for primitive-to-primitive. Also, the
// object-to-object is ScatterMap.kt, which doesn't have a template.
// -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

// Default empty map to avoid allocations
private val EmptyObjectFloatMap = MutableObjectFloatMap<Any?>(0)

/**
 * Returns an empty, read-only [ObjectFloatMap].
 */
@Suppress("UNCHECKED_CAST")
public fun <K> emptyObjectFloatMap(): ObjectFloatMap<K> =
    EmptyObjectFloatMap as ObjectFloatMap<K>

/**
 * Returns an empty, read-only [ObjectFloatMap].
 */
@Suppress("UNCHECKED_CAST")
public fun <K> objectFloatMap(): ObjectFloatMap<K> =
    EmptyObjectFloatMap as ObjectFloatMap<K>

/**
 * Returns a new [ObjectFloatMap] with only [key1] associated with [value1].
 */
public fun <K> objectFloatMapOf(
    key1: K,
    value1: Float
): ObjectFloatMap<K> =
    MutableObjectFloatMap<K>().also { map ->
        map[key1] = value1
    }

/**
 * Returns a new [ObjectFloatMap] with only [key1] and [key2] associated with
 * [value1] and [value2], respectively.
 */
public fun <K> objectFloatMapOf(
    key1: K,
    value1: Float,
    key2: K,
    value2: Float,
): ObjectFloatMap<K> =
    MutableObjectFloatMap<K>().also { map ->
        map[key1] = value1
        map[key2] = value2
    }

/**
 * Returns a new [ObjectFloatMap] with only [key1], [key2], and [key3] associated with
 * [value1], [value2], and [value3], respectively.
 */
public fun <K> objectFloatMapOf(
    key1: K,
    value1: Float,
    key2: K,
    value2: Float,
    key3: K,
    value3: Float,
): ObjectFloatMap<K> =
    MutableObjectFloatMap<K>().also { map ->
        map[key1] = value1
        map[key2] = value2
        map[key3] = value3
    }

/**
 * Returns a new [ObjectFloatMap] with only [key1], [key2], [key3], and [key4] associated with
 * [value1], [value2], [value3], and [value4], respectively.
 */
public fun <K> objectFloatMapOf(
    key1: K,
    value1: Float,
    key2: K,
    value2: Float,
    key3: K,
    value3: Float,
    key4: K,
    value4: Float,
): ObjectFloatMap<K> =
    MutableObjectFloatMap<K>().also { map ->
        map[key1] = value1
        map[key2] = value2
        map[key3] = value3
        map[key4] = value4
    }

/**
 * Returns a new [ObjectFloatMap] with only [key1], [key2], [key3], [key4], and [key5] associated
 * with [value1], [value2], [value3], [value4], and [value5], respectively.
 */
public fun <K> objectFloatMapOf(
    key1: K,
    value1: Float,
    key2: K,
    value2: Float,
    key3: K,
    value3: Float,
    key4: K,
    value4: Float,
    key5: K,
    value5: Float,
): ObjectFloatMap<K> =
    MutableObjectFloatMap<K>().also { map ->
        map[key1] = value1
        map[key2] = value2
        map[key3] = value3
        map[key4] = value4
        map[key5] = value5
    }

/**
 * Returns a new empty [MutableObjectFloatMap].
 */
public fun <K> mutableObjectFloatMapOf(): MutableObjectFloatMap<K> = MutableObjectFloatMap()

/**
 * Returns a new [MutableObjectFloatMap] with only [key1] associated with [value1].
 */
public fun <K> mutableObjectFloatMapOf(
    key1: K,
    value1: Float,
): MutableObjectFloatMap<K> =
    MutableObjectFloatMap<K>().also { map ->
        map[key1] = value1
    }

/**
 * Returns a new [MutableObjectFloatMap] with only [key1] and [key2] associated with
 * [value1] and [value2], respectively.
 */
public fun <K> mutableObjectFloatMapOf(
    key1: K,
    value1: Float,
    key2: K,
    value2: Float,
): MutableObjectFloatMap<K> =
    MutableObjectFloatMap<K>().also { map ->
        map[key1] = value1
        map[key2] = value2
    }

/**
 * Returns a new [MutableObjectFloatMap] with only [key1], [key2], and [key3] associated with
 * [value1], [value2], and [value3], respectively.
 */
public fun <K> mutableObjectFloatMapOf(
    key1: K,
    value1: Float,
    key2: K,
    value2: Float,
    key3: K,
    value3: Float,
): MutableObjectFloatMap<K> =
    MutableObjectFloatMap<K>().also { map ->
        map[key1] = value1
        map[key2] = value2
        map[key3] = value3
    }

/**
 * Returns a new [MutableObjectFloatMap] with only [key1], [key2], [key3], and [key4]
 * associated with [value1], [value2], [value3], and [value4], respectively.
 */
public fun <K> mutableObjectFloatMapOf(
    key1: K,
    value1: Float,
    key2: K,
    value2: Float,
    key3: K,
    value3: Float,
    key4: K,
    value4: Float,
): MutableObjectFloatMap<K> =
    MutableObjectFloatMap<K>().also { map ->
        map[key1] = value1
        map[key2] = value2
        map[key3] = value3
        map[key4] = value4
    }

/**
 * Returns a new [MutableObjectFloatMap] with only [key1], [key2], [key3], [key4], and [key5]
 * associated with [value1], [value2], [value3], [value4], and [value5], respectively.
 */
public fun <K> mutableObjectFloatMapOf(
    key1: K,
    value1: Float,
    key2: K,
    value2: Float,
    key3: K,
    value3: Float,
    key4: K,
    value4: Float,
    key5: K,
    value5: Float,
): MutableObjectFloatMap<K> =
    MutableObjectFloatMap<K>().also { map ->
        map[key1] = value1
        map[key2] = value2
        map[key3] = value3
        map[key4] = value4
        map[key5] = value5
    }

/**
 * [ObjectFloatMap] is a container with a [Map]-like interface for keys with
 * reference types and [Float] primitives for values.
 *
 * The underlying implementation is designed to avoid allocations from boxing,
 * and insertion, removal, retrieval, and iteration operations. Allocations
 * may still happen on insertion when the underlying storage needs to grow to
 * accommodate newly added entries to the table. In addition, this implementation
 * minimizes memory usage by avoiding the use of separate objects to hold
 * key/value pairs.
 *
 * This implementation makes no guarantee as to the order of the keys and
 * values stored, nor does it make guarantees that the order remains constant
 * over time.
 *
 * This implementation is not thread-safe: if multiple threads access this
 * container concurrently, and one or more threads modify the structure of
 * the map (insertion or removal for instance), the calling code must provide
 * the appropriate synchronization. Multiple threads are safe to read from this
 * map concurrently if no write is happening.
 *
 * This implementation is read-only and only allows data to be queried. A
 * mutable implementation is provided by [MutableObjectFloatMap].
 *
 * @see [MutableObjectFloatMap]
 * @see ScatterMap
 */
public sealed class ObjectFloatMap<K> {
    // NOTE: Our arrays are marked internal to implement inlined forEach{}
    // The backing array for the metadata bytes contains
    // `capacity + 1 + ClonedMetadataCount` entries, including when
    // the table is empty (see [EmptyGroup]).
    @PublishedApi
    @JvmField
    internal var metadata: LongArray = EmptyGroup

    @PublishedApi
    @JvmField
    internal var keys: Array<Any?> = EMPTY_OBJECTS

    @PublishedApi
    @JvmField
    internal var values: FloatArray = EmptyFloatArray

    // We use a backing field for capacity to avoid invokevirtual calls
    // every time we need to look at the capacity
    @Suppress("PropertyName")
    @JvmField
    internal var _capacity: Int = 0

    /**
     * Returns the number of key-value pairs that can be stored in this map
     * without requiring internal storage reallocation.
     */
    public val capacity: Int
        get() = _capacity

    // We use a backing field for capacity to avoid invokevirtual calls
    // every time we need to look at the size
    @Suppress("PropertyName")
    @JvmField
    internal var _size: Int = 0

    /**
     * Returns the number of key-value pairs in this map.
     */
    public val size: Int
        get() = _size

    /**
     * Returns `true` if this map has at least one entry.
     */
    public fun any(): Boolean = _size != 0

    /**
     * Returns `true` if this map has no entries.
     */
    public fun none(): Boolean = _size == 0

    /**
     * Indicates whether this map is empty.
     */
    public fun isEmpty(): Boolean = _size == 0

    /**
     * Returns `true` if this map is not empty.
     */
    public fun isNotEmpty(): Boolean = _size != 0

    /**
     * Returns the value corresponding to the given [key], or `null` if such
     * a key is not present in the map.
     * @throws NoSuchElementException when [key] is not found
     */
    public operator fun get(key: K): Float {
        val index = findKeyIndex(key)
        if (index < 0) {
            throw NoSuchElementException("There is no key $key in the map")
        }
        return values[index]
    }

    /**
     * Returns the value to which the specified [key] is mapped,
     * or [defaultValue] if this map contains no mapping for the key.
     */
    public fun getOrDefault(key: K, defaultValue: Float): Float {
        val index = findKeyIndex(key)
        if (index >= 0) {
            return values[index]
        }
        return defaultValue
    }

    /**
     * Returns the value for the given [key] if the value is present
     * and not null. Otherwise, returns the result of the [defaultValue]
     * function.
     */
    public inline fun getOrElse(key: K, defaultValue: () -> Float): Float {
        val index = findKeyIndex(key)
        if (index >= 0) {
            return values[index]
        }
        return defaultValue()
    }

    /**
     * Iterates over every key/value pair stored in this map by invoking
     * the specified [block] lambda.
     */
    @PublishedApi
    internal inline fun forEachIndexed(block: (index: Int) -> Unit) {
        val m = metadata
        val lastIndex = m.size - 2 // We always have 0 or at least 2 entries

        for (i in 0..lastIndex) {
            var slot = m[i]
            if (slot.maskEmptyOrDeleted() != BitmaskMsb) {
                // Branch-less if (i == lastIndex) 7 else 8
                // i - lastIndex returns a negative value when i < lastIndex,
                // so 1 is set as the MSB. By inverting and shifting we get
                // 0 when i < lastIndex, 1 otherwise.
                val bitCount = 8 - ((i - lastIndex).inv() ushr 31)
                for (j in 0 until bitCount) {
                    if (isFull(slot and 0xFFL)) {
                        val index = (i shl 3) + j
                        block(index)
                    }
                    slot = slot shr 8
                }
                if (bitCount != 8) return
            }
        }
    }

    /**
     * Iterates over every key/value pair stored in this map by invoking
     * the specified [block] lambda.
     */
    public inline fun forEach(block: (key: K, value: Float) -> Unit) {
        val k = keys
        val v = values

        forEachIndexed { index ->
            @Suppress("UNCHECKED_CAST")
            block(k[index] as K, v[index])
        }
    }

    /**
     * Iterates over every key stored in this map by invoking the specified
     * [block] lambda.
     */
    public inline fun forEachKey(block: (key: K) -> Unit) {
        val k = keys

        forEachIndexed { index ->
            @Suppress("UNCHECKED_CAST")
            block(k[index] as K)
        }
    }

    /**
     * Iterates over every value stored in this map by invoking the specified
     * [block] lambda.
     */
    public inline fun forEachValue(block: (value: Float) -> Unit) {
        val v = values

        forEachIndexed { index ->
            block(v[index])
        }
    }

    /**
     * Returns true if all entries match the given [predicate].
     */
    public inline fun all(predicate: (K, Float) -> Boolean): Boolean {
        forEach { key, value ->
            if (!predicate(key, value)) return false
        }
        return true
    }

    /**
     * Returns true if at least one entry matches the given [predicate].
     */
    public inline fun any(predicate: (K, Float) -> Boolean): Boolean {
        forEach { key, value ->
            if (predicate(key, value)) return true
        }
        return false
    }

    /**
     * Returns the number of entries in this map.
     */
    public fun count(): Int = size

    /**
     * Returns the number of entries matching the given [predicate].
     */
    public inline fun count(predicate: (K, Float) -> Boolean): Int {
        var count = 0
        forEach { key, value ->
            if (predicate(key, value)) count++
        }
        return count
    }

    /**
     * Returns true if the specified [key] is present in this hash map, false
     * otherwise.
     */
    public operator fun contains(key: K): Boolean = findKeyIndex(key) >= 0

    /**
     * Returns true if the specified [key] is present in this hash map, false
     * otherwise.
     */
    public fun containsKey(key: K): Boolean = findKeyIndex(key) >= 0

    /**
     * Returns true if the specified [value] is present in this hash map, false
     * otherwise.
     */
    public fun containsValue(value: Float): Boolean {
        forEachValue { v ->
            if (value == v) return true
        }
        return false
    }

    /**
     * Creates a String from the entries, separated by [separator] and using [prefix] before
     * and [postfix] after, if supplied.
     *
     * When a non-negative value of [limit] is provided, a maximum of [limit] items are used
     * to generate the string. If the collection holds more than [limit] items, the string
     * is terminated with [truncated].
     */
    @JvmOverloads
    public fun joinToString(
        separator: CharSequence = ", ",
        prefix: CharSequence = "",
        postfix: CharSequence = "", // I know this should be suffix, but this is kotlin's name
        limit: Int = -1,
        truncated: CharSequence = "...",
    ): String = buildString {
        append(prefix)
        var index = 0
        this@ObjectFloatMap.forEach { key, value ->
            if (index == limit) {
                append(truncated)
                return@buildString
            }
            if (index != 0) {
                append(separator)
            }
            append(key)
            append('=')
            append(value)
            index++
        }
        append(postfix)
    }

    /**
     * Creates a String from the entries, separated by [separator] and using [prefix] before
     * and [postfix] after, if supplied. Each entry is created with [transform].
     *
     * When a non-negative value of [limit] is provided, a maximum of [limit] items are used
     * to generate the string. If the collection holds more than [limit] items, the string
     * is terminated with [truncated].
     */
    @JvmOverloads
    public inline fun joinToString(
        separator: CharSequence = ", ",
        prefix: CharSequence = "",
        postfix: CharSequence = "", // I know this should be suffix, but this is kotlin's name
        limit: Int = -1,
        truncated: CharSequence = "...",
        crossinline transform: (key: K, value: Float) -> CharSequence
    ): String = buildString {
        append(prefix)
        var index = 0
        this@ObjectFloatMap.forEach { key, value ->
            if (index == limit) {
                append(truncated)
                return@buildString
            }
            if (index != 0) {
                append(separator)
            }
            append(transform(key, value))
            index++
        }
        append(postfix)
    }

    /**
     * Returns the hash code value for this map. The hash code the sum of the hash
     * codes of each key/value pair.
     */
    public override fun hashCode(): Int {
        var hash = 0

        forEach { key, value ->
            hash += key.hashCode() xor value.hashCode()
        }

        return hash
    }

    /**
     * Compares the specified object [other] with this hash map for equality.
     * The two objects are considered equal if [other]:
     * - Is a [ObjectFloatMap]
     * - Has the same [size] as this map
     * - Contains key/value pairs equal to this map's pair
     */
    public override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }

        if (other !is ObjectFloatMap<*>) {
            return false
        }
        if (other.size != size) {
            return false
        }

        @Suppress("UNCHECKED_CAST")
        val o = other as ObjectFloatMap<Any?>

        forEach { key, value ->
            if (value != o[key]) {
                return false
            }
        }

        return true
    }

    /**
     * Returns a string representation of this map. The map is denoted in the
     * string by the `{}`. Each key/value pair present in the map is represented
     * inside '{}` by a substring of the form `key=value`, and pairs are
     * separated by `, `.
     */
    public override fun toString(): String {
        if (isEmpty()) {
            return "{}"
        }

        val s = StringBuilder().append('{')
        var i = 0
        forEach { key, value ->
            s.append(if (key === this) "(this)" else key)
            s.append("=")
            s.append(value)
            i++
            if (i < _size) {
                s.append(',').append(' ')
            }
        }

        return s.append('}').toString()
    }

    /**
     * Scans the hash table to find the index in the backing arrays of the
     * specified [key]. Returns -1 if the key is not present.
     */
    @PublishedApi
    internal fun findKeyIndex(key: K): Int {
        val hash = hash(key)
        val hash2 = h2(hash)

        val probeMask = _capacity
        var probeOffset = h1(hash) and probeMask
        var probeIndex = 0

        while (true) {
            val g = group(metadata, probeOffset)
            var m = g.match(hash2)
            while (m.hasNext()) {
                val index = (probeOffset + m.get()) and probeMask
                if (keys[index] == key) {
                    return index
                }
                m = m.next()
            }

            if (g.maskEmpty() != 0L) {
                break
            }

            probeIndex += GroupWidth
            probeOffset = (probeOffset + probeIndex) and probeMask
        }

        return -1
    }
}

/**
 * [MutableObjectFloatMap] is a container with a [MutableMap]-like interface for keys with
 * reference types and [Float] primitives for values.
 *
 * The underlying implementation is designed to avoid allocations from boxing,
 * and insertion, removal, retrieval, and iteration operations. Allocations
 * may still happen on insertion when the underlying storage needs to grow to
 * accommodate newly added entries to the table. In addition, this implementation
 * minimizes memory usage by avoiding the use of separate objects to hold
 * key/value pairs.
 *
 * This implementation is not thread-safe: if multiple threads access this
 * container concurrently, and one or more threads modify the structure of
 * the map (insertion or removal for instance), the calling code must provide
 * the appropriate synchronization. Multiple threads are safe to read from this
 * map concurrently if no write is happening.
 *
 * @constructor Creates a new [MutableObjectFloatMap]
 * @param initialCapacity The initial desired capacity for this container.
 * the container will honor this value by guaranteeing its internal structures
 * can hold that many entries without requiring any allocations. The initial
 * capacity can be set to 0.
 *
 * @see MutableScatterMap
 */
public class MutableObjectFloatMap<K>(
    initialCapacity: Int = DefaultScatterCapacity
) : ObjectFloatMap<K>() {
    // Number of entries we can add before we need to grow
    private var growthLimit = 0

    init {
        requirePrecondition(initialCapacity >= 0) { "Capacity must be a positive value." }
        initializeStorage(unloadedCapacity(initialCapacity))
    }

    private fun initializeStorage(initialCapacity: Int) {
        val newCapacity = if (initialCapacity > 0) {
            // Since we use longs for storage, our capacity is never < 7, enforce
            // it here. We do have a special case for 0 to create small empty maps
            maxOf(7, normalizeCapacity(initialCapacity))
        } else {
            0
        }
        _capacity = newCapacity
        initializeMetadata(newCapacity)
        keys = arrayOfNulls(newCapacity)
        values = FloatArray(newCapacity)
    }

    private fun initializeMetadata(capacity: Int) {
        metadata = if (capacity == 0) {
            EmptyGroup
        } else {
            // Round up to the next multiple of 8 and find how many longs we need
            val size = (((capacity + 1 + ClonedMetadataCount) + 7) and 0x7.inv()) shr 3
            LongArray(size).apply {
                fill(AllEmpty)
            }
        }
        writeRawMetadata(metadata, capacity, Sentinel)
        initializeGrowth()
    }

    private fun initializeGrowth() {
        growthLimit = loadedCapacity(capacity) - _size
    }

    /**
     * Returns the value to which the specified [key] is mapped,
     * if the value is present in the map and not `null`. Otherwise,
     * calls `defaultValue()` and puts the result in the map associated
     * with [key].
     */
    public inline fun getOrPut(key: K, defaultValue: () -> Float): Float {
        val index = findKeyIndex(key)
        if (index >= 0) {
            return values[index]
        }
        val value = defaultValue()
        set(key, value)
        return value
    }

    /**
     * Creates a new mapping from [key] to [value] in this map. If [key] is
     * already present in the map, the association is modified and the previously
     * associated value is replaced with [value]. If [key] is not present, a new
     * entry is added to the map, which may require to grow the underlying storage
     * and cause allocations.
     */
    public operator fun set(key: K, value: Float) {
        var index = findIndex(key)
        if (index < 0) index = index.inv()
        keys[index] = key
        values[index] = value
    }

    /**
     * Creates a new mapping from [key] to [value] in this map. If [key] is
     * already present in the map, the association is modified and the previously
     * associated value is replaced with [value]. If [key] is not present, a new
     * entry is added to the map, which may require to grow the underlying storage
     * and cause allocations.
     */
    public fun put(key: K, value: Float) {
        set(key, value)
    }

    /**
     * Creates a new mapping from [key] to [value] in this map. If [key] is
     * already present in the map, the association is modified and the previously
     * associated value is replaced with [value]. If [key] is not present, a new
     * entry is added to the map, which may require to grow the underlying storage
     * and cause allocations.
     *
     * @return value previously associated with [key] or [default] if key was not present.
     */
    public fun put(key: K, value: Float, default: Float): Float {
        var index = findIndex(key)
        var previous = default
        if (index < 0) {
            index = index.inv()
        } else {
            previous = values[index]
        }
        keys[index] = key
        values[index] = value

        return previous
    }

    /**
     * Puts all the key/value mappings in the [from] map into this map.
     */
    public fun putAll(from: ObjectFloatMap<K>) {
        from.forEach { key, value ->
            this[key] = value
        }
    }

    /**
     * Puts all the key/value mappings in the [from] map into this map.
     */
    public inline operator fun plusAssign(from: ObjectFloatMap<K>): Unit = putAll(from)

    /**
     * Removes the specified [key] and its associated value from the map.
     */
    public fun remove(key: K) {
        val index = findKeyIndex(key)
        if (index >= 0) {
            removeValueAt(index)
        }
    }

    /**
     * Removes the specified [key] and its associated value from the map if the
     * associated value equals [value]. Returns whether the removal happened.
     */
    public fun remove(key: K, value: Float): Boolean {
        val index = findKeyIndex(key)
        if (index >= 0) {
            if (values[index] == value) {
                removeValueAt(index)
                return true
            }
        }
        return false
    }

    /**
     * Removes any mapping for which the specified [predicate] returns true.
     */
    public inline fun removeIf(predicate: (K, Float) -> Boolean) {
        forEachIndexed { index ->
            @Suppress("UNCHECKED_CAST")
            if (predicate(keys[index] as K, values[index])) {
                removeValueAt(index)
            }
        }
    }

    /**
     * Removes the specified [key] and its associated value from the map.
     */
    public inline operator fun minusAssign(key: K) {
        remove(key)
    }

    /**
     * Removes the specified [keys] and their associated value from the map.
     */
    public inline operator fun minusAssign(@Suppress("ArrayReturn") keys: Array<out K>) {
        for (key in keys) {
            remove(key)
        }
    }

    /**
     * Removes the specified [keys] and their associated value from the map.
     */
    public inline operator fun minusAssign(keys: Iterable<K>) {
        for (key in keys) {
            remove(key)
        }
    }

    /**
     * Removes the specified [keys] and their associated value from the map.
     */
    public inline operator fun minusAssign(keys: Sequence<K>) {
        for (key in keys) {
            remove(key)
        }
    }

    /**
     * Removes the specified [keys] and their associated value from the map.
     */
    public inline operator fun minusAssign(keys: ScatterSet<K>) {
        keys.forEach { key ->
            remove(key)
        }
    }

    @PublishedApi
    internal fun removeValueAt(index: Int) {
        _size -= 1

        // TODO: We could just mark the entry as empty if there's a group
        //       window around this entry that was already empty
        writeMetadata(metadata, _capacity, index, Deleted)
        keys[index] = null
    }

    /**
     * Removes all mappings from this map.
     */
    public fun clear() {
        _size = 0
        if (metadata !== EmptyGroup) {
            metadata.fill(AllEmpty)
            writeRawMetadata(metadata, _capacity, Sentinel)
        }
        keys.fill(null, 0, _capacity)
        initializeGrowth()
    }

    /**
     * Scans the hash table to find the index at which we can store a value
     * for the give [key]. If the key already exists in the table, its index
     * will be returned, otherwise the index of an empty slot will be returned.
     * Calling this function may cause the internal storage to be reallocated
     * if the table is full.
     */
    private fun findIndex(key: K): Int {
        val hash = hash(key)
        val hash1 = h1(hash)
        val hash2 = h2(hash)

        val probeMask = _capacity
        var probeOffset = hash1 and probeMask
        var probeIndex = 0

        while (true) {
            val g = group(metadata, probeOffset)
            var m = g.match(hash2)
            while (m.hasNext()) {
                val index = (probeOffset + m.get()) and probeMask
                if (keys[index] == key) {
                    return index
                }
                m = m.next()
            }

            if (g.maskEmpty() != 0L) {
                break
            }

            probeIndex += GroupWidth
            probeOffset = (probeOffset + probeIndex) and probeMask
        }

        var index = findFirstAvailableSlot(hash1)
        if (growthLimit == 0 && !isDeleted(metadata, index)) {
            adjustStorage()
            index = findFirstAvailableSlot(hash1)
        }

        _size += 1
        growthLimit -= if (isEmpty(metadata, index)) 1 else 0
        writeMetadata(metadata, _capacity, index, hash2.toLong())

        return index.inv()
    }

    /**
     * Finds the first empty or deleted slot in the table in which we can
     * store a value without resizing the internal storage.
     */
    private fun findFirstAvailableSlot(hash1: Int): Int {
        val probeMask = _capacity
        var probeOffset = hash1 and probeMask
        var probeIndex = 0

        while (true) {
            val g = group(metadata, probeOffset)
            val m = g.maskEmptyOrDeleted()
            if (m != 0L) {
                return (probeOffset + m.lowestBitSet()) and probeMask
            }
            probeIndex += GroupWidth
            probeOffset = (probeOffset + probeIndex) and probeMask
        }
    }

    /**
     * Trims this [MutableObjectFloatMap]'s storage so it is sized appropriately
     * to hold the current mappings.
     *
     * Returns the number of empty entries removed from this map's storage.
     * Returns be 0 if no trimming is necessary or possible.
     */
    public fun trim(): Int {
        val previousCapacity = _capacity
        val newCapacity = normalizeCapacity(unloadedCapacity(_size))
        if (newCapacity < previousCapacity) {
            resizeStorage(newCapacity)
            return previousCapacity - _capacity
        }
        return 0
    }

    /**
     * Grow internal storage if necessary. This function can instead opt to
     * remove deleted entries from the table to avoid an expensive reallocation
     * of the underlying storage. This "rehash in place" occurs when the
     * current size is <= 25/32 of the table capacity. The choice of 25/32 is
     * detailed in the implementation of abseil's `raw_hash_set`.
     */
    private fun adjustStorage() {
        if (_capacity > GroupWidth && _size.toULong() * 32UL <= _capacity.toULong() * 25UL) {
            dropDeletes()
        } else {
            resizeStorage(nextCapacity(_capacity))
        }
    }

    private fun dropDeletes() {
        val metadata = metadata
        val capacity = _capacity
        val keys = keys
        val values = values

        // Converts Sentinel and Deleted to Empty, and Full to Deleted
        convertMetadataForCleanup(metadata, capacity)

        var swapIndex = -1
        var index = 0

        // Drop deleted items and re-hashes surviving entries
        while (index != capacity) {
            var m = readRawMetadata(metadata, index)
            // Formerly Deleted entry, we can use it as a swap spot
            if (m == Empty) {
                swapIndex = index
                index++
                continue
            }

            // Formerly Full entries are now marked Deleted. If we see an
            // entry that's not marked Deleted, we can ignore it completely
            if (m != Deleted) {
                index++
                continue
            }

            val hash = hash(keys[index])
            val hash1 = h1(hash)
            val targetIndex = findFirstAvailableSlot(hash1)

            // Test if the current index (i) and the new index (targetIndex) fall
            // within the same group based on the hash. If the group doesn't change,
            // we don't move the entry
            val probeOffset = hash1 and capacity
            val newProbeIndex = ((targetIndex - probeOffset) and capacity) / GroupWidth
            val oldProbeIndex = ((index - probeOffset) and capacity) / GroupWidth

            if (newProbeIndex == oldProbeIndex) {
                val hash2 = h2(hash)
                writeRawMetadata(metadata, index, hash2.toLong())

                // Copies the metadata into the clone area
                metadata[metadata.lastIndex] =
                    (Empty shl 56) or (metadata[0] and 0x00ffffff_ffffffffL)

                index++
                continue
            }

            m = readRawMetadata(metadata, targetIndex)
            if (m == Empty) {
                // The target is empty so we can transfer directly
                val hash2 = h2(hash)
                writeRawMetadata(metadata, targetIndex, hash2.toLong())
                writeRawMetadata(metadata, index, Empty)

                keys[targetIndex] = keys[index]
                keys[index] = null

                values[targetIndex] = values[index]
                values[index] = 0f

                swapIndex = index
            } else /* m == Deleted */ {
                // The target isn't empty so we use an empty slot denoted by
                // swapIndex to perform the swap
                val hash2 = h2(hash)
                writeRawMetadata(metadata, targetIndex, hash2.toLong())

                if (swapIndex == -1) {
                    swapIndex = findEmptySlot(metadata, index + 1, capacity)
                }

                keys[swapIndex] = keys[targetIndex]
                keys[targetIndex] = keys[index]
                keys[index] = keys[swapIndex]

                values[swapIndex] = values[targetIndex]
                values[targetIndex] = values[index]
                values[index] = values[swapIndex]

                // Since we exchanged two slots we must repeat the process with
                // element we just moved in the current location
                index--
            }

            // Copies the metadata into the clone area
            metadata[metadata.lastIndex] = (Empty shl 56) or (metadata[0] and 0x00ffffff_ffffffffL)

            index++
        }

        initializeGrowth()
    }

    private fun resizeStorage(newCapacity: Int) {
        val previousMetadata = metadata
        val previousKeys = keys
        val previousValues = values
        val previousCapacity = _capacity

        initializeStorage(newCapacity)

        val newMetadata = metadata
        val newKeys = keys
        val newValues = values
        val capacity = _capacity

        for (i in 0 until previousCapacity) {
            if (isFull(previousMetadata, i)) {
                val previousKey = previousKeys[i]
                val hash = hash(previousKey)
                val index = findFirstAvailableSlot(h1(hash))

                writeMetadata(newMetadata, capacity, index, h2(hash).toLong())
                newKeys[index] = previousKey
                newValues[index] = previousValues[i]
            }
        }
    }

    /**
     * Writes the "H2" part of an entry into the metadata array at the specified
     * [index]. The index must be a valid index. This function ensures the
     * metadata is also written in the clone area at the end.
     */
    private inline fun writeMetadata(index: Int, value: Long) {
        val m = metadata
        writeRawMetadata(m, index, value)

        // Mirroring
        val c = _capacity
        val cloneIndex = ((index - ClonedMetadataCount) and c) +
            (ClonedMetadataCount and c)
        writeRawMetadata(m, cloneIndex, value)
    }
}
