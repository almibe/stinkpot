/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.almibe.ligature

interface Subject
interface Predicate
interface Object

data class IRI(val value: String) : Subject, Predicate, Object
interface BlankNode : Subject, Object
data class LabeledBlankNode(val label: String) : BlankNode
class UnlabeledBlankNode: BlankNode
data class Triple(val subject: Subject, val predicate: Predicate, val `object`: Object)

interface Literal : Object { val value: String}
data class LangLiteral(override val value: String, val langTag: String) : Literal
data class TypedLiteral(override val value: String,
                        val datatypeIRI: IRI = IRI("http://www.w3.org/2001/XMLSchema#string")) : Literal
