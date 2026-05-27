package edu.flinders.timetable.data;

import edu.flinders.timetable.model.ClassRecord;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class CsvParserTest {

    @BeforeAll
    static void beforeAll() {
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @AfterAll
    static void afterAll() {
    }

    //this is a simple test for TV1.01 if you want to showcase work, but theres not really much to see here - thomas
    @Test
    @Tag("Thomas")
    @Tag("Critical")
    @DisplayName("TV1.01 - Import classes from a .csv file")
    void tv101ImportClassesFromCsvFile() {
        CsvParser parser = new CsvParser();
        Path csvFile = Path.of("examples", "sample-topic-data.csv");

        //this checks if the CSV parser crashes and/or cant read the file, then the test fails.
        List<ClassRecord> records = assertDoesNotThrow(() -> parser.parse(csvFile));

        //this checks if the parser creates 6 ClassRecord objects, since the sample CSV file has 6 class rows.
        assertEquals(6, records.size());
    }
}
