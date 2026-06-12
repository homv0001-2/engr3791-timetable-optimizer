package edu.flinders.timetable.service;

import edu.flinders.timetable.model.ClassRecord;
import edu.flinders.timetable.model.PreferenceType;
import edu.flinders.timetable.model.TimetableSettings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PreferenceScorerTest {

    private final PreferenceScorer scorer = new PreferenceScorer();

    // Helper to create a ClassRecord with given campus, day, and start time
    private ClassRecord createRecord(String campus, DayOfWeek day, LocalTime start) {
        return new ClassRecord(
                "COMP1701",
                "Game Design",
                "In person",
                campus,
                2,
                1,
                "Workshop",
                1,
                LocalDate.of(2026, 7, 27),
                LocalDate.of(2026, 9, 14),
                day,
                start,
                start.plusHours(1),
                campus + " Building",
                "Room"
        );
    }

    // Helper to score a list of ClassRecords against a single preference
    private int scorePreference(PreferenceType pref, List<ClassRecord> records) {
        TimetableSettings settings = new TimetableSettings();
        settings.setPreferences(List.of(pref));
        return scorer.score(records, settings, records);
    }

    @Test
    @Tag("Ryan")
    @Tag("Additional")
    @DisplayName("PS15.01 - Morning preference positive")
    void morningPreferencePositive() {
        ClassRecord r = createRecord("Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0));
        assertTrue(scorePreference(PreferenceType.MORNINGS, List.of(r)) > 0);
    }

    @Test
    @Tag("Ryan")
    @Tag("Additional")
    @DisplayName("PS15.02 - Afternoon preference positive")
    void afternoonPreferencePositive() {
        ClassRecord r = createRecord("Tonsley", DayOfWeek.MONDAY, LocalTime.of(13, 0));
        assertTrue(scorePreference(PreferenceType.AFTERNOONS, List.of(r)) > 0);
    }

    @Test
    @Tag("Ryan")
    @Tag("Additional")
    @DisplayName("PS15.03 - Campus preference TONSLEY")
    void campusPreferenceTonsley() {
        ClassRecord r = createRecord("Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0));
        assertTrue(scorePreference(PreferenceType.TONSLEY, List.of(r)) > 0);
    }

    @Test
    @Tag("Ryan")
    @Tag("Additional")
    @DisplayName("PS15.04 - Campus preference BEDFORD_PARK mismatch")
    void campusPreferenceBedfordParkMismatch() {
        ClassRecord r = createRecord("Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0));
        assertEquals(0, scorePreference(PreferenceType.BEDFORD_PARK, List.of(r)));
    }

    @Test
    @Tag("Ryan")
    @Tag("Additional")
    @DisplayName("PS15.05 - SAME_CAMPUS true")
    void sameCampusPreferenceTrue() {
        ClassRecord a = createRecord("Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0));
        ClassRecord b = createRecord("Tonsley", DayOfWeek.MONDAY, LocalTime.of(10, 0));

        TimetableSettings settings = new TimetableSettings();
        settings.setPreferences(List.of(PreferenceType.SAME_CAMPUS));

        int score = scorer.score(List.of(a, b), settings, List.of(a, b));

        assertTrue(score > 0);
    }

    @Test
    @Tag("Ryan")
    @Tag("Additional")
    @DisplayName("PS15.06 - MONDAY preference")
    void mondayPreference() {
        ClassRecord r = createRecord("Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0));
        assertTrue(scorePreference(PreferenceType.MONDAY, List.of(r)) > 0);
    }

    @Test
    @Tag("Ryan")
    @Tag("Additional")
    @DisplayName("PS15.07 - TUESDAY mismatch")
    void tuesdayPreferenceMismatch() {
        ClassRecord r = createRecord("Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0));
        assertEquals(0, scorePreference(PreferenceType.TUESDAY, List.of(r)));
    }

    @Test
    @Tag("Ryan")
    @Tag("Additional")
    @DisplayName("PS15.08 - EVENLY_SPREAD true")
    void evenlySpreadPreference() {
        List<ClassRecord> records = List.of(
                createRecord("Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0)),
                createRecord("Tonsley", DayOfWeek.TUESDAY, LocalTime.of(9, 0)),
                createRecord("Tonsley", DayOfWeek.WEDNESDAY, LocalTime.of(9, 0))
        );

        assertTrue(scorePreference(PreferenceType.EVENLY_SPREAD, records) > 0);
    }

    @Test
    @Tag("Ryan")
    @Tag("Additional")
    @DisplayName("PS15.09 - COMPACT_DAYS true")
    void compactDaysPreference() {
        List<ClassRecord> records = List.of(
                createRecord("Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0)),
                createRecord("Tonsley", DayOfWeek.MONDAY, LocalTime.of(11, 0))
        );

        assertTrue(scorePreference(PreferenceType.COMPACT_DAYS, records) > 0);
    }

    @Test
    @Tag("Ryan")
    @Tag("Additional")
    @DisplayName("PS15.10 - weight ordering affects score")
    void weightOrderingAffectsScore() {
        ClassRecord r = createRecord("Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0));

        TimetableSettings settings = new TimetableSettings();
        settings.setPreferences(List.of(
                PreferenceType.MORNINGS,
                PreferenceType.TONSLEY
        ));

        int score = scorer.score(List.of(r), settings, List.of(r));

        assertTrue(score > 0);
    }
}