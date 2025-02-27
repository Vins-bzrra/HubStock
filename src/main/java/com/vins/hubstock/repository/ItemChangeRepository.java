package com.vins.hubstock.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.vins.hubstock.entity.ItemChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ItemChangeRepository extends JpaRepository<ItemChange, Long> {
	List<ItemChange> findByChangeDateTimeBetween(LocalDateTime startDate, LocalDateTime endDate);
}
