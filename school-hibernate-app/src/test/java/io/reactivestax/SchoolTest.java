package io.reactivestax;

import io.reactivestax.database.HibernateUtil;
import io.reactivestax.entity.Course;
import io.reactivestax.entity.Enrollment;
import io.reactivestax.entity.Grade;
import io.reactivestax.entity.Student;
import io.reactivestax.service.School;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SchoolTest {

    SessionFactory sessionFactory;
    Session session;
    School school;

    @Before
    public void setUp() {
        this.sessionFactory = HibernateUtil.getSessionFactory("hibernatetest.cfg.xml");
        this.session = this.sessionFactory.openSession();
        this.session.beginTransaction();
        this.school = new School("hibernatetest.cfg.xml");
    }

    @After
    public void tearDown() {
        this.session.getTransaction().rollback();
        this.session.close();
        this.sessionFactory.close();
    }

    @Test
    public void testAddCourse(){
        this.school.addCourse("java");
        Course course = this.session.createQuery("from Course where courseName= :courseName", Course.class).setParameter("courseName",
                "java").getSingleResult();
        assertEquals("java", course.getCourseName());
    }

    @Test
    public void testEnrollStudentWithExistingCourse(){
        this.school.addCourse("java");
        this.school.addCourse("css");
        this.school.enrollStudent(1, "John", "java");
        this.school.enrollStudent(1, "John", "css");
        Enrollment enrollment = this.session.get(Enrollment.class, 2);
        assertEquals("css", enrollment.getCourse().getCourseName());
    }

    @Test
    public void testEnrollStudentWithNonExistingCourse() {
        this.school.addCourse("css");
        this.school.enrollStudent(1, "John", "java");
        List<Enrollment> enrollmentList = this.session.createQuery("from Enrollment", Enrollment.class).getResultList();
        assertEquals(0, enrollmentList.size());
    }

    @Test
    public void testAssignGradeWithNonExistingCourse(){
        this.school.assignGrade(1, "java", 45.5);
        Grade grade = this.session.get(Grade.class, 1);
        assertNull(grade);
    }

    @Test
    public void testAssignGradeWithNonExistingStudent(){
        this.school.addCourse("java");
        this.school.assignGrade(1, "java", 45.5);
        Grade grade = this.session.get(Grade.class, 1);
        assertNull(grade);
    }

    @Test
    public void testAssignGradeWithExistingCourseAndStudent(){
        this.school.addCourse("java");
        this.school.enrollStudent(1, "john", "java");
        this.school.assignGrade(1, "java", 45.5);
        Grade grade = this.session.get(Grade.class, 1);
        assertEquals(45.5, grade.getGradeValue(), 0.1);
    }

    @Test
    public void testListCourses(){
        this.school.addCourse("java");
        this.school.addCourse("css");
        this.school.addCourse("python");
        this.school.addCourse("ruby");
        List<Course> courses = this.school.listCourses();
        assertEquals(4, courses.size());
    }

    @Test
    public void testListGradesByCourseId(){
        this.school.addCourse("java");
        this.school.addCourse("css");
        this.school.enrollStudent(1,"john", "java");
        this.school.enrollStudent(2,"jason", "java");
        this.school.assignGrade(1, "java", 45.5);
        this.school.assignGrade(2, "java", 50.5);
        List<Grade> grades = this.school.listGradesByCourseId(1);
        assertEquals(2, grades.size());
    }

    @Test
    public void testReportUniqueCourses(){
        this.school.addCourse("java");
        this.school.addCourse("css");
        this.school.addCourse("python");
        this.school.addCourse("ruby");
        List<Course> courses = this.school.reportUniqueCourses();
        assertEquals(4, courses.size());
    }

    @Test
    public void testReportUniqueStudents(){
        this.school.addCourse("java");
        this.school.addCourse("css");
        this.school.enrollStudent(1,"john", "java");
        this.school.enrollStudent(2,"jason", "java");
        List<Student> students = this.school.reportUniqueStudents();
        assertEquals(2, students.size());
    }

    @Test
    public void testReportAverageScore(){
        this.school.addCourse("java");
        this.school.addCourse("css");
        this.school.enrollStudent(1,"john", "java");
        this.school.enrollStudent(2,"jason", "java");
        this.school.assignGrade(1, "java", 45.5);
        this.school.assignGrade(2, "java", 50.5);
        double averageScore = this.school.reportAverageScore(1);
        assertEquals(48.0, averageScore, 0.1);
    }

    @Test
    public void testReportCumulativeAverage(){
        this.school.addCourse("java");
        this.school.addCourse("css");
        this.school.enrollStudent(1,"john", "java");
        this.school.enrollStudent(1,"john", "css");
        this.school.assignGrade(1, "java", 45.5);
        this.school.assignGrade(1, "css", 50.5);
        double averageScore = this.school.reportCumulativeAverage(1);
        assertEquals(48.0, averageScore, 0.1);
    }
}
