package edu.flinders.timetable.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class TextUtilTest {

    @Test
    @Tag("Henry")
    @Tag("Additional")
    @DisplayName("TX18.01 - Detect blank text.")
    void isBlankBehaviour() {

        // verifies null, empty, whitespace, and normal text cases
        assertAll(
                () -> assertTrue(TextUtil.isBlank(null)),
                () -> assertTrue(TextUtil.isBlank("")),
                () -> assertTrue(TextUtil.isBlank("   ")),
                () -> assertFalse(TextUtil.isBlank("text"))
        );
    }

    @Test
    @Tag("Henry")
    @Tag("Additional")
    @DisplayName("TX18.02 - Clean whitespace.")
    void cleanBehaviour() {

        // verifies null handling and whitespace trimming
        assertAll(
                () -> assertEquals("", TextUtil.clean(null)),
                () -> assertEquals("text", TextUtil.clean("  text  ")),
                () -> assertEquals("a", TextUtil.clean(" a "))
        );
    }

    @Test
    @Tag("Henry")
    @Tag("Additional")
    @DisplayName("TX18.03 - Contains ignore case.")
    void containsIgnoreCaseBehaviour() {

        // verifies case-insensitive matching and whitespace tolerance
        assertAll(
                () -> assertTrue(TextUtil.containsIgnoreCase("Hello World", "hello")),
                () -> assertTrue(TextUtil.containsIgnoreCase("Hello World", "WORLD")),
                () -> assertTrue(TextUtil.containsIgnoreCase("Hello World", "  world  ")),
                () -> assertTrue(TextUtil.containsIgnoreCase("Hello World", null)), // blank search = true
                () -> assertTrue(TextUtil.containsIgnoreCase("Hello World", "")),
                () -> assertFalse(TextUtil.containsIgnoreCase(null, "hello"))
        );
    }

    @Test
    @Tag("Henry")
    @Tag("Additional")
    @DisplayName("TX18.04 - Equals ignore case.")
    void equalsIgnoreCaseBehaviour() {

        // verifies trimming, null handling, and case-insensitive equality
        assertAll(
                () -> assertTrue(TextUtil.equalsIgnoreCase("Test", "test")),
                () -> assertTrue(TextUtil.equalsIgnoreCase("  Test  ", "test")),
                () -> assertTrue(TextUtil.equalsIgnoreCase(null, null)),
                () -> assertFalse(TextUtil.equalsIgnoreCase("Test", null)),
                () -> assertFalse(TextUtil.equalsIgnoreCase(null, "Test")),
                () -> assertFalse(TextUtil.equalsIgnoreCase("Test1", "Test2"))
        );
    }

    @Test
    @Tag("Henry")
    @Tag("Additional")
    @DisplayName("TX18.05 - First non-blank.")
    void firstNonBlankBehaviour() {

        // verifies fallback behaviour when first value is blank or null
        assertAll(
                () -> assertEquals("new", TextUtil.firstNonBlank("new", "old")),
                () -> assertEquals("old", TextUtil.firstNonBlank(null, "old")),
                () -> assertEquals("old", TextUtil.firstNonBlank("   ", "old")),
                () -> assertEquals("new", TextUtil.firstNonBlank("  new  ", "old"))
        );
    }
}