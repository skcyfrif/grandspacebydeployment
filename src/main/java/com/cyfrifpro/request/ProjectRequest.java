package com.cyfrifpro.request;

import java.math.BigDecimal;
import java.time.LocalDate;

//import com.cyfrifpro.model.Client;
//import com.cyfrifpro.model.Manager;

import lombok.Data;

@Data
public class ProjectRequest {

	private Long id;
	private String name;
	private String description;
	private BigDecimal areaInSquareFeet;
	private BigDecimal budget;
//	private LocalDate startDate;
//	private LocalDate endDate;
	private boolean confirmed;	
	private Long clientId;
	private Long managerId;
    private boolean budgetConfirmed;
	private String status; 

}
