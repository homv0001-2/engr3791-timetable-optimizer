package edu.flinders.timetable.application;

import edu.flinders.timetable.model.Timetable;
import edu.flinders.timetable.model.TimetableSettings;
import edu.flinders.timetable.result.PendingSwapResult;
import edu.flinders.timetable.result.TimetableGenerationResult;
import edu.flinders.timetable.service.ExportService;
import edu.flinders.timetable.service.TimetableService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TimetableManagerTest {

    @Test
    @Tag("Thomas")
    @DisplayName("TM3.01 - Generate timetable.")
    void generateTimetable() {

        // this creates timetable generation settings that will be passed to the service.
        TimetableSettings settings = new TimetableSettings();

        // this creates a test timetable service that verifies the same settings object
        // is passed through by the manager.
        TimetableService service = new TimetableService(
                null,
                null
        ) {
            @Override
            public TimetableGenerationResult generateTimetable(TimetableSettings s) {
                assertSame(settings, s);
                return null;
            }
        };

        // this creates a test export service that simply returns the output path.
        ExportService exportService = new ExportService(null) {
            @Override
            public Path exportTimetable(Timetable timetable, Path outputPath) {
                return outputPath;
            }
        };

        // this creates the timetable manager using the test services.
        TimetableManager manager = new TimetableManager(service, exportService);

        // this verifies that the result returned from the service is passed back unchanged.
        assertNull(manager.generateTimetable(settings));
    }

    @Test
    @Tag("Thomas")
    @DisplayName("TM3.02 - Browse timetables.")
    void browseTimetables() {

        // this creates a sample list of timetables that will be returned by the service.
        List<Timetable> list = List.of(new Timetable(null));

        // this creates a test service that returns the sample timetable list.
        TimetableService service = new TimetableService(null, null) {
            @Override
            public List<Timetable> browseTimetables() {
                return list;
            }
        };

        // this creates a simple export service for manager construction.
        ExportService exportService = new ExportService(null) {
            @Override
            public Path exportTimetable(Timetable timetable, Path outputPath) {
                return outputPath;
            }
        };

        // this creates the timetable manager.
        TimetableManager manager = new TimetableManager(service, exportService);

        // this verifies that the manager returns the same list provided by the service.
        assertEquals(list, manager.browseTimetables());
    }

    @Test
    @Tag("Thomas")
    @DisplayName("TM3.03 - View timetable optional.")
    void viewTimetable() {

        // this creates a sample timetable that will be returned by the service.
        Timetable t = new Timetable(null);

        // this creates a test service that always returns the sample timetable.
        TimetableService service = new TimetableService(null, null) {
            @Override
            public Optional<Timetable> viewTimetable(String name) {
                return Optional.of(t);
            }
        };

        // this creates a simple export service for manager construction.
        ExportService exportService = new ExportService(null) {
            @Override
            public Path exportTimetable(Timetable timetable, Path outputPath) {
                return outputPath;
            }
        };

        // this creates the timetable manager.
        TimetableManager manager = new TimetableManager(service, exportService);

        // this verifies that a timetable was successfully returned.
        assertTrue(manager.viewTimetable("x").isPresent());
    }

    @Test
    @Tag("Thomas")
    @DisplayName("TM3.04 - Swap flow.")
    void swapFlow() {

        // this creates a sample swap result that will be returned by prepareSwap.
        PendingSwapResult swap = new PendingSwapResult(
                true,
                false,
                null,
                "",
                List.of()
        );

        // this creates a test service that validates the swap parameters
        // and ensures the same swap result is later applied.
        TimetableService service = new TimetableService(null, null) {

            @Override
            public PendingSwapResult prepareSwap(String a, String b, String c) {
                assertEquals("t1", a);
                assertEquals("g1", b);
                assertEquals("g2", c);
                return swap;
            }

            @Override
            public void applySwap(PendingSwapResult result) {
                assertSame(swap, result);
            }
        };

        // this creates a simple export service for manager construction.
        ExportService exportService = new ExportService(null) {
            @Override
            public Path exportTimetable(Timetable timetable, Path outputPath) {
                return outputPath;
            }
        };

        // this creates the timetable manager.
        TimetableManager manager = new TimetableManager(service, exportService);

        // this prepares a swap and immediately applies it.
        manager.applySwap(manager.prepareSwap("t1", "g1", "g2"));
    }

    @Test
    @Tag("Thomas")
    @DisplayName("TM3.05 - Delete timetable.")
    void deleteTimetable() {

        // this creates a test service that only deletes a timetable named "test".
        TimetableService service = new TimetableService(null, null) {
            @Override
            public boolean deleteTimetable(String name) {
                return name.equals("test");
            }
        };

        // this creates a simple export service for manager construction.
        ExportService exportService = new ExportService(null) {
            @Override
            public Path exportTimetable(Timetable timetable, Path outputPath) {
                return outputPath;
            }
        };

        // this creates the timetable manager.
        TimetableManager manager = new TimetableManager(service, exportService);

        // this verifies that an existing timetable can be deleted.
        assertTrue(manager.deleteTimetable("test"));

        // this verifies that a non-existent timetable cannot be deleted.
        assertFalse(manager.deleteTimetable("nope"));
    }

    @Test
    @Tag("Thomas")
    @DisplayName("TM3.06 - Export timetable.")
    void exportTimetable() {

        // this creates a sample output path and timetable for export.
        Path out = Paths.get("out.csv");
        Timetable timetable = new Timetable(null);

        // this creates a basic timetable service for manager construction.
        TimetableService service = new TimetableService(null, null);

        // this creates a test export service that verifies the timetable passed in
        // and returns the expected output path.
        ExportService exportService = new ExportService(null) {
            @Override
            public Path exportTimetable(Timetable t, Path outputPath) {
                assertSame(timetable, t);
                return out;
            }
        };

        // this creates the timetable manager.
        TimetableManager manager = new TimetableManager(service, exportService);

        // this verifies that the exported path returned by the service is passed back.
        assertEquals(out, manager.exportTimetable(timetable, out));
    }
}