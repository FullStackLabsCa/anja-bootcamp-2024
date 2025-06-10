package io.reactivestax;

import java.time.LocalDate;

public class Task {
    private String taskId;
    private String employeeId;
    private String department;
    private LocalDate completedDate;
    private int durationInMinutes;
    private TaskStatus status;

    public Task(String taskId, String employeeId, String department,
                LocalDate completedDate, int durationInMinutes, TaskStatus status) {
        this.taskId = taskId;
        this.employeeId = employeeId;
        this.department = department;
        this.completedDate = completedDate;
        this.durationInMinutes = durationInMinutes;
        this.status = status;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getDepartment() {
        return department;
    }

    public LocalDate getCompletedDate() {
        return completedDate;
    }

    public int getDurationInMinutes() {
        return durationInMinutes;
    }

    public TaskStatus getStatus() {
        return status;
    }
}
