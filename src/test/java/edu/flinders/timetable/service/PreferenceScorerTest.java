package edu.flinders.timetable.service;

import edu.flinders.timetable.model.ClassRecord;
import edu.flinders.timetable.model.PreferenceType;
import edu.flinders.timetable.model.TimetableSettings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class PreferenceScorerTest {

    private final PreferenceScorer scorer = new PreferenceScorer();

    private ClassRecord record(String campus, DayOfWeek day, LocalTime start) {
        // this helper creates one class record for checking timetable preferences.
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

    @Test
    @Tag("homv0001")
    @Tag("Additional")
    @DisplayName("PS8.01 - Morning preference gives positive score")
    void ps801MorningPreferenceGivesPositiveScore() {
        // this creates settings where the user prefers morning classes.
        TimetableSettings settings = new TimetableSettings();
        settings.setPreferences(List.of(PreferenceType.MORNINGS));

        // this creates a class that starts in the morning.
        ClassRecord morningClass = record("Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0));

        // this calculates the preference score.
        int score = scorer.score(List.of(morningClass), settings, List.of(morningClass));

        // this checks that the morning class gets a positive score.
        assertTrue(score > 0);
    }

    @Test
    @Tag("homv0001")
    @Tag("Additional")
    @DisplayName("PS8.02 - Morning preference gives zero for afternoon class")
    void ps802MorningPreferenceGivesZeroForAfternoonClass() {
        // this creates settings where the user prefers morning classes.
        TimetableSettings settings = new TimetableSettings();
        settings.setPreferences(List.of(PreferenceType.MORNINGS));

        // this creates a class that starts in the afternoon.
        ClassRecord afternoonClass = record("Tonsley", DayOfWeek.MONDAY, LocalTime.of(13, 0));

        // this calculates the preference score.
        int score = scorer.score(List.of(afternoonClass), settings, List.of(afternoonClass));

        // this checks that the afternoon class does not get points for the morning preference.
        assertEquals(0, score);
    }

    @Test
    @Tag("homv0001")
    @Tag("Additional")
    @DisplayName("PS8.03 - Campus preference gives positive score")
    void ps803CampusPreferenceGivesPositiveScore() {
        // this creates settings where the user prefers Tonsley classes.
        TimetableSettings settings = new TimetableSettings();
        settings.setPreferences(List.of(PreferenceType.TONSLEY));

        // this creates a class at Tonsley.
        ClassRecord tonsleyClass = record("Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0));

        // this calculates the preference score.
        int score = scorer.score(List.of(tonsleyClass), settings, List.of(tonsleyClass));

        // this checks that the Tonsley class gets a positive preference score.
        assertTrue(score > 0);
    }
}
