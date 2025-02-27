package com.vins.hubstock.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.vins.hubstock.entity.ItemMovementHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ItemMovementHistoryRepository extends JpaRepository<ItemMovementHistory, Long> {
	List<ItemMovementHistory> findByMovementDateTimeBetween(LocalDateTime startDate, LocalDateTime endDate);
}
