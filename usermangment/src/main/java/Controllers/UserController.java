package Controllers;

import Entities.Course;
import Entities.User;
import Entities.UserRole;
import Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            userService.registerUser(user);
            return ResponseEntity.ok("Registration successful");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
        Object[] userAndRole = userService.loginUser(email, password);
        if (userAndRole != null) {
            User user = (User) userAndRole[0];
            UserRole role = (UserRole) userAndRole[1];
            return ResponseEntity.ok("Login successful. User role: " + role);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @GetMapping("/{userId}/courses")
    public List<Course> getEnrolledCourses(@PathVariable Long userId) {
        return userService.getEnrolledCourses(userId);
    }

    @GetMapping("/user-trace")
    public ResponseEntity<String> getUserCount(@RequestParam("role") UserRole role) {
        long count = userService.countUsersByRole(role);
        return ResponseEntity.ok("Number of " + role + "s: " + count);
    }
}
