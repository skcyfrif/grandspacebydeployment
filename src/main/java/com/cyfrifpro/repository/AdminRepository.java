package com.cyfrifpro.repository;

import com.cyfrifpro.model.Admin;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
	
	Optional<Admin> findByEmail(String email);
	boolean existsByEmail(String email);
	
}
