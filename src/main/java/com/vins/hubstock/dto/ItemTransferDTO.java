package com.vins.hubstock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemTransferDTO {

	private String patrimony;
	private String newOwner;
	private String newUnitLocation;
}
