package io.reactivestax.service;

import io.reactivestax.database.HibernateUtil;
import io.reactivestax.entity.Course;
import io.reactivestax.entity.Enrollment;
import io.reactivestax.entity.Grade;
import io.reactivestax.entity.Student;
import io.reactivestax.repository.SchoolRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.hibernate.Session;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class School {

    Logger logger = Logger.getLogger(School.class.getName());

    // Add course
    public void addCourse(String courseName) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Course course = new Course();
            course.setCourseName(courseName);
            session.persist(course);
            session.getTransaction().commit();
        }
    }

    // Enroll student
    public void enrollStudent(int studentId, String studentName, String courseName) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            SchoolRepository schoolRepository = new SchoolRepository();
            Course course = schoolRepository.getCourseByName(courseName, session);
            if (course == null) {
                logger.log(Level.WARNING, "Error: Course {0} does not exist.", courseName);
            } else {
                Student student = schoolRepository.getStudentById(studentId, session);
                Enrollment enrollment = new Enrollment();
                enrollment.setCourse(course);
                if (student == null) {
                    student = new Student();
                    student.setStudentId(studentId);
                    student.setStudentName(studentName);
                    enrollment.setStudent(student);
                    student.getEnrollments().add(enrollment);
                    session.persist(student);
                } else {
                    enrollment.setStudent(student);
                    session.persist(enrollment);
                }
            }
            session.getTransaction().commit();
        }
    }

    // Assign grade
    public void assignGrade(int studentId, String courseName, double grade) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            SchoolRepository schoolRepository = new SchoolRepository();
            Course course = schoolRepository.getCourseByName(courseName, session);
            if (course == null) {
                logger.log(Level.WARNING, "Error: Course {0} does not exist.", courseName);
            } else {
                Student student = schoolRepository.getStudentById(studentId, session);
                if (student == null) {
                    logger.log(Level.WARNING, "Error: Student with student id {0} does not exist.", studentId);
                } else {
                    Grade gradeEntity = new Grade();
                    gradeEntity.setGradeValue(grade);
                    gradeEntity.setStudent(student);
                    gradeEntity.setCourse(course);
                    session.persist(gradeEntity);
                }
            }
            session.getTransaction().commit();
        }
    }

    public void listCourses() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            SchoolRepository schoolRepository = new SchoolRepository();
            List<Course> allCourses = schoolRepository.getAllCourses(session);
            for (Course course : allCourses) {
                System.out.println(course.getCourseName());
            }
            session.getTransaction().commit();
        }
    }

    public void listGradesByCourseId(int courseId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            SchoolRepository schoolRepository = new SchoolRepository();
            List<Grade> gradeList = schoolRepository.getGradesByCourseId(courseId, session);
            for (Grade grade : gradeList) {
                System.out.println(grade.getGradeValue());
            }
        }
    }

    public void reportUniqueCourses() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            SchoolRepository schoolRepository = new SchoolRepository();
            List<Course> allCourses = schoolRepository.getAllCourses(session);
            for (Course course : allCourses) {
                System.out.println(course.getCourseName());
            }
            session.getTransaction().commit();
        }
    }

    public void reportUniqueStudents() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            SchoolRepository schoolRepository = new SchoolRepository();
            List<Student> allStudents = schoolRepository.getAllStudents(session);
            for (Student student : allStudents) {
                System.out.println(student.getStudentName());
            }
            session.getTransaction().commit();
        }
    }

    public void reportAverageScore(int courseId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            SchoolRepository schoolRepository = new SchoolRepository();
            List<Grade> gradeList = schoolRepository.getGradesByCourseId(courseId, session);
            double sum = 0;
            for (Grade grade : gradeList) {
                sum += grade.getGradeValue();
            }
            System.out.println("Average is: " + sum/gradeList.size());
        }
    }

    public void reportCumulativeAverage(int studentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            SchoolRepository schoolRepository = new SchoolRepository();
            List<Grade> gradeList = schoolRepository.getGradesByStudentId(studentId, session);
            double sum = 0;
            for (Grade grade : gradeList) {
                sum += grade.getGradeValue();
            }
            System.out.println("Average is: " + sum/gradeList.size());
        }
    }
}