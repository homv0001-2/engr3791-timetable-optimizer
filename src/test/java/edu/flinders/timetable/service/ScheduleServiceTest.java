package edu.flinders.timetable.service;

import edu.flinders.timetable.model.ClassRecord;
import edu.flinders.timetable.result.ScheduleWarning;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleServiceTest {

    // Helper method to build a ClassRecord with full scheduling details
    private ClassRecord createRecord(
            String topicCode,
            String topicName,
            String attendanceMode,
            String campus,
            int semester,
            int availability,
            String classFormat,
            int instance,
            LocalDate firstDate,
            LocalDate lastDate,
            DayOfWeek day,
            LocalTime start,
            LocalTime end,
            String building,
            String room
    ) {
        return new ClassRecord(
                topicCode,
                topicName,
                attendanceMode,
                campus,
                semester,
                availability,
                classFormat,
                instance,
                firstDate,
                lastDate,
                day,
                start,
                end,
                building,
                room
        );
    }

    @Test
    @Tag("Henry")
    @Tag("Core")
    @DisplayName("SS16.01 - Time clash detected.")
    void timeClashDetected() {

        ScheduleService service = new ScheduleService();

        // Two overlapping classes on same day/time range
        ClassRecord a = createRecord(
                "COMP", "A", "In person", "City", 1, 1,
                "Lecture", 1,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 6, 1),
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                "B1", "R1"
        );

        ClassRecord b = createRecord(
                "COMP", "B", "In person", "City", 1, 1,
                "Lecture", 1,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 6, 1),
                DayOfWeek.MONDAY,
                LocalTime.of(9, 30),
                LocalTime.of(10, 30),
                "B1", "R2"
        );

        // Run warning detection (no lecture overlap allowed in this test)
        List<ScheduleWarning> warnings = service.findWarnings(List.of(a, b), false);

        assertEquals(1, warnings.size());
        assertEquals(ScheduleWarning.Type.TIME_CLASH, warnings.get(0).getType());
    }

    @Test
    @Tag("Henry")
    @Tag("Core")
    @DisplayName("SS16.02 - Different days safe.")
    void differentDaysNoWarning() {

        ScheduleService service = new ScheduleService();

        // Same time but different days should not trigger warnings
        ClassRecord a = createRecord(
                "COMP", "A", "In person", "City", 1, 1,
                "Lecture", 1,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 6, 1),
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                "B1", "R1"
        );

        ClassRecord b = createRecord(
                "COMP", "B", "In person", "City", 1, 1,
                "Lecture", 1,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 6, 1),
                DayOfWeek.TUESDAY,
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                "B1", "R2"
        );

        List<ScheduleWarning> warnings = service.findWarnings(List.of(a, b), false);

        assertTrue(warnings.isEmpty());
    }

    @Test
    @Tag("Henry")
    @Tag("Core")
    @DisplayName("SS16.03 - Commute gap detected.")
    void commuteGapDetected() {

        ScheduleService service = new ScheduleService();

        // Classes on same day but different campuses with tight gap
        ClassRecord a = createRecord(
                "COMP", "A", "In person", "City", 1, 1,
                "Tutorial", 1,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 6, 1),
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                "B1", "R1"
        );

        ClassRecord b = createRecord(
                "COMP", "B", "In person", "Tonsley", 1, 1,
                "Tutorial", 1,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 6, 1),
                DayOfWeek.MONDAY,
                LocalTime.of(10, 10),
                LocalTime.of(11, 0),
                "B2", "R2"
        );

        List<ScheduleWarning> warnings = service.findWarnings(List.of(a, b), false);

        assertEquals(1, warnings.size());
        assertEquals(ScheduleWarning.Type.COMMUTE_GAP, warnings.get(0).getType());
    }

    @Test
    @Tag("Henry")
    @Tag("Core")
    @DisplayName("SS16.04 - Same campus safe.")
    void sameCampusNoCommuteWarning() {

        ScheduleService service = new ScheduleService();

        // Same campus removes commute penalty even with short gap
        ClassRecord a = createRecord(
                "COMP", "A", "In person", "City", 1, 1,
                "Tutorial", 1,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 6, 1),
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                "B1", "R1"
        );

        ClassRecord b = createRecord(
                "COMP", "B", "In person", "City", 1, 1,
                "Tutorial", 1,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 6, 1),
                DayOfWeek.MONDAY,
                LocalTime.of(10, 10),
                LocalTime.of(11, 0),
                "B2", "R2"
        );

        List<ScheduleWarning> warnings = service.findWarnings(List.of(a, b), false);

        assertTrue(warnings.isEmpty());
    }

    @Test
    @Tag("Henry")
    @Tag("Core")
    @DisplayName("SS16.05 - Allow lecture overlap.")
    void allowLectureOverlapDisablesWarning() {

        ScheduleService service = new ScheduleService();

        // Overlapping lectures should be ignored when flag is enabled
        ClassRecord a = createRecord(
                "COMP", "A", "In person", "City", 1, 1,
                "Lecture", 1,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 6, 1),
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                "B1", "R1"
        );

        ClassRecord b = createRecord(
                "COMP", "B", "In person", "City", 1, 1,
                "Lecture", 1,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 6, 1),
                DayOfWeek.MONDAY,
                LocalTime.of(9, 30),
                LocalTime.of(10, 30),
                "B1", "R2"
        );

        List<ScheduleWarning> warnings = service.findWarnings(List.of(a, b), true);

        assertTrue(warnings.isEmpty());
    }

    @Test
    @Tag("Henry")
    @Tag("Core")
    @DisplayName("SS16.06 - Large gap safe.")
    void largeGapNoWarning() {

        ScheduleService service = new ScheduleService();

        // Large time gap should not trigger commute warning
        ClassRecord a = createRecord(
                "COMP", "A", "In person", "City", 1, 1,
                "Tutorial", 1,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 6, 1),
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                "B1", "R1"
        );

        ClassRecord b = createRecord(
                "COMP", "B", "In person", "Tonsley", 1, 1,
                "Tutorial", 1,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 6, 1),
                DayOfWeek.MONDAY,
                LocalTime.of(10, 50),
                LocalTime.of(11, 30),
                "B2", "R2"
        );

        List<ScheduleWarning> warnings = service.findWarnings(List.of(a, b), false);

        assertTrue(warnings.isEmpty());
    }
}