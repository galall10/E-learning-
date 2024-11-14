package Services;

import Entities.Course;
import Entities.User;
import Entities.UserRole;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public User registerUser(User user) {
        entityManager.persist(user);
        return user;
    }

    public Object[] loginUser(String email, String password) {
        User user = entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getSingleResult();
        if (user != null && password.equals(user.getPassword())) {
            UserRole role = user.getRole();
            return new Object[]{user, role};
        }
        return null;
    }

    public List<Course> getEnrolledCourses(Long userId) {
        User user = entityManager.find(User.class, userId);
        if (user != null) {
            return user.getCourses();
        }
        return Collections.emptyList();
    }

    @Transactional
    public long countUsersByRole(UserRole role) {
        return entityManager.createQuery("SELECT COUNT(u) FROM User u WHERE u.role = :role", Long.class)
                .setParameter("role", role)
                .getSingleResult();
    }
}
