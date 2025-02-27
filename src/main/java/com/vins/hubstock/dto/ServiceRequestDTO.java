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
public class ServiceRequestDTO {
	private String requestedRegistration;
	private String item;
	private String quantity;
	private LocalDate dateServiceExecution;
	private String description;
	private String title;
}
