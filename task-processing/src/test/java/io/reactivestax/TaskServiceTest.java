package io.reactivestax;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceTest {
    private List<Task> tasks;

    @BeforeEach
    void setUp() {
        tasks = Arrays.asList(
                new Task("T1", "E1", "HR", LocalDate.of(2023, 1, 1), 30, TaskStatus.COMPLETED),
                new Task("T2", "E1", "HR", LocalDate.of(2023, 1, 2), 40, TaskStatus.CANCELLED),
                new Task("T3", "E2", "Engineering", LocalDate.of(2023, 1, 1), 20, TaskStatus.COMPLETED),
                new Task("T4", "E3", "Engineering", LocalDate.of(2023, 1, 1), 15, TaskStatus.COMPLETED),
                new Task("T5", "E3", "Engineering", LocalDate.of(2023, 1, 2), 10, TaskStatus.COMPLETED),
                new Task("T6", "E3", "Engineering", LocalDate.of(2023, 1, 2), 25, TaskStatus.CANCELLED),
                new Task("T7", "E3", "Engineering", LocalDate.of(2023, 1, 3), 30, TaskStatus.FAILED),
                new Task("T8", "E2", "Sales", LocalDate.of(2023, 1, 2), 50, TaskStatus.FAILED),
                new Task("T9", "E2", "Sales", LocalDate.of(2023, 1, 3), 60, TaskStatus.CANCELLED),
                new Task("T10", "E2", "Sales", LocalDate.of(2023, 1, 3), 70, TaskStatus.COMPLETED)
        );
    }

    @Test
    void testAverageDurationByDepartment() {
        Map<String, Double> result = TaskService.averageDurationByDepartment(tasks);
        assertEquals(3, result.size());
        assertEquals(30.0, result.get("HR"), 0.01);
        assertEquals(15.0, result.get("Engineering"), 0.01); // (20+15+10)/3
    }

    @Test
    void testFastestTaskByEmployee() {
        Map<String, Optional<Task>> result = TaskService.fastestTaskByEmployee(tasks);
        assertEquals(3, result.size());
        assertTrue(result.get("E3").isPresent());
        assertEquals("T5", result.get("E3").get().getTaskId());
    }

    @Test
    void testTop3EmployeesByProductivity() {
        List<String> result = TaskService.top3EmployeesByProductivity(tasks);
        assertEquals(Arrays.asList("E2", "E1", "E3"), result);
    }

    @Test
    void testEmployeesWithAllStatuses() {
        List<String> result = TaskService.employeesWithAllStatuses(tasks);
        assertIterableEquals(List.of("E2", "E3"), result);
    }

    @Test
    void testBusiestDayPerDepartment() {
        Map<String, LocalDate> result = TaskService.busiestDayPerDepartment(tasks);
        System.out.println(result);
        assertEquals(LocalDate.of(2023, 1, 1), result.get("Engineering")); // T5 only
        assertEquals(LocalDate.of(2023, 1, 1), result.get("HR"));
    }
}