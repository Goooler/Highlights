package dev.snipme.highlights.internal

import dev.snipme.highlights.model.PhraseLocation
import dev.snipme.highlights.model.SyntaxLanguage
import kotlin.test.Test
import kotlin.test.assertEquals

internal class CodeAnalyzerTest {

    @Test
    fun `Returns structure of code analyzed first time`() {
        val testCode = """
            /** a */
            // b
            class C extends {}
            "d";
            @E
            ...
            123.00f
        """.trimIndent()

        val result = CodeAnalyzer.analyze(testCode)

        assertEquals(
            listOf(
                PhraseLocation(30, 31),
                PhraseLocation(31, 32)
            ),
            result.marks
        )

        assertEquals(
            listOf(
                PhraseLocation(36, 37),
                PhraseLocation(41, 42),
                PhraseLocation(42, 43),
                PhraseLocation(43, 44),
                PhraseLocation(48, 49),
            ),
            result.punctuations
        )

        assertEquals(
            listOf(
                PhraseLocation(14, 19),
                PhraseLocation(22, 29)
            ),
            result.keywords
        )

        assertEquals(
            listOf(
                PhraseLocation(33, 36),
            ),
            result.strings
        )

        assertEquals(
            listOf(
                PhraseLocation(45, 52),
            ),
            result.literals
        )

        assertEquals(
            listOf(
                PhraseLocation(9, 13),
            ),
            result.comments
        )

        assertEquals(
            listOf(
                PhraseLocation(0, 8),
            ),
            result.multilineComments
        )

        assertEquals(
            listOf(
                PhraseLocation(38, 40),
            ),
            result.annotations
        )

        assertEquals(false, result.incremental)
    }

    @Test
    fun `Returns incremental structure of code analyzed second time`() {
        val testCode = """
            /** a */
            // b
            class C extends {}
            "d";
            @E
            ...
            123.00f
        """.trimIndent()

        val firstResult = CodeAnalyzer.analyze(testCode)
        assertEquals(false, firstResult.incremental)

        val secondTestCode = """
            /** a */
            // b
            class C extends {}
            "d";
            @E
            ...
            123.00f
            
            class
        """.trimIndent()

        val snapshot = CodeSnapshot(testCode, firstResult, SyntaxLanguage.DEFAULT)

        val result = CodeAnalyzer.analyze(secondTestCode, snapshot = snapshot)
        assertEquals(true, result.incremental)

        assertEquals(
            listOf(
                PhraseLocation(30, 31),
                PhraseLocation(31, 32),
            ),
            result.marks
        )

        assertEquals(
            listOf(
                PhraseLocation(36, 37),
                PhraseLocation(41, 42),
                PhraseLocation(42, 43),
                PhraseLocation(43, 44),
                PhraseLocation(48, 49),
            ),
            result.punctuations
        )

        assertEquals(
            listOf(
                PhraseLocation(14, 19),
                PhraseLocation(22, 29),
            ),
            result.keywords
        )

        assertEquals(
            listOf(
                PhraseLocation(33, 36),
            ),
            result.strings
        )

        assertEquals(
            listOf(
                PhraseLocation(45, 52),
            ),
            result.literals
        )

        assertEquals(
            listOf(
                PhraseLocation(9, 13),
            ),
            result.comments
        )

        assertEquals(
            listOf(
                PhraseLocation(0, 8),
            ),
            result.multilineComments
        )

        assertEquals(
            listOf(
                PhraseLocation(38, 40),
            ),
            result.annotations
        )
    }

    @Test
    fun `Returns incremental structure of decreased code analyzed second time`() {
        val testCode = """
            /** a */
            // b
            class C extends {}
            "d";
            @E
            ...
            123.00f
        """.trimIndent()

        val firstResult = CodeAnalyzer.analyze(testCode)
        assertEquals(false, firstResult.incremental)

        val secondTestCode = """
            /** a */
            // b
            class 
        """.trimIndent()

        val snapshot = CodeSnapshot(testCode, firstResult, SyntaxLanguage.DEFAULT)

        val result = CodeAnalyzer.analyze(secondTestCode, snapshot = snapshot)
        assertEquals(true, result.incremental)

        assertEquals(
            emptyList(),
            result.marks
        )

        assertEquals(
            emptyList(),
            result.punctuations
        )

        assertEquals(
            listOf(
                PhraseLocation(14, 19),
            ),
            result.keywords
        )

        assertEquals(
            emptyList(),
            result.strings
        )

        assertEquals(
            emptyList(),
            result.literals
        )

        assertEquals(
            listOf(
                PhraseLocation(9, 13),
            ),
            result.comments
        )

        assertEquals(
            listOf(
                PhraseLocation(0, 8),
            ),
            result.multilineComments
        )

        assertEquals(
            emptyList(),
            result.annotations
        )
    }
}