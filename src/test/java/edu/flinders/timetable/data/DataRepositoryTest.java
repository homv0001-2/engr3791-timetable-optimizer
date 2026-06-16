package edu.flinders.timetable.data;

import edu.flinders.timetable.model.ClassRecord;
import edu.flinders.timetable.model.Timetable;
import edu.flinders.timetable.result.ImportResult;
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

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class DataRepositoryTest {

    // repository instance to be tested
    private DataRepository repository;

    @BeforeEach
    void setup() {
        // instantiate a fresh repository for each test
        repository = new DataRepository();
    }

    /**
     * Helper method to quickly create a ClassRecord with specified parameters.
     */
    private ClassRecord record(String topicCode, String classFormat, int instance,
                               LocalTime start, LocalTime end, String building, String room) {
        return new ClassRecord(
                topicCode,
                "Game Design",
                "In person",
                "Tonsley",
                2,
                1,
                classFormat,
                instance,
                LocalDate.of(2026, 7, 27),
                LocalDate.of(2026, 9, 14),
                DayOfWeek.MONDAY,
                start,
                end,
                building,
                room
        );
    }

    @Test
    @Tag("Jeff")
    @Tag("Core")
    @DisplayName("DR5.01 - Import new class.")
    void importNewClassRecordIncreasesNewCount() {
        // create a new class record to import
        ClassRecord newRecord = record("COMP1701", "Workshop", 1,
                LocalTime.of(9, 0), LocalTime.of(10, 0), "Tonsley T1", "1.08");

        // import the record and capture the result
        ImportResult result = repository.importRecords(List.of(newRecord));

        // verify that the new record count increased, no updates occurred, and repository now has one record
        assertAll(
                () -> assertEquals(1, result.getNewRecordCount()),
                () -> assertEquals(0, result.getUpdatedRecordCount()),
                () -> assertEquals(1, repository.listClassRecords().size())
        );
    }

    @Test
    @Tag("Jeff")
    @Tag("Core")
    @DisplayName("DR5.02 - Update duplicate.")
    void importDuplicateUpdatesTimeAndLocation() {
        // original record
        ClassRecord original = record("COMP1701", "Workshop", 1,
                LocalTime.of(9, 0), LocalTime.of(10, 0), "Old Building", "Old Room");

        // updated record with new time and location
        ClassRecord updated = record("COMP1701", "Workshop", 1,
                LocalTime.of(11, 0), LocalTime.of(12, 0), "New Building", "New Room");

        // import original first
        repository.importRecords(List.of(original));
        // import updated record, should count as update
        ImportResult secondResult = repository.importRecords(List.of(updated));

        ClassRecord stored = repository.listClassRecords().get(0);

        // verify the update behavior
        assertAll(
                () -> assertEquals(0, secondResult.getNewRecordCount()),
                () -> assertEquals(1, secondResult.getUpdatedRecordCount()),
                () -> assertEquals(1, repository.listClassRecords().size()),
                () -> assertEquals(LocalTime.of(11, 0), stored.getStartTime()),
                () -> assertEquals("New Building", stored.getBuilding()),
                () -> assertEquals("New Room", stored.getRoom())
        );
    }

    @Test
    @Tag("Jeff")
    @Tag("Core")
    @DisplayName("DR5.03 - Find by import key.")
    void findClassByImportKeyReturnsClass() {
        // import a class record
        ClassRecord classRecord = record("COMP1701", "Workshop", 1,
                LocalTime.of(9, 0), LocalTime.of(10, 0), "Tonsley T1", "1.08");
        repository.importRecords(List.of(classRecord));

        // verify that we can find it using its unique import key
        assertTrue(repository.findClassByImportKey(classRecord.importKey()).isPresent());
    }

    @Test
    @Tag("Jeff")
    @Tag("Core")
    @DisplayName("DR5.04 - Replace record.")
    void replaceExistingClassRecord() {
        // original record
        ClassRecord original = record("COMP1701", "Workshop", 1,
                LocalTime.of(9, 0), LocalTime.of(10, 0), "Tonsley T1", "1.08");
        repository.importRecords(List.of(original));

        // replacement record
        ClassRecord replacement = record("COMP1701", "Tutorial", 2,
                LocalTime.of(13, 0), LocalTime.of(14, 0), "Festival Tower", "506");

        // replace using the original's import key
        boolean replaced = repository.replaceClass(original.importKey(), replacement);

        // verify replacement worked
        assertAll(
                () -> assertTrue(replaced),
                () -> assertEquals("Tutorial", repository.listClassRecords().get(0).getClassFormat()),
                () -> assertEquals(2, repository.listClassRecords().get(0).getClassInstance())
        );
    }

    @Test
    @Tag("Jeff")
    @Tag("Core")
    @DisplayName("DR5.05 - Delete existing record.")
    void deleteExistingClassRecord() {
        ClassRecord classRecord = record("COMP1701", "Workshop", 1,
                LocalTime.of(9, 0), LocalTime.of(10, 0), "Tonsley T1", "1.08");
        repository.importRecords(List.of(classRecord));

        // attempt deletion
        boolean deleted = repository.deleteClass(classRecord.importKey());

        // verify deletion and empty repository
        assertAll(
                () -> assertTrue(deleted),
                () -> assertTrue(repository.listClassRecords().isEmpty())
        );
    }

    @Test
    @Tag("Jeff")
    @Tag("Core")
    @DisplayName("DR5.06 - Delete missing record.")
    void deleteMissingClassRecordReturnsFalse() {
        // attempt deletion of a non-existent record
        boolean deleted = repository.deleteClass("missing-key");

        // should return false
        assertFalse(deleted);
    }

    @Test
    @Tag("Jeff")
    @Tag("Core")
    @DisplayName("DR5.07 - Save/view/delete/timetable.")
    void saveFindAndDeleteTimetable() {
        // create a timetable
        Timetable timetable = new Timetable("My Timetable");

        // save it to the repository
        repository.saveTimetable(timetable);

        // verify save, retrieval, list, and deletion functionality
        assertAll(
                () -> assertTrue(repository.timetableNameExists("My Timetable")),
                () -> assertTrue(repository.findTimetable("My Timetable").isPresent()),
                () -> assertEquals(1, repository.listTimetables().size()),
                () -> assertTrue(repository.deleteTimetable("My Timetable")),
                () -> assertFalse(repository.findTimetable("My Timetable").isPresent())
        );
    }
}