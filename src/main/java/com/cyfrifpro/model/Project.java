package com.cyfrifpro.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Project {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private String description;
	private BigDecimal areaInSquareFeet;
	private BigDecimal budget;
//	private LocalDate startDate;
//	private LocalDate endDate;
	private boolean confirmed;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "client_id")
	@JsonManagedReference
	private Client client; // A project is assigned to a client

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "manager_id")
	@JsonManagedReference
	private Manager manager; // A project is managed by a manager

	//Manager assignh or not
	private String status; // e.g., IN_PROGRESS, COMPLETED, CONFIRMED

	// getters and setters
}
