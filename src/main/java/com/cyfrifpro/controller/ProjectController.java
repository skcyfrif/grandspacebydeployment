package com.cyfrifpro.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cyfrifpro.model.Project;
import com.cyfrifpro.repository.ProjectRepository;
import com.cyfrifpro.request.ProjectRequest;
import com.cyfrifpro.response.ProjectResponse;
import com.cyfrifpro.service.AdminService;
//import com.cyfrifpro.service.PaymentService;
import com.cyfrifpro.service.ProjectService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/project")
@CrossOrigin(origins = "http://127.0.0.1:5500")
@Tag(name = "ProjectController", description = "By using this class we can map all kind of project requests.")
public class ProjectController {

	@Autowired
	private ProjectService projectService;

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private AdminService adminService;

	@Autowired
	private ModelMapper modelMapper;

	// @Autowired
//	private PaymentService paymentService;

	// Submit a project
	@PostMapping("/submit")
	@Operation(summary = "Post Api", description = "This is a method for project submission")
	public ResponseEntity<ProjectResponse> submitProject(@RequestBody ProjectRequest projectRequest) {
		ProjectResponse projectResponse = projectService.submitProject(projectRequest);
		return ResponseEntity.ok(projectResponse);
	}

	// Endpoint to get all projects
	@GetMapping("/allProjects")
	public List<ProjectResponse> getAllProjects() {
		List<Project> projects = projectService.getAllProjects();
		// Map each project to a response DTO (ProjectResponse)
		return projects.stream().map(project -> modelMapper.map(project, ProjectResponse.class))
				.collect(Collectors.toList());
	}

	// Endpoint to assign a manager to a project
	@PutMapping("assignManager/{projectId}")
	@Operation(summary = "Put Api", description = "This is a method for manager assigning")
	public ResponseEntity<Project> assignManager(@PathVariable Long projectId,
			@RequestBody ProjectRequest projectRequest) {
		// Set the projectId in the request object
		projectRequest.setId(projectId);

		try {
			// Call the service to assign the client and manager to the project
			Project project = projectService.assignedManager(projectRequest);

			// Return the updated project
			return ResponseEntity.ok(project);
		} catch (RuntimeException ex) {
			// If the client or manager is not premium, handle the error
			return ResponseEntity.badRequest().build();
		}
	}

	// Endpoint to assign a client to a project
	@PutMapping("assignClient/{projectId}")
	@Operation(summary = "Put Api", description = "This is a method for client assigning")
	public ResponseEntity<Project> assignClient(@PathVariable Long projectId,
			@RequestBody ProjectRequest projectRequest) {
		// Set the projectId in the request object
		projectRequest.setId(projectId);

		try {
			// Call the service to assign the client and manager to the project
			Project project = projectService.assignedClient(projectRequest);

			// Return the updated project
			return ResponseEntity.ok(project);
		} catch (RuntimeException ex) {
			// If the client or manager is not premium, handle the error
			return ResponseEntity.badRequest().build();
		}
	}

	// Endpoint to get a project by ID
	@GetMapping("/{projectId}")
	@Operation(summary = "Get Api", description = "This is a method for geting a project by its id")
	public Optional<Project> getProjectById(@PathVariable Long projectId) {
		return projectService.findProjectById(projectId);
	}

	// Endpoint to get projects by client ID
	@GetMapping("/client/{clientId}")
	@Operation(summary = "Get Api", description = "This is a method for geting a project by client id")
	public List<Project> getProjectsByClientId(@PathVariable Long clientId) {
		return projectService.findProjectsByClientId(clientId);
	}

	// Endpoint to get projects by manager ID
	@GetMapping("/manager/{managerId}")
	@Operation(summary = "Get Api", description = "This is a method for geting a project by assigned manager id")
	public List<Project> getProjectsByManagerId(@PathVariable Long managerId) {
		return projectService.findProjectsByManagerId(managerId);
	}

	// Endpoint for client to confirm the project
	@PutMapping("/updateConfirm/{projectId}")
	@Operation(summary = "Put Api", description = "This is a method for client to confirm the project")
	public ResponseEntity<Project> updatePremium(@PathVariable Long projectId) {
		Optional<Project> updatedConfirm = projectService.updateConfirmationStatus(projectId);
		return updatedConfirm.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	// Admin reviews the project and assigns it to all managers
	@PostMapping("/{projectId}/assign-to-managers")
	@Operation(summary = "Post Api", description = "This is a method for notify all the managers about project")
	public ResponseEntity<String> assignProjectToManagers(@PathVariable Long projectId) {
		Project project = projectRepository.findById(projectId)
				.orElseThrow(() -> new RuntimeException("Project not found"));

		adminService.assignProjectToManagers(project);
		return ResponseEntity.status(HttpStatus.OK).body("Project assigned to all managers.");
	}

	@PutMapping("/update-status/completed/{projectId}")
	public String updateStatusToCompleted(@PathVariable Long projectId) {
		Optional<Project> updatedProject = projectService.updateStatusToCompleted(projectId);
		if (updatedProject.isPresent()) {
			return "Project status updated to 'COMPLETED' for project ID: " + projectId;
		} else {
			return "Project not found with ID: " + projectId;
		}
	}

	// Endpoint to get total number of projects
	@GetMapping("/projects/count")
	public long getTotalProjectsCount() {
		return projectService.countTotalProjects();
	}

	// Endpoint to get the count of NOT_ASSIGNED projects
	@GetMapping("/projects/not-assigned/count")
	public long getNotAssignedProjectsCount() {
		return projectService.countNotAssignedProjects();
	}

	@GetMapping("/projects/assigned/count")
	public long getAssignedProjectsCount() {
		return projectService.countAssignedProjects();
	}

	@GetMapping("/projects/confirmed/count")
	public long getConfirmedProjectsCount() {
		return projectService.countConfirmedProjects();
	}

	@GetMapping("/projects/completed/count")
	public long getCompletedProjectsCount() {
		return projectService.countCompletedProjects();
	}

	@GetMapping("/projects/awaiting-estimates/count")
	public long getAwaitingEstimatesProjectsCount() {
		return projectService.countAwaitingEstimatesProjects();
	}

	@GetMapping("/projects/work-in-process/count")
	public long getWorkInProcessProjectsCount() {
		return projectService.countWORKINPROGRESSProjects();
	}

	@GetMapping("/projects/dispatched/count")
	public long getDispatchedProjectsCount() {
		return projectService.countDISPATCHEDProjects();
	}

	@GetMapping("/not-assigned-projects")
	public List<Project> getNotAssignedProjects() {
		return projectService.getNotAssignedProjects();
	}

	@GetMapping("/assigned-projects")
	public List<Project> getAssignedProjects() {
		return projectService.getAssignedProjects();
	}

	@GetMapping("/confirmed-projects")
	public List<Project> getConfirmedProjects() {
		return projectService.getConfirmedProjects();
	}

	@GetMapping("/completed-projects")
	public List<Project> getCompletedProjects() {
		return projectService.getCompletedProjects();
	}

	@GetMapping("/awaiting-estimates-projects")
	public List<Project> getAwaitingEstimatesProjects() {
		return projectService.getAwaitingEstimatesProjects();
	}

	@GetMapping("/work-in-process-projects")
	public List<Project> getWorkInProcessProjects() {
		return projectService.getWORKINPROGRESSProjects();
	}

	@GetMapping("/dispatched-projects")
	public List<Project> getDispatchedProjects() {
		return projectService.getDISPATCHEDProjects();
	}

	@PostMapping("/dispatch/{projectId}")
	public ResponseEntity<String> dispatchProject(@PathVariable Long projectId) {
		try {
			Optional<Project> updatedProject = projectService.updateStatusToDispatched(projectId);
			if (updatedProject.isPresent()) {
				return ResponseEntity.ok("Project status updated to 'DISPATCHED'.");
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found.");
			}
		} catch (IllegalStateException ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + ex.getMessage());
		}
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteProject(@PathVariable Long id) {
		boolean isDeleted = projectService.deleteProjectById(id);

		if (isDeleted) {
			return ResponseEntity.ok("Project deleted successfully.");
		} else {
			return ResponseEntity.status(404).body("Project not found.");
		}
	}

}
