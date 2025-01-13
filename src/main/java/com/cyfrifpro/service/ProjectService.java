package com.cyfrifpro.service;

//import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.slf4j.LoggerFactory;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.stereotype.Service;

import com.cyfrifpro.model.Client;
//import com.cyfrifpro.model.Client;
import com.cyfrifpro.model.Manager;
import com.cyfrifpro.model.Project;
import com.cyfrifpro.repository.ClientRepository;
//import com.cyfrifpro.repository.ManagerBudgetRepository;
import com.cyfrifpro.repository.ManagerRepository;
import com.cyfrifpro.repository.ProjectRepository;
import com.cyfrifpro.request.ProjectRequest;
import com.cyfrifpro.response.ProjectResponse;

import ch.qos.logback.classic.Logger;

//import ch.qos.logback.classic.Logger;

@Service
public class ProjectService {

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private ManagerRepository managerRepository;

//	@Autowired
//	private ManagerBudgetRepository managerBudgetRepository;

	public ProjectResponse submitProject(ProjectRequest projectRequest) {
		// Fetch the client from the database using the client ID in the projectRequest
		Client client = clientRepository.findById(projectRequest.getClientId())
				.orElseThrow(() -> new IllegalArgumentException("Client not found"));

		// Check if the client is premium
		if (!client.isPremium()) {
			throw new IllegalStateException("Project submission is allowed only for premium clients.");
		}

		// Map the projectRequest to a Project entity
		Project project = modelMapper.map(projectRequest, Project.class);

		// Set default values for confirmed and status
		project.setConfirmed(false); // Project is not confirmed yet
		project.setStatus("NOT_ASSIGNED"); // Set default status to NOT_ASSIGNED or any status you prefer

		// Associate the project with the client
		project.setClient(client);

		// Save the project to the database
		project = projectRepository.save(project);

		// Map the saved project entity to a ProjectResponse DTO and return it
		return modelMapper.map(project, ProjectResponse.class);
	}

	// Method to get all projects
	public List<Project> getAllProjects() {
		return projectRepository.findAll();
	}

	// Method to find a project by ID
	public Optional<Project> findProjectById(Long projectId) {
		return projectRepository.findById(projectId);
	}

	// Method to find projects by client ID
	public List<Project> findProjectsByClientId(Long clientId) {
		return projectRepository.findByClientId(clientId);
	}

	// Method to find projects by manager ID
	public List<Project> findProjectsByManagerId(Long managerId) {
		return projectRepository.findByManagerId(managerId);
	}

	// Method to update the confirmation status of a project
	public Optional<Project> updateConfirmationStatus(Long projectId) {
		Optional<Project> projectOptional = projectRepository.findById(projectId);
		if (projectOptional.isPresent()) {
			Project project = projectOptional.get();
			project.setConfirmed(true); // Set the premium field to true
			return clientRepository.save(project);
		}
		return Optional.empty(); // Return empty if the client does not exist
	}

	////////////////////////////////////////////////////////////
	private static final Logger log = (Logger) LoggerFactory.getLogger(ProjectService.class);

	// Assign a client and manager to a project only if both the client and the
	// manager are premium
	public Project assignedManager(ProjectRequest projectRequest) {
		// Fetch the project by ID
		Project project = projectRepository.findById(projectRequest.getId()).orElseThrow(() -> {
			RuntimeException ex = new RuntimeException("Project not found");
			log.error("Exception: {}", ex.getMessage(), ex); // Log the exception
			return ex;
		});

		// Fetch the manager by ID
		Manager manager = managerRepository.findById(projectRequest.getManagerId()).orElseThrow(() -> {
			RuntimeException ex = new RuntimeException("Manager not found");
			log.error("Exception: {}", ex.getMessage(), ex); // Log the exception
			return ex;
		});

		// Check if the manager is premium
		if (!manager.isPremium()) {
			RuntimeException ex = new RuntimeException("Only premium managers can be assigned to projects.");
			log.error("Exception: {}", ex.getMessage(), ex); // Log the exception
			throw ex;
		}

		// Assign the manager to the project
		project.setManager(manager);

		// Set the project status to "ASSIGNED"
		project.setStatus("ASSIGNED");

		// Save the updated project
		return projectRepository.save(project);
	}

	public Project assignedClient(ProjectRequest projectRequest) {
		Project project = projectRepository.findById(projectRequest.getId()).orElseThrow(() -> {
			RuntimeException ex = new RuntimeException("Project not found");
			log.error("Exception: {}", ex.getMessage(), ex); // Log the exception
			return ex;
		});

		Client client = clientRepository.findById(projectRequest.getClientId()).orElseThrow(() -> {
			String errorMessage = "Client not found";
			log.error("Exception: {}", errorMessage);
			return new RuntimeException(errorMessage);
		});

		if (!client.isPremium()) {
			RuntimeException ex = new RuntimeException("Only premium clients can be assigned a manager.");
			log.error("Exception: {}", ex.getMessage(), ex); // Log the exception
			throw ex;
		}

		project.setClient(client);
//		project.setManager(manager);
		return projectRepository.save(project);
	}

	public Optional<Project> updateStatusToCompleted(Long projectId) {
		// Fetch the project by ID
		Optional<Project> projectOptional = projectRepository.findById(projectId);
		if (projectOptional.isPresent()) {
			Project project = projectOptional.get();

			// Update the status to "COMPLETED"
			project.setStatus("COMPLETED");

			// Save the updated project back to the repository
			return Optional.of(projectRepository.save(project));
		}
		return Optional.empty(); // Return empty if the project is not found
	}

	// Method to count total number of projects
	public long countTotalProjects() {
		return projectRepository.count();
	}

	// Method to count projects with "NOT_ASSIGNED" status
	public long countNotAssignedProjects() {
		return projectRepository.countByStatus("NOT_ASSIGNED");
	}

	// Method to count projects with "ASSIGNED" status
	public long countAssignedProjects() {
		return projectRepository.countByStatus("ASSIGNED");
	}

	// Method to count projects with "AWAITING_ESTIMATES" status
	public long countConfirmedProjects() {
		return projectRepository.countByStatus("CONFIRMED");
	}

	// Method to count projects with "AWAITING_ESTIMATES" status
	public long countCompletedProjects() {
		return projectRepository.countByStatus("COMPLETED");
	}

	// Method to count projects with "AWAITING_ESTIMATES" status
	public long countAwaitingEstimatesProjects() {
		return projectRepository.countByStatus("AWAITING_ESTIMATES");
	}

	// Method to count projects with "AWAITING_ESTIMATES" status
	public long countWORKINPROGRESSProjects() {
		return projectRepository.countByStatus("WORK_IN_PROGRESS");
	}

	// Method to count projects with "AWAITING_ESTIMATES" status
	public long countDISPATCHEDProjects() {
		return projectRepository.countByStatus("DISPATCHED");
	}

	// Method to get all projects with the status "NOT_ASSIGNED"
	public List<Project> getNotAssignedProjects() {
		return projectRepository.findByStatus("NOT_ASSIGNED");
	}

	// Method to get all projects with the status "ASSIGNED"
	public List<Project> getAssignedProjects() {
		return projectRepository.findByStatus("ASSIGNED");
	}

	// Method to get all projects with the status "ASSIGNED"
	public List<Project> getConfirmedProjects() {
		return projectRepository.findByStatus("CONFIRMED");
	}

	// Method to get all projects with the status "ASSIGNED"
	public List<Project> getCompletedProjects() {
		return projectRepository.findByStatus("COMPLETED");
	}

	// Method to get all projects with the status "AWAITING_ESTIMATES"
	public List<Project> getAwaitingEstimatesProjects() {
		return projectRepository.findByStatus("AWAITING_ESTIMATES");
	}

	// Method to get all projects with the status "AWAITING_ESTIMATES"
	public List<Project> getWORKINPROGRESSProjects() {
		return projectRepository.findByStatus("WORK_IN_PROGRESS");
	}

	// Method to get all projects with the status "AWAITING_ESTIMATES"
	public List<Project> getDISPATCHEDProjects() {
		return projectRepository.findByStatus("DISPATCHED");
	}

	public Optional<Project> updateStatusToDispatched(Long projectId) {
		// Fetch the project by ID
		Optional<Project> projectOptional = projectRepository.findById(projectId);

		// Check if project exists
		if (projectOptional.isPresent()) {
			Project project = projectOptional.get();

			// Check if the current status allows it to be dispatched (e.g., only from
			// "WORK_IN_PROGRESS" or "ASSIGNED")
//	        if (!project.getStatus().equals("WORK_IN_PROGRESS") && !project.getStatus().equals("ASSIGNED")) {
//	            String errorMessage = "Project status must be 'WORK_IN_PROGRESS' or 'ASSIGNED' to be dispatched.";
//	            log.error("Exception: {}", errorMessage);
//	            throw new IllegalStateException(errorMessage);
//	        }

			// Update the status to "DISPATCHED"
			project.setStatus("DISPATCHED");

			// Save the updated project back to the repository
			projectRepository.save(project);

			// Return the updated project
			return Optional.of(project);
		}

		// Return empty if project is not found
		return Optional.empty();
	}

	public boolean deleteProjectById(Long projectId) {
		// Check if the project exists before attempting to delete
		Optional<Project> projectOptional = projectRepository.findById(projectId);

		// If the project exists, delete it
		if (projectOptional.isPresent()) {
			projectRepository.deleteById(projectId); // Delete the project
			return true; // Return true indicating the deletion was successful
		}

		// Return false if the project was not found
		return false;
	}

}
