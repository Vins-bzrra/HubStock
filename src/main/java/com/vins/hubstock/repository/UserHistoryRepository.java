package com.vins.hubstock.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.vins.hubstock.entity.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserHistoryRepository extends JpaRepository<UserHistory, Long> {
	List<UserHistory> findByChangeDateTimeBetween(LocalDateTime startDate, LocalDateTime endDate);
}

