package com.vins.hubstock.repository;

import java.util.List;
import java.util.Optional;

import com.vins.hubstock.entity.Items;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface ItemRepository extends JpaRepository<Items, Long>, JpaSpecificationExecutor<Items>{
	
	Optional<Items> findById(Long id);
	
	Optional<Items> findByPatrimony(String patrimony);
	
	Optional<Items> findBySerialNumber(String serialNumber);
	
	boolean existsByPatrimonyAndIdNot(String patrimony, Long id);
	
	List<Items> findByCurrentOwnerAndUnitLocation(String currentOwner, String unitLocation);

	List<Items> findByCurrentOwner(String client);
}
