/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.libraryweasel.stinkpot

import org.testng.Assert
import org.testng.annotations.Test

class TurtleTests {
    val stinkpot = Stinkpot()

    val spidermanEnemy = Triple(IRI("http://example.org/#spiderman"),
    IRI("http://www.perceive.net/schemas/relationship/enemyOf"), IRI("http://example.org/#green-goblin"))

    val spidermanName = Triple(IRI("http://example.org/#spiderman"),
    IRI("http://xmlns.com/foaf/0.1/name"), TypedLiteral("Spiderman", IRI("http://www.w3.org/2001/XMLSchema#string")))

    val spidermanNameRu = Triple(IRI("http://example.org/#spiderman"),
    IRI("http://xmlns.com/foaf/0.1/name"), LangLiteral("Человек-паук", "ru"))

    @Test fun supportBasicIRITriple() {
        val expectedResult = spidermanEnemy
        val results = stinkpot.parseTurtle(this.javaClass.getResource("/turtle/basicTriple.ttl").readText())
        results.size == 1
        results.first() == expectedResult
    }

    @Test fun supportPredicateLists() {
        val expectedResults = listOf(spidermanEnemy, spidermanName)
        val results = stinkpot.parseTurtle(this.javaClass.getResource("/turtle/predicateList.ttl").readText())
        results.size == 2
        results == expectedResults
    }

    @Test fun supportObjectLists() {
        val expectedResults = listOf(spidermanName, spidermanNameRu)
        val results = stinkpot.parseTurtle(this.javaClass.getResource("/turtle/objectList.ttl").readText())
        results.size == 2
        results == expectedResults
    }

    @Test fun supportComments() {
        val expectedResults = listOf(spidermanEnemy, spidermanName)
        val results = stinkpot.parseTurtle(this.javaClass.getResource("/turtle/comments.ttl").readText())
        results.size == 2
        results == expectedResults
    }

    @Test fun supportMultilineTriples() {
        val expectedResults = listOf(spidermanEnemy)
        val results = stinkpot.parseTurtle(this.javaClass.getResource("/turtle/multilineTriple.ttl").readText())
        results.size == 1
        results == expectedResults
    }

    val base = "http://one.example/"
    val base2 = "http://one.example2/"
    val baseTwo = "http://two.example/"
    val baseTwo2 = "http://two.example2/"

    val base3 = "http://another.example/"

    @Test fun turtleIRIParsing() {
        val expectedResults = listOf(
        Triple(IRI("http://one.example/subject1"), IRI("http://one.example/predicate1"), IRI("http://one.example/object1")),
        Triple(IRI("${base}subject2"), IRI("${base}predicate2"), IRI("${base}object2")),
        Triple(IRI("${base2}subject2"), IRI("${base2}predicate2"), IRI("${base2}object2")),
        Triple(IRI("${baseTwo}subject3"), IRI("${baseTwo}predicate3"), IRI("${baseTwo}object3")),
        Triple(IRI("${baseTwo2}subject3"), IRI("${baseTwo2}predicate3"), IRI("${baseTwo2}object3")),
        Triple(IRI("${base2}path/subject4"), IRI("${base2}path/predicate4"), IRI("${base2}path/object4")),
        Triple(IRI("${base3}subject5"), IRI("${base3}predicate5"), IRI("${base3}object5")),
        Triple(IRI("${base3}subject6"), IRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), IRI("${base3}subject7")),
        Triple(IRI("http://伝言.example/?user=أكرم&amp;channel=R%26D"), IRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), IRI("${base3}subject8"))
        )
        val results = stinkpot.parseTurtle(this.javaClass.getResource("/turtle/comprehensivePrefixBaseExample.ttl").readText())
        Assert.assertEquals(results, expectedResults)
    }

    //TODO test literals
    @Test fun supportLanguageLiterals() {
        val expectedResults = listOf(spidermanNameRu)
        val results = stinkpot.parseTurtle(this.javaClass.getResource("/turtle/literalWithLanguage.ttl").readText())
        Assert.assertEquals(results, expectedResults)
    }

    @Test fun supportQuotedLiterals() {
        val base = "http://www.libraryweasel.org/fake/madeup#"
        val show = IRI("http://example.org/vocab/show/218")
        val label = IRI("http://www.w3.org/2000/01/rdf-schema#label")
        val localName = IRI("http://example.org/vocab/show/localName")
        val blurb = IRI("http://example.org/vocab/show/blurb")
        val multilineText = """This is a multi-line
        literal with many quotes (\"\"\"\"\")
and up to two sequential apostrophes ('')."""
        val expectedResults = listOf(
            Triple(show, label, TypedLiteral("That Seventies Show")),
            Triple(show, label, TypedLiteral("That Seventies Show")),
            Triple(show, label, TypedLiteral("That Seventies Show")),
            Triple(show, IRI("${base}pred"), TypedLiteral("That Seventies Show", IRI("${base}string"))),
            Triple(show, localName, LangLiteral("That Seventies Show", "en")),
            Triple(show, localName, LangLiteral("Cette Série des Années Soixante-dix", "fr")),
            Triple(show, localName, LangLiteral("Cette Série des Années Septante", "fr-be")),
            Triple(show, blurb, TypedLiteral(multilineText))
            //Triple(show, blurb, TypedLiteral(multilineText)),
            //Triple(show, blurb, TypedLiteral(multilineText))
        )
        val results = stinkpot.parseTurtle(this.javaClass.getResource("/turtle/quotedLiterals.ttl").readText())
        Assert.assertEquals(results, expectedResults)
    }

    //TODO numbers.ttl
    //TODO booleans.ttl

    //TODO test blank nodes
    //TODO blankNodes.ttl

    //TODO Nesting Unlabeled Blank Nodes in Turtle
    //TODO nestedUnlabeledBlankNodes.ttl
    //TODO complexUnlabeledBlankNodes.ttl

    //TODO Collections
    //TODO collections.ttl

    //TODO examples 19-26 and wordnetStinkpot.ttl
}