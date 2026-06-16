package edu.flinders.timetable.service;

import edu.flinders.timetable.data.DataRepository;
import edu.flinders.timetable.model.ClassGroup;
import edu.flinders.timetable.model.ClassRecord;
import edu.flinders.timetable.model.SearchCriteria;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class ClassServiceTest {

    static class StubRepository extends DataRepository {

        List<ClassRecord> records;

        boolean replaceResult = true;
        boolean deleteResult = true;
        Optional<ClassRecord> findResult = Optional.empty();

        StubRepository(List<ClassRecord> records) {
            this.records = records;
        }

        @Override
        public List<ClassRecord> listClassRecords() {
            return records;
        }

        @Override
        public Optional<ClassRecord> findClassByImportKey(String importKey) {
            return findResult;
        }

        @Override
        public boolean replaceClass(String importKey, ClassRecord updatedRecord) {
            return replaceResult;
        }

        @Override
        public boolean deleteClass(String importKey) {
            return deleteResult;
        }
    }

    private ClassRecord record(String code, String campus, int semester, String format, int instance, LocalDate date) {
        return new ClassRecord(
                code,
                "Test Topic",
                "In person",
                campus,
                semester,
                1,
                format,
                instance,
                date,
                date.plusWeeks(10),
                java.time.DayOfWeek.MONDAY,
                java.time.LocalTime.of(9, 0),
                java.time.LocalTime.of(10, 0),
                "Building",
                "Room"
        );
    }

    @Test
    @Tag("Ryan")
    @Tag("Core")
    @DisplayName("CS13.01 - Browse class groups.")
    void cs101() {

        List<ClassRecord> input = List.of(
                record("COMP1001", "City", 1, "Lecture", 1, LocalDate.of(2026, 1, 1)),
                record("COMP1001", "City", 1, "Lecture", 2, LocalDate.of(2026, 1, 2))
        );

        ClassService service = new ClassService(new StubRepository(input));

        List<ClassGroup> result = service.browseClasses();

        assertEquals(1, result.size());
    }

    @Test
    @Tag("Ryan")
    @Tag("Core")
    @DisplayName("CS13.02 - View sorted groups.")
    void cs102() {

        ClassRecord r1 = record("COMP2002", "City", 1, "Lecture", 2, LocalDate.of(2026, 1, 3));
        ClassRecord r2 = record("COMP1001", "City", 1, "Lecture", 1, LocalDate.of(2026, 1, 1));

        ClassService service = new ClassService(new StubRepository(List.of(r1, r2)));

        List<ClassRecord> result = service.viewClasses();

        assertEquals("COMP1001", result.get(0).getTopicCode());
    }

    @Test
    @Tag("Ryan")
    @Tag("Core")
    @DisplayName("CS13.03 - Null search criteria.")
    void cs103() {

        ClassRecord r1 = record("COMP1001", "City", 1, "Lecture", 1, LocalDate.of(2026, 1, 1));

        ClassService service = new ClassService(new StubRepository(List.of(r1)));

        List<ClassRecord> result = service.searchClasses(null);

        assertEquals(1, result.size());
    }

    @Test
    @Tag("Ryan")
    @Tag("Core")
    @DisplayName("CS13.04 - Filter class search.")
    void cs104() {

        ClassRecord match = record("COMP1001", "City", 1, "Lecture", 1, LocalDate.of(2026, 1, 1));
        ClassRecord noMatch = record("MATH1001", "City", 1, "Lecture", 1, LocalDate.of(2026, 1, 1));

        SearchCriteria criteria = new SearchCriteria() {
            @Override
            public boolean matches(ClassRecord record) {
                return "COMP1001".equals(record.getTopicCode());
            }
        };

        ClassService service = new ClassService(new StubRepository(List.of(match, noMatch)));

        List<ClassRecord> result = service.searchClasses(criteria);

        assertEquals(1, result.size());
    }

    @Test
    @Tag("Ryan")
    @Tag("Core")
    @DisplayName("CS13.05 - Find class key.")
    void cs105() {

        ClassRecord r1 = record("COMP1001", "City", 1, "Lecture", 1, LocalDate.of(2026, 1, 1));

        StubRepository repo = new StubRepository(List.of(r1));
        repo.findResult = Optional.of(r1);

        ClassService service = new ClassService(repo);

        Optional<ClassRecord> result = service.findClassByKey("key");

        assertTrue(result.isPresent());
    }

    @Test
    @Tag("Ryan")
    @Tag("Core")
    @DisplayName("CS13.06 - Edit class delegates.")
    void cs106() {

        StubRepository repo = new StubRepository(List.of());
        repo.replaceResult = true;

        ClassService service = new ClassService(repo);

        boolean result = service.editClass("key", record("COMP1001", "City", 1, "Lecture", 1, LocalDate.now()));

        assertTrue(result);
    }

    @Test
    @Tag("Ryan")
    @Tag("Core")
    @DisplayName("CS13.07 - Delete class delegates.")
    void cs107() {

        StubRepository repo = new StubRepository(List.of());
        repo.deleteResult = true;

        ClassService service = new ClassService(repo);

        boolean result = service.deleteClass("key");

        assertTrue(result);
    }

    @Test
    @Tag("Ryan")
    @Tag("Core")
    @DisplayName("CS13.08 - Multiple group browse.")
    void cs108() {

        List<ClassRecord> input = List.of(
                record("COMP1001", "City", 1, "Lecture", 1, LocalDate.of(2026, 1, 1)),
                record("COMP1001", "City", 1, "Lecture", 2, LocalDate.of(2026, 1, 2)),
                record("COMP2002", "Tonsley", 1, "Tutorial", 1, LocalDate.of(2026, 1, 1))
        );

        ClassService service = new ClassService(new StubRepository(input));

        List<ClassGroup> groups = service.browseClasses();

        assertTrue(groups.size() >= 1);
    }
}