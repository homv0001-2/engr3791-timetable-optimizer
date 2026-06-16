package edu.flinders.timetable.presentation;

import edu.flinders.timetable.application.ClassManager;
import edu.flinders.timetable.application.ImportManager;
import edu.flinders.timetable.application.TimetableManager;
import edu.flinders.timetable.data.CsvParser;
import edu.flinders.timetable.data.DataRepository;
import edu.flinders.timetable.data.CsvFormatException;
import edu.flinders.timetable.model.*;
import edu.flinders.timetable.result.*;
import edu.flinders.timetable.service.TimetableService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;

class ConsoleUITest {

    private void setInput(String data) {
        // redirect System.in to simulate user input
        InputStream in = new ByteArrayInputStream(data.getBytes());
        System.setIn(in);
    }

    @Test
    @Tag("Sunny")
    @Tag("Additional")
    @DisplayName("CU9.01 - Basic menu flow.")
    void basicFlow() {

        // ImportManager stub that simulates CSV parsing
        ImportManager importManager = new ImportManager(
                new CsvParser() {
                    @Override
                    public List<ClassRecord> parse(Path path) {
                        return List.of();
                    }
                },
                new DataRepository() {
                    @Override
                    public ImportResult importRecords(List<ClassRecord> records) {
                        return new ImportResult();
                    }
                }
        );

        // ClassManager stub with empty responses
        ClassManager classManager = new ClassManager(null) {
            @Override public List<ClassGroup> browseClasses() { return List.of(); }
            @Override public List<ClassRecord> viewClasses() { return List.of(); }
            @Override public List<ClassRecord> searchClasses(SearchCriteria criteria) { return List.of(); }
            @Override public Optional<ClassRecord> findClass(String key) { return Optional.empty(); }
            @Override public boolean editClass(String key, ClassRecord updated) { return true; }
            @Override public boolean deleteClass(String key) { return true; }
        };

        // TimetableManager stub simulating timetable operations
        TimetableManager timetableManager = new TimetableManager(
                new TimetableService(null, null) {
                    @Override
                    public TimetableGenerationResult generateTimetable(TimetableSettings settings) {
                        return new TimetableGenerationResult(
                                true,
                                new Timetable("test", new ArrayList<>()),
                                "ok",
                                List.of(new ScheduleWarning(
                                        ScheduleWarning.Type.TIME_CLASH,
                                        null,
                                        null,
                                        "Overlap warning"
                                ))
                        );
                    }

                    @Override public List<Timetable> browseTimetables() { return List.of(); }
                    @Override public Optional<Timetable> viewTimetable(String name) { return Optional.of(new Timetable("test")); }
                    @Override public PendingSwapResult prepareSwap(String a, String b, String c) {
                        return new PendingSwapResult(true, false, null, "", List.of());
                    }
                    @Override public void applySwap(PendingSwapResult result) {}
                    @Override public boolean deleteTimetable(String name) { return true; }
                    @Override public TimetableSettings getLastSettings() { return new TimetableSettings(); }
                },
                new edu.flinders.timetable.service.ExportService(null) {
                    @Override
                    public Path exportTimetable(Timetable timetable, Path outputPath) {
                        return outputPath;
                    }
                }
        );

        // simulate a user flow: import CSV, browse timetables, view timetable, exit
        setInput(
                "1\n" +
                        "dummy.csv\n" +
                        "8\n" +
                        "9\n" +
                        "test\n" +
                        "0\n"
        );

        // run the console UI with the stubs
        new ConsoleUI(importManager, classManager, timetableManager).start();
    }

    @Test
    @Tag("Sunny")
    @Tag("Additional")
    @DisplayName("CU9.02 - Invalid menu option.")
    void invalidMenuOption() {

        // simulate entering invalid option and then exit
        setInput("999\n0\n");

        new ConsoleUI(dummyImport(), dummyClassManager(), dummyTimetableManager()).start();
    }

    @Test
    @Tag("Sunny")
    @Tag("Additional")
    @DisplayName("CU9.03 - CSV import failure.")
    void importFailure() {

        // simulate CSV parsing throwing an exception
        ImportManager importManager = new ImportManager(
                new CsvParser() {
                    @Override
                    public List<ClassRecord> parse(Path path) {
                        throw new CsvFormatException("bad csv");
                    }
                },
                new DataRepository() {
                    @Override
                    public ImportResult importRecords(List<ClassRecord> records) {
                        return new ImportResult();
                    }
                }
        );

        setInput("1\nfile.csv\n0\n");

        new ConsoleUI(importManager, dummyClassManager(), dummyTimetableManager()).start();
    }

    @Test
    @Tag("Sunny")
    @Tag("Additional")
    @DisplayName("CU9.04 - Empty class views.")
    void emptyClassViews() {

        // simulate browsing classes and viewing classes when none exist
        setInput("2\n3\n0\n");

        new ConsoleUI(dummyImport(), dummyClassManager(), dummyTimetableManager()).start();
    }

    @Test
    @Tag("Sunny")
    @Tag("Additional")
    @DisplayName("CU9.05 - Empty timetables.")
    void emptyTimetables() {

        // simulate browsing timetables when none exist
        setInput("8\n0\n");

        new ConsoleUI(dummyImport(), dummyClassManager(), dummyTimetableManager()).start();
    }

    @Test
    @Tag("Sunny")
    @Tag("Additional")
    @DisplayName("CU9.06 - Missing timetable view.")
    void viewMissingTimetable() {

        // simulate attempting to view a timetable that doesn't exist
        setInput("9\nunknown\n0\n");

        new ConsoleUI(dummyImport(), dummyClassManager(), dummyTimetableManager()).start();
    }

    @Test
    @Tag("Sunny")
    @Tag("Additional")
    @DisplayName("CU9.07 - Missing timetable export.")
    void exportMissingTimetable() {

        // simulate attempting to export a missing timetable
        setInput("12\nmissing\n0\n");

        new ConsoleUI(dummyImport(), dummyClassManager(), dummyTimetableManager()).start();
    }

    @Test
    @Tag("Sunny")
    @Tag("Additional")
    @DisplayName("CU9.08 - Delete cancelled.")
    void deleteCancelled() {

        // simulate user cancelling deletion of a timetable
        setInput("11\ntest\nn\n0\n");

        new ConsoleUI(dummyImport(), dummyClassManager(), dummyTimetableManager()).start();
    }

    // helper stubs for import, class manager, timetable manager
    private ImportManager dummyImport() {
        // import manager always succeeds
        return new ImportManager(
                new CsvParser() {
                    @Override public List<ClassRecord> parse(Path path) { return List.of(); }
                },
                new DataRepository() {
                    @Override public ImportResult importRecords(List<ClassRecord> records) {
                        return new ImportResult();
                    }
                }
        );
    }

    private ClassManager dummyClassManager() {
        // class manager returns empty results
        return new ClassManager(null) {
            @Override public List<ClassGroup> browseClasses() { return List.of(); }
            @Override public List<ClassRecord> viewClasses() { return List.of(); }
            @Override public List<ClassRecord> searchClasses(SearchCriteria criteria) { return List.of(); }
            @Override public Optional<ClassRecord> findClass(String key) { return Optional.empty(); }
            @Override public boolean editClass(String key, ClassRecord updated) { return true; }
            @Override public boolean deleteClass(String key) { return true; }
        };
    }

    private TimetableManager dummyTimetableManager() {
        // timetable manager returns empty or failing results
        return new TimetableManager(
                new TimetableService(null, null) {
                    @Override public TimetableGenerationResult generateTimetable(TimetableSettings settings) {
                        return new TimetableGenerationResult(true, new Timetable("test", new ArrayList<>()), "ok", List.of());
                    }
                    @Override public List<Timetable> browseTimetables() { return List.of(); }
                    @Override public Optional<Timetable> viewTimetable(String name) { return Optional.empty(); }
                    @Override public PendingSwapResult prepareSwap(String a, String b, String c) {
                        return new PendingSwapResult(false, false, null, "fail", List.of());
                    }
                    @Override public void applySwap(PendingSwapResult result) {}
                    @Override public boolean deleteTimetable(String name) { return false; }
                    @Override public TimetableSettings getLastSettings() { return new TimetableSettings(); }
                },
                new edu.flinders.timetable.service.ExportService(null) {
                    @Override public Path exportTimetable(Timetable timetable, Path outputPath) {
                        return outputPath;
                    }
                }
        );
    }
}