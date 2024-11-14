package Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Notification {
    @Id
    private Long id;
    private String message;
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;


    @ManyToOne
    private Enrollment enrollment;

    public Notification(String message, Enrollment enrollment) {
        this();
        this.message = message;
        this.enrollment = enrollment;
    }
}