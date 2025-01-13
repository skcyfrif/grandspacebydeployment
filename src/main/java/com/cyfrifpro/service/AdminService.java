package com.cyfrifpro.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cyfrifpro.model.Manager;
import com.cyfrifpro.model.Project;
import com.cyfrifpro.repository.ManagerRepository;
import com.cyfrifpro.repository.ProjectRepository;

@Service
public class AdminService {

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private ManagerRepository managerRepository;

	public void assignProjectToManagers(Project project) {
		// Get all managers
		List<Manager> managers = managerRepository.findAll();

		// Assign the project to all managers
		for (Manager manager : managers) {
			// Add project to manager's list (assuming a bid/estimate process)
			manager.addProjectToBidList(project);
		}

		// Update the project status to "AWAITING_ESTIMATES"
		project.setStatus("AWAITING_ESTIMATES");
		projectRepository.save(project);
	}
}
