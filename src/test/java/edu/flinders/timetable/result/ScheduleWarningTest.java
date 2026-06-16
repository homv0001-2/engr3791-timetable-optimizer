package edu.flinders.timetable.result;

import edu.flinders.timetable.model.ClassRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class ScheduleWarningTest {

    @Test
    @Tag("Ryan")
    @Tag("Critical")
    @DisplayName("SWA12.01 - Time clash warning.")
    void timeClashWarning() {
        // first and second class references for the warning
        ClassRecord first = null;
        ClassRecord second = null;

        // create a TIME_CLASH warning
        ScheduleWarning warning = new ScheduleWarning(
                ScheduleWarning.Type.TIME_CLASH,
                first,
                second,
                "Classes overlap in time"
        );

        // verify type, class references, and message
        assertAll(
                () -> assertEquals(ScheduleWarning.Type.TIME_CLASH, warning.getType()),
                () -> assertEquals(first, warning.getFirstClass()),
                () -> assertEquals(second, warning.getSecondClass()),
                () -> assertEquals("Classes overlap in time", warning.getMessage())
        );
    }

    @Test
    @Tag("Ryan")
    @Tag("Critical")
    @DisplayName("SWA12.02 - Commute gap warning.")
    void commuteGapWarning() {
        // first and second class references for the warning
        ClassRecord first = null;
        ClassRecord second = null;

        // create a COMMUTE_GAP warning
        ScheduleWarning warning = new ScheduleWarning(
                ScheduleWarning.Type.COMMUTE_GAP,
                first,
                second,
                "Not enough travel time between classes"
        );

        // verify type, class references, and message
        assertAll(
                () -> assertEquals(ScheduleWarning.Type.COMMUTE_GAP, warning.getType()),
                () -> assertEquals(first, warning.getFirstClass()),
                () -> assertEquals(second, warning.getSecondClass()),
                () -> assertEquals("Not enough travel time between classes", warning.getMessage())
        );
    }

    @Test
    @Tag("Ryan")
    @Tag("Critical")
    @DisplayName("SWA12.03 - Warning toString.")
    void warningToStringFormatsCorrectly() {
        // create a warning to test toString
        ScheduleWarning warning = new ScheduleWarning(
                ScheduleWarning.Type.TIME_CLASH,
                null,
                null,
                "Overlap detected"
        );

        // verify the string representation
        assertEquals("TIME_CLASH: Overlap detected", warning.toString());
    }
}