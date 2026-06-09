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
    @DisplayName("ES14.01 - ExportService delegates exportTimetable correctly")
    void es101DelegatesExportCorrectly() {

        // this stub captures calls made by ExportService
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

        Path result = service.exportTimetable(timetable, outputPath);

        assertAll(
                () -> assertEquals(expectedReturn, result),
                () -> assertEquals(1, exporter.callCount),
                () -> assertEquals(timetable, exporter.receivedTimetable),
                () -> assertEquals(outputPath, exporter.receivedPath)
        );
    }

    @Test
    @Tag("Ryan")
    @Tag("Additional")
    @DisplayName("ES14.02 - Parameters are passed through unchanged")
    void es102ParametersPassedThrough() {

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

        Path result = service.exportTimetable(timetable, outputPath);

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
    @DisplayName("ES14.03 - ExportService correctly uses injected FileExporter")
    void es103ConstructorInjectionWorks() {

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

        ExportService service = new ExportService(exporter);

        Timetable timetable = new Timetable(null);
        Path path = Path.of("test.csv");

        exporter.returnValue = path;

        Path result = service.exportTimetable(timetable, path);

        assertEquals(path, result);
        assertEquals(1, exporter.callCount);
    }
}