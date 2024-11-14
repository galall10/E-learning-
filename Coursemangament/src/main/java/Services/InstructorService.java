package Services;

import Entities.Course;
import Entities.Enrollment;
import Entities.User;
import Entities.UserRole;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.SecurityContext;
import java.util.Date;

@Stateless
@LocalBean
public class InstructorService {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private SecurityContext securityContext; // Inject for authorization

    private OtherMicroserviceClient otherMicroserviceClient;

    // Method to create a new course
    @Transactional
    public void createCourse(String name, double duration, String category, double rating, int capacity) {
        authenticateWithRole("INSTRUCTOR");

        User instructor = getCurrentUser();
        int numberOfStudents = 0;
        // Create the course
        Course course = new Course(name, category, capacity, duration, rating, numberOfStudents, instructor);
        em.persist(course);
    }

    // Method to accept student enrollment
    @Transactional
    public void acceptEnrollment(Long courseId, Long userId) {
        authenticateWithRole("INSTRUCTOR");

        Course course = em.find(Course.class, courseId);
        if (course == null) {
            throw new NotFoundException("Course not found");
        }

        // Check if the course belongs to the instructor
        if (!course.getInstructor().getId().equals(getCurrentUser().getId())) {
            throw new ForbiddenException("You are not the instructor of this course");
        }
        User student = em.find(User.class, userId);
        if (student == null) {
            throw new NotFoundException("Student not found");
        }
        // Check if the student is already enrolled in the course
        for (Enrollment enrollment : course.getEnrolledStudents()) {
            if (enrollment.getStudent() != null && enrollment.getStudent().getId().equals(student.getId())) {
                throw new IllegalStateException("Student is already enrolled in the course");
            }
        }
        // Check course capacity
        if (course.getNumberOfStudents() >= course.getCapacity()) {
            throw new IllegalStateException("Course has reached its maximum capacity");
        }

        // Create an enrollment object and associate the student with the course
        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(course);
        enrollment.setStudent(student);
        enrollment.setEnrollmentDate(new Date());
        em.persist(enrollment);

        // Update the number of students enrolled in the course
        course.setNumberOfStudents(course.getNumberOfStudents() + 1);
    }

    // Method to reject student enrollment
    @Transactional
    public void rejectEnrollment(Long courseId, Long userId) {
        authenticateWithRole("INSTRUCTOR");

        Course course = em.find(Course.class, courseId);
        if (course == null) {
            throw new NotFoundException("Course not found");
        }

        // Check if the course belongs to the instructor
        if (!course.getInstructor().getId().equals(getCurrentUser().getId())) {
            throw new ForbiddenException("You are not the instructor of this course");
        }

        User student = em.find(User.class, userId);
        if (student == null) {
            throw new NotFoundException("Student not found");
        }

        // Remove the enrollment
        for (Enrollment enrollment : course.getEnrolledStudents()) {
            if (enrollment.getStudent() != null && enrollment.getStudent().getId().equals(student.getId())) {
                em.remove(enrollment);
                return;
            }
        }

        throw new NotFoundException("Enrollment not found for the specified student in this course");
    }

    // Helper method to get the current authenticated user
    private void authenticateWithRole(String role) {
        // Retrieve the current user's name from the Principal
        String userName = securityContext.getUserPrincipal().getName();

        // Use the retrieved user name to perform further authentication
        Object[] loginResult = otherMicroserviceClient.loginUser(userName, "password");

        if (loginResult == null) {
            throw new ForbiddenException("Invalid credentials");
        }
        UserRole userRole = (UserRole) loginResult[1];
        if (userRole != UserRole.valueOf(role)) {
            throw new ForbiddenException("Access denied");
        }
    }


    private User getCurrentUser() {
        Object[] loginResult = otherMicroserviceClient.loginUser(securityContext.getUserPrincipal().getName(), "password");

        if (loginResult == null) {
            throw new ForbiddenException("Invalid credentials");
        }

        return (User) loginResult[0];
    }

}
