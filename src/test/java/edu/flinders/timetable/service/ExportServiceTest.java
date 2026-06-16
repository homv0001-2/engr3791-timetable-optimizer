package edu.flinders.timetable.service;

import edu.flinders.timetable.data.FileExporter;
import edu.flinders.timetable.model.Timetable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class ExportServiceTest {

    @Test
    @Tag("Ryan")
    @Tag("Core")
    @DisplayName("ES14.01 - Delegate export.")
    void delegatesExportTimetableCorrectly() {

        // Stub captures calls to FileExporter to verify delegation
        class StubExporter extends FileExporter {
            int callCount = 0;
            Timetable receivedTimetable;
            Path receivedPath;
            Path returnValue;

            @Override
            public Path exportTimetableToCsv(Timetable timetable, Path outputPath) {
                callCount++;
                receivedTimetable = timetable;
                receivedPath = outputPath;
                return returnValue;
            }
        }

        StubExporter exporter = new StubExporter();

        Timetable timetable = new Timetable(null);
        Path outputPath = Path.of("output.csv");
        Path expectedReturn = Path.of("result.csv");

        exporter.returnValue = expectedReturn;

        ExportService service = new ExportService(exporter);

        // Execute method under test
        Path result = service.exportTimetable(timetable, outputPath);

        // Verify delegation and return value
        assertAll(
                () -> assertEquals(expectedReturn, result),
                () -> assertEquals(1, exporter.callCount),
                () -> assertEquals(timetable, exporter.receivedTimetable),
                () -> assertEquals(outputPath, exporter.receivedPath)
        );
    }

    @Test
    @Tag("Ryan")
    @Tag("Core")
    @DisplayName("ES14.02 - Preserve parameters.")
    void passesParametersThroughUnchanged() {

        // Stub to capture parameters passed to FileExporter
        class StubExporter extends FileExporter {
            int callCount = 0;
            Timetable receivedTimetable;
            Path receivedPath;
            Path returnValue;

            @Override
            public Path exportTimetableToCsv(Timetable timetable, Path outputPath) {
                callCount++;
                receivedTimetable = timetable;
                receivedPath = outputPath;
                return returnValue;
            }
        }

        StubExporter exporter = new StubExporter();

        Timetable timetable = new Timetable(null);
        Path outputPath = Path.of("timetable.csv");

        exporter.returnValue = outputPath;

        ExportService service = new ExportService(exporter);

        // Execute method under test
        Path result = service.exportTimetable(timetable, outputPath);

        // Verify parameters were passed through correctly
        assertAll(
                () -> assertEquals(outputPath, result),
                () -> assertEquals(1, exporter.callCount),
                () -> assertSame(timetable, exporter.receivedTimetable),
                () -> assertEquals(outputPath, exporter.receivedPath)
        );
    }

    @Test
    @Tag("Ryan")
    @Tag("Core")
    @DisplayName("ES14.03 - Inject exporter used.")
    void constructorInjectionUsesExporter() {

        // Stub only counts calls and returns a fixed path
        class StubExporter extends FileExporter {
            int callCount = 0;
            Path returnValue;

            @Override
            public Path exportTimetableToCsv(Timetable timetable, Path outputPath) {
                callCount++;
                return returnValue;
            }
        }

        StubExporter exporter = new StubExporter();

        // Inject stub via constructor
        ExportService service = new ExportService(exporter);

        Timetable timetable = new Timetable(null);
        Path path = Path.of("test.csv");

        exporter.returnValue = path;

        // Execute method under test
        Path result = service.exportTimetable(timetable, path);

        // Verify the stub was used and call count incremented
        assertEquals(path, result);
        assertEquals(1, exporter.callCount);
    }
}