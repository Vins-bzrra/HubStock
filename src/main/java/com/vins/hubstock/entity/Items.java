package com.vins.hubstock.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "item")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Items {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String brand;

	@Column(nullable = false)
	private String model;

	@Column(nullable = false, unique = true)
	private String patrimony;

	@Column(nullable = false)
	private Date acquisitionDate;

	@Column(nullable = false)
	private String supplier;

	@Column(nullable = false)
	private String unitLocation;

	@Column(nullable = false)
	private String status;

	@Column(nullable = false)
	private String currentOwner;

	@Column(nullable = false)
	private String category;
	
	@Column(nullable = false, unique = true)
	private String serialNumber;

	@Column
	private String description;

	@OneToMany(mappedBy = "item", cascade = { CascadeType.ALL, CascadeType.REMOVE })
	@JsonIgnore
	private List<ItemMovementHistory> movementHistory = new ArrayList<>();

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Items other = (Items) obj;
		return Objects.equals(id, other.id);
	}

}
