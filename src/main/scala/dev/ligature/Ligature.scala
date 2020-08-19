/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature

import cats.effect.{IO, Resource}

import scala.util.Try

sealed trait Element
sealed trait Entity extends Element
case class NamedElement(identifier: String) extends Entity
case class AnonymousEntity(identifier: Long) extends Entity
sealed trait Literal extends Element
sealed trait RangeLiteral extends Literal
case class Range[T <: RangeLiteral, U <: RangeLiteral](start: T, end: U)(implicit ev: T =:= U)
case class LangLiteral(value: String, langTag: String) extends RangeLiteral
case class StringLiteral(value: String) extends RangeLiteral
case class BooleanLiteral(value: Boolean) extends Literal
case class LongLiteral(value: Long) extends RangeLiteral
case class DoubleLiteral(value: Double) extends RangeLiteral

object Ligature {
  val a: NamedElement = NamedElement("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")

  /**
   * Accepts a String representing an identifier and returns true or false depending on if it is valid.
   */
  def validNamedEntity(identifier: String): Boolean =
    "[a-zA-Z_][^\\s()\\[\\]{}'\"`<>\\\\]*".r.matches(identifier)

  /**
   * Accepts a String representing a lang tag and returns true or false depending on if it is valid.
   */
  def validLangTag(langTag: String): Boolean =
    "[a-zA-Z]+(-[a-zA-Z0-9]+)*".r.matches(langTag)
}

case class Statement(subject: Entity, predicate: NamedElement, `object`: Element)
case class PersistedStatement(collection: NamedElement, statement: Statement, context: AnonymousEntity)

trait Ligature {
  def start(): Resource[IO, LigatureSession]
}

trait LigatureSession {
  def compute: Resource[IO, ReadTx]
  def write: Resource[IO, WriteTx]
}

trait ReadTx {
  /**
   * Returns a Iterable of all existing collections.
   */
  def collections: IO[Iterator[NamedElement]]

  /**
   * Returns a Iterable of all existing collections that start with the given prefix.
   */
  def collections(prefix: NamedElement): IO[Iterator[NamedElement]]

  /**
   * Returns a Iterable of all existing collections that are within the given range.
   * `from` is inclusive and `to` is exclusive.
   */
  def collections(from: NamedElement, to: NamedElement): IO[Iterator[NamedElement]]

  /**
   * Accepts nothing but returns a Iterable of all Statements in the Collection.
   */
  def allStatements(collection: NamedElement): IO[Iterator[PersistedStatement]]

  /**
   * Is passed a pattern and returns a seq with all matching Statements.
   */
  def matchStatements(collection: NamedElement,
                      subject: Option[Entity] = None,
                      predicate: Option[NamedElement] = None,
                      `object`: Option[Element] = None): IO[Iterator[PersistedStatement]]

//  /**
//   * Is passed a pattern and returns a seq with all matching Statements.
//   */
//  def matchStatements(collection: NamedEntity,
//                      subject: Option[Entity],
//                      predicate: Option[Predicate],
//                      range: Range[_, _]): IO[Any, Throwable, Iterable[PersistedStatement]]

  /**
   * Returns the Statement with the given context.
   * Returns None if the context doesn't exist.
   */
  def statementByContext(collection: NamedElement, context: AnonymousEntity): IO[Option[PersistedStatement]]

  def isOpen: Boolean
}

trait WriteTx {
  /**
   * Creates a collection with the given name or does nothing if the collection already exists.
   * Only useful for creating an empty collection.
   */
  def createCollection(collection: NamedElement): IO[Try[NamedElement]]

  /**
   * Deletes the collection of the name given and does nothing if the collection doesn't exist.
   */
  def deleteCollection(collection: NamedElement): IO[Try[NamedElement]]

  /**
   * Returns a new, unique to this collection, AnonymousEntity
   */
  def newEntity(collection: NamedElement): IO[Try[AnonymousEntity]]
  def addStatement(collection: NamedElement, statement: Statement): IO[Try[PersistedStatement]]
//  Commenting out the below as part of #125
//  def removeStatement(collection: NamedEntity, statement: Statement): IO[Any, Throwable, Try[Statement]]
//  def removeEntity(collection: NamedEntity, entity: Entity): IO[Any, Throwable, Try[Entity]]
//  def removePredicate(collection: NamedEntity, predicate: Predicate): IO[Any, Throwable, Try[Predicate]]

  /**
   * Cancels this transaction.
   */
  def cancel(): Unit

  def isOpen: Boolean
}
