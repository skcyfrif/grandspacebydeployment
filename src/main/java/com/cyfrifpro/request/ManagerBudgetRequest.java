package com.cyfrifpro.request;


import java.math.BigDecimal;

import lombok.Data;

@Data
public class ManagerBudgetRequest {

    private Long managerId;
    private BigDecimal estimatedBudget;

    // Getters and Setters
}

