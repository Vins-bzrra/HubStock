package com.vins.hubstock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServicePendingDTO {
    private String title;
    private Long serviceId;
    private Boolean status;
    private String description;
    private LocalDate dateServiceExecution;
    private String item;
    private String quantity;
    private String authorName;
    private String nameUserRequested;
}
