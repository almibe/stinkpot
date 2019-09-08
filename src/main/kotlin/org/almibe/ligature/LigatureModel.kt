/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.almibe.ligature

import java.io.Closeable
import java.math.BigDecimal
import java.util.stream.Stream

sealed class Value
data class Node(val label: String, val types: Collection<String>): Value()

sealed class Literal: Value()
data class LangLiteral(val value: String, val langTag: String) : Literal()
data class StringLiteral(val value: String) : Literal()
data class BooleanLiteral(val value: Boolean): Literal()
data class IntLiteral(val value: Int) : Literal()
data class LongLiteral(val value: Long) : Literal()
data class DecimalLiteral(val value: BigDecimal) : Literal()

data class Attribute(val label: String)

data class Statement(
        val entity: Node,
        val attribute: Attribute,
        val value: Value,
        val context: Node? = null
)

interface Store: Closeable {
    fun getDatasetNames(): Stream<String>
    fun getDataset(name: String): Dataset
    fun deleteDataset(name: String)
    override fun close()
}

interface Dataset {
    fun getDatasetName(): String
    fun addStatements(statements: Stream<Statement>)
    fun removeStatements(statements: Stream<Statement>)
    fun matchAll(
            entity: Node? = null,
            attribute: Attribute? = null,
            value: Value? = null,
            context: Node? = null
    ): Stream<Statement>
    fun allStatements(): Stream<Statement>
    fun allNodes(types: Collection<String> = listOf()): Stream<Node>
    fun allAttributes(): Stream<Attribute>
    fun allLiterals(): Stream<Literal>
    fun allTypes(): Stream<String>
    fun newNode(types: Collection<String> = listOf()): Node
    fun relabelNode(node: Node, label: String?)
    fun deleteNode(node: Node)
    fun nodeTypes(node: Node): Collection<String>
    fun addNodeTypes(node: Node, types: Collection<String>)
    fun removeNodeTypes(node: Node, types: Collection<String>)
}
