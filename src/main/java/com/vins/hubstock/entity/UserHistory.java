package com.vins.hubstock.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Table(name = "userHistory")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserHistory {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nameUserRemoved; 
    
    @Column(nullable = false)
    private String lastNameUserRemoved; 
    
    @Column(nullable = false)
    private String registrationNumberUserRemoved; 
    
    @Column(nullable = false)
    private String registrationNumberResponsibleUser; 
    
    @Column(nullable = false)
    private LocalDateTime changeDateTime;
    
}
