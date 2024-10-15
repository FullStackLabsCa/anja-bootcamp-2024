package io.reactivestax;

import io.reactivestax.database.HibernateUtil;
import io.reactivestax.service.School;

import java.util.Scanner;
import java.util.logging.Logger;

// Command-line interface for enrolling students
public class SchoolCLI {
    private static final School school = new School("hibernate.cfg.xml");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the School Management System");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            String[] command = input.split(" ");

            switch (command[0]) {
                case "add_course":
                    school.addCourse(command[1]);
                    break;
                case "enroll_student":
                    school.enrollStudent(Integer.parseInt(command[1]), command[2], command[3]);
                    break;
                case "assign_grade":
                    school.assignGrade(Integer.parseInt(command[1]), command[2], Double.parseDouble(command[3]));
                    break;
                case "list_courses":
                    System.out.println(school.listCourses());
                    break;
                case "list_grades":
                    System.out.println(school.listGradesByCourseId(Integer.parseInt(command[1])));
                    break;
                case "report_unique_courses":
                    System.out.println(school.reportUniqueCourses());
                    break;
                case "report_unique_students":
                    System.out.println(school.reportUniqueStudents());
                    break;
                case "report_average_score":
                    System.out.println(school.reportAverageScore(Integer.parseInt(command[1])));
                    break;
                case "report_cumulative_average":
                    System.out.println(school.reportCumulativeAverage(Integer.parseInt(command[1])));
                    break;
                case "exit":
                    System.out.println("Exiting...");
                    HibernateUtil.getSessionFactory().close();
                    return;
                default:
                    System.out.println("Unknown command");
            }
        }

    }
}