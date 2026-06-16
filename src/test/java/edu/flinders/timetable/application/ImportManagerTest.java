package edu.flinders.timetable.application;

import edu.flinders.timetable.data.CsvParser;
import edu.flinders.timetable.data.DataRepository;
import edu.flinders.timetable.model.ClassRecord;
import edu.flinders.timetable.result.ImportResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ImportManagerTest {

    private ClassRecord record() {
        // this helper creates a sample class record that can be returned by the parser.
        return new ClassRecord(
                "COMP1701",
                "Game Design",
                "In person",
                "Tonsley",
                2,
                1,
                "Workshop",
                1,
                LocalDate.of(2026, 7, 27),
                LocalDate.of(2026, 9, 14),
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                "Tonsley T1",
                "1.08"
        );
    }

    @Test
    @Tag("Thomas")
    @Tag("Critical")
    @DisplayName("IM2.01 - Import CSV success.")
    void importCsvProcessesRecordsSuccessfully() {

        // this creates a dummy path that will be passed to the parser.
        Path dummyPath = Paths.get("dummy.csv");

        // this creates a test parser that verifies the correct path is received
        // and returns one sample class record.
        CsvParser parser = new CsvParser() {
            @Override
            public List<ClassRecord> parse(Path path) {
                assertEquals(dummyPath, path);
                return List.of(record());
            }
        };

        // this creates a test repository that verifies one record was received
        // and returns a successful import result.
        DataRepository repository = new DataRepository() {
            @Override
            public ImportResult importRecords(List<ClassRecord> records) {
                assertEquals(1, records.size());
                return new ImportResult();
            }
        };

        // this creates the import manager using the test parser and repository.
        ImportManager manager = new ImportManager(parser, repository);

        // this imports the CSV file through the manager.
        ImportResult result = manager.importCsv(dummyPath);

        // this checks that a valid import result was returned.
        assertNotNull(result);
    }

    @Test
    @Tag("Thomas")
    @Tag("Critical")
    @DisplayName("IM2.02 - Empty CSV import.")
    void importCsvHandlesEmptyCsvResults() {

        // this creates a dummy path representing an empty CSV file.
        Path dummyPath = Paths.get("empty.csv");

        // this creates a test parser that returns no records.
        CsvParser parser = new CsvParser() {
            @Override
            public List<ClassRecord> parse(Path path) {
                assertEquals(dummyPath, path);
                return List.of();
            }
        };

        // this creates a repository that verifies it received an empty list
        // and returns a successful import result.
        DataRepository repository = new DataRepository() {
            @Override
            public ImportResult importRecords(List<ClassRecord> records) {
                assertTrue(records.isEmpty());
                return new ImportResult();
            }
        };

        // this creates the import manager using the test dependencies.
        ImportManager manager = new ImportManager(parser, repository);

        // this imports the empty CSV file.
        ImportResult result = manager.importCsv(dummyPath);

        // this checks that a valid import result was still returned.
        assertNotNull(result);
    }

    @Test
    @Tag("Thomas")
    @Tag("Critical")
    @DisplayName("IM2.03 - Constructor wiring.")
    void constructorWorks() {

        // this creates a parser that returns an empty list of records.
        CsvParser parser = new CsvParser() {
            @Override
            public List<ClassRecord> parse(Path path) {
                return List.of();
            }
        };

        // this creates a repository that returns a successful import result.
        DataRepository repository = new DataRepository() {
            @Override
            public ImportResult importRecords(List<ClassRecord> records) {
                return new ImportResult();
            }
        };

        // this creates the import manager with the supplied dependencies.
        ImportManager manager = new ImportManager(parser, repository);

        // this verifies that importCsv can be called successfully and returns a result.
        assertNotNull(manager.importCsv(Paths.get("test.csv")));
    }
}