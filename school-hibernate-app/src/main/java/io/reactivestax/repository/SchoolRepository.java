package io.reactivestax.repository;

import io.reactivestax.entity.Course;
import io.reactivestax.entity.Grade;
import io.reactivestax.entity.Student;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;

import java.util.List;

public class SchoolRepository {
    public Course getCourseByName(String courseName, Session session) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Course> courseCriteriaQuery = criteriaBuilder.createQuery(Course.class);
        Root<Course> courseRoot = courseCriteriaQuery.from(Course.class);
        courseCriteriaQuery.where(criteriaBuilder.equal(courseRoot.get("courseName"), courseName));
        return session.createQuery(courseCriteriaQuery).getSingleResultOrNull();
    }

    public List<Course> getAllCourses(Session session) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Course> courseCriteriaQuery = criteriaBuilder.createQuery(Course.class);
        Root<Course> courseRoot = courseCriteriaQuery.from(Course.class);
        courseCriteriaQuery.select(courseRoot);
        return session.createQuery(courseCriteriaQuery).getResultList();
    }

    public List<Student> getAllStudents(Session session) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Student> studentCriteriaQuery = criteriaBuilder.createQuery(Student.class);
        Root<Student> studentRoot = studentCriteriaQuery.from(Student.class);
        studentCriteriaQuery.select(studentRoot);
        return session.createQuery(studentCriteriaQuery).getResultList();
    }

    public List<Grade> getGradesByCourseId(int courseId, Session session) {
        Course course = session.get(Course.class, courseId);
        return course.getGrades();
    }

    public List<Grade> getGradesByStudentId(int studentId, Session session) {
        Student student = session.get(Student.class, studentId);
        return student.getGrades();
    }

    public Student getStudentById(int studentId, Session session) {
        return session.get(Student.class, studentId);
    }
}
