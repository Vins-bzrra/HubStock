package com.vins.hubstock.repository;

import com.vins.hubstock.entity.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {

    @Query("SELECT sr FROM ServiceRequest sr WHERE sr.userRequested.id = :userId AND sr.iscompleted = false")
    List<ServiceRequest> findPendingByUserId(@Param("userId") Long userId);

    @Query("SELECT sr FROM ServiceRequest sr WHERE sr.iscompleted = false")
    List<ServiceRequest> findAllPending();

    @Query("SELECT sr FROM ServiceRequest sr WHERE sr.iscompleted = true")
    List<ServiceRequest> findAllFinish();
}
