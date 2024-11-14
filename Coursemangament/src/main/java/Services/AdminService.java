package Services;

import Entities.Course;
import Entities.CourseStatus;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.SecurityContext;
import java.util.List;

@Stateless
@LocalBean
public class AdminService {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private SecurityContext securityContext;

    private void authenticateWithRole(String role) {
        if (!securityContext.isUserInRole(role)) {
            throw new ForbiddenException("Access denied");
        }
    }

    public void editCourse(Long courseId, Course updatedCourse) {
        authenticateWithRole("ADMIN");

        Course course = em.find(Course.class, courseId);
        if (course == null) {
            throw new NotFoundException("Course not found");
        }

        if (updatedCourse == null) {
            throw new BadRequestException("Updated course cannot be null");
        }

        course.checkPolicyCompliance();

        course.setName(updatedCourse.getName());
        course.setCategory(updatedCourse.getCategory());
        course.setCapacity(updatedCourse.getCapacity());
        course.setDuration(updatedCourse.getDuration());
        course.setRating(updatedCourse.getRating());
        course.setNumberOfStudents(updatedCourse.getNumberOfStudents());
        course.setInstructor(updatedCourse.getInstructor());
        em.merge(course);
    }

    public void removeCourse(Long courseId) {
        authenticateWithRole("ADMIN");

        Course course = em.find(Course.class, courseId);
        if (course == null) {
            throw new NotFoundException("Course not found");
        }

        course.checkPolicyCompliance();
        em.remove(course);
    }

    public List<Course> getCoursesPopularity() {
        authenticateWithRole("ADMIN");
        return em.createQuery("SELECT c FROM Course c ORDER BY c.numberOfStudents DESC", Course.class)
                .getResultList();
    }

    public List<Course> getCoursesByRatings() {
        authenticateWithRole("ADMIN");
        return em.createQuery("SELECT c FROM Course c ORDER BY c.rating DESC", Course.class)
                .getResultList();
    }

    public List<Course> getCoursesByReviews() {
        authenticateWithRole("ADMIN");
        return em.createQuery("SELECT c FROM Course c LEFT JOIN c.reviews r GROUP BY c ORDER BY COUNT(r) DESC", Course.class)
                .getResultList();
    }

    public void approveCourse(Long courseId) {
        authenticateWithRole("ADMIN");

        Course course = em.find(Course.class, courseId);
        if (course != null) {
            course.setStatus(CourseStatus.APPROVED);
            em.merge(course);
        } else {
            throw new EntityNotFoundException("Course with ID " + courseId + " not found.");
        }
    }

    public void rejectCourse(Long courseId) {
        authenticateWithRole("ADMIN");

        Course course = em.find(Course.class, courseId);
        if (course != null) {
            course.setStatus(CourseStatus.REJECTED);
            em.merge(course);
        } else {
            throw new EntityNotFoundException("Course with ID " + courseId + " not found.");
        }
    }

    public List<Course> getAllApprovedCourses() {
        return em.createQuery("SELECT c FROM Course c WHERE c.status = :status", Course.class)
                .setParameter("status", CourseStatus.APPROVED)
                .getResultList();
    }

    public List<Course> getCoursesFor_Check() {
        authenticateWithRole("ADMIN");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Course> cq = cb.createQuery(Course.class);
        Root<Course> root = cq.from(Course.class);
        cq.select(root);
        cq.where(cb.equal(root.get("status"), CourseStatus.SUBMITTED));

        return em.createQuery(cq).getResultList();
    }
}
