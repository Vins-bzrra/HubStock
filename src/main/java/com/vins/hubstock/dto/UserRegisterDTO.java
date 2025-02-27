package com.vins.hubstock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDTO {
	private String name;
	private String lastName;
	private String registrationNumber;
	private String password;
	private String userRole;
}
