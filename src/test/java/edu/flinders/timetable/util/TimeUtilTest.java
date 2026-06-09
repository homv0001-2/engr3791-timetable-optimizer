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
    @Tag("Core")
    @DisplayName("TU19.01 - overlaps detects overlapping time ranges correctly")
    void tu201OverlapsBehaviour() {

        LocalTime aStart = LocalTime.of(9, 0);
        LocalTime aEnd = LocalTime.of(10, 0);

        LocalTime bStartOverlap = LocalTime.of(9, 30);
        LocalTime bEndOverlap = LocalTime.of(10, 30);

        LocalTime bStartNoOverlap = LocalTime.of(10, 0);
        LocalTime bEndNoOverlap = LocalTime.of(11, 0);

        assertAll(
                () -> assertTrue(TimeUtil.overlaps(aStart, aEnd, bStartOverlap, bEndOverlap)),
                () -> assertTrue(TimeUtil.overlaps(bStartOverlap, bEndOverlap, aStart, aEnd)),
                () -> assertFalse(TimeUtil.overlaps(aStart, aEnd, bStartNoOverlap, bEndNoOverlap))
        );
    }

    @Test
    @Tag("Henry")
    @Tag("Core")
    @DisplayName("TU19.02 - overlaps returns false when times just touch but do not overlap")
    void tu202OverlapsBoundaryBehaviour() {

        LocalTime aStart = LocalTime.of(9, 0);
        LocalTime aEnd = LocalTime.of(10, 0);

        LocalTime bStart = LocalTime.of(10, 0);
        LocalTime bEnd = LocalTime.of(11, 0);

        assertFalse(TimeUtil.overlaps(aStart, aEnd, bStart, bEnd));
    }

    @Test
    @Tag("Henry")
    @Tag("Core")
    @DisplayName("TU19.03 - minutesBetween calculates correct duration difference")
    void tu203MinutesBetweenBehaviour() {

        LocalTime firstEnd = LocalTime.of(10, 0);
        LocalTime secondStart = LocalTime.of(10, 30);

        LocalTime earlierEnd = LocalTime.of(9, 15);
        LocalTime laterStart = LocalTime.of(10, 45);

        assertAll(
                () -> assertEquals(30, TimeUtil.minutesBetween(firstEnd, secondStart)),
                () -> assertEquals(90, TimeUtil.minutesBetween(earlierEnd, laterStart))
        );
    }

    @Test
    @Tag("Henry")
    @Tag("Additional")
    @DisplayName("TU19.04 - minutesBetween supports negative durations")
    void tu204MinutesBetweenNegativeBehaviour() {

        LocalTime firstEnd = LocalTime.of(11, 0);
        LocalTime secondStart = LocalTime.of(10, 30);

        assertTrue(TimeUtil.minutesBetween(firstEnd, secondStart) < 0);
    }
}