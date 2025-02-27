package com.vins.hubstock.repository;

import java.util.List;
import java.util.Optional;

import com.vins.hubstock.entity.Units;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UnitsRepository extends JpaRepository<Units, Long>{
	
	Optional<Units> findById(Long id);
	
	List<Units> findByClientId(Long clientId);
}
