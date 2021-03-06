package com.objectpartners.plummer.junit5.version5

import com.objectpartners.plummer.junit5.ApplicationTest
import com.objectpartners.plummer.junit5.STATES
import com.objectpartners.plummer.junit5.StatesService
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
class StatesServiceTest : ApplicationTest() {

    @Autowired lateinit var dataBean: StatesService

    @Test
    fun testAutowiring() {
        assertNotNull(dataBean)
    }

    @Nested
    inner class states() {
        @Nested
        inner class whenRetrievingAll() {

            var values: List<String> = Collections.emptyList()

            @BeforeEach
            fun init() {
                values = dataBean.getAllStates().toList()
            }

            @Test
            fun shouldReturnFiftyValues() {
                assertEquals(values.size, 50)
            }

            @Test
            fun shouldReturnAlphabetizedValues() {
                val sortedValues = ArrayList(values)
                sortedValues.sort()
                assertEquals(values, sortedValues)
            }

            @TestFactory
            fun shouldReturnAllValuesFromDataset(): Collection<DynamicTest> {
                return STATES.map { state ->
                    dynamicTest(state) {
                        assertTrue(values.contains(state))
                    }
                }.toList()
            }

            @AfterEach
            fun cleanup() {
                values = Collections.emptyList()
            }
        }

        @Nested
        @Tag("inputs")
        inner class whenRetrievingByStartingCharacter {
            @Nested
            inner class withMatchingValue() {
                var matches: List<String> = Collections.emptyList()

                @BeforeEach
                fun init() {
                    matches = dataBean.getStatesStartingWith('A')
                }

                @Test
                fun shouldReturnMatchingValues() {
                    assertEquals(matches.size, 4)
                }

                @Test
                fun shouldReturnSortedValues() {
                    val sortedMatches = ArrayList(matches)
                    sortedMatches.sort()
                    assertEquals(sortedMatches, matches)
                }
            }

            @Nested
            inner class withNonMatchingValue() {
                @Test
                fun shouldReturnEmptyList() {
                    val matches: List<String> = dataBean.getStatesStartingWith('!')
                    assertTrue(matches.isEmpty())
                }
            }

            @Nested
            inner class withInvalidValue() {
                @Test
                fun shouldThrowError() {
                    assertThrows(IllegalArgumentException::class.java, { dataBean.getStatesStartingWith(null) })
                }
            }
        }
    }
}
