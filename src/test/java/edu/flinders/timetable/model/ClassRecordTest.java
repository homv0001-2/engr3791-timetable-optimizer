package edu.flinders.timetable.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class ClassRecordTest {

    private ClassRecord record(String classFormat, int instance, LocalDate firstDate, DayOfWeek day, LocalTime start, LocalTime end, String building, String room) {
        // this helper creates a simple ClassRecord so the tests stay shorter and easier to read.
        return new ClassRecord(
                "COMP1701",
                "Game Design",
                "In person",
                "Tonsley",
                2,
                1,
                classFormat,
                instance,
                firstDate,
                firstDate.plusWeeks(7),
                day,
                start,
                end,
                building,
                room
        );
    }

    @Test
    @Tag("homv0001")
    @Tag("Core")
    @DisplayName("CR4.01 - Import key ignores time and location")
    void cr401ImportKeyIgnoresTimeAndLocation() {
        // this is the first record with the original time and location.
        ClassRecord first = record("Workshop", 1, LocalDate.of(2026, 7, 27), DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0), "Old Building", "Old Room");

        // this is almost the same record, but time and location are different.
        ClassRecord second = record("Workshop", 1, LocalDate.of(2026, 7, 27), DayOfWeek.MONDAY, LocalTime.of(11, 0), LocalTime.of(12, 0), "New Building", "New Room");

        // this checks that both records have the same import key, because imports should update time/location instead of duplicating.
        assertEquals(first.importKey(), second.importKey());
    }

    @Test
    @Tag("homv0001")
    @Tag("Core")
    @DisplayName("CR4.02 - Group key ignores date day time and location")
    void cr402GroupKeyIgnoresDateDayTimeAndLocation() {
        // this is the first class record in a group.
        ClassRecord first = record("Workshop", 1, LocalDate.of(2026, 7, 27), DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0), "Building A", "Room A");

        // this record has the same topic/class/instance, but a different date, day, time, and location.
        ClassRecord second = record("Workshop", 1, LocalDate.of(2026, 10, 5), DayOfWeek.FRIDAY, LocalTime.of(12, 0), LocalTime.of(13, 0), "Building B", "Room B");

        // this checks that the records are in the same browse group, but they are not the exact same imported row.
        assertAll(
                () -> assertEquals(first.groupKey(), second.groupKey()),
                () -> assertNotEquals(first.importKey(), second.importKey())
        );
    }

    @Test
    @Tag("homv0001")
    @Tag("Core")
    @DisplayName("CR4.03 - Copy creates separate class record")
    void cr403CopyCreatesSeparateClassRecord() {
        // this creates the original class record.
        ClassRecord original = record("Tutorial", 2, LocalDate.of(2026, 7, 27), DayOfWeek.WEDNESDAY, LocalTime.of(11, 0), LocalTime.of(12, 0), "Tonsley T1", "1.08");

        // this makes a copy of the record.
        ClassRecord copy = original.copy();

        // this changes only the copy, not the original.
        copy.setRoom("Changed Room");

        // this checks that the copy is a different object and changing it did not change the original.
        assertAll(
                () -> assertNotSame(original, copy),
                () -> assertEquals("1.08", original.getRoom()),
                () -> assertEquals("Changed Room", copy.getRoom())
        );
    }

    @Test
    @Tag("homv0001")
    @Tag("Additional")
    @DisplayName("CR4.04 - Lecture class is detected")
    void cr404LectureClassIsDetected() {
        // this creates a class with "Lecture" in the class format.
        ClassRecord lecture = record("Online Lecture", 1, LocalDate.of(2026, 7, 27), DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0), "Online", "Room");

        // this checks that the isLecture method recognises it as a lecture.
        assertTrue(lecture.isLecture());
    }

    @Test
    @Tag("homv0001")
    @Tag("Additional")
    @DisplayName("CR4.05 - Display line contains important class details")
    void cr405DisplayLineContainsImportantClassDetails() {
        // this creates a normal class record.
        ClassRecord classRecord = record("Workshop", 1, LocalDate.of(2026, 7, 27), DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0), "Tonsley T1", "1.08");

        // this creates the display text that would be shown to a user.
        String display = classRecord.displayLine();

        // this checks that the display text includes the main important details.
        assertAll(
                () -> assertTrue(display.contains("COMP1701")),
                () -> assertTrue(display.contains("Tonsley")),
                () -> assertTrue(display.contains("Workshop")),
                () -> assertTrue(display.contains("Tonsley T1"))
        );
    }
}
