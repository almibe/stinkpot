/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.libraryweasel.ligature

import kotlinx.coroutines.flow.Flow

sealed class Object
data class Entity(val identifier: String): Object() {
    init {
        require(validIdentifier(identifier)) {
            "Invalid identifier: $identifier"
        }
    }
}
sealed class Literal: Object()
data class LangLiteral(val value: String, val langTag: String): Literal() {
    init {
        require(validLangTag(langTag)) {
            "Invalid lang tag: $langTag"
        }
    }
}
data class StringLiteral(val value: String): Literal()
data class BooleanLiteral(val value: Boolean): Literal()
data class LongLiteral(val value: Long): Literal()
data class DoubleLiteral(val value: Double): Literal()

data class Predicate(val identifier: String) {
    init {
        require(validIdentifier(identifier)) {
            "Invalid identifier: $identifier"
        }
    }
}

val a = Predicate("_a")
val default = Entity("_")

data class Statement(val subject: Entity, val predicate: Predicate, val `object`: Object, val context: Entity)

sealed class Range<T>(open val start: T, open val end: T)
data class LangLiteralRange(override val start: LangLiteral, override val end: LangLiteral): Range<LangLiteral>(start, end)
data class StringLiteralRange(override val start: String, override val end: String): Range<String>(start, end)
data class LongLiteralRange(override val start: Long, override val end: Long): Range<Long>(start, end)
data class DoubleLiteralRange(override val start: Double, override val end: Double): Range<Double>(start, end)

interface LigatureStore {
    fun readTx(): ReadTx
    fun writeTx(): WriteTx

    /**
     * Close connection with the Store.
     */
    fun close()
}

interface ReadTx {
    /**
     * Returns a Flow of all existing collections.
     */
    fun allCollections(): Flow<Entity>

    /**
     * Returns a collection based on the name passed.
     * Calling this function will not create a new collection, it just binds a Store and Collection name.
     */
    fun collectionReadTx(collectionName: Entity): CollectionReadTx

    /**
     * Cancels this transaction.
     */
    fun cancel()
}

interface WriteTx: ReadTx {
    /**
     * Deletes the collection of the name given and does nothing if the collection doesn't exist.
     */
    fun deleteCollection(collectionName: Entity)

    /**
     * Creates a new collection or does nothing if collection already exists.
     * Regardless the collection is returned.
     */
    fun collectionWriteTx(collectionName: Entity): CollectionWriteTx

    /**
     * Commits this transaction.
     */
    fun commit()
}

interface CollectionReadTx {
    val collectionName: Entity
    /**
     * Accepts nothing but returns a Flow of all Statements in the Collection.
     */
    fun allStatements(): Flow<Statement>

    /**
     * Is passed a pattern and returns a seq with all matching Statements.
     */
    fun matchStatements(subject: Entity? = null, predicate: Predicate? = null, `object`: Object? = null, context: Entity? = null): Flow<Statement>

    /**
     * Is passed a pattern and returns a seq with all matching Statements.
     */
    fun matchStatements(subject: Entity? = null, predicate: Predicate? = null, range: Range<*>, context: Entity? = null): Flow<Statement>
}

interface CollectionWriteTx: CollectionReadTx {
    /**
     * Returns a new, unique to this collection identifier in the form _:NUMBER"
     */
    fun newEntity(): Entity
    fun addStatement(statement: Statement)
    fun removeStatement(statement: Statement)
}

/**
 * Accepts a String representing an identifier and returns true or false depending on if it is valid.
 */
fun validIdentifier(identifier: String): Boolean {
    return "[a-zA-Z_][^\\s\\(\\)\\[\\]\\{\\}'\"`<>\\\\]*".toRegex().matches(identifier)
}

/**
 * Accepts a String representing a lang tag and returns true or false depending on if it is valid.
 */
fun validLangTag(langTag: String): Boolean {
    return "[a-zA-Z]+(-[a-zA-Z0-9]+)*".toRegex().matches(langTag)
}
