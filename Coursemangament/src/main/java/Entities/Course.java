package Entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String category;
    private int capacity;
    private double duration;
    private double rating;
    private int numberOfStudents;

    @ManyToOne
    private User instructor;

    @OneToMany(mappedBy = "course")
    private List<Enrollment> enrolledStudents;

    @OneToMany(mappedBy = "course")
    private List<Review> reviews;

    @Enumerated(EnumType.STRING)
    private CourseStatus status; // Enum (SUBMITTED, IN_REVIEW, APPROVED, NEEDS_REVISION)

    private boolean policyCompliant; // Field to indicate whether the course complies with platform policies

    // Default constructor
    public Course() {}

    // Parameterized constructor
    public Course(String name, String category, int capacity, double duration, double rating, int numberOfStudents, User instructor) {
        this.name = name;
        this.category = category;
        this.capacity = capacity;
        this.duration = duration;
        this.rating = rating;
        this.numberOfStudents = numberOfStudents;
        this.instructor = instructor;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getNumberOfStudents() {
        return numberOfStudents;
    }

    public void setNumberOfStudents(int numberOfStudents) {
        this.numberOfStudents = numberOfStudents;
    }

    public User getInstructor() {
        return instructor;
    }

    public void setInstructor(User instructor) {
        this.instructor = instructor;
    }

    public List<Enrollment> getEnrolledStudents() {
        return enrolledStudents;
    }

    public void setEnrolledStudents(List<Enrollment> enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public CourseStatus getStatus() {
        return status;
    }

    public void setStatus(CourseStatus status) {
        this.status = status;
    }

    public boolean isPolicyCompliant() {
        return policyCompliant;
    }

    public void setPolicyCompliant(boolean policyCompliant) {
        this.policyCompliant = policyCompliant;
    }

    // Method to perform policy checks internally
    public void checkPolicyCompliance() {
        // Perform policy checks here
        // Example: Check course content, metadata, etc. for policy compliance
        // Update policyCompliant field accordingly
        // This method can be invoked when creating, editing, or updating courses
    }
}
