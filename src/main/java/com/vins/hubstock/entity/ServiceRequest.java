package com.vins.hubstock.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "service_request")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServiceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String authorName;

    @Column(nullable = false)
    private String authorRegistration;

    @Column(nullable = false)
    private String item;

    @Column(nullable = false)
    private String quantity;

    @Column(nullable = false)
    private LocalDate dateServiceExecution;

    @Column(nullable = false)
    private LocalDateTime dateCreateService;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Boolean iscompleted;

    @Column
    private String finishBy;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users userRequested;

}
