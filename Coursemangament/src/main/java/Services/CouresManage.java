package Services;

import Entities.Course;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.ws.rs.core.SecurityContext;
import java.util.List;

@Stateless
@LocalBean
public class CouresManage {

    @PersistenceContext
    private EntityManager em;

    // Method to get course details by ID
    public Course getCourseDetails(Long courseId) {
        Course course = em.find(Course.class, courseId);
        if (course == null) {
            throw new EntityNotFoundException("Course not found");
        }
        return course;
    }

    // Method to search for courses based on certain criteria
    public List<Course> searchCourses(String keyword) {
        Query query = em.createQuery("SELECT c FROM Course c WHERE c.name LIKE :keyword");
        query.setParameter("keyword", "%" + keyword + "%");
        List<Course> courses = query.getResultList();
        return courses;
    }
}
