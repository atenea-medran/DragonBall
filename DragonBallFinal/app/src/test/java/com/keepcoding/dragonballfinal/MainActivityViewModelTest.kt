package com.keepcoding.dragonballfinal

import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class MainActivityViewModelTest {

    private val viewModel = MainActivityViewModel()

    @Test
    fun `test user valid`() {
        with(viewModel) {
            assertTrue(isUserValid("1234@."))
            assertTrue(isUserValid("@."))
            assertTrue(isUserValid(".@"))
            assertFalse(isUserValid("1234"))
            assertFalse(isUserValid("1234@"))
            assertFalse(isUserValid("1234."))
            assertFalse(isUserValid(""))
        }
    }

    @Test
    fun `test pass valid`() {
        with(viewModel) {
            assertTrue(isPassValid("1234"))
            assertTrue(isPassValid("abcd"))
            assertFalse(isPassValid("abc"))
            assertFalse(isPassValid("123"))
            assertFalse(isPassValid(""))
        }
    }
}