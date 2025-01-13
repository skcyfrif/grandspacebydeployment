package com.cyfrifpro.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.cyfrifpro.model.Client;
import com.cyfrifpro.model.Manager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ProjectResponse {

	private Long id;
	private String name;
	private String description;
	private BigDecimal budget;
	private BigDecimal areaInSquareFeet;
	private LocalDate startDate;
	private LocalDate endDate;
	private Client client; // Nested DTO for Client
	private Manager manager; // Nested DTO for Manager
	private boolean confirmed;
	private String status; // e.g., IN_PROGRESS, COMPLETED, CONFIRMED

//	private BigDecimal phase1Amount;
//	private BigDecimal phase2Amount;
//	private BigDecimal phase3Amount;
//	private BigDecimal phase4Amount;
//
//	private boolean phase1Paid;
//	private boolean phase2Paid;
//	private boolean phase3Paid;
//	private boolean phase4Paid;
}
