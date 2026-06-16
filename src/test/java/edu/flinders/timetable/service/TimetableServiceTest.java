package edu.flinders.timetable.service;

import edu.flinders.timetable.data.DataRepository;
import edu.flinders.timetable.model.ClassRecord;
import edu.flinders.timetable.model.TimetableSettings;
import edu.flinders.timetable.result.TimetableGenerationResult;
import edu.flinders.timetable.result.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class TimetableServiceTest {

    private DataRepository repository;
    private TimetableService service;

    @BeforeEach
    void setUp() {
        // Creates a fresh repository and timetable service before each test
        repository = new DataRepository();
        service = new TimetableService(repository, new ScheduleService());
    }

    // Helper to create a simple class record for timetable generation tests
    private ClassRecord createRecord(String topicCode, String classFormat, int instance, String campus, DayOfWeek day, LocalTime start, LocalTime end) {
        return new ClassRecord(
                topicCode,
                "Game Design",
                "In person",
                campus,
                2,
                1,
                classFormat,
                instance,
                LocalDate.of(2026, 7, 27),
                LocalDate.of(2026, 9, 14),
                day,
                start,
                end,
                campus + " Building",
                "Room"
        );
    }

    // Helper to create valid timetable settings for one topic, one semester, one campus
    private TimetableSettings createSettings(String name) {
        TimetableSettings settings = new TimetableSettings();
        settings.setTimetableName(name);
        settings.setTopicCodes(Set.of("COMP1701"));
        settings.setSemesters(Set.of(2));
        settings.setCampuses(Set.of("Tonsley"));
        return settings;
    }

    @Test
    @Tag("Henry")
    @Tag("Core")
    @DisplayName("TS17.01 - Null settings invalid.")
    void nullTimetableSettingsAreInvalid() {
        // Validate null settings
        ValidationResult result = service.validateSettings(null);

        // Null settings should be invalid
        assertFalse(result.isValid());
    }

    @Test
    @Tag("Henry")
    @Tag("Core")
    @DisplayName("TS17.02 - Empty topic invalid.")
    void emptyTopicSelectionIsInvalid() {
        TimetableSettings settings = createSettings("Empty Topic Test");

        // Remove all selected topics
        settings.setTopicCodes(Set.of());

        // Validate settings
        ValidationResult result = service.validateSettings(settings);

        // At least one topic is required
        assertFalse(result.isValid());
    }

    @Test
    @Tag("Henry")
    @Tag("Core")
    @DisplayName("TS17.03 - Invalid semester.")
    void invalidSemesterIsRejected() {
        TimetableSettings settings = createSettings("Bad Semester Test");

        // Set invalid semester (not 1 or 2)
        settings.setSemesters(Set.of(3));

        ValidationResult result = service.validateSettings(settings);

        // Only semesters 1 and 2 are valid
        assertFalse(result.isValid());
    }

    @Test
    @Tag("Henry")
    @Tag("Core")
    @DisplayName("TS17.04 - Generate and save.")
    void generateTimetableSavesValidTimetable() {
        // Add a class matching the timetable settings
        repository.importRecords(List.of(
                createRecord("COMP1701", "Workshop", 1, "Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0))
        ));

        // Generate timetable
        TimetableGenerationResult result = service.generateTimetable(createSettings("My Timetable"));

        // Timetable should be generated and saved
        assertAll(
                () -> assertTrue(result.isSuccess()),
                () -> assertNotNull(result.getTimetable()),
                () -> assertEquals("My Timetable", result.getTimetable().getName()),
                () -> assertTrue(repository.findTimetable("My Timetable").isPresent())
        );
    }

    @Test
    @Tag("Henry")
    @Tag("Core")
    @DisplayName("TS17.05 - Auto timetable name.")
    void blankTimetableNameIsAutomaticallyGenerated() {
        repository.importRecords(List.of(
                createRecord("COMP1701", "Workshop", 1, "Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0))
        ));

        // Pass blank name
        TimetableGenerationResult result = service.generateTimetable(createSettings("   "));

        // Service generates default name
        assertAll(
                () -> assertTrue(result.isSuccess()),
                () -> assertEquals("Timetable 1", result.getTimetable().getName())
        );
    }

    @Test
    @Tag("Henry")
    @Tag("Core")
    @DisplayName("TS17.06 - Reject duplicate name.")
    void duplicateTimetableNameIsRejected() {
        repository.importRecords(List.of(
                createRecord("COMP1701", "Workshop", 1, "Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0))
        ));

        // Create first timetable
        service.generateTimetable(createSettings("Duplicate Test"));

        // Validate same name again
        ValidationResult result = service.validateSettings(createSettings("Duplicate Test"));

        // Duplicate names are invalid
        assertFalse(result.isValid());
    }

    @Test
    @Tag("Henry")
    @Tag("Core")
    @DisplayName("TS17.07 - No matching classes fail.")
    void generateTimetableFailsWhenNoClassesMatch() {
        // Import a class for a different topic
        repository.importRecords(List.of(
                createRecord("COMP9999", "Workshop", 1, "Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0))
        ));

        // Generate timetable for COMP1701
        TimetableGenerationResult result = service.generateTimetable(createSettings("No Match Test"));

        // Generation should fail due to no matching classes
        assertFalse(result.isSuccess());
    }
}