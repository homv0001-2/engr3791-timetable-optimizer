package edu.flinders.timetable;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MainTest {

    @Test
    @Tag("Jeff")
        @Tag("Critical")
    @DisplayName("MA20.01 - Test main method.")
    void mainDoesNotHang() {

        // simulate user typing "exit" so the application terminates immediately
        System.setIn(new ByteArrayInputStream("exit\n".getBytes()));

        // verify that main runs without throwing exceptions or hanging
        assertDoesNotThrow(() -> Main.main(new String[]{}));
    }
}