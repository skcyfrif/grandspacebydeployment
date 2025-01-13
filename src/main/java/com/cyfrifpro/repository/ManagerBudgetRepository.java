package com.cyfrifpro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cyfrifpro.model.ManagerBudget;

@Repository
public interface ManagerBudgetRepository extends JpaRepository<ManagerBudget, Long> {
	
	// Find the ManagerBudget by both projectId and managerId
    Optional<ManagerBudget> findByProjectIdAndManagerId(Long projectId, Long managerId);
	
//	List<ManagerBudget> findByProjectId(Long projectId);
	
	Optional<ManagerBudget> findByProjectId(Long projectId);

	Optional<ManagerBudget> findByManagerId(Long managerId);

}
