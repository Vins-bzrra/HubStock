package com.vins.hubstock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class ServiceDetailsDTO {
    private String title;
    private Long serviceId;
    private Boolean status;
    private String description;
    private LocalDate dateCreateService;
    private LocalDate dateServiceExecution;
    private String item;
    private String quantity;
    private String authorName;
    private String nameUserRequested;
    private String registrationNumberRequested;
}
