package Services;

import Entities.*;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.SecurityContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Stateless
@LocalBean
public class StudentService {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private SecurityContext securityContext;

    @Inject
    private NotificationProducer notificationProducer;

    // Inject the client for communication with the other microservice
    @Inject
    private OtherMicroserviceClient otherMicroserviceClient;

    @Transactional
    public void makeEnrollment(Long courseId) {
        authenticateWithRole("STUDENT");

        Course course = em.find(Course.class, courseId);
        if (course == null) {
            throw new NotFoundException("Course not found");
        }

        Enrollment enrollment = new Enrollment(getCurrentUser(), course, new Date());
        em.persist(enrollment);

        notificationProducer.sendNotification("You have been enrolled in course " + course.getName(), enrollment.getId());
    }

    @Transactional
    public void cancelEnrollment(Long enrollmentId, String courseName) {
        authenticateWithRole("STUDENT");

        Enrollment enrollment = em.find(Enrollment.class, enrollmentId);
        if (enrollment == null) {
            throw new NotFoundException("Enrollment not found");
        }

        if (!Objects.equals(enrollment.getStudent().getId(), getCurrentUser().getId())) {
            throw new ForbiddenException("You are not authorized to cancel this enrollment");
        }

        em.remove(enrollment);
        Course course = enrollment.getCourse();
        course.setNumberOfStudents(course.getNumberOfStudents() - 1);
        notificationProducer.sendNotification("You have canceled the enrollment " + courseName, enrollmentId);
    }

    @Transactional
    public void addReview(Long courseId, String comment, double rating) {
        authenticateWithRole("STUDENT");

        Course course = em.find(Course.class, courseId);
        if (course == null) {
            throw new NotFoundException("Course not found");
        }

        Review review = new Review();
        review.setUser(getCurrentUser());
        review.setCourse(course);
        review.setComment(comment);
        review.setReviewDate(new Date());
        em.persist(review);

        updateCourseRating(course, rating);
    }

    private void updateCourseRating(Course course, double newRating) {
        List<Review> reviews = course.getReviews();
        double totalRating = reviews.stream().mapToDouble(Review::getRating).sum() + newRating;
        double updatedRating = totalRating / (reviews.size() + 1);

        course.setRating(updatedRating);
        em.merge(course);
    }

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
