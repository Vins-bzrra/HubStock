package com.vins.hubstock.entity;

import java.time.LocalDateTime;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "movementHistory")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemMovementHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String previousOwner;

	@Column(nullable = false)
	private String previousUnitLocation;

	@Column(nullable = false)
	private String newOwner;

	@Column(nullable = false)
	private String newUnitLocation;

	@Column(nullable = false)
	private String movementType; 

	@Column(nullable = false)
	private String nameUser;
	
	@Column(nullable = false)
	private String registrationUser;

	@Column(nullable = false)
	private LocalDateTime movementDateTime;

	@ManyToOne
	@JoinColumn(name = "item_id", referencedColumnName = "id")
	@JsonIgnore
	private Items item;

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
		ItemMovementHistory other = (ItemMovementHistory) obj;
		return Objects.equals(id, other.id);
	}
	
}
