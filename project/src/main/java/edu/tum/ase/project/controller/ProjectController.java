package edu.tum.ase.project.controller;

import edu.tum.ase.project.model.Project;
import edu.tum.ase.project.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    // Create
    @RequestMapping(method = RequestMethod.POST, path = "/projects")
    public Project createProject(@RequestBody Project project) {
        return projectService.createProject(project);
    }

    // Read
    @RequestMapping(method = RequestMethod.GET, path = "/projects")
    public List<Project> getProjects() {
        return projectService.getProjects();
    }

    // Update
    @RequestMapping(method = RequestMethod.PUT, path = "/projects/{projectId}")
    public Project updateProject(@RequestBody Project project, @PathVariable("projectId") String projectId) {
        return projectService.updateProject(project);
    }

    // Delete
    @RequestMapping(method = RequestMethod.DELETE, path = "/projects/{projectId}")
    public void deleteProject(@PathVariable("projectId") String projectId) {
        projectService.deleteProject(projectId);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/projects/{projectId}/{userName}")
    public Project shareProject(@PathVariable("projectId") String projectId, @PathVariable("userName") String userName ) {
        return projectService.shareProject(projectId, userName);
    }
}
