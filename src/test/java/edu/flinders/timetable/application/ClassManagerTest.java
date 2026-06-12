package edu.flinders.timetable.application;

import edu.flinders.timetable.model.ClassGroup;
import edu.flinders.timetable.model.ClassRecord;
import edu.flinders.timetable.model.SearchCriteria;
import edu.flinders.timetable.service.ClassService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.DisplayName.class) // Ensures tests run in order by their display name
class ClassManagerTest {

    private ClassManager classManager;

    @BeforeEach
    void setup() {
        // Initialize ClassService with null data source (could be a mock or real service)
        ClassService service = new ClassService(null);

        // Create ClassManager using the service
        classManager = new ClassManager(service);
    }

    @Test
    @Tag("Thomas")
    @Tag("Core")
    @DisplayName("CM1.01 - Verifies that browsing classes retrieves a non-null list of class groups.")
    void browseClass() {
        // Call method to retrieve all class groups
        List<ClassGroup> result = classManager.browseClasses();

        // Check that the result is not null, ensuring the method returns a valid object
        assertNotNull(result);
    }

    @Test
    @Tag("Thomas")
    @Tag("Core")
    @DisplayName("CM1.02 - Checks that viewing all classes returns a non-null list of class records.")
    void cm102ViewClass() {
        // Call method to view all individual class records
        List<ClassRecord> result = classManager.viewClasses();

        // Verify that the returned list is not null
        assertNotNull(result);
    }

    @Test
    @Tag("Thomas")
    @Tag("Core")
    @DisplayName("CM1.03 - Ensures that searching classes with a given criteria returns a non-null list.")
    void searchClass() {
        // Create empty search criteria (could be extended with filters)
        SearchCriteria criteria = new SearchCriteria();

        // Perform search using criteria
        List<ClassRecord> result = classManager.searchClasses(criteria);

        // Verify that the search returns a non-null result
        assertNotNull(result);
    }

    @Test
    @Tag("Thomas")
    @Tag("Core")
    @DisplayName("CM1.04 - Confirms that attempting to find a class by key returns a non-null Optional object.")
    void findClass() {
        // Attempt to find a class using a key
        Optional<ClassRecord> result = classManager.findClass("KEY");

        // Ensure that the Optional object itself is not null
        assertNotNull(result);
    }

    @Test
    @Tag("Thomas")
    @Tag("Critical")
    @DisplayName("CM1.05 - Validates that editing a class does not throw exceptions and completes successfully.")
    void editClass() {
        // Assert that editing a class with a given key does not throw any exceptions
        assertDoesNotThrow(() ->
                classManager.editClass("KEY", null)
        );
    }

    @Test
    @Tag("Thomas")
    @Tag("Critical")
    @DisplayName("CM1.06 - Checks that deleting a class by key executes without exceptions")
    void deleteClass() {
        // Assert that deleting a class with a given key does not throw any exceptions
        assertDoesNotThrow(() ->
                classManager.deleteClass("KEY")
        );
    }
}