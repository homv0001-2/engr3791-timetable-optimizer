package edu.flinders.timetable.presentation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class InputHandlerTest {

    private InputHandler handler(String input) {
        // this creates an InputHandler that reads from simulated user input.
        return new InputHandler(new Scanner(new ByteArrayInputStream(input.getBytes())));
    }

    @Test
    @Tag("Sunny")
    @Tag("Additional")
    @DisplayName("IH10.01 - Trim input line.")
    void readLineTrim() {
        // this simulates a user entering text with leading and trailing spaces.
        InputHandler h = handler("  hello  \n");

        // this verifies that readLine trims whitespace before returning the value.
        assertEquals("hello", h.readLine("Prompt: "));
    }

    @Test
    @Tag("Sunny")
    @Tag("Additional")
    @DisplayName("IH10.02 - Read valid integer.")
    void readIntValid() {
        // this simulates a user entering a valid integer.
        InputHandler h = handler("42\n");

        // this verifies that the integer is parsed and returned correctly.
        assertEquals(42, h.readInt("Prompt: ", -1));
    }

    @Test
    @Tag("Sunny")
    @Tag("Additional")
    @DisplayName("IH10.03 - Blank integer default.")
    void readIntBlank() {
        // this simulates a user pressing enter without entering a value.
        InputHandler h = handler("\n");

        // this verifies that the default value is returned for blank input.
        assertEquals(-1, h.readInt("Prompt: ", -1));
    }

    @Test
    @Tag("Sunny")
    @Tag("Additional")
    @DisplayName("IH10.04 - Invalid integer default.")
    void readIntInvalid() {
        // this simulates a user entering a non-numeric value.
        InputHandler h = handler("abc\n");

        // this verifies that the default value is returned for invalid input.
        assertEquals(-1, h.readInt("Prompt: ", -1));
    }

    @Test
    @Tag("Sunny")
    @Tag("Additional")
    @DisplayName("IH10.05 - Confirm yes.")
    void confirmYes() {
        // this simulates a user entering a short confirmation response.
        InputHandler h = handler("y\n");

        // this verifies that confirmation succeeds for "y".
        assertTrue(h.confirm("Confirm?"));
    }

    @Test
    @Tag("Sunny")
    @Tag("Additional")
    @DisplayName("IH10.06 - Confirm yes word.")
    void confirmYesFull() {
        // this simulates a user entering the full confirmation word.
        InputHandler h = handler("yes\n");

        // this verifies that confirmation succeeds for "yes".
        assertTrue(h.confirm("Confirm?"));
    }

    @Test
    @Tag("Sunny")
    @Tag("Additional")
    @DisplayName("IH10.07 - Reject no.")
    void confirmNo() {
        // this simulates a user rejecting confirmation.
        InputHandler h = handler("no\n");

        // this verifies that confirmation returns false.
        assertFalse(h.confirm("Confirm?"));
    }

    @Test
    @Tag("Sunny")
    @Tag("Additional")
    @DisplayName("IH10.08 - Reject blank.")
    void confirmBlank() {
        // this simulates a user providing no confirmation input.
        InputHandler h = handler("\n");

        // this verifies that blank input is treated as a negative response.
        assertFalse(h.confirm("Confirm?"));
    }

    @Test
    @Tag("Sunny")
    @Tag("Additional")
    @DisplayName("IH10.09 - Parse CSV values.")
    void csvValues() {
        // this simulates comma-separated input with spaces and empty entries.
        InputHandler h = handler("A, B , , C\n");

        // this reads the values entered by the user.
        Set<String> values = h.readCsvValues("Enter: ");

        // this verifies that values are trimmed, split correctly,
        // and empty entries are ignored.
        assertEquals(3, values.size());
        assertTrue(values.contains("A"));
        assertTrue(values.contains("B"));
        assertTrue(values.contains("C"));
    }

    @Test
    @Tag("Sunny")
    @Tag("Additional")
    @DisplayName("IH10.10 - Blank CSV is empty.")
    void csvBlank() {
        // this simulates a user entering no CSV values.
        InputHandler h = handler("\n");

        // this reads the CSV values.
        Set<String> values = h.readCsvValues("Enter: ");

        // this verifies that an empty set is returned.
        assertTrue(values.isEmpty());
    }
}