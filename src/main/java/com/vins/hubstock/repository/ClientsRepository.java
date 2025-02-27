package com.vins.hubstock.repository;

import java.util.Optional;

import com.vins.hubstock.entity.Clients;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ClientsRepository extends JpaRepository<Clients, Long>{
	
	Optional<Clients> findById(Long id);
}
