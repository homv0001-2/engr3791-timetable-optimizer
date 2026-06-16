package edu.flinders.timetable.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class TimeUtilTest {

    @Test
    @Tag("Henry")
    @Tag("Additional")
    @DisplayName("TU19.01 - Detect overlaps.")
    void overlapsBehaviour() {

        // define a time range from 9:00 to 10:00
        LocalTime aStart = LocalTime.of(9, 0);
        LocalTime aEnd = LocalTime.of(10, 0);

        // overlapping range from 9:30 to 10:30
        LocalTime bStartOverlap = LocalTime.of(9, 30);
        LocalTime bEndOverlap = LocalTime.of(10, 30);

        // non-overlapping range from 10:00 to 11:00
        LocalTime bStartNoOverlap = LocalTime.of(10, 0);
        LocalTime bEndNoOverlap = LocalTime.of(11, 0);

        // verifies that overlaps are correctly detected
        assertAll(
                () -> assertTrue(TimeUtil.overlaps(aStart, aEnd, bStartOverlap, bEndOverlap)),
                () -> assertTrue(TimeUtil.overlaps(bStartOverlap, bEndOverlap, aStart, aEnd)),
                () -> assertFalse(TimeUtil.overlaps(aStart, aEnd, bStartNoOverlap, bEndNoOverlap))
        );
    }

    @Test
    @Tag("Henry")
    @Tag("Additional")
    @DisplayName("TU19.02 - Touching times safe.")
    void overlapsBoundaryBehaviour() {

        // define a range from 9:00 to 10:00
        LocalTime aStart = LocalTime.of(9, 0);
        LocalTime aEnd = LocalTime.of(10, 0);

        // another range that starts exactly at the previous end time
        LocalTime bStart = LocalTime.of(10, 0);
        LocalTime bEnd = LocalTime.of(11, 0);

        // verifies that touching intervals do not count as overlapping
        assertFalse(TimeUtil.overlaps(aStart, aEnd, bStart, bEnd));
    }

    @Test
    @Tag("Henry")
    @Tag("Core")
    @DisplayName("TU19.03 - Minutes between.")
    void minutesBetweenBehaviour() {

        // example ranges to calculate positive minute differences
        LocalTime firstEnd = LocalTime.of(10, 0);
        LocalTime secondStart = LocalTime.of(10, 30);

        LocalTime earlierEnd = LocalTime.of(9, 15);
        LocalTime laterStart = LocalTime.of(10, 45);

        // verifies that minutes between two times are computed correctly
        assertAll(
                () -> assertEquals(30, TimeUtil.minutesBetween(firstEnd, secondStart)),
                () -> assertEquals(90, TimeUtil.minutesBetween(earlierEnd, laterStart))
        );
    }

    @Test
    @Tag("Henry")
    @Tag("Additional")
    @DisplayName("TU19.04 - Negative duration.")
    void minutesBetweenNegativeBehaviour() {

        // example where first time is after the second time
        LocalTime firstEnd = LocalTime.of(11, 0);
        LocalTime secondStart = LocalTime.of(10, 30);

        // verifies that negative durations are returned when appropriate
        assertTrue(TimeUtil.minutesBetween(firstEnd, secondStart) < 0);
    }
}