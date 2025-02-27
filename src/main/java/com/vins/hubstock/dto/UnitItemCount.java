package com.vins.hubstock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UnitItemCount {
	private String unit;
	private String model;
	private String brand;
	private String category;
	private Long quantity;
	
}
