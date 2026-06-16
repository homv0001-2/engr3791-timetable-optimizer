package edu.flinders.timetable.result;

import edu.flinders.timetable.model.Timetable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class ResultObjectsTest {

    @Test
    @Tag("Sunny")
    @Tag("Additional")
    @DisplayName("RO11.01 - Import result counts.")
    void importResultCountsAndErrors() {
        // this creates an empty import result object.
        ImportResult result = new ImportResult();

        // this manually updates the result like the import process would.
        result.incrementNewRecordCount();
        result.incrementUpdatedRecordCount();
        result.addError("bad row");

        // this checks that the result object stores counts and errors correctly.
        assertAll(
                () -> assertEquals(1, result.getNewRecordCount()),
                () -> assertEquals(1, result.getUpdatedRecordCount()),
                () -> assertTrue(result.hasErrors()),
                () -> assertEquals("bad row", result.getErrors().get(0))
        );
    }

    @Test
    @Tag("Sunny")
    @Tag("Additional")
    @DisplayName("RO11.02 - Failure message.")
    void timetableGenerationFailureStoresMessage() {
        // this creates a failed timetable generation result.
        TimetableGenerationResult result = TimetableGenerationResult.failure("No matching classes");

        // this verifies that failure state stores no timetable and keeps the message.
        assertAll(
                () -> assertFalse(result.isSuccess()),
                () -> assertNull(result.getTimetable()),
                () -> assertEquals("No matching classes", result.getMessage())
        );
    }

    @Test
    @Tag("Sunny")
    @Tag("Additional")
    @DisplayName("RO11.03 - Success object.")
    void timetableGenerationSuccessStoresTimetable() {
        // this creates a simple timetable.
        Timetable timetable = new Timetable("Success Test");

        // this creates a successful generation result using the timetable.
        TimetableGenerationResult result = TimetableGenerationResult.success(timetable, List.of());

        // this verifies that success stores the timetable and no warnings.
        assertAll(
                () -> assertTrue(result.isSuccess()),
                () -> assertEquals(timetable, result.getTimetable()),
                () -> assertTrue(result.getWarnings().isEmpty())
        );
    }
}