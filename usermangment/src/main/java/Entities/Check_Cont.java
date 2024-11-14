package Entities;

import jakarta.persistence.*;

@Entity
public class Check_Cont {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User Admin;

    @OneToOne
    private Course course;

    private boolean approved_on;
}
