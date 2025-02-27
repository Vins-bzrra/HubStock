package com.vins.hubstock.repository;

import com.vins.hubstock.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByRegistrationNumber(String registrationNumber);

    Optional<Users> findById(Long id);
}
