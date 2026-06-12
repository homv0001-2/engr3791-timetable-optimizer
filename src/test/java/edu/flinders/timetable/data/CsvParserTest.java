package edu.flinders.timetable.data;

import edu.flinders.timetable.model.ClassRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class CsvParserTest {

    // this creates a CsvParser instance that will be used for all tests
    private final CsvParser parser = new CsvParser();

    // this defines the expected header row of a valid CSV file
    private static final String HEADER = "Topic,Availability,Class,Class instance,Date,Day,Time,Location";

    // this creates a single valid CSV data row as a string
    private static String validRow() {
        return "COMP1701 Game Design,In person - Flinders City Campus - S2 - 1,Workshop-1,1,27 Jul - 14 Sep,Monday,09:00 - 10:00,Festival Tower, 506 Computer Lab";
    }

    @Test
    @Tag("Thomas")
    @Tag("Critical")
    @DisplayName("CP4.01 - Verifies that a valid CSV file can be parsed successfully and produces the expected number of class records.")
    void importClassesFromCsvFile() {
        // this creates a path to a sample CSV file for testing
        Path csvFile = Path.of("examples", "sample-topic-data.csv");

        // this parses the CSV file and asserts that no exception is thrown
        List<ClassRecord> records = assertDoesNotThrow(() -> parser.parse(csvFile));

        // this verifies that the parser produced the expected number of class records
        assertEquals(6, records.size());
    }

    @Test
    @Tag("Thomas")
    @Tag("Critical")
    @DisplayName("CP4.02 - Ensures that location fields containing commas are parsed correctly into separate building and room values.")
    void parseLocationThatContainsAComma() {
        // this creates CSV lines with a header and one valid data row
        List<String> lines = List.of(HEADER, validRow());

        // this parses the CSV lines into ClassRecord objects
        List<ClassRecord> records = parser.parseLines(lines);

        // this retrieves the first class record for verification
        ClassRecord record = records.get(0);

        // this asserts that the building and room fields were parsed correctly
        assertAll(
                () -> assertEquals("Festival Tower", record.getBuilding()),
                () -> assertEquals("506 Computer Lab", record.getRoom())
        );
    }

    @Test
    @Tag("Thomas")
    @Tag("Core")
    @DisplayName("CP4.03 - Validates that topic code, topic name, attendance mode, campus, semester, and availability number are correctly extracted from a CSV row.")
    void parseTopicAndAvailabilityFields() {
        // this creates CSV lines with a header and one valid data row
        List<String> lines = List.of(HEADER, validRow());

        // this parses the CSV lines and retrieves the first record
        ClassRecord record = parser.parseLines(lines).get(0);

        // this asserts that all key fields are correctly extracted
        assertAll(
                () -> assertEquals("COMP1701", record.getTopicCode()),
                () -> assertEquals("Game Design", record.getTopicName()),
                () -> assertEquals("In person", record.getAttendanceMode()),
                () -> assertEquals("Flinders City Campus", record.getCampus()),
                () -> assertEquals(2, record.getSemester()),
                () -> assertEquals(1, record.getAvailabilityNumber())
        );
    }

    @Test
    @Tag("Thomas")
    @Tag("Core")
    @DisplayName("CP4.04 - Confirms that class dates, day of week, start time, and end time are parsed correctly from the CSV data.")
    void parseDateDayAndTimeFields() {
        // this creates CSV lines with a header and one valid data row
        List<String> lines = List.of(HEADER, validRow());

        // this parses the CSV lines and retrieves the first record
        ClassRecord record = parser.parseLines(lines).get(0);

        // this asserts that the date, day, and time fields are parsed correctly
        assertAll(
                () -> assertEquals(LocalDate.of(2026, 7, 27), record.getFirstClassDate()),
                () -> assertEquals(LocalDate.of(2026, 9, 14), record.getLastClassDate()),
                () -> assertEquals(DayOfWeek.MONDAY, record.getDay()),
                () -> assertEquals(LocalTime.of(9, 0), record.getStartTime()),
                () -> assertEquals(LocalTime.of(10, 0), record.getEndTime())
        );
    }

    @Test
    @Tag("Thomas")
    @Tag("Critical")
    @DisplayName("CP4.05 - Verifies that a CSV containing only a header row is rejected with a CsvFormatException.")
    void rejectCsvWithoutADataRow() {
        // this creates CSV lines containing only the header
        List<String> lines = List.of(HEADER);

        // this asserts that parsing fails because no data rows exist
        assertThrows(CsvFormatException.class, () -> parser.parseLines(lines));
    }

    @Test
    @Tag("Thomas")
    @Tag("Critical")
    @DisplayName("TV4.06 - Ensures that a CSV with an invalid or unexpected header format is rejected with a CsvFormatException.")
    void rejectCsvWithWrongHeader() {
        // this creates a CSV header with the wrong format
        String wrongHeader = "Subject,Availability,Class,Class instance,Date,Day,Time,Location";

        // this asserts that parsing fails due to header mismatch
        assertThrows(CsvFormatException.class, () -> parser.parseLines(List.of(wrongHeader, validRow())));
    }

    @Test
    @Tag("Thomas")
    @Tag("Critical")
    @DisplayName("CP4.07 - Verifies that class records with an instance number of zero are rejected as invalid.")
    void rejectClassInstanceZero() {
        // this creates a CSV row with a class instance of zero
        String row = "COMP1701 Game Design,In person - Tonsley - S2 - 1,Workshop,0,27 Jul - 14 Sep,Monday,09:00 - 10:00,Tonsley T1, 1.08 Lecture Room";

        // this asserts that parsing fails due to invalid class instance
        assertThrows(CsvFormatException.class, () -> parser.parseLines(List.of(HEADER, row)));
    }

    @Test
    @Tag("Thomas")
    @Tag("Critical")
    @DisplayName("CP4.08 - Ensures that class records with an invalid time range (start time after end time) are rejected.")
    void rejectTimeRangeWhereStartIsAfterEnd() {
        // this creates a CSV row with start time after end time
        String row = "COMP1701 Game Design,In person - Tonsley - S2 - 1,Workshop,1,27 Jul - 14 Sep,Monday,12:00 - 10:00,Tonsley T1, 1.08 Lecture Room";

        // this asserts that parsing fails due to invalid time range
        assertThrows(CsvFormatException.class, () -> parser.parseLines(List.of(HEADER, row)));
    }
}