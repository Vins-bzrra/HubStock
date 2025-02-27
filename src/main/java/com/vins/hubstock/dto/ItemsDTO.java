package com.vins.hubstock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemsDTO {
	
	private String brand;
    private String model;
    private String patrimony;
    private Date acquisitionDate;
    private String supplier;
    private String unitLocation;
    private String status;
    private String currentOwner;
    private String category;
    private String serialNumber;
    private String description;
    private Long id;

}
