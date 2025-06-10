package io.reactivestax;

import org.w3c.dom.ls.LSInput;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class TaskService {
    private TaskService() {
    }

    public static Map<String, Double> averageDurationByDepartment(List<Task> tasks) {
        return tasks.stream().filter(task -> task.getStatus().equals(TaskStatus.COMPLETED))
                .collect(Collectors.groupingBy(Task::getDepartment, Collectors.averagingDouble(Task::getDurationInMinutes)));
    }

    public static Map<String, Optional<Task>> fastestTaskByEmployee(List<Task> tasks) {
        return tasks.stream().filter(task -> task.getStatus().equals(TaskStatus.COMPLETED))
                .collect(Collectors.groupingBy(Task::getEmployeeId,
                        Collectors.minBy(Comparator.comparing(Task::getDurationInMinutes))));
    }

    public static List<String> top3EmployeesByProductivity(List<Task> tasks) {
        return tasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.COMPLETED)
                .collect(Collectors.groupingBy(Task::getEmployeeId,
                        Collectors.summingInt(Task::getDurationInMinutes)))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList();
    }


    public static List<String> employeesWithAllStatuses(List<Task> tasks) {
        return tasks.stream().collect(Collectors.groupingBy(Task::getEmployeeId, Collectors.groupingBy(Task::getStatus,
                Collectors.counting()))).entrySet().stream().filter(entry -> entry.getValue().size() == TaskStatus.values().length).map(Map.Entry::getKey).toList();
    }

    public static Map<String, LocalDate> busiestDayPerDepartment(List<Task> tasks) {
        return tasks.stream()
                .collect(Collectors.groupingBy(Task::getDepartment, Collectors.groupingBy(Task::getCompletedDate, Collectors.summingInt(Task::getDurationInMinutes))))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().entrySet().stream()
                .max(Comparator.comparing(Map.Entry<LocalDate, Integer>::getValue))
                .get()
                .getKey()));
    }
}
