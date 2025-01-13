package com.cyfrifpro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cyfrifpro.model.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

	// Find project by ID
	Optional<Project> findById(Long id);

	// Find projects by client ID (optional example)
	List<Project> findByClientId(Long clientId);

	// Find projects by manager ID (optional example)
	List<Project> findByManagerId(Long managerId);

	// Custom query to count projects with a specific status
	long countByStatus(String status);

	// Custom query method to find projects by status
	List<Project> findByStatus(String status);

	@Query("SELECT COUNT(DISTINCT p.manager.id) FROM Project p WHERE p.manager IS NOT NULL")
	long countActiveManagers();

}
